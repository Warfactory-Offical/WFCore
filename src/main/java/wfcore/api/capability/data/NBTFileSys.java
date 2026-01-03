package wfcore.api.capability.data;

import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;

public class NBTFileSys {
    private enum FileTypes {
        DIRECTORY,
        DATA;

        public byte getKey() {
            return (byte) this.ordinal();
        }
    }

    // each data inheritor must specify the method it uses to create an instance of itself from NBT
    public static DataHandler DATA_HANDLER = new DataHandler().initializeDataHandler();

    // folders are nbt compounds with a sublist of nbt compounds keyed as "children", while files have a "data" compound
    private static final String DATA_CLASS_ID_KEY = "data_class_id";
    private static final String ROOT_KEY = "wf_filesys_root";
    private static final String FILE_TYPE_KEY = "file_type";
    private static final String DATA_KEY = "file_data";
    private static final String PATH_SEP = "/";
    public static final String ROOT_PATH = PATH_SEP;

    public static final int BYTE_TAG_ID = 1;
    public static final int INT_TAG_ID = 4;
    public static final int COMPOUND_TAG_ID = 10;

    private static boolean isClassIdRegistered(int classId) {
        return DATA_HANDLER.DATA_READER_REGISTRY.containsKey(classId);
    }

    private static Function<NBTTagCompound, ? extends IData> getNBTSupplier(int classId) {
        return DATA_HANDLER.DATA_READER_REGISTRY.get(classId);
    }

    public static String[] splitPath(String path) {
        return path.split(PATH_SEP);
    }

    // assumes path is already valid
    public static String getFileName(String path) {
        int lastSep = path.lastIndexOf(PATH_SEP);
        return path.substring(lastSep + 1);
    }

    // assumes path is valid
    public static String getParent(String path) {
        int lastSep = path.lastIndexOf(PATH_SEP);
        if (lastSep < 1) { lastSep = path.length(); }
        return path.substring(0, lastSep);
    }

    private static boolean isValidPathName(@NotNull String name) {
        if (name.isEmpty()) { return false; }
        if (name.contains(PATH_SEP)) { return false; }
        if (name.equals("*")) { return false; }

        return true;
    }

    private static boolean isValidPath(@NotNull String path) {
        for (String pathComp : splitPath(path)) {
            if (!isValidPathName(pathComp)) {
                return false;
            }
        }

        return true;
    }

    // don't use forward slashes or single asterisks
    public static String makePath(@NotNull String... orderedComponents) {
        if (orderedComponents.length == 0) { return PATH_SEP; }  // return root if no arguments passed

        StringBuilder path = new StringBuilder();
        for (String comp : orderedComponents) {
            if (!isValidPathName(comp)) { return null; }  // give up on path; components are invalid
            path.append(PATH_SEP);
            path.append(comp);
        }

        return path.toString();
    }

    private static FileTypes getFileType(NBTTagCompound nbtFile) {
        if (!nbtFile.hasKey(FILE_TYPE_KEY, BYTE_TAG_ID)) {
            return null;
        }

        return FileTypes.values()[nbtFile.getByte(FILE_TYPE_KEY)];
    }

    private static void setFileType(NBTTagCompound nbtFile, FileTypes type) {
        nbtFile.setByte(FILE_TYPE_KEY, type.getKey());
    }

    // will return null if error occurs and file can't be found
    private static @Nullable NBTTagCompound locateFile(@NotNull NBTTagCompound nbt, String path, boolean createIfAbsent) {
        if (!isValidPath(path)) { return null; }

        // confirm that the nbt is part of the file system, or if not see if it has the root to one
        var fileSys = nbt;
        var rootType = getFileType(fileSys);

        // if this is not part of a file system, then it needs to contain the root to one
        if (rootType == null) {
            if (!nbt.hasKey(ROOT_KEY, COMPOUND_TAG_ID)) { return null; }
            fileSys = nbt.getCompoundTag(ROOT_KEY);
        }

        // if this is a part of a file system, but is not a directory, it must be root to be the target
        else if (rootType != FileTypes.DIRECTORY) {
            if (!path.equals(ROOT_PATH)) { return null; }
            return nbt;
        }

        // begin searching through the root we found
        for (String pathComp : splitPath(path)) {
            // the current fileSys must be a folder
            var type = getFileType(fileSys);
            if (type == null) {
                fileSys.setByte(FILE_TYPE_KEY, FileTypes.DIRECTORY.getKey());
            }

            // cannot locate a file inside a non-directory file
            else if (type != FileTypes.DIRECTORY) {
                return null;
            }

            // path not present or wrong nbt type
            if (!fileSys.hasKey(pathComp, COMPOUND_TAG_ID)) {
                if (!createIfAbsent) { return null; }

                // if something existed with the same key, but different type, it will get replaced
                fileSys.setTag(pathComp, new NBTTagCompound());
            }

            fileSys = fileSys.getCompoundTag(pathComp);  // go down to appropriate child
        }

        // by the end of this we will have arrived at the child
        return fileSys;
    }

