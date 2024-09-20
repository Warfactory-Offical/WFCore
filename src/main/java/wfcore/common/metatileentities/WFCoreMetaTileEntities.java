package wfcore.common.metatileentities;



import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import wfcore.common.metatileentities.multi.MetaTileEntityAlloyKiln;
import wfcore.common.metatileentities.multi.MetaTileEntityBoilerTower;
import wfcore.common.metatileentities.multi.MetaTileEntityLargePrimitiveBlastFurnace;
import wfcore.common.metatileentities.multi.MetaTileEntityRadar;
import wfcore.common.metatileentities.multi.electric.*;
import wfcore.common.metatileentities.multi.electric.computing.MetaTileEntityComputingServer;
import wfcore.common.metatileentities.multi.steam.*;

import static gregtech.common.metatileentities.MetaTileEntities.*;

public class WFCoreMetaTileEntities {

    public static MetaTileEntityProjector PROJECTOR;
    public static MetaTileEntityLargeSteamForgeHammer LARGESTEAMFORGEHAMMER;
    public static MetaTileEntityLargeSteamCompressor LARGESTEAMCOMPRESSOR;
    public static MetaTileEntityLargeSteamWasher LARGESTEAMWASHER;
    public static MetaTileEntityLargeSteamCentrifuge LARGESTEAMCENTRIFUGE;
    public static MetaTileEntityLargeSteamMixer LARGESTEAMMIXER;
    public static MetaTileEntitySteamWiremill STEAMWIREMILL;
    public static MetaTileEntityBoilerTower BOILERTOWER;
    public static MetaTileEntityComputingServer COMPUTINGSERVER;
    public static MetaTileEntityLargePrimitiveBlastFurnace LARGE_PRIMITIVE_BLAST_FURNACE;
    public static MetaTileEntityAlloyKiln ALLOY_KILN;
    public static MetaTileEntityRadar RADAR;

    public static int id = 0;

    public static void init() {
        //Multis
        PROJECTOR = registerMetaTileEntity(id++, new MetaTileEntityProjector(location("projector")));
        LARGESTEAMFORGEHAMMER = registerMetaTileEntity(id++, new MetaTileEntityLargeSteamForgeHammer(location("largesteamforgehammer")));
        LARGESTEAMCOMPRESSOR = registerMetaTileEntity(id++, new MetaTileEntityLargeSteamCompressor(location("largesteamcompressor")));
        LARGESTEAMWASHER = registerMetaTileEntity(id++, new MetaTileEntityLargeSteamWasher(location("largesteamwasher")));
        LARGESTEAMCENTRIFUGE = registerMetaTileEntity(id++, new MetaTileEntityLargeSteamCentrifuge(location("largesteamcentrifuge")));
        LARGESTEAMMIXER = registerMetaTileEntity(id++, new MetaTileEntityLargeSteamMixer(location("largesteammixer")));
        STEAMWIREMILL = registerMetaTileEntity(id++, new MetaTileEntitySteamWiremill(location("steamwiremill")));
        BOILERTOWER = registerMetaTileEntity(id++, new MetaTileEntityBoilerTower(location("boilertower")));
        COMPUTINGSERVER = registerMetaTileEntity(id++, new MetaTileEntityComputingServer(location("computingserver")));
        LARGE_PRIMITIVE_BLAST_FURNACE = registerMetaTileEntity(id++, new MetaTileEntityLargePrimitiveBlastFurnace(location("large_primitive_blast_furnace")));
        ALLOY_KILN = registerMetaTileEntity(id++, new MetaTileEntityAlloyKiln(location("alloy_kiln")));
        RADAR = registerMetaTileEntity(id++, new MetaTileEntityRadar(location("radar")));
    }

    private static ResourceLocation location(@NotNull String name) {
        return new ResourceLocation("wfcore", name);
    }

    /*public static void registerWFCoreSimpleMetaTileEntity(GCYWSimpleMachineMetaTileEntity[] machines, int startId, int maxTier, String name, RecipeMap<?> map, ICubeRenderer texture, boolean hasFrontFacing,  Function<String, ResourceLocation> resourceId, Function<Integer, Integer> tankScalingFunction) {
        for(int i = 0; i < maxTier; ++i) {
            String voltageName = GTValues.VN[i + 1].toLowerCase();
            machines[i + 1] = registerMetaTileEntity(startId + i, new WFCoreSimpleMachineMetaTileEntity(resourceId.apply(String.format("%s.%s", name, voltageName)), map, texture, i + 1, hasFrontFacing, tankScalingFunction));
        }

    }*/



    static {
    }
}