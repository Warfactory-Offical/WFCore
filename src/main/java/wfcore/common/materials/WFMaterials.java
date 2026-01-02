package wfcore.common.materials;

import gregtech.api.GTValues;
import gregtech.api.fluids.FluidBuilder;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.BlastProperty;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static gregtech.api.GTValues.HV;
import static gregtech.api.GTValues.VA;
import static gregtech.api.unification.material.Materials.STD_METAL;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_DOUBLE_PLATE;
import static gregtech.api.unification.material.info.MaterialIconSet.METALLIC;
import static gregtech.api.util.GTUtility.gregtechId;

public class WFMaterials {
    private static final AtomicBoolean INIT = new AtomicBoolean(false);
    public static Material AdvancedAlloy;
    public static Material Desh;
    public static Material Australium;
    public static Material Schrabidium;
    public static Material Unobtainium;
    public static Material CMBSteel;
    public static Material Verticium;
    public static Material GalvanizedSteel;

    public static void register() {

        var atomicID = new AtomicInteger(800);

        if (INIT.getAndSet(true)) {
            return;
        }


        FirstDegreeMaterials.register();
        HbmMaterials.register(atomicID);
        registerWFMaterials(atomicID);

        /*
         * FOR ADDON DEVELOPERS:
         *
         * GTCEu will not take more than 3000 IDs. Anything past ID 2999
         * is considered FAIR GAME, take whatever you like.
         *
         * If you would like to reserve IDs, feel free to reach out to the
         * development team and claim a range of IDs! We will mark any
         * claimed ranges below this comment. Max value is 32767.
         *
         * - Gregicality: 3000-19999
         * - Gregification: 20000-20999
         * - HtmlTech: 21000-21499
         * - GregTech Food Option: 21500-22499
         * - FREE RANGE 22500-23599
         * - MechTech: 23600-23999
         * - FREE RANGE 24000-31999
         * - Reserved for CraftTweaker: 32000-32767
         */

    }


    public static void registerWFMaterials(AtomicInteger id){
        GalvanizedSteel = new Material.Builder(id.getAndAdd(1), gregtechId("Galvanized_Steel"))
                .cableProperties(GTValues.V[GTValues.MV], 4, 0, true)
                .color(0xa4a4a4).ingot()
                .liquid(new FluidBuilder().temperature(1373))
                .iconSet(METALLIC)
                .flags(STD_METAL, GENERATE_LONG_ROD, GENERATE_FRAME,
                        GENERATE_DOUBLE_PLATE)
                .fluidPipeProperties(1200, 40, true)
                .blast(b -> b.temp(2700, BlastProperty.GasTier.LOW).blastStats(VA[HV], 200))
                .build();

    }

}
