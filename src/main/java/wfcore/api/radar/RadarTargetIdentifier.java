package wfcore.api.radar;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.jetbrains.annotations.NotNull;
import wfcore.WFCore;

import java.util.Objects;

public class RadarTargetIdentifier {
    @NotNull
    public final String id;

    public int intensity = 0;

    public RadarTargetIdentifier(@NotNull String id) {
        this.id = id;
    }

    public RadarTargetIdentifier intensity(int intensity) {
        this.intensity = intensity;
        return this;
    }

    @Override
    public String toString() {
        return id;
        /*
        StringBuilder propertiesString = new StringBuilder("[");
        var propertyEntries = properties.object2ObjectEntrySet();
        var propIt = propertyEntries.iterator();

        // iterate over all properties and stringify
        while (propIt.hasNext()) {
            var property = propIt.next();
            propertiesString.append(property.getKey());
            propertiesString.append(": ");
            propertiesString.append(property.getValue());
            if (propIt.hasNext()) { propertiesString.append(", "); }
        }

        propertiesString.append("]");
        return propertiesString.toString();
         */
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) { return true; }

        if (other instanceof RadarTargetIdentifier identifier) {
            return Objects.equals(id, identifier.id);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    // parses block state strings to pick out properties and separate from resource location
    public static RadarTargetIdentifier fromBlockState(IBlockState state) {
        ResourceLocation stateBlockResource = state.getBlock().getRegistryName();
        if (stateBlockResource == null || stateBlockResource.toString().length() < 1) {
            String status = stateBlockResource == null ? "no" : "empty";
            WFCore.LOGGER.atError().log("Got a state with " + status + " resource location: " + state);
            stateBlockResource = new ResourceLocation(state.getBlock().getClass().getName());
        }

        return new RadarTargetIdentifier(stateBlockResource.toString());
    }

    // will try, in increasing preference, the blockstate string, then the resource location, then display name string if possible
    public static RadarTargetIdentifier getBestIdentifier(TileEntity targetTE) {
        // get the targeted TE and try to pull the display name
        ResourceLocation teResource = TileEntity.getKey(targetTE.getClass());
        ITextComponent targTEDisplayName = targetTE.getDisplayName();
        String displayNameKey = null;

        // try to get a more interesting display name
        if (targTEDisplayName != null) {
            // get the default values for the raw display name
            displayNameKey = targTEDisplayName.getUnformattedComponentText();

            // we want to use the key in most cases, not the translated name
            if (targTEDisplayName instanceof TextComponentTranslation translatable) {
                displayNameKey = translatable.getKey();

                // display the transformation if there is formatting to be done
                if (translatable.getFormatArgs().length > 0) {
                    displayNameKey += "\n->\n" + String.format(translatable.getKey(), translatable.getFormatArgs());
                }
            }
        }

        // try the displayNameKey first if it isn't null
        if (displayNameKey != null) {
            return new RadarTargetIdentifier(displayNameKey);
        }

        // if we have no display name and no resource, then we just have to go by block
        if (teResource == null || teResource.toString().length() < 1) {
            String status = teResource == null ? "no" : "empty";
            WFCore.LOGGER.atError().log("Got a tile entity with " + status + " resource location: " + targetTE);
            return new RadarTargetIdentifier(targetTE.getClass().getName());
        }

        // return just the tile entity resource key
        return new RadarTargetIdentifier(teResource.toString());
    }

    public static RadarTargetIdentifier getBestIdentifier(Entity target) {
        // get the entity key
        ResourceLocation entityKey = EntityList.getKey(target);

        // get the nbt data associated with the entity - may be useful later
        //NBTTagCompound entityNBT = target.getEntityData();

        if (entityKey == null) {
            WFCore.LOGGER.atError().log("Got an entity with no resource location: " + target);
            entityKey = new ResourceLocation(target.getClass().getName());
        }

        return new RadarTargetIdentifier(entityKey.toString());
    }
}
