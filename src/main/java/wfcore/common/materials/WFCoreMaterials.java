package wfcore.common.materials;

import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.materials.*;
import gregtech.api.unification.ore.OrePrefix;

import java.util.concurrent.atomic.AtomicBoolean;

public class WFCoreMaterials {
    private static final AtomicBoolean INIT = new AtomicBoolean(false);



    public static void register() {
        if (INIT.getAndSet(true)) {
            return;
        }


        FirstDegreeMaterials.register();
        HbmMaterials.register();
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

        ;

    }
    public static Material AdvancedAlloy;
    public static Material Desh;
    public static Material Australium;
    public static Material Schrabidium;
    public static Material Unobtainium;
    public static Material CMBSteel;
    public static Material Neoveline;
    public static Material Verticium;

}
