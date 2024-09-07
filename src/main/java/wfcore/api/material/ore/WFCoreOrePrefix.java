package wfcore.api.material.ore;

import gregtech.api.unification.ore.OrePrefix;
import wfcore.api.material.info.WFCoreMaterialFlags;
import wfcore.api.material.info.WFCoreMaterialIconType;

import static gregtech.api.GTValues.M;
import static gregtech.api.unification.ore.OrePrefix.Flags.ENABLE_UNIFICATION;

public class WFCoreOrePrefix {


    public static final OrePrefix billet = new OrePrefix("billet", (M + 1) / 2 + 3
            , null, WFCoreMaterialIconType.billet,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(WFCoreMaterialFlags.GENERATE_BILLET));

    public static final OrePrefix wiredense = new OrePrefix("wiredense", M, null, WFCoreMaterialIconType.wiredense,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(WFCoreMaterialFlags.GENERATE_DENSE_WIRE));

    public static final OrePrefix ntmpipe = new OrePrefix("ntmpipe", M * 3, null, WFCoreMaterialIconType.ntmpipe,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(WFCoreMaterialFlags.GENERATE_NTMPIPE));

    public static final OrePrefix shell = new OrePrefix("shell", M * 4, null, WFCoreMaterialIconType.shell,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(WFCoreMaterialFlags.GENERATE_SHELL));

    public static final OrePrefix plateTriple = new OrePrefix("plateTriple", M * 3, null, WFCoreMaterialIconType.plateWelded,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(WFCoreMaterialFlags.GENERATE_CAST_PLATE));

    public static final OrePrefix plateSextuple = new OrePrefix("plateSextuple", M * 6, null, WFCoreMaterialIconType.plateSextuple,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(WFCoreMaterialFlags.GENERATE_WELDED_PLATE));

}
