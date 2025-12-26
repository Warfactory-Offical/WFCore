package wfcore.common.metatileentities;


import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import wfcore.common.metatileentities.multi.electric.MetaTileEntityRadar;
import wfcore.common.metatileentities.multi.primitive.MetaTileEntityWarfactoryBlastFurnace;

import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;

public class WFCoreMetaTileEntities {


    public static MetaTileEntityWarfactoryBlastFurnace LARGEBLASTFURNACE;
    public static MetaTileEntityRadar RADAR;


    public static int id = 10000;

    static {
    }

    public static void init() {
        //Multis
        LARGEBLASTFURNACE = registerMetaTileEntity(id++, new MetaTileEntityWarfactoryBlastFurnace(location("largeblastfurnace")));
        RADAR = registerMetaTileEntity(id++, new MetaTileEntityRadar(location("radar")));
    }

    private static ResourceLocation location(@NotNull String name) {
        return new ResourceLocation("wfcore", name);
    }
}