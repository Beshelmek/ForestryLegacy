/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.biome.BiomeGenBase;
import buildcraft.api.power.PowerFramework;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.GlobalManager;
import forestry.core.BioPowerFramework;
import forestry.core.proxy.Proxies;
import forestry.core.triggers.ForestryTrigger;
import forestry.core.utils.Localization;

public class Config {

	public static final String CATEGORY_COMMON = "common";

	public static net.minecraftforge.common.Configuration idConfig;
	public static Configuration config;

	public static String gameMode;

	public static HashSet<String> disabledStructures = new HashSet<String>();
	
	// Native Plugins
	public static boolean disableApiculture = false;
	public static boolean disableArboriculture = false;
	public static boolean disableCultivation = false;
	public static boolean disableEnergy = false;
	public static boolean disableFactory = false;
	public static boolean disableFarming = false;
	public static boolean disableFood = false;
	public static boolean disableMail = false;
	public static boolean disableStorage = false;

	public static boolean disablePermissions = false;
	public static boolean disableNags = false;
	public static boolean disableVersionCheck = false;

	public static boolean invalidFingerprint = false;
	
	// Graphics
	public static boolean enableParticleFX = true;

	// Bees
	public static boolean clearInvalidChromosomes = false;

	// Dungeon loot
	public static boolean dungeonLootRare = false;

	// World generation
	public static boolean generateApatiteOre = true;
	public static boolean generateCopperOre = true;
	public static boolean generateTinOre = true;
	public static boolean generateBeehives = true;
	public static boolean generateBogEarth = false;

	// Performance
	public static boolean enableBackpackResupply = true;
	public static int planterThrottle;
	public static int harvesterThrottle;

	// Customization
	public static boolean tooltipLiquidAmount = false;
	public static boolean planterSideSensitive = true;
	/**
	 * True if the harvester should decide output to pipes according to side.
	 */
	public static boolean harvesterSideSensitive = true;
	public static boolean craftingBronzeEnabled = true;
	public static boolean craftingFarmsEnabled = true;
	public static boolean craftingFarmsUncraftingEnabled = true;
	public static boolean craftingStampsEnabled = true;
	public static boolean applePickup = true;
	public static boolean squareFarms = false;

	// Mail
	public static boolean mailAlertEnabled = true;

	public static String fakeUserLogin = "[Forestry]";
	public static boolean fakeUserAutoop = true;
	
	// Hints
	public static boolean disableHints = false;
	public static HashMap<String, String[]> hints = new HashMap<String, String[]>();
	public static boolean disableEnergyStat = false;

