package wfcore.mixins;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.Name("WarfactoryFMLPlugin")
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.TransformerExclusions("wfcore.mixins")
@IFMLLoadingPlugin.SortingIndex(2167) //SIX SEVEN
public class WFEarlyMixinLoader implements IEarlyMixinLoader, IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public List<String> getMixinConfigs() {
        String[] configs = {
                "mixins.wfcore.early.minecraft.json",
                "mixins.wfcore.early.forge.json"
        };
        return Arrays.asList(configs);
    }
}
