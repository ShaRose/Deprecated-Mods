package net.minecraft.src;

import java.awt.Point;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleButtonModel;

public class mod_AutoFertilizer extends BaseMod {

	public static Block afertblock;
	public static Item afertitem;
	private static ModSettingScreen modscreen;

	private static Random rand = new Random();
	private static SettingBoolean settingBooleanFertBlockNeedsPower;
	private static SettingFloat settingFloatFertBoatHoverHeight;
	private static SettingFloat settingFloatFertBoatSpeedBoost;

	private static SettingFloat settingFloatFertBoatSpeedWorking;
	private static SettingInt settingIntChanceGrowCactus;
	private static SettingInt settingIntChanceGrowCrops;
	private static SettingInt settingIntChanceGrowFlowers;
	private static SettingInt settingIntChanceGrowGrass;
	private static SettingInt settingIntChanceGrowReed;
	private static SettingInt settingIntChanceGrowTallGrass;
	private static SettingInt settingIntChanceHardenLava;
	private static SettingInt settingIntChanceSeedCrops;
	private static SettingInt settingIntChanceSplashWater;
	private static SettingInt settingIntChanceWaterField;

	private static SettingInt settingIntFertBlockTicksPerSecond;
	private static SettingInt settingIntFertBoatTicksPerSecond;
	private static SettingInt settingIntFertilizeRange;
	private static ModSettings settings;

	private static WidgetSimplewindow subscreenFertBlock;
	private static WidgetSimplewindow subscreenFertBoat;

	private static WidgetSimplewindow subscreenFertilization;
	private static Field wolfshakeflag;
	private static Field wolfshakingflag;
	private static Field wolfshakingprevtime;
	private static Field wolfshakingtime;

	public static int BitGetDirection(int metadata) {
		return (metadata > 8 ? metadata - 8 : metadata);
	}

	public static boolean BitGetOn(int metadata) {
		return metadata > 8;
	}

	public static int BitSetDirection(int metadata, int value) {
		return value + (mod_AutoFertilizer.BitGetOn(metadata) ? 8 : 0);
	}

	public static int BitSetOn(int metadata, boolean flag) {
		if (mod_AutoFertilizer.BitGetOn(metadata) == flag) {
			return metadata;
		}
		if (flag) {
			return metadata + 8;
		}
		return metadata - 8;
	}

