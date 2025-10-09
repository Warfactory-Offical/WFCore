package wfcore.common.materials;

import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.DustProperty;
import gregtech.api.unification.material.properties.FluidPipeProperties;
import gregtech.api.unification.material.properties.IngotProperty;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.GTValues;
import gregtech.api.unification.Elements;
import gregtech.api.fluids.FluidBuilder;
import gregtech.api.unification.material.properties.BlastProperty.GasTier;
import wfcore.api.material.info.WFCoreMaterialFlags;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialIconSet.*;
import static gregtech.api.util.GTUtility.gregtechId;
import static gregtech.api.GTValues.MV;
import static wfcore.common.materials.WFCoreMaterials.*;
public class HbmMaterials {
    public static void register() {
        int id = 800;
        AdvancedAlloy = new Material.Builder(id++, gregtechId("Advanced_Alloy"))
                .cableProperties(GTValues.V[GTValues.LV], 4, 0, true)
                .color(0xE24207).ingot()
                .liquid(new FluidBuilder().temperature(1373))
                .iconSet(METALLIC)
                .flags(STD_METAL, GENERATE_LONG_ROD, GENERATE_FINE_WIRE, GENERATE_SPRING, GENERATE_FOIL, GENERATE_FRAME,
                        GENERATE_DOUBLE_PLATE)
                .fluidPipeProperties(1200, 40, true)
                .blast(b -> b.temp(1373, GasTier.LOW).blastStats(VA[MV], 200))
                .build();
        Desh = new Material.Builder(id++, gregtechId("Workers_Alloy"))
                .cableProperties(GTValues.V[GTValues.MV], 4, 0, true)
                .color(0xA80300).ingot()
                .liquid(new FluidBuilder().temperature(1373))
                .iconSet(METALLIC)
                .flags(STD_METAL, GENERATE_LONG_ROD, GENERATE_FINE_WIRE, GENERATE_SPRING, GENERATE_FOIL, GENERATE_FRAME,
                        GENERATE_DOUBLE_PLATE)
                .fluidPipeProperties(1200, 40, true)
                .blast(b -> b.temp(2700, GasTier.LOW).blastStats(VA[HV], 200))
                .build();
        Australium = new Material.Builder(id++, gregtechId("Australium"))
                .cableProperties(GTValues.V[GTValues.MV], 4, 0, true)
                .color(0xFED73A).ingot()
                .liquid(new FluidBuilder().temperature(1373))
                .iconSet(METALLIC)
                .flags(STD_METAL, GENERATE_LONG_ROD, GENERATE_FINE_WIRE, GENERATE_SPRING, GENERATE_FOIL, GENERATE_FRAME,
                        GENERATE_DOUBLE_PLATE, WFCoreMaterialFlags.GENERATE_NTMPIPE)
                .fluidPipeProperties(1200, 40, true)
                .blast(b -> b.temp(2700, GasTier.LOW).blastStats(VA[HV], 200))
                .build();
        Schrabidium = new Material.Builder(id++, gregtechId("Schrabidium"))
                .cableProperties(GTValues.V[GTValues.MV], 4, 0, true)
                .color(0x2CD8DF).ingot()
                .liquid(new FluidBuilder().temperature(1373))
                .iconSet(METALLIC)
                .flags(STD_METAL, GENERATE_LONG_ROD, GENERATE_FINE_WIRE, GENERATE_SPRING, GENERATE_FOIL, GENERATE_FRAME,
                        GENERATE_DOUBLE_PLATE)
                .fluidPipeProperties(1200, 40, true)
                .blast(b -> b.temp(2700, GasTier.LOW).blastStats(VA[HV], 200))
                .build();
        Bromine.setProperty(PropertyKey.DUST, new DustProperty());
        Bromine.setProperty(PropertyKey.INGOT, new IngotProperty());
        Actinium.setProperty(PropertyKey.INGOT, new IngotProperty());
        Unobtainium = new Material.Builder(id++, gregtechId("Unobtainium"))
                .cableProperties(GTValues.V[GTValues.MV], 4, 0, true)
                .color(0x013C7F).ingot()
                .liquid(new FluidBuilder().temperature(1373))
                .iconSet(METALLIC)
                .flags(STD_METAL, GENERATE_LONG_ROD, GENERATE_FINE_WIRE, GENERATE_SPRING, GENERATE_FOIL, GENERATE_FRAME,
                        GENERATE_DOUBLE_PLATE)
                .fluidPipeProperties(1200, 40, true)
                .blast(b -> b.temp(2700, GasTier.LOW).blastStats(VA[HV], 200))
                .build();
        CMBSteel = new Material.Builder(id++, gregtechId("C_M_B_Steel"))
                .cableProperties(GTValues.V[GTValues.MV], 4, 0, true)
                .color(0x001431).ingot()
                .liquid(new FluidBuilder().temperature(1373))
                .iconSet(METALLIC)
                .flags(STD_METAL, GENERATE_LONG_ROD, GENERATE_FINE_WIRE, GENERATE_SPRING, GENERATE_FOIL, GENERATE_FRAME,
                        GENERATE_DOUBLE_PLATE)
                .fluidPipeProperties(1200, 40, true)
                .blast(b -> b.temp(2700, GasTier.LOW).blastStats(VA[HV], 200))
                .build();
        Astatine.setProperty(PropertyKey.DUST, new DustProperty());
        Astatine.setProperty(PropertyKey.INGOT, new IngotProperty());
        Verticium = new Material.Builder(id++, gregtechId("Verticium"))
                .cableProperties(GTValues.V[GTValues.MV], 4, 0, true)
                .color(0x29AE01).ingot()
                .liquid(new FluidBuilder().temperature(1373))
                .iconSet(METALLIC)
                .flags(STD_METAL, GENERATE_LONG_ROD, GENERATE_FINE_WIRE, GENERATE_SPRING, GENERATE_FOIL, GENERATE_FRAME,
                        GENERATE_DOUBLE_PLATE)
                .fluidPipeProperties(1200, 40, true)
                .blast(b -> b.temp(2700, GasTier.LOW).blastStats(VA[HV], 200))
                .build();
    }

}
