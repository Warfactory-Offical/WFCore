package wfcore.api.utils;

import net.minecraft.tileentity.TileEntity;

import java.util.HashSet;
import java.util.Set;

public final class RadarTeWhitelist {

    // Holds the tileentites that are scanned for in the radar
    //Provide TE full class names here
    public static final Set<Class<? extends TileEntity>> TE_WHITELIST = new HashSet<>();
    static {
        //TODO: add any viable tileenttity from GT, AE2, WARFORGE and more
    }
}
