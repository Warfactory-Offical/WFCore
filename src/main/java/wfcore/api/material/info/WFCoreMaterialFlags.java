package wfcore.api.material.info;

import gregtech.api.unification.material.info.MaterialFlag;
import gregtech.api.unification.material.info.MaterialFlags;
import gregtech.api.unification.material.properties.PropertyKey;

public class WFCoreMaterialFlags {
    public static final MaterialFlag GENERATE_BILLET = new MaterialFlag.Builder("generate_billet")
            .requireProps(PropertyKey.INGOT)
            .build();

    public static final MaterialFlag GENERATE_SHELL = new MaterialFlag.Builder("generate_shell")
            .requireProps(PropertyKey.INGOT)
            .requireFlags(MaterialFlags.GENERATE_PLATE)
            .build();

    public static final MaterialFlag GENERATE_NTMPIPE = new MaterialFlag.Builder("generate_ntmpipe")
            .requireProps(PropertyKey.INGOT)
            .requireFlags(MaterialFlags.GENERATE_PLATE)
            .build();

    public static final MaterialFlag GENERATE_DENSE_WIRE = new MaterialFlag.Builder("generate_dense_wire")
            .requireProps(PropertyKey.WIRE)
            .requireFlags(MaterialFlags.GENERATE_FINE_WIRE)
            .build();

    public static final MaterialFlag GENERATE_CAST_PLATE = new MaterialFlag.Builder("generate_cast_plate")
            .requireProps(PropertyKey.INGOT)
            .requireFlags(MaterialFlags.GENERATE_PLATE)
            .build();

    public static final MaterialFlag GENERATE_WELDED_PLATE = new MaterialFlag.Builder("generate_welded_plate")
            .requireProps(PropertyKey.INGOT)
            .requireFlags(MaterialFlags.GENERATE_PLATE)
            .build();

}