	public static boolean Fert(World worldObj, int x, int y, int z,
			int tickrate, int scanup, int scandown) {
		Point[] radiuspointsbackup = mod_AutoFertilizer
				.SetRadius(mod_AutoFertilizer.settingIntFertilizeRange.get());
		for (int p = 0; p < radiuspointsbackup.length; p++) {
			int i = -scanup;
			int block = 0;
			while (((i < scandown) && (block == 0))
					|| (block == mod_AutoFertilizer.afertblock.blockID)) {
				i++;
				block = worldObj.getBlockId(x + radiuspointsbackup[p].x, y - i,
						z + radiuspointsbackup[p].y);
			}
			boolean iscrops = false;
			if (block == Block.crops.blockID) {
				int level = worldObj.getBlockMetadata(x
						+ radiuspointsbackup[p].x, y - i, z
						+ radiuspointsbackup[p].y);
				if ((level < 7) && mod_AutoFertilizer.RandomGrowCrops(tickrate)) {
					worldObj.setBlockMetadataWithNotify(x
							+ radiuspointsbackup[p].x, y - i, z
							+ radiuspointsbackup[p].y, level + 1);
				}
				i++;
				iscrops = true;
				block = Block.tilledField.blockID;
			}
			if (block == Block.tilledField.blockID) {
				if (mod_AutoFertilizer.RandomWaterField(tickrate)) {
					worldObj.setBlockMetadataWithNotify(x
							+ radiuspointsbackup[p].x, y - i, z
							+ radiuspointsbackup[p].y, 7);
				}
				if (!iscrops && mod_AutoFertilizer.RandomSeedCrops(tickrate)) {
					worldObj.setBlockWithNotify(x + radiuspointsbackup[p].x,
							(y - i) + 1, z + radiuspointsbackup[p].y,
							Block.crops.blockID);
				}
			}

			if ((block == Block.grass.blockID) && (worldObj.getBlockId(x + radiuspointsbackup[p].x, (y - i) + 1, z + radiuspointsbackup[p].y) == 0)) {
				if (mod_AutoFertilizer.RandomGrowFlowers(tickrate)
						&& Block.plantYellow.canBlockStay(worldObj, x
								+ radiuspointsbackup[p].x, (y - i) + 1, z
								+ radiuspointsbackup[p].y)) {
					worldObj.setBlockWithNotify(
							x + radiuspointsbackup[p].x,
							(y - i) + 1,
							z + radiuspointsbackup[p].y,
							(mod_AutoFertilizer.rand.nextBoolean() ? Block.plantYellow.blockID
									: Block.plantRed.blockID));
				} else {
					if (mod_AutoFertilizer.RandomGrowTallGrass(tickrate)
							&& Block.tallGrass.canBlockStay(worldObj, x
									+ radiuspointsbackup[p].x, (y - i) + 1, z
									+ radiuspointsbackup[p].y)) {
						worldObj.setBlockAndMetadataWithNotify(x
								+ radiuspointsbackup[p].x, (y - i) + 1, z
								+ radiuspointsbackup[p].y,
								Block.tallGrass.blockID,
								mod_AutoFertilizer.rand.nextInt(1) + 1);
					}
				}

			}

			if ((block == Block.dirt.blockID)
					&& !worldObj.getBlockMaterial(x + radiuspointsbackup[p].x,
							(y - i) + 1, z + radiuspointsbackup[p].y)
							.getCanBlockGrass()
					&& mod_AutoFertilizer.RandomGrowGrass(tickrate)) {
				worldObj.setBlockWithNotify(x + radiuspointsbackup[p].x, y - i,
						z + radiuspointsbackup[p].y, Block.grass.blockID);
			}
			if ((block == Block.cactus.blockID)
					&& mod_AutoFertilizer.RandomGrowCactus(tickrate)) {
				worldObj.setBlockMetadata(x + radiuspointsbackup[p].x, y - i, z
						+ radiuspointsbackup[p].y, 15);
				Block.cactus.updateTick(worldObj, x + radiuspointsbackup[p].x,
						y - i, z + radiuspointsbackup[p].y, worldObj.rand);
			}
			if ((block == Block.reed.blockID)
					&& mod_AutoFertilizer.RandomGrowReed(tickrate)) {
				worldObj.setBlockMetadata(x + radiuspointsbackup[p].x, y - i, z
						+ radiuspointsbackup[p].y, 15);
				Block.reed.updateTick(worldObj, x + radiuspointsbackup[p].x, y
						- i, z + radiuspointsbackup[p].y, worldObj.rand);
			}
			if (((block == Block.lavaStill.blockID) || (block == Block.lavaStill.blockID))
					&& mod_AutoFertilizer.RandomHardenLava(tickrate)) {
				int depth = worldObj.getBlockMetadata(x
						+ radiuspointsbackup[p].x, y - i, z
						+ radiuspointsbackup[p].y);
				if (depth == 0) {
					worldObj.setBlockWithNotify(x + radiuspointsbackup[p].x, y
							- i, z + radiuspointsbackup[p].y,
							Block.obsidian.blockID);
				} else if (depth <= 4) {
					worldObj.setBlockWithNotify(x + radiuspointsbackup[p].x, y
							- i, z + radiuspointsbackup[p].y,
							Block.cobblestone.blockID);
				} else {
					worldObj.setBlockWithNotify(x + radiuspointsbackup[p].x, y
							- i, z + radiuspointsbackup[p].y, 0);
				}

				worldObj.playSoundEffect((x + radiuspointsbackup[p].x) + 0.5F,
						(y - i) + 0.5F, (z + radiuspointsbackup[p].y) + 0.5F,
						"random.fizz", 0.5F,
						2.6F + ((worldObj.rand.nextFloat() - worldObj.rand
								.nextFloat()) * 0.8F));
				for (int l = 0; l < 8; l++) {
					worldObj.spawnParticle("largesmoke",
							(x + radiuspointsbackup[p].x) + Math.random(),
							(y - i) + 1.2D, (z + radiuspointsbackup[p].y)
									+ Math.random(), 0.0D, 0.0D, 0.0D);
				}

			}

			if (mod_AutoFertilizer.RandomSplashWater(tickrate)) {
				float splashx = x + radiuspointsbackup[p].x
						+ mod_AutoFertilizer.rand.nextFloat();
				int splashy = (y - i) + 1;
				float splashz = z + radiuspointsbackup[p].y
						+ mod_AutoFertilizer.rand.nextFloat();
				worldObj.spawnParticle("splash", splashx, (y - i) + 1, splashz,
						0, 0.05D, 0);
				int blockid = worldObj.getBlockId(x + radiuspointsbackup[p].x,
						splashy - 1, z + radiuspointsbackup[p].y);
				if (blockid == Block.fire.blockID) {
					worldObj.setBlock(x + radiuspointsbackup[p].x, splashy - 1,
							z + radiuspointsbackup[p].y, 0);
					worldObj.playSoundEffect(
							splashx,
							splashy + 1,
							splashz,
							"random.fizz",
							0.7F,
							1.6F + ((mod_AutoFertilizer.rand.nextFloat() - mod_AutoFertilizer.rand
									.nextFloat()) * 0.4F));
				}

				List list = worldObj.getEntitiesWithinAABB(Entity.class,
						AxisAlignedBB.getBoundingBox(splashx - 0.5,
								splashy - 0.5, splashz - 0.5, splashx + 0.5,
								splashy + 0.5, splashz + 0.5));
				for (Object object : list) {
					Entity ent = (Entity) object;
					if (ent.fire > 0) {
						ent.fire = 0;
						worldObj.playSoundAtEntity(
								ent,
								"random.fizz",
								0.7F,
								1.6F + ((mod_AutoFertilizer.rand.nextFloat() - mod_AutoFertilizer.rand
										.nextFloat()) * 0.4F));
					}
					if ((ent instanceof EntityWolf)
							&& (mod_AutoFertilizer.wolfshakeflag != null)
							&& (mod_AutoFertilizer.rand.nextInt(6) == 0)) {
						try {
							if (!(Boolean) mod_AutoFertilizer.wolfshakingflag
									.get(ent)) {
								mod_AutoFertilizer.wolfshakingflag.set(ent,
										true);
								mod_AutoFertilizer.wolfshakeflag
										.set(ent, false);
								mod_AutoFertilizer.wolfshakingtime.set(ent,
										0.0F);
								mod_AutoFertilizer.wolfshakingprevtime.set(ent,
										0.0F);
							}
						} catch (Throwable e) {
						}
					}
				}
			}
		}
		return true;
	}

