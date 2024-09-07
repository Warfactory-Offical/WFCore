package wfcore.api.material.modifications;

import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialFlag;
import wfcore.api.material.info.WFCoreMaterialFlags;

import java.util.List;

import static gregtech.api.unification.material.Materials.*;

public class WFCoreMaterialExtraFlags {

    public static void setFlags(Material[] materials, MaterialFlag... flags) {
        for (Material material : materials) {
            material.addFlags(flags);
        }
    }

    public static void setFlags(List<Material> materials, MaterialFlag... flags) {
        for (Material material : materials) {
            material.addFlags(flags);
        }
    }

    public static void register() {
        billet();
        shell();
        densewire();
        ntmpipe();
        plateTriple();
        plateSextuple();
    }


    private static void billet() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};

        setFlags(materials, WFCoreMaterialFlags.GENERATE_BILLET);
    }

    private static void shell() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};

        setFlags(materials, WFCoreMaterialFlags.GENERATE_DENSE_WIRE);
    }

    private static void densewire() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};

        setFlags(materials, WFCoreMaterialFlags.GENERATE_SHELL);
    }

    private static void ntmpipe() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};

        setFlags(materials, WFCoreMaterialFlags.GENERATE_NTMPIPE);
    }

    private static void plateTriple() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};

        setFlags(materials, WFCoreMaterialFlags.GENERATE_CAST_PLATE);
    }

    private static void plateSextuple() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};

        setFlags(materials, WFCoreMaterialFlags.GENERATE_WELDED_PLATE);
    }
}
