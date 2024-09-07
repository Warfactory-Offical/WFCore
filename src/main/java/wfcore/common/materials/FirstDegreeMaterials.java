package wfcore.common.materials;

import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.DustProperty;
import gregtech.api.unification.material.properties.FluidPipeProperties;
import gregtech.api.unification.material.properties.IngotProperty;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.GTValues;
import gregtech.api.fluids.FluidBuilder;
import gregtech.api.unification.Elements;
import gregtech.api.unification.material.properties.BlastProperty.GasTier;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialIconSet.*;
import static gregtech.api.util.GTUtility.gregtechId;
import static gregtech.api.GTValues.MV;
import static gregtech.api.GTValues.V;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.util.GTUtility.gregtechId;
import static wfcore.common.materials.WFCoreMaterials.*;
public class FirstDegreeMaterials {
    public static void register() {
        int id = 700;
        Brick.setProperty(PropertyKey.FLUID_PIPE, new FluidPipeProperties(2200, 20, false, false, false, false));
   }
}