	public static boolean GetFertBlockNeedsPower() {
		return mod_AutoFertilizer.settingBooleanFertBlockNeedsPower.get();
	}

	public static int GetFertBlockTicksPerSecond() {
		return mod_AutoFertilizer.settingIntFertBlockTicksPerSecond.get();
	}

	public static float GetFertBoatHoverHeight() {
		return mod_AutoFertilizer.settingFloatFertBoatHoverHeight.get();
	}

	public static float GetFertBoatSpeedBoost() {
		return mod_AutoFertilizer.settingFloatFertBoatSpeedBoost.get();
	}

	public static float GetFertBoatSpeedWorking() {
		return mod_AutoFertilizer.settingFloatFertBoatSpeedWorking.get();
	}

	public static int GetFertBoatTicksPerSecond() {
		return mod_AutoFertilizer.settingIntFertBoatTicksPerSecond.get();
	}

	private static boolean RandomGrowCactus(int tickspersecond) {
		float chance = mod_AutoFertilizer.settingIntChanceGrowCactus.get()
				/ (100F * tickspersecond);
		if (chance == 1) {
			return true;
		}
		return chance > mod_AutoFertilizer.rand.nextFloat();
	}

	private static boolean RandomGrowCrops(int tickspersecond) {
		float chance = mod_AutoFertilizer.settingIntChanceGrowCrops.get()
				/ (100F * tickspersecond);
		if (chance == 1) {
			return true;
		}
		return chance > mod_AutoFertilizer.rand.nextFloat();
	}

	private static boolean RandomGrowFlowers(int tickspersecond) {
		float chance = mod_AutoFertilizer.settingIntChanceGrowFlowers.get()
				/ (100F * tickspersecond);
		if (chance == 1) {
			return true;
		}
		return chance > mod_AutoFertilizer.rand.nextFloat();
	}

	private static boolean RandomGrowGrass(int tickspersecond) {
		float chance = mod_AutoFertilizer.settingIntChanceGrowGrass.get()
				/ (100F * tickspersecond);
		if (chance == 1) {
			return true;
		}
		return chance > mod_AutoFertilizer.rand.nextFloat();
	}

	private static boolean RandomGrowReed(int tickspersecond) {
		float chance = mod_AutoFertilizer.settingIntChanceGrowReed.get()
				/ (100F * tickspersecond);
		if (chance == 1) {
			return true;
		}
		return chance > mod_AutoFertilizer.rand.nextFloat();
	}

	private static boolean RandomGrowTallGrass(int tickspersecond) {
		float chance = mod_AutoFertilizer.settingIntChanceGrowTallGrass.get()
				/ (100F * tickspersecond);
		if (chance == 1) {
			return true;
		}
		return chance > mod_AutoFertilizer.rand.nextFloat();
	}

	private static boolean RandomHardenLava(int tickspersecond) {
		float chance = mod_AutoFertilizer.settingIntChanceHardenLava.get()
				/ (100F * tickspersecond);
		if (chance == 1) {
			return true;
		}
		return chance > mod_AutoFertilizer.rand.nextFloat();
	}

	private static boolean RandomSeedCrops(int tickspersecond) {
		float chance = mod_AutoFertilizer.settingIntChanceSeedCrops.get()
				/ (100F * tickspersecond);
		if (chance == 1) {
			return true;
		}
		return chance > mod_AutoFertilizer.rand.nextFloat();
	}

	private static boolean RandomSplashWater(int tickspersecond) {
		float chance = mod_AutoFertilizer.settingIntChanceSplashWater.get()
				/ (100F * tickspersecond);
		if (chance == 1) {
			return true;
		}
		return chance > mod_AutoFertilizer.rand.nextFloat();
	}

	private static boolean RandomWaterField(int tickspersecond) {
		float chance = mod_AutoFertilizer.settingIntChanceWaterField.get()
				/ (100F * tickspersecond);
		if (chance == 1) {
			return true;
		}
		return chance > mod_AutoFertilizer.rand.nextFloat();
	}

	private static Point[] SetRadius(int radius) {
		Vector<Point> points = new Vector<Point>();
		for (int X = -radius; X < (radius + 1); X++) {
			for (int Y = -radius; Y < (radius + 1); Y++) {
				if (Math.sqrt((X * X) + (Y * Y)) < (radius + 0.5D)) {
					points.add(new Point(X, Y));
				}
			}
		}
		return points.toArray(new Point[0]);
	}

	public mod_AutoFertilizer() {
		mod_AutoFertilizer.settings = new ModSettings("mod_AutoFertilizer");
		mod_AutoFertilizer.modscreen = new ModSettingScreen("AutoFertilizer");
		WidgetSinglecolumn widgetsinglecolumn = new WidgetSinglecolumn(
				new Widget[0]);
		{
			mod_AutoFertilizer.settingIntChanceWaterField = mod_AutoFertilizer.settings
					.addSetting(widgetsinglecolumn, "Chance to Water Fields",
							"AutoFert.WaterField", 100, 0, 1, 500);

			mod_AutoFertilizer.settingIntChanceSeedCrops = mod_AutoFertilizer.settings
					.addSetting(widgetsinglecolumn, "Chance to Seed Crops",
							"AutoFert.SeedCrops", 0, 0, 1, 500);

			mod_AutoFertilizer.settingIntChanceGrowCrops = mod_AutoFertilizer.settings
					.addSetting(widgetsinglecolumn, "Chance to Grow Crops",
							"AutoFert.GrowCrops", 25, 0, 1, 500);

			mod_AutoFertilizer.settingIntChanceGrowGrass = mod_AutoFertilizer.settings
					.addSetting(widgetsinglecolumn, "Chance to Grow Grass",
							"AutoFert.GrowGrass", 50, 0, 1, 500);

			mod_AutoFertilizer.settingIntChanceGrowFlowers = mod_AutoFertilizer.settings
					.addSetting(widgetsinglecolumn, "Chance to Grow Flowers",
							"AutoFert.Flowers", 0, 0, 1, 500);

			mod_AutoFertilizer.settingIntChanceGrowTallGrass = mod_AutoFertilizer.settings
					.addSetting(widgetsinglecolumn,
							"Chance to Grow Tall Grass", "AutoFert.TallGrass",
							0, 0, 1, 500);

			mod_AutoFertilizer.settingIntChanceGrowReed = mod_AutoFertilizer.settings
					.addSetting(widgetsinglecolumn, "Chance to Grow Reed",
							"AutoFert.GrowReed", 50, 0, 1, 500);

			mod_AutoFertilizer.settingIntChanceGrowCactus = mod_AutoFertilizer.settings
					.addSetting(widgetsinglecolumn, "Chance to Grow Cactus",
							"AutoFert.GrowCactus", 50, 0, 1, 500);

			mod_AutoFertilizer.settingIntChanceHardenLava = mod_AutoFertilizer.settings
					.addSetting(widgetsinglecolumn, "Chance to Harden Lava",
							"AutoFert.HardenLava", 50, 0, 1, 500);

			mod_AutoFertilizer.settingIntChanceSplashWater = mod_AutoFertilizer.settings
					.addSetting(widgetsinglecolumn, "Chance to Splash Water",
							"AutoFert.SplashWater", 125, 0, 1, 500);

			mod_AutoFertilizer.settingIntFertilizeRange = mod_AutoFertilizer.settings
					.addSetting(widgetsinglecolumn, "Fertilizer Range",
							"AutoFert.FertilizeRange", 3, 1, 1, 10);
		}

		mod_AutoFertilizer.subscreenFertilization = new WidgetSimplewindow(
				widgetsinglecolumn, "Fertilization Options");
		mod_AutoFertilizer.modscreen.append(GuiApiHelper.makeButton(
				"Fertilization Options", "show", GuiModScreen.class, true,
				new Class[] { Widget.class },
				mod_AutoFertilizer.subscreenFertilization));

		widgetsinglecolumn = new WidgetSinglecolumn(new Widget[0]);
		{
			mod_AutoFertilizer.settingBooleanFertBlockNeedsPower = mod_AutoFertilizer.settings
					.addSetting(widgetsinglecolumn, "Requires Restone Power",
							"AutoFert.FertBlockNeedsPower", false);
			mod_AutoFertilizer.settingIntFertBlockTicksPerSecond = mod_AutoFertilizer.settings
					.addSetting(widgetsinglecolumn, "Ticks Per Second",
							"AutoFert.FertBlockTicksPerSecond", 20, 1, 1, 20);
		}
		mod_AutoFertilizer.subscreenFertBlock = new WidgetSimplewindow(
				widgetsinglecolumn, "Sprinkler Options");
		mod_AutoFertilizer.modscreen.append(GuiApiHelper.makeButton(
				"Sprinkler Options", "show", GuiModScreen.class, true,
				new Class[] { Widget.class },
				mod_AutoFertilizer.subscreenFertBlock));

		widgetsinglecolumn = new WidgetSinglecolumn(new Widget[0]);
		{
			mod_AutoFertilizer.settingFloatFertBoatSpeedWorking = mod_AutoFertilizer.settings
					.addSetting(widgetsinglecolumn, "Speed in Work Mode",
							"AutoFert.FertBoatSpeedWorking", 0.15f, 0.05f,
							0.05f, 1f);
			mod_AutoFertilizer.settingFloatFertBoatSpeedBoost = mod_AutoFertilizer.settings
					.addSetting(widgetsinglecolumn, "Speed in Boost Mode",
							"AutoFert.FertBoatSpeedBoost", 0.3f, 0.01f, 0.01f,
							1f);
			mod_AutoFertilizer.settingFloatFertBoatHoverHeight = mod_AutoFertilizer.settings
					.addSetting(widgetsinglecolumn, "Hover Height",
							"AutoFert.FertBoatHoverHeight", 3f, 0.1f, 0.1f, 6f);
			mod_AutoFertilizer.settingIntFertBoatTicksPerSecond = mod_AutoFertilizer.settings
					.addSetting(widgetsinglecolumn, "Ticks Per Second",
							"AutoFert.FertBoatTicksPerSecond", 20, 1, 1, 20);
		}
		mod_AutoFertilizer.subscreenFertBoat = new WidgetSimplewindow(
				widgetsinglecolumn, "Fertilization Boat Options");
		mod_AutoFertilizer.modscreen.append(GuiApiHelper.makeButton(
				"Fertilization Boat Options", "show", GuiModScreen.class, true,
				new Class[] { Widget.class },
				mod_AutoFertilizer.subscreenFertBoat));

		mod_AutoFertilizer.modscreen.append(GuiApiHelper.makeButton("Reset all settings","resetAll",settings,true));
		mod_AutoFertilizer.settings.load();

		boolean MCP = EntityWolf.class.getName() == "net.minecraft.src.EntityWolf";
		try {
			mod_AutoFertilizer.wolfshakeflag = EntityWolf.class
					.getDeclaredField(MCP ? "isWolfShaking" : "d");
			mod_AutoFertilizer.wolfshakingflag = EntityWolf.class
					.getDeclaredField(MCP ? "field_25052_g" : "e");
			mod_AutoFertilizer.wolfshakingprevtime = EntityWolf.class
					.getDeclaredField(MCP ? "prevTimeWolfIsShaking" : "aq");
			mod_AutoFertilizer.wolfshakingtime = EntityWolf.class
					.getDeclaredField(MCP ? "timeWolfIsShaking" : "ap");
			mod_AutoFertilizer.wolfshakeflag.setAccessible(true);
			mod_AutoFertilizer.wolfshakingflag.setAccessible(true);
			mod_AutoFertilizer.wolfshakingprevtime.setAccessible(true);
			mod_AutoFertilizer.wolfshakingtime.setAccessible(true);
		} catch (Throwable e) {
			mod_AutoFertilizer.wolfshakeflag = null;
			mod_AutoFertilizer.wolfshakingflag = null;
			mod_AutoFertilizer.wolfshakingprevtime = null;
			mod_AutoFertilizer.wolfshakingtime = null;
			ModLoader
					.getLogger()
					.log(Level.FINE,
							"AutoFertilizer is disabling the wolf shaking feature due to an exception for the reflection initialization.",
							e);
		}

		mod_AutoFertilizer.afertblock = new AutoFertilizerBlock(97,
				ModLoader.addOverride("/terrain.png",
						"/item/autofertsprinkler.png"),
				ModLoader.getUniqueBlockModelID(this, true)).setHardness(0.0F)
				.setStepSound(Block.soundMetalFootstep)
				.setBlockName("block.autofertsprinkler");
		ModLoader.RegisterBlock(mod_AutoFertilizer.afertblock);

		mod_AutoFertilizer.afertitem = new AutoFertItem(3039);
		ModLoader.AddName(mod_AutoFertilizer.afertblock, "Sprinkler");
		ModLoader.AddName(new ItemStack(mod_AutoFertilizer.afertitem, 1, 0),
				"Auto Fertilizer Boat");
		ModLoader.AddName(new ItemStack(mod_AutoFertilizer.afertitem, 1, 1),
				"Auto Fertilizer Cart");

		ModLoader.RegisterEntityID(AutoFertBoatEntity.class, "AutoFertBoat",
				ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(AutoFertCartEntity.class, "AutoFertCart",
				ModLoader.getUniqueEntityId());
		mod_AutoFertilizer.SetRadius(3);

		CraftingManager.getInstance().addRecipe(
				new ItemStack(mod_AutoFertilizer.afertitem, 1, 0),
				new Object[] { "B", "S", Character.valueOf('S'),
						new ItemStack(mod_AutoFertilizer.afertblock, 1),
						Character.valueOf('B'), Item.boat });

		CraftingManager.getInstance().addRecipe(
				new ItemStack(mod_AutoFertilizer.afertitem, 1, 1),
				new Object[] { "S", "C", Character.valueOf('S'),
						new ItemStack(mod_AutoFertilizer.afertblock, 1),
						Character.valueOf('C'), Item.minecartEmpty });

		CraftingManager.getInstance()
				.addRecipe(
						new ItemStack(mod_AutoFertilizer.afertblock, 1),
						new Object[] { "MBM", "MWM", " M ",
								Character.valueOf('M'),
								new ItemStack(Item.ingotIron),
								Character.valueOf('B'),
								new ItemStack(Item.dyePowder, 1, 15),
								Character.valueOf('W'),
								new ItemStack(Item.bucketWater) });
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void AddRenderer(Map map) {
		map.put(AutoFertBoatEntity.class, new AutoFertBoatRender());
		map.put(AutoFertCartEntity.class, new AutoFertCartRender());
	}

	@Override
	public void RenderInvBlock(RenderBlocks renderblocks, Block block, int i,
			int somethingelse) {
		if (block == mod_AutoFertilizer.afertblock) {
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, -1F, 0.0F);
			renderSprinker(block, -0.5D, -0.5D, -0.5D, 0.0D, 0.0D);
			tessellator.draw();
		}
	}

	public void renderSprinker(Block block, double d, double d1, double d2,
			double d3, double d4) {
		Tessellator tessellator = Tessellator.instance;
		int i = block.getBlockTextureFromSide(0);
		int j = (i & 0xf) << 4;
		int k = i & 0xf0;
		float f = j / 256F;
		float f1 = (j + 15.99F) / 256F;
		float f2 = k / 256F;
		float f3 = (k + 15.99F) / 256F;
		double d5 = f + 0.02734375D;
		double d6 = f2 + 0.06D;
		double d7 = f + 0.03515625D;
		double d8 = f2 + 0.06D;
		d += 0.5D;
		d2 += 0.5D;
		double d9 = d - 0.5D;
		double d10 = d + 0.5D;
		double d11 = d2 - 0.5D;
		double d12 = d2 + 0.5D;
		double d13 = 0.0625D;
		double d14 = 0.65D;

		tessellator.addVertexWithUV((d + (d3 * (1.0D - d14))) - d13, d1 + d14,
				(d2 + (d4 * (1.0D - d14))) - d13, d5, d6);
		tessellator.addVertexWithUV((d + (d3 * (1.0D - d14))) - d13, d1 + d14,
				d2 + (d4 * (1.0D - d14)) + d13, d5, d8);
		tessellator.addVertexWithUV(d + (d3 * (1.0D - d14)) + d13, d1 + d14, d2
				+ (d4 * (1.0D - d14)) + d13, d7, d8);
		tessellator.addVertexWithUV(d + (d3 * (1.0D - d14)) + d13, d1 + d14,
				(d2 + (d4 * (1.0D - d14))) - d13, d7, d6);
		tessellator.addVertexWithUV(d - d13, d1 + 1.0D, d11, f, f2);
		tessellator.addVertexWithUV((d - d13) + d3, d1 + 0.0D, d11 + d4, f, f3);
		tessellator
				.addVertexWithUV((d - d13) + d3, d1 + 0.0D, d12 + d4, f1, f3);
		tessellator.addVertexWithUV(d - d13, d1 + 1.0D, d12, f1, f2);
		tessellator.addVertexWithUV(d - d13, d1 + 1.0D, d12, f1, f2);
		tessellator.addVertexWithUV((d - d13) + d3, d1 + 0.0D, d11 + d4, f, f3);
		tessellator
				.addVertexWithUV((d - d13) + d3, d1 + 0.0D, d12 + d4, f1, f3);
		tessellator.addVertexWithUV(d - d13, d1 + 1.0D, d11, f, f2);
		tessellator.addVertexWithUV(d + d13, d1 + 1.0D, d12, f, f2);
		tessellator.addVertexWithUV(d + d3 + d13, d1 + 0.0D, d12 + d4, f, f3);
		tessellator.addVertexWithUV(d + d3 + d13, d1 + 0.0D, d11 + d4, f1, f3);
		tessellator.addVertexWithUV(d + d13, d1 + 1.0D, d11, f1, f2);
		tessellator.addVertexWithUV(d + d13, d1 + 1.0D, d11, f1, f2);
		tessellator.addVertexWithUV(d + d3 + d13, d1 + 0.0D, d11 + d4, f1, f3);
		tessellator.addVertexWithUV(d + d3 + d13, d1 + 0.0D, d12 + d4, f, f3);
		tessellator.addVertexWithUV(d + d13, d1 + 1.0D, d12, f, f2);
		tessellator.addVertexWithUV(d9, d1 + 1.0D, d2 + d13, f, f2);
		tessellator.addVertexWithUV(d9 + d3, d1 + 0.0D, d2 + d13 + d4, f, f3);
		tessellator.addVertexWithUV(d10 + d3, d1 + 0.0D, d2 + d13 + d4, f1, f3);
		tessellator.addVertexWithUV(d10, d1 + 1.0D, d2 + d13, f1, f2);
		tessellator.addVertexWithUV(d10, d1 + 1.0D, d2 + d13, f1, f2);
		tessellator.addVertexWithUV(d10 + d3, d1 + 0.0D, d2 + d13 + d4, f1, f3);
		tessellator.addVertexWithUV(d9 + d3, d1 + 0.0D, d2 + d13 + d4, f, f3);
		tessellator.addVertexWithUV(d9, d1 + 1.0D, d2 + d13, f, f2);
		tessellator.addVertexWithUV(d10, d1 + 1.0D, d2 - d13, f, f2);
		tessellator
				.addVertexWithUV(d10 + d3, d1 + 0.0D, (d2 - d13) + d4, f, f3);
		tessellator
				.addVertexWithUV(d9 + d3, d1 + 0.0D, (d2 - d13) + d4, f1, f3);
		tessellator.addVertexWithUV(d9, d1 + 1.0D, d2 - d13, f1, f2);
		tessellator.addVertexWithUV(d9, d1 + 1.0D, d2 - d13, f1, f2);
		tessellator
				.addVertexWithUV(d9 + d3, d1 + 0.0D, (d2 - d13) + d4, f1, f3);
		tessellator
				.addVertexWithUV(d10 + d3, d1 + 0.0D, (d2 - d13) + d4, f, f3);
		tessellator.addVertexWithUV(d10, d1 + 1.0D, d2 - d13, f, f2);
	}

	@Override
	public boolean RenderWorldBlock(RenderBlocks renderblocks,
			IBlockAccess iblockaccess, int i, int j, int k, Block block,
			int type) {
		if (block == mod_AutoFertilizer.afertblock) {
			int metadata = mod_AutoFertilizer.BitGetDirection(iblockaccess
					.getBlockMetadata(i, j, k));
			Tessellator tessellator = Tessellator.instance;
			float f = block.getBlockBrightness(iblockaccess, i, j, k);
			if (Block.lightValue[block.blockID] > 0) {
				f = 1.0F;
			}
			tessellator.setColorOpaque_F(f, f, f);

			double d = 0.40000000596046448D;
			double d1 = 0.5D - d;
			double d2 = 0.20000000298023224D;
			switch (metadata) {
			case 1:
				renderSprinker(block, i - d1, j + d2, k, -d, 0.0D);
				break;
			case 2:
				renderSprinker(block, i + d1, j + d2, k, d, 0.0D);
				break;
			case 3:
				renderSprinker(block, i, j + d2, k - d1, 0.0D, -d);
				break;
			case 4:
				renderSprinker(block, i, j + d2, k + d1, 0.0D, d);
				break;
			default:
				renderSprinker(block, i, j, k, 0.0D, 0.0D);
				break;
			}
			return true;
		}
		return false;
	}

	@Override
	public String Version() {
		return "1.8.1";
	}
}