	@SuppressWarnings({ "all" })
	public static void load() {

		// Init id config
		idConfig = new net.minecraftforge.common.Configuration(new File(Proxies.common.getForestryRoot(), "config/forestry/base.conf"));
		idConfig.load();

		config = new Configuration();

		if (PowerFramework.currentFramework == null) {
			Property powerFrameworkClass = config.get("power.framework", CATEGORY_COMMON, Defaults.DEFAULT_POWER_FRAMEWORK);
			try {
				PowerFramework.currentFramework = (PowerFramework) Class.forName(powerFrameworkClass.Value).getConstructor(null).newInstance(null);
			} catch (Throwable e) {
				PowerFramework.currentFramework = new BioPowerFramework();
			}
		}

		Property property = config.get("difficulty.gamemode", CATEGORY_COMMON, "EASY");
		property.Comment = "set to your preferred game mode. available modes are EASY, NORMAL, HARD. mismatch with the server may cause visual glitches with recipes. setting an unavailable mode will create a new mode definition file.";
		gameMode = property.Value;
		
		property = config.get("difficulty.recreate.definitions", CATEGORY_COMMON, true);
		property.Comment = "set to true to force recreation of the game mode definitions in config/forestry/gamemodes";
		boolean recreate = Boolean.parseBoolean(property.Value);		

		if(recreate)
			Proxies.log.info("Recreating all gamemode definitions from the defaults. This may be caused by an upgrade");
		
		// Make sure the default mode files are there.
		File easyMode = config.getCategoryFile("gamemodes/EASY");
		if (recreate) {
			CopyFileToFS(easyMode, "/config/forestry/gamemodes/EASY.conf");
		}

		File normalMode = config.getCategoryFile("gamemodes/NORMAL");
		if (!normalMode.exists() || recreate) {
			CopyFileToFS(normalMode, "/config/forestry/gamemodes/NORMAL.conf");
		}

		File hardMode = config.getCategoryFile("gamemodes/HARD");
		if (!hardMode.exists() || recreate) {
			CopyFileToFS(hardMode, "/config/forestry/gamemodes/HARD.conf");
		}

		config.set("difficulty.recreate.definitions", CATEGORY_COMMON, false);
		
		Property particleFX = config.get("performance.particleFX.enabled", CATEGORY_COMMON, true);
		particleFX.Comment = "set to false to disable particle fx on slower machines";
		enableParticleFX = Boolean.parseBoolean(particleFX.Value);

		Property genApatiteOre = config.get("world.generate.apatite", CATEGORY_COMMON, true);
		genApatiteOre.Comment = "set to false to force forestry to skip generating own apatite ore blocks in the world";
		generateApatiteOre = Boolean.parseBoolean(genApatiteOre.Value);

		Property genBeehives = config.get("world.generate.beehives", CATEGORY_COMMON, true);
		genBeehives.Comment = "set to false to force forestry to skip generating beehives in the world";
		generateBeehives = Boolean.parseBoolean(genBeehives.Value);

		Property genCopperOre = config.get("world.generate.copper", CATEGORY_COMMON, true);
		genCopperOre.Comment = "set to false to force forestry to skip generating own copper ore blocks in the world";
		generateCopperOre = Boolean.parseBoolean(genCopperOre.Value);

		Property genTinOre = config.get("world.generate.tin", CATEGORY_COMMON, true);
		genTinOre.Comment = "set to false to force forestry to skip generating own tin ore blocks in the world";
		generateTinOre = Boolean.parseBoolean(genTinOre.Value);

		Property planterSideSense = config.get("planters.sidesensitive", CATEGORY_COMMON, true);
		planterSideSense.Comment = "set to false if farms should output all harvested products regardless of side a pipe is attached to";
		planterSideSensitive = Boolean.parseBoolean(planterSideSense.Value);
		Property harvesterSideSense = config.get("harvesters.sidesensitive", CATEGORY_COMMON, true);
		harvesterSideSense.Comment = "set to false if harvesters should output all harvested products regardless of side a pipe is attached to";
		harvesterSideSensitive = Boolean.parseBoolean(harvesterSideSense.Value);

		Property bronzeRecipe = config.get("crafting.bronze.enabled", CATEGORY_COMMON, true);
		bronzeRecipe.Comment = "set to false to disable crafting recipe for bronze";
		craftingBronzeEnabled = Boolean.parseBoolean(bronzeRecipe.Value);
		
		property = config.get("crafting.farms.enabled", CATEGORY_COMMON, false);
		property.Comment = "set to false to disable crafting recipes for all old harvesters and planters.";
		craftingFarmsEnabled = Boolean.parseBoolean(property.Value);		
		property = config.get("crafting.farms.uncrafting.enabled", CATEGORY_COMMON, true);
		property.Comment = "set to false to disable uncrafting recipes for old planters and harvesters";
		craftingFarmsUncraftingEnabled = Boolean.parseBoolean(property.Value);
		
		Property craftingStamps = config.get("crafting.stamps.enabled", CATEGORY_COMMON, true);
		craftingStamps.Comment = "set to false to disable crafting recipes for stamps";
		craftingStampsEnabled = Boolean.parseBoolean(craftingStamps.Value);

		Property indicatorEnable = config.get("tweaks.mailalert.enabled", CATEGORY_COMMON, true);
		indicatorEnable.Comment = "set to false to disable the mail alert box";
		mailAlertEnabled = Boolean.parseBoolean(indicatorEnable.Value);

		Property appleHarvest = config.get("tweaks.apple.pickup.enabled", CATEGORY_COMMON, true);
		appleHarvest.Comment = "set to false to disable apple pickup by appropriate harvesters";
		applePickup = Boolean.parseBoolean(appleHarvest.Value);

		Property clearGenome = config.get("genetics.clear.invalid.chromosomes", CATEGORY_COMMON, true);
		clearGenome.Comment = "NEW: set to true to clear chromosomes which contain invalid alleles. might rescue your save if it is crashing after removal of a bee addon.";
		clearInvalidChromosomes = Boolean.parseBoolean(clearGenome.Value);

		Property dungeonLootRarity = config.get("difficulty.dungeonloot.rare", CATEGORY_COMMON, false);
		dungeonLootRarity.Comment = "set to true to make dungeon loot generated by forestry rarer";
		dungeonLootRare = Boolean.parseBoolean(dungeonLootRarity.Value);

		Property resupplyEnable = config.get("performance.backpacks.resupply", CATEGORY_COMMON, true);
		resupplyEnable.Comment = "leaving this enabled will cycle the list of active players PER INGAME TICK to check for resupply via backpack. you want to set this to false on busy servers.";
		Config.enableBackpackResupply = Boolean.parseBoolean(resupplyEnable.Value);

		Property propThrottle = config.get("performance.planter", CATEGORY_COMMON, Defaults.PLANTER_PROCESSING_THROTTLE);
		propThrottle.Comment = "higher numbers increase working speeds of planters but also increase cpu load.";
		Config.planterThrottle = Integer.parseInt(propThrottle.Value);

		propThrottle = config.get("performance.harvester", CATEGORY_COMMON, Defaults.HARVESTER_PROCESSING_THROTTLE);
		propThrottle.Comment = "higher numbers increase working speeds of harvesters but also increase cpu load.";
		Config.harvesterThrottle = Integer.parseInt(propThrottle.Value);

		property = config.get("tweaks.hints.disabled", CATEGORY_COMMON, false);
		property.Comment = "set to true to disable hints on machine and engine guis.";
		Config.disableHints = Boolean.parseBoolean(property.Value);
		property = config.get("tweaks.energystat.disabled", CATEGORY_COMMON, true);
		property.Comment = "set to true to disable energy statistics on energy consumers.";
		Config.disableEnergyStat = Boolean.parseBoolean(property.Value);
		property = config.get("tweaks.tooltip.liquidamount.disabled", CATEGORY_COMMON, true);
		property.Comment = "set to true to disable displaying liquid amounts in tank tooltips.";
		Config.tooltipLiquidAmount = !Boolean.parseBoolean(property.Value);

		property = config.get("tweaks.permissions.disabled", CATEGORY_COMMON, false);
		property.Comment = "set to true to disable access restrictions on forestry machines.";
		Config.disablePermissions = Boolean.parseBoolean(property.Value);

		property = config.get("tweaks.nags.disabled", CATEGORY_COMMON, false);
		property.Comment = "set to true to disable some nagging and warnings (e.g. GregTech).";
		Config.disableNags = Boolean.parseBoolean(property.Value);

		property = config.get("tweaks.upgradenotice.disabled", CATEGORY_COMMON, false);
		property.Comment = "set to true to disable update and version check notice.";
		Config.disableVersionCheck = Boolean.parseBoolean(property.Value);

		property = config.get("tweaks.farms.squared", CATEGORY_COMMON, false);
		property.Comment = "set to true to have farms use a square layout instead of a diamond one.";
		Config.squareFarms = Boolean.parseBoolean(property.Value);

		property = config.get("structures.schemata.disabled", CATEGORY_COMMON, "");
		property.Comment = "add schemata keys to disable them. current keys: alveary3x3;farm3x3;farm3x4;farm3x5;farm4x4;farm5x5";
		disabledStructures.addAll(Arrays.asList(parseStructureKeys(property.Value)));
		for(String str : disabledStructures) {
			Proxies.log.finer("Disabled structure '%s'.", str);
		}
		
		property = config.get("permissions.login", CATEGORY_COMMON, "[Forestry]");
		property.Comment = "set the default login used by Forestry.";
		Config.fakeUserLogin = property.Value;

		property = config.get("permissions.autoop", CATEGORY_COMMON, true);
		property.Comment = "set to false to prevent Forestry's user from autooping.";
		Config.fakeUserAutoop = Boolean.parseBoolean(property.Value);

		registerClimates();
		ConfigureBlocks.initialize();
		ForestryTrigger.initialize();

		// CONFIGURE GLOBALS
		GlobalManager.leafBlockIds.add(Block.leaves.blockID);
		GlobalManager.dirtBlockIds.add(Block.dirt.blockID);
		GlobalManager.dirtBlockIds.add(Block.grass.blockID);
		GlobalManager.sandBlockIds.add(Block.sand.blockID);
		GlobalManager.snowBlockIds.add(Block.snow.blockID);

		config.save();
		idConfig.save();

		loadHints();
	}

	public static void modsLoaded() {
	}

	private static void CopyFileToFS(File destination, String resourcePath) {
		InputStream stream = Config.class.getResourceAsStream(resourcePath);
		OutputStream outstream;
		int readBytes;
		byte[] buffer = new byte[4096];
		try {

			if (destination.getParentFile() != null) {
				destination.getParentFile().mkdirs();
			}

			if (!destination.exists() && !destination.createNewFile())
				return;

			outstream = new FileOutputStream(destination);
			while ((readBytes = stream.read(buffer)) > 0) {
				outstream.write(buffer, 0, readBytes);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void loadHints() {

		Properties prop = new Properties();

		try {
			InputStream hintStream = Localization.class.getResourceAsStream("/config/forestry/hints.properties");
			prop.load(hintStream);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		for (String key : prop.stringPropertyNames()) {
			hints.put(key, parseHints(prop.getProperty(key)));
		}
	}

	private static String[] parseHints(String list) {
		if (list.isEmpty())
			return new String[0];
		else
			return list.split("[;]+");
	}

	private static String[] parseStructureKeys(String list) {
		if(list.isEmpty())
			return new String[0];
		else
			return list.split("[;]+");
					
	}
	
	private static void registerClimates() {

		for (BiomeGenBase biome : BiomeGenBase.biomeList) {
			if (biome == null) {
				continue;
			}

			if (biome.biomeID == BiomeGenBase.hell.biomeID) {
				EnumTemperature.hellishBiomeIds.add(biome.biomeID);
			} else if (biome.temperature >= 2.0f) {
				EnumTemperature.hotBiomeIds.add(biome.biomeID);
			} else if (biome.temperature >= 1.2f) {
				EnumTemperature.warmBiomeIds.add(biome.biomeID);
			} else if (biome.temperature >= 0.2f) {
				EnumTemperature.normalBiomeIds.add(biome.biomeID);
			} else if (biome.temperature >= 0.15f) {
				EnumTemperature.coldBiomeIds.add(biome.biomeID);
			} else {
				EnumTemperature.icyBiomeIds.add(biome.biomeID);
			}
		}

		// / HUMIDITY
		for (BiomeGenBase biome : BiomeGenBase.biomeList) {
			if (biome == null) {
				continue;
			}

			if (biome.rainfall >= 0.9f) {
				EnumHumidity.dampBiomeIds.add(biome.biomeID);
			} else if (biome.rainfall >= 0.3f) {
				EnumHumidity.normalBiomeIds.add(biome.biomeID);
			} else {
				EnumHumidity.aridBiomeIds.add(biome.biomeID);
			}
		}

	}

	public static int getOrCreateBlockIdProperty(String key, int defaultId) {
		int blockid = Integer.parseInt(idConfig.getBlock(key, defaultId).value);
		idConfig.save();
		return blockid;
	}

	public static int getOrCreateItemIdProperty(String key, int defaultId) {
		int itemid = Integer.parseInt(idConfig.getItem(key, defaultId).value);
		idConfig.save();
		
		if(Item.itemsList[itemid + 256] != null)
			throw new RuntimeException(String.format("Tried to recreate an item id (%s / shifted:%s) for an item which already exists as %s. Check your config and resolve the conflict.", itemid, itemid + 256, Item.itemsList[itemid + 256].toString()));
		
		return itemid;
	}

	public static boolean getOrCreateBooleanProperty(String key, String kind, boolean defaults) {
		return Boolean.parseBoolean(config.get(key, kind, defaults).Value);
	}

}
