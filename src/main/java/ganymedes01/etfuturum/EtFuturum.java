package ganymedes01.etfuturum;

import ganymedes01.etfuturum.configuration.ConfigurationHandler;
import ganymedes01.etfuturum.core.proxy.CommonProxy;
import ganymedes01.etfuturum.entities.ModEntityList;
import ganymedes01.etfuturum.items.ItemEntityEgg;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.network.ArmourStandInteractHandler;
import ganymedes01.etfuturum.network.ArmourStandInteractMessage;
import ganymedes01.etfuturum.recipes.ModRecipes;
import ganymedes01.etfuturum.world.EtFuturumWorldGenerator;
import ganymedes01.etfuturum.world.OceanMonument;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION_NUMBER, dependencies = Reference.DEPENDENCIES, guiFactory = Reference.GUI_FACTORY_CLASS)
public class EtFuturum {

	@Instance(Reference.MOD_ID)
	public static EtFuturum instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;

	public static SimpleNetworkWrapper networkWrapper;

	public static CreativeTabs creativeTab = new CreativeTabs(Reference.MOD_ID) {
		@Override
		public Item getTabIconItem() {
			return enablePrismarine ? ModItems.prismarine_shard : Items.skull;
		}
	};

	public static boolean enableStones = true;
	public static boolean enableIronTrapdoor = true;
	public static boolean enableMutton = true;
	public static boolean enableSponge = true;
	public static boolean enablePrismarine = true;
	public static boolean enableDoors = true;
	public static boolean enableInvertedDaylightSensor = true;
	public static boolean enableCoarseDirt = true;
	public static boolean enableRedSandstone = true;
	public static boolean enableEnchants = true;
	public static boolean enableAnvil = true;
	public static boolean enableFences = true;
	public static boolean enableSilkTouchingMushrooms = true;
	public static boolean enableBanners = true;
	public static boolean enableSlimeBlock = true;
	public static boolean enableArmourStand = true;
	public static boolean enableRabbit = true;
	public static boolean enableOldGravel = true;
	public static boolean enableRecipeForPrismarine = true;
	public static boolean enableEndermite = true;
	public static boolean enableBeetroot = true;
	public static boolean enableChorusFruit = true;
	public static boolean enableGrassPath = true;
	public static boolean enableSticksFromDeadBushes = true;
	public static boolean enableBowRendering = true;
	public static boolean enableTippedArrows = true;
	public static boolean enableLingeringPotions = true;
	public static boolean enableBurnableBlocks = true;
	public static boolean enableFancySkulls = true;
	public static boolean enableSkullDrop = true;
	public static boolean enableDmgIndicator = true;
	public static boolean enableTransparentAmour = true;
	public static boolean enableCryingObsidian = true;
	public static boolean enableUpdatedFoodValues = true;
	public static boolean enableUpdatedHarvestLevels = true;
	public static boolean enableVillagerZombies = true;

	public static int maxStonesPerCluster = 33;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigurationHandler.INSTANCE.init(new File(event.getModConfigurationDirectory().getAbsolutePath() + File.separator + Reference.MOD_ID + ".cfg"));

		GameRegistry.registerWorldGenerator(new EtFuturumWorldGenerator(), 0);

		ModBlocks.init();
		ModItems.init();

		OceanMonument.makeMap();

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
		networkWrapper.registerMessage(ArmourStandInteractHandler.class, ArmourStandInteractMessage.class, 0, Side.SERVER);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

		ModRecipes.init();

		proxy.registerEvents();
		proxy.registerEntities();
		proxy.registerRenderers();

		if (ModEntityList.hasEntitiesWithEggs()) {
			ModEntityList.entity_egg = new ItemEntityEgg();
			GameRegistry.registerItem(ModEntityList.entity_egg, "entity_egg");
			OreDictionary.registerOre("mobEgg", ModEntityList.entity_egg);
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		Items.blaze_rod.setFull3D();

		if (enableUpdatedFoodValues) {
			setFinalField(ItemFood.class, Items.carrot, 3, "healAmount", "field_77853_b");
			setFinalField(ItemFood.class, Items.baked_potato, 5, "healAmount", "field_77853_b");
		}

		if (enableUpdatedHarvestLevels) {
			Blocks.packed_ice.setHarvestLevel("pickaxe", 0);
			Blocks.ladder.setHarvestLevel("axe", 0);
			Blocks.melon_block.setHarvestLevel("axe", 0);
		}
	}

	private void setFinalField(Class<?> cls, Object obj, Object newValue, String... fieldNames) {
		try {
			Field field = ReflectionHelper.findField(cls, fieldNames);
			field.setAccessible(true);

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

			field.set(obj, newValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}