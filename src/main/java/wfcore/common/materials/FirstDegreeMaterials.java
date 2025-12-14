package wfcore.common.materials;

import gregtech.api.unification.material.properties.FluidPipeProperties;
import gregtech.api.unification.material.properties.PropertyKey;

import static gregtech.api.unification.material.Materials.Brick;

public class FirstDegreeMaterials {
    public static void register() {
        int id = 700;
        Brick.setProperty(PropertyKey.FLUID_PIPE, new FluidPipeProperties(2200, 20, false, false, false, false));
    }
}