    public static boolean hasData(@NotNull NBTTagCompound nbt, String path) {
        return locateFile(nbt, path, false) != null;
    }

    // will write new data or overwrite old data; won't delete directories or change file type; auto-creates path as needed
    public static <T extends IData> boolean writeData(@NotNull NBTTagCompound nbt, String path, IData data) {
        NBTTagCompound targetNBT = locateFile(nbt, path, true);
        if (targetNBT == null) { return false; }

        // must be a request to just create a folder
        if (data == null) {
            var type = getFileType(targetNBT);

            // if folder or file exists, no write can occur
            if (type != null) { return false; }

            // write as folder
            setFileType(targetNBT, FileTypes.DIRECTORY);
            return true;
        }

        var type = getFileType(targetNBT);

        // write data into location, overwriting as needed
        if (type == null) {
            targetNBT.setByte(FILE_TYPE_KEY, FileTypes.DATA.getKey());
        }

        // if the target is an existing folder, we cannot write data at its location
        else if (type == FileTypes.DIRECTORY) {
            return false;
        }

        // write the file
        String fileName = getFileName(path);
        targetNBT.setInteger(DATA_CLASS_ID_KEY, data.getTypeId().ordinal());
        targetNBT.setTag(DATA_KEY, data.toNBT());
        return true;
    }

    public static boolean deleteData(@NotNull NBTTagCompound nbt, String path, IData data) {
        String parent = getParent(path);
        String fileName = getFileName(path);
        NBTTagCompound targetNBT = locateFile(nbt, parent, false);
        if (targetNBT == null) { return false; }

        var type = getFileType(targetNBT);
        if (type != FileTypes.DIRECTORY) { return false; }  // can delete data not in a directory
        if (!targetNBT.hasKey(fileName, COMPOUND_TAG_ID)) { return false; }  // can't delete a file that doesn't exist

        targetNBT.removeTag(fileName);
        return true;
    }

    // assumes the type of this nbt is already data
    private static @Nullable IData getData(NBTTagCompound data) {
        if (!data.hasKey(DATA_CLASS_ID_KEY, INT_TAG_ID)) { return null; }  // no class id -> can't decode

        int classId = data.getInteger(DATA_CLASS_ID_KEY);
        if (!isClassIdRegistered(classId)) { return null; }  // unregistered class id -> can't decode

        if (!data.hasKey(DATA_KEY, COMPOUND_TAG_ID)) { return null; }  // no data -> can't decode

        return getNBTSupplier(classId).apply(data.getCompoundTag(DATA_KEY));
    }

    // will return null if read can't occur; will recursively read if supplied path is a folder
    public static @Nullable ArrayList<IData> readData(@NotNull NBTTagCompound nbt, String path) {
        NBTTagCompound targetNBT = locateFile(nbt, path, false);
        if (targetNBT == null) { return null; }

        // handle single file case and prepare for iteration
        var result = new ArrayList<IData>();
        var type = getFileType(targetNBT);
        if (type != FileTypes.DIRECTORY) {
            result.add(getData(targetNBT));
            return result;
        }

        // handle recursive search
        for (String fileName : targetNBT.getKeySet()) {
            var recurseResult = readData(targetNBT, makePath(fileName));
            if (recurseResult == null) { continue; }
            result.addAll(recurseResult);  // recursively read in all the data
        }

        return result;
    }
}
