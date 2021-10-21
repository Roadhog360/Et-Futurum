package ganymedes01.etfuturum.configuration.configs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ganymedes01.etfuturum.configuration.ConfigBase;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class ConfigMixins extends ConfigBase {
	
	public static boolean deepslateLayerOptimization;

	static final String catBackport = "backported features";
	static final String catOptimization = "optimizations";
	static final String catMisc = "misc";
	
	private static final List<ConfigCategory> configCats = new ArrayList<ConfigCategory>();
	
	public static List<ConfigCategory> getConfigCats() {
		return configCats;
	}
	
	public static final String PATH = ConfigBase.configDir + File.separator + "mixins.cfg";
	public static final ConfigMixins configInstance = new ConfigMixins(new File(Launch.minecraftHome, PATH));
	
	public ConfigMixins(File file) {
		super(file);
		setCategoryComment(catBackport, "Backports that can typically only have a clean implementation with mixins.");
		setCategoryComment(catOptimization, "Better implementations of existing features. This is generally used when doing something through the Forge API would be slower or less practical than using a Mixin.");
		setCategoryComment(catMisc, "Mixins that don't fit in any other category.");
		
		configCats.add(getCategory(catBackport));
		configCats.add(getCategory(catOptimization));
		configCats.add(getCategory(catMisc));
	}

	protected void syncConfigs() {
		Configuration cfg = configInstance;
		
		deepslateLayerOptimization = cfg.getBoolean("deepslateLayerOptimization", catOptimization, true, "If \"deepslateGenerationMode\" is set to 0, this config option is used. This optimizes deepslate generation by adding on to the stone generator instead of sending lots of setblocks, making the performance impact of using the layer deepslate option from fairly noticeable, to none.\nModified Classes: net.minecraft.world.gen.feature.WorldGenMinable net.minecraft.world.gen.ChunkProviderGenerate");
	}
	
}