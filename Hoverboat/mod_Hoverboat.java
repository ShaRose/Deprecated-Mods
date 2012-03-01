package net.minecraft.src;

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.minecraft.client.Minecraft;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleButtonModel;

public class mod_Hoverboat extends BaseMod {
	public static Item hoverboatitem = (new HoverboatItem(3038)).setIconIndex(
			ModLoader.addOverride("/gui/items.png", "/gui/hoverboat.png"))
			.setItemName("item.hoverBoat");
	public static mod_Hoverboat Instance = null;
	public static boolean isMCP = false;
	private static Vector<Class<?>> ProjectileBlacklist;
	private static Vector<HoverboatProjectileType> projectiles = new Vector<HoverboatProjectileType>();
	private static Vector<Class<?>> ProjectileWhitelist;

	private static void addProjectiles(ClassLoader classloader, String s) {
		try {
			String s1 = s.split("\\.")[0];
			if (s1.contains("$")) {
				return;
			}
			Package package1 = (mod_Hoverboat.class).getPackage();
			if (package1 != null) {
				s1 = (new StringBuilder(String.valueOf(package1.getName())))
						.append(".").append(s1).toString();
			}
			Class<?> class1 = classloader.loadClass(s1);
			if (class1.isAssignableFrom(Hoverboat_ThirdParty.class)) {
				return;
			}
			Hoverboat_ThirdParty hoverboat_thirdparty = (Hoverboat_ThirdParty) class1
					.newInstance();
			if (hoverboat_thirdparty != null) {
				if (hoverboat_thirdparty.CanLoad().booleanValue()) {
					System.out.println((new StringBuilder(
							"Hoverboat Third Party Loaded: ")).append(
							hoverboat_thirdparty.toString()).toString());
					try {
						HoverboatProjectileType ahoverboatprojectiletype[] = hoverboat_thirdparty
								.LoadProtectiles();
						if ((ahoverboatprojectiletype == null)
								|| (ahoverboatprojectiletype.length == 0)) {
							System.out.println((new StringBuilder(
									"\tHoverboat Third Party Mod "))
									.append(hoverboat_thirdparty.toString())
									.append(" Failed To Load Any Projectiles.")
									.toString());
						} else {
							for (int i = 0; i < ahoverboatprojectiletype.length; i++) {
								System.out
										.println((new StringBuilder(
												"\tHoverboat Third Party Mod "))
												.append(hoverboat_thirdparty
														.toString())
												.append(" Loaded Projectile ")
												.append(ahoverboatprojectiletype[i]
														.GetName()).toString());
								mod_Hoverboat.projectiles
										.add(ahoverboatprojectiletype[i]);
							}

						}
					} catch (Throwable throwable1) {
						System.out
								.println((new StringBuilder(
										"\tHoverboat Third Party Mod "))
										.append(hoverboat_thirdparty.toString())
										.append(" Failed To Load Projectiles. Exception:")
										.toString());
						throwable1.printStackTrace();
					}
				} else {
					System.out
							.println((new StringBuilder(
									"Hoverboat Third Party Is Not Able To Load: "))
									.append(hoverboat_thirdparty.toString())
									.toString());
				}
			}
		} catch (Throwable throwable) {
		}
	}

	private LinkedList<HoverboatProjectileType> AllowedDrops;
	private LinkedList<HoverboatProjectileType> AllowedProjectiles;
	private boolean Loaded;
	public ModSettingScreen modscreen;
	public SettingBoolean settingBoolAllowFastFov;
	public SettingBoolean settingBoolEnableArrows;
	public SettingBoolean settingBoolEnableTNT;
	public SettingBoolean settingBoolFireProof;
	public SettingBoolean settingBoolShowHUD;
	public SettingBoolean settingBoolSub;
	public SettingBoolean settingBoolUseAmmo;
	public SettingText settingDefaultDrop;
	public SettingText settingDefaultProjectile;
	public SettingFloat settingFloatAcceleration;
	public SettingFloat settingFloatAltTurnSpeed;
	public SettingFloat settingFloatArrowVel;
	public SettingFloat settingFloatBrakeMult;
	public SettingFloat settingFloatBrakeThreshold;
	public SettingFloat settingFloatCannonAccuracy;
	public SettingFloat settingFloatDefaultHover;
	public SettingFloat settingFloatDropAccuracy;
	public SettingFloat settingFloatFireballSpeed;
	public SettingFloat settingFloatMaxAltTurnSpeed;
	public SettingFloat settingFloatMaxFriction;
	public SettingFloat settingFloatMaxHover;
	public SettingFloat settingFloatMaxJump;
	public SettingFloat settingFloatMaxSpeed;
	public SettingFloat settingFloatMinFriction;
	public SettingFloat settingFloatMinHover;
	public SettingFloat settingFloatParticles;
	public SettingFloat settingFloatTNTVel;
	public SettingFloat settingFloatViewShift;
	public SettingInt settingIntArrowFireRate;
	public SettingInt settingIntTNTFireRate;
	public SettingInt settingIntTntLines;
	public SettingInt settingIntTntTicks;
	public SettingKey settingKeyBrake;
	public SettingKey settingKeyFireArrow;
	public SettingKey settingKeyFireTnt;
	public SettingKey settingKeyPark;
	public SettingKey settingKeySelectBoat;
	public SettingKey settingKeySelectFlight;
	public SettingKey settingKeySelectHover;
	public SettingKey settingKeyShiftArrowLeft;
	public SettingKey settingKeyShiftArrowRight;
	public SettingKey settingKeyShiftDropLeft;
	public SettingKey settingKeyShiftDropRight;
	public SettingKey settingKeyShiftModeLeft;
	public SettingKey settingKeyShiftModeRight;
	public SettingKey settingKeySteeringLeft;
	public SettingKey settingKeySteeringRight;
	public SettingMulti settingMultiBoatMode;
	public SettingMulti settingMultiChestSize;
	public SettingMulti settingMultiDefKeyboardSteering;
	public ModSettings settings;

	public WidgetSimplewindow subscreenArrowCannonSelection;
	public WidgetSimplewindow subscreenDropsSelection;

	private void AddDefaultsButton() {
		SimpleButtonModel simplebuttonmodel = new SimpleButtonModel();
		simplebuttonmodel.addActionCallback(new ModAction(settings, "resetAll",
				new Class[0]));
		Button button = new Button(simplebuttonmodel);
		button.setText("Reset all to defaults");
		modscreen.append(button);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void AddRenderer(Map map) {
		map.put(Hoverboat.class, new HoverboatRenderer());
	}

	private void EnsureDrops() {
		if (AllowedDrops.size() == 0) {
			ReloadAllowedDrops();
		}
	}

	private void EnsureProjectiles() {
		if (AllowedProjectiles.size() == 0) {
			ReloadAllowedProjectiles();
		}
	}

	public HoverboatProjectileType GetDefaultDrop() {
		EnsureDrops();
		String s = settingDefaultDrop.get();
		HoverboatProjectileType ret = GetDropFromString(s);
		return (ret == null ? AllowedDrops.getFirst() : ret);
	}

	public HoverboatProjectileType GetDefaultProjectile() {
		EnsureProjectiles();
		String s = settingDefaultProjectile.get();
		HoverboatProjectileType ret = GetProjectileFromString(s);

		return (ret == null ? AllowedProjectiles.getFirst() : ret);
	}

	public HoverboatProjectileType GetDropFromString(String s) {
		for (Iterator<HoverboatProjectileType> iterator = AllowedDrops
				.iterator(); iterator.hasNext();) {
			HoverboatProjectileType hoverboatprojectiletype = iterator.next();
			if (hoverboatprojectiletype.GetName() == s) {
				return hoverboatprojectiletype;
			}
		}
		return null;
	}

	public HoverboatProjectileType GetNextDrop(
			HoverboatProjectileType hoverboatprojectiletype) {
		EnsureDrops();
		int i = AllowedDrops.indexOf(hoverboatprojectiletype);
		if ((i == -1) || (i == (AllowedDrops.size() - 1))) {
			return AllowedDrops.getFirst();
		}
		return AllowedDrops.get(i + 1);
	}

	public HoverboatProjectileType GetNextProjectile(
			HoverboatProjectileType hoverboatprojectiletype) {
		EnsureProjectiles();
		int i = AllowedProjectiles.indexOf(hoverboatprojectiletype);
		if ((i == -1) || (i == (AllowedProjectiles.size() - 1))) {
			return AllowedProjectiles.getFirst();
		}
		return AllowedProjectiles.get(i + 1);
	}

	public HoverboatProjectileType GetPrevDrop(
			HoverboatProjectileType hoverboatprojectiletype) {
		EnsureDrops();
		int i = AllowedDrops.indexOf(hoverboatprojectiletype);
		if ((i == -1) || (i == 0)) {
			return AllowedDrops.getLast();
		}
		return AllowedDrops.get(i - 1);
	}

	public HoverboatProjectileType GetPrevProjectile(
			HoverboatProjectileType hoverboatprojectiletype) {
		EnsureProjectiles();
		int i = AllowedProjectiles.indexOf(hoverboatprojectiletype);
		if ((i == -1) || (i == 0)) {
			return AllowedProjectiles.getLast();
		}
		return AllowedProjectiles.get(i - 1);
	}

	public HoverboatProjectileType GetProjectileFromString(String s) {
		for (Iterator<HoverboatProjectileType> iterator = AllowedProjectiles
				.iterator(); iterator.hasNext();) {
			HoverboatProjectileType hoverboatprojectiletype = iterator.next();
			if (hoverboatprojectiletype.GetName() == s) {
				return hoverboatprojectiletype;
			}
		}
		return null;
	}

	public boolean GetValidDrop(HoverboatProjectileType hoverboatprojectiletype) {
		EnsureDrops();
		return AllowedDrops.indexOf(hoverboatprojectiletype) != -1;
	}

	public boolean GetValidProjectile(
			HoverboatProjectileType hoverboatprojectiletype) {
		EnsureProjectiles();
		return AllowedProjectiles.indexOf(hoverboatprojectiletype) != -1;
	}

	private void InitArrowSettings() {
		WidgetClassicTwocolumn widgetsinglecolumn = new WidgetClassicTwocolumn();
		widgetsinglecolumn.childDefaultWidth = 170;
		widgetsinglecolumn.splitDistance = 0;
		Button button;
		for (Iterator<?> iterator = mod_Hoverboat.projectiles.iterator(); iterator
				.hasNext(); widgetsinglecolumn.add(button)) {
			HoverboatProjectileType hoverboatprojectiletype = (HoverboatProjectileType) iterator
					.next();
			hoverboatprojectiletype.settingBoolProjectileEnabled = settings
					.addSetting(
							widgetsinglecolumn,
							(new StringBuilder("Allow ")).append(
									hoverboatprojectiletype.GetName())
									.toString(),
							(new StringBuilder("Hoverboat.Projectile."))
									.append(hoverboatprojectiletype.GetName())
									.toString(), true);
			SimpleButtonModel simplebuttonmodel = new SimpleButtonModel();
			simplebuttonmodel.addActionCallback(new ModAction(
					hoverboatprojectiletype, "MakeDefaultArrow", new Class[0]));
			button = new Button(simplebuttonmodel);
			button.setText((new StringBuilder("Make "))
					.append(hoverboatprojectiletype.GetName())
					.append(" Default").toString());
		}

		subscreenArrowCannonSelection = new WidgetSimplewindow(
				widgetsinglecolumn, "Arrow / Cannon Selection Screen");
		subscreenArrowCannonSelection.backButton.getModel().addActionCallback(
				new ModAction(this, "ReloadAllowedProjectiles", new Class[0]));
	}

	private void InitDropSettings() {
		WidgetClassicTwocolumn widgetsinglecolumn = new WidgetClassicTwocolumn();
		widgetsinglecolumn.childDefaultWidth = 170;
		widgetsinglecolumn.splitDistance = 0;
		Button button;
		for (Iterator<?> iterator = mod_Hoverboat.projectiles.iterator(); iterator
				.hasNext(); widgetsinglecolumn.add(button)) {
			HoverboatProjectileType hoverboatprojectiletype = (HoverboatProjectileType) iterator
					.next();
			hoverboatprojectiletype.settingBoolDropEnabled = settings
					.addSetting(
							widgetsinglecolumn,
							(new StringBuilder("Allow ")).append(
									hoverboatprojectiletype.GetName())
									.toString(),
							(new StringBuilder("Hoverboat.Drop.")).append(
									hoverboatprojectiletype.GetName())
									.toString(), true);
			SimpleButtonModel simplebuttonmodel = new SimpleButtonModel();
			simplebuttonmodel.addActionCallback(new ModAction(
					hoverboatprojectiletype, "MakeDefaultDrop", new Class[0]));
			button = new Button(simplebuttonmodel);
			button.setText((new StringBuilder("Make "))
					.append(hoverboatprojectiletype.GetName())
					.append(" Default").toString());
		}

		subscreenDropsSelection = new WidgetSimplewindow(widgetsinglecolumn,
				"Drop Selection Screen");
		subscreenDropsSelection.backButton.getModel().addActionCallback(
				new ModAction(this, "ReloadAllowedDrops", new Class[0]));
	}

	private void InitializeProjectiles() {
		mod_Hoverboat.projectiles = new Vector<HoverboatProjectileType>();
		try {
			HashMap<?, ?> list = (HashMap<?, ?>) ModLoader.getPrivateValue(
					EntityList.class, null,
					(mod_Hoverboat.isMCP ? "classToStringMapping" : "b"));
			mod_Hoverboat.projectiles.add(new HoverboatProjectileArrow(
					EntityArrow.class, Item.arrow.shiftedIndex, false));
			mod_Hoverboat.projectiles.add(new HoverboatProjectileArrow(
					EntityArrow.class, Item.arrow.shiftedIndex, true));
			mod_Hoverboat.projectiles.add(new HoverboatProjectileDefault(
					EntityXPOrb.class, -1, "XP Orb Spout"));
			mod_Hoverboat.projectiles.add(new HoverboatProjectileFireball(
					EntityFireball.class, -1));
			for (Iterator<?> iterator = list.keySet().iterator(); iterator
					.hasNext();) {
				Class<?> class1 = (Class<?>) iterator.next();
				if (!mod_Hoverboat.ProjectileBlacklist.contains(class1)
						&& !Modifier.isAbstract(class1.getModifiers())) {
					HoverboatProjectileType proj = new HoverboatProjectileDefault(
							class1, -1, (String) list.get(class1));
					if ((EntityLiving.class).isAssignableFrom(class1)
							|| (Material.class).isAssignableFrom(class1)
							|| (proj.throwable || mod_Hoverboat.ProjectileWhitelist
									.contains(class1))) {
						mod_Hoverboat.projectiles.add(proj);
					}
				}
			}
			Collections.sort(mod_Hoverboat.projectiles);

			mod_Hoverboat.projectiles.add(new HoverboatProjectileTNT(
					EntityTNTPrimed.class, 46, "TNT Cannon"));
			mod_Hoverboat.projectiles.add(new HoverboatProjectileSpiderJockey(
					EntitySpider.class, -1, "Spider Jockey"));

			File location = new File((mod_Hoverboat.class)
					.getProtectionDomain().getCodeSource().getLocation()
					.toURI());
			ClassLoader classloader = (mod_Hoverboat.class).getClassLoader();
			if (location.isFile()
					&& (location.getName().endsWith(".jar") || location
							.getName().endsWith(".zip"))) {
				FileInputStream fileinputstream = new FileInputStream(location);
				ZipInputStream zipinputstream = new ZipInputStream(
						fileinputstream);
				do {
					ZipEntry zipentry = zipinputstream.getNextEntry();
					if (zipentry == null) {
						break;
					}
					String s1 = zipentry.getName();
					if (!zipentry.isDirectory()
							&& s1.startsWith("hoverboat_tp_")
							&& s1.endsWith(".class")) {
						mod_Hoverboat.addProjectiles(classloader, s1);
					}
				} while (true);
				fileinputstream.close();
			} else if (location.isDirectory()) {
				Package package1 = (ModLoader.class).getPackage();
				if (package1 != null) {
					String s = package1.getName().replace('.',
							File.separatorChar);
					location = new File(location, s);
				}
				File afile[] = location.listFiles();
				if (afile != null) {
					for (int j = 0; j < afile.length; j++) {
						String s2 = afile[j].getName();
						if (afile[j].isFile() && s2.startsWith("hoverboat_tp_")
								&& s2.endsWith(".class")) {
							mod_Hoverboat.addProjectiles(classloader, s2);
						}
					}

				}
			}

			if (Block.blocksList != null) {
				for (int i = 0; i < Block.blocksList.length; i++) {
					if ((Block.blocksList[i] != null)
							&& HoverboatProjectileBlock.IsAllowed(i)) {
						mod_Hoverboat.projectiles
								.add(new HoverboatProjectileBlock(i));
					}
				}

			}

		} catch (Throwable throwable) {
			throw new RuntimeException(
					"Failed to get a list of registered entities.\r\n"
							+ throwable.getMessage(), throwable);
		}
		Loaded = true;
	}

	private void InitSettings() {
		settings = new ModSettings("mod_Hoverboat");
		modscreen = new ModSettingScreen("Hoverboat");
		WidgetSinglecolumn widgetsinglecolumn = new WidgetSinglecolumn();
		settingMultiBoatMode = settings.addSetting(widgetsinglecolumn,
				"Default Boat Mode", "Hoverboat.DefaultMode", 1, new String[] {
						"Boat", "Hover", "Flight" });
		settingMultiChestSize = settings.addSetting(widgetsinglecolumn,
				"Chest Size", "Hoverboat.ChestSize", 1, new String[] {
						"Disabled", "Regular", "Double" });
		settingMultiDefKeyboardSteering = settings.addSetting(
				widgetsinglecolumn, "Default Steering",
				"Hoverboat.DefaultSteering", 0, new String[] {
						"Minecraft Standard", "Free View", "Flight Style",
						"Versatile Mode" });
		settingFloatMaxSpeed = settings.addSetting(widgetsinglecolumn,
				"Max Speed", "Hoverboat.MaxSpeed", 1.1F, 0.01F, 0.01F, 3F);
		settingFloatAcceleration = settings.addSetting(widgetsinglecolumn,
				"Acceleration", "Hoverboat.Acceleration", 1.4F, 0.01F, 0.01F,
				10F);
		settingFloatMaxAltTurnSpeed = settings.addSetting(widgetsinglecolumn,
				"Alternate Turn Speed", "Hoverboat.AlternateTurnSpeed", 5F,
				0.1F, 0.1F, 10F);
		settingFloatAltTurnSpeed = settings.addSetting(widgetsinglecolumn,
				"Alternate Turn Damper", "Hoverboat.AlternateTurnSpeedDamper",
				0.25F, 0.01F, 0.01F, 1F);
		settingFloatMinHover = settings.addSetting(widgetsinglecolumn,
				"Min Hover Height", "Hoverboat.MinHoverHeight", 1.1F, 0.1F,
				0.1F, 3F);
		settingFloatDefaultHover = settings.addSetting(widgetsinglecolumn,
				"Default Hover Height", "Hoverboat.DefaultHoverHeight", 2.6F,
				0.5F, 0.1F, 4F);
		settingFloatMaxHover = settings.addSetting(widgetsinglecolumn,
				"Max Hover Height", "Hoverboat.MaxHoverHeight", 3.7F, 1.0F,
				0.1F, 6F);
		settingFloatMaxJump = settings.addSetting(widgetsinglecolumn,
				"Max Jump Height", "Hoverboat.MaxJumpHeight", 9F, 1.0F, 0.1F,
				14F);

		settingFloatMinFriction = settings.addSetting(widgetsinglecolumn,
				"Unmanned Friction", "Hoverboat.MinFriction", 0.8F, 0.0F,
				0.01F, 1F);
		settingFloatMaxFriction = settings.addSetting(widgetsinglecolumn,
				"Manned Friction", "Hoverboat.MaxFriction", 0.99F, 0.0F, 0.01F,
				1F);

		settingFloatViewShift = settings.addSetting(widgetsinglecolumn,
				"View (Seat) Shift", "Hoverboat.SeatShift", 0.0F, -1F, 0.01F,
				1.0F);
		settingFloatParticles = settings.addSetting(widgetsinglecolumn,
				"Particle Multiplier", "Hoverboat.ParticleMultiplier", 1.0F,
				0.0F, 0.01F, 1.0F);
		settingFloatBrakeMult = settings.addSetting(widgetsinglecolumn,
				"Brake Speed", "Hoverboat.BrakeMult", 0.9F, 0.0F, 0.01F, 1.0F);
		settingFloatBrakeThreshold = settings.addSetting(widgetsinglecolumn,
				"Brake Threshold", "Hoverboat.BrakeThreshold", 0.4F, 0.0F,
				0.01F, 0.6F);
		settingBoolSub = settings.addSetting(widgetsinglecolumn,
				"Submarine Mode", "Hoverboat.SubEnabled", true, "Enabled",
				"Disabled");
		settingBoolFireProof = settings.addSetting(widgetsinglecolumn,
				"Fire Proof", "Hoverboat.FireProof", true, "Yes", "No");
		settingBoolUseAmmo = settings.addSetting(widgetsinglecolumn,
				"Use Ammunition", "Hoverboat.UseAmmo", false, "Yes", "No");
		settingBoolShowHUD = settings.addSetting(widgetsinglecolumn,
				"On Screen Display", "Hoverboat.ShowHUD", true, "Enabled",
				"Disabled");

		settingIntTntTicks = settings.addSetting(widgetsinglecolumn,
				"TNT Timer", "Hoverboat.TNTTicks", 80, 10, 160);

		settingBoolAllowFastFov = settings.addSetting(widgetsinglecolumn,
				"Have FOV Based on speed?", "Hoverboat.FastFOV", false);

		modscreen
				.append(GuiApiHelper.makeButton("General Settings", "show",
						GuiModScreen.class, true, new Class[] { Widget.class },
						new WidgetSimplewindow(widgetsinglecolumn,
								"General Settings")));

		widgetsinglecolumn = new WidgetSinglecolumn();
		settingBoolEnableTNT = settings.addSetting(widgetsinglecolumn,
				"Enable Drops", "Hoverboat.EnableTNT", true, "Enabled",
				"Disabled");
		settingIntTNTFireRate = settings.addSetting(widgetsinglecolumn,
				"Drop Rate", "Hoverboat.DropRate", 150, 1, 5000);
		settingIntTntLines = settings.addSetting(widgetsinglecolumn,
				"Drop Lines", "Hoverboat.DropLines", 3, 0, 20);
		settingFloatDropAccuracy = settings.addSetting(widgetsinglecolumn,
				"Drop Accuracy", "Hoverboat.DropAccuracy", 0.0F, 0.0F, 0.01F,
				1F);
		widgetsinglecolumn.add(GuiApiHelper.makeButton("View Selection List",
				"showDropSelectionScreen", this, true));

		modscreen.append(GuiApiHelper.makeButton("Drop Settings", "show",
				GuiModScreen.class, true, new Class[] { Widget.class },
				new WidgetSimplewindow(widgetsinglecolumn, "Drop Settings")));

		widgetsinglecolumn = new WidgetSinglecolumn();
		settingBoolEnableArrows = settings.addSetting(widgetsinglecolumn,
				"Cannon / Arrow Firing", "Hoverboat.EnableProjectiles", true,
				"Enabled", "Disabled");
		settingIntArrowFireRate = settings.addSetting(widgetsinglecolumn,
				"Fire Rate", "Hoverboat.CannonFireRate", 75, 1, 5000);
		settingFloatFireballSpeed = settings.addSetting(widgetsinglecolumn,
				"Fireball Speed Booster", "Hoverboat.FireballBooster", 0.7F,
				0.01F, 0.01F, 3F);
		settingFloatTNTVel = settings.addSetting(widgetsinglecolumn,
				"Cannon Velocity", "Hoverboat.CannonVelocity", 1.5F, 0.5F,
				0.01F, 4F);
		settingFloatArrowVel = settings.addSetting(widgetsinglecolumn,
				"Arrow Velocity Multiplier", "Hoverboat.ArrowMult", 1.0F, 0.5F,
				0.01F, 4F);

		settingFloatCannonAccuracy = settings.addSetting(widgetsinglecolumn,
				"Cannon Accuracy", "Hoverboat.CannonAccuracy", 0.0F, 0.0F,
				0.1F, 40F);

		widgetsinglecolumn.add(GuiApiHelper.makeButton("View Selection List",
				"showArrowSelectionScreen", this, true));

		modscreen.append(GuiApiHelper.makeButton("Arrow / Cannon Settings",
				"show", GuiModScreen.class, true, new Class[] { Widget.class },
				new WidgetSimplewindow(widgetsinglecolumn,
						"Arrow / Cannon Settings")));
		widgetsinglecolumn = new WidgetSinglecolumn();
		settingKeyPark = settings.addSetting(widgetsinglecolumn, "Park",
				"Hoverboat.KeyPark", 54);
		settingKeyBrake = settings.addSetting(widgetsinglecolumn, "Brake",
				"Hoverboat.KeyBrake", 46);
		settingKeyFireArrow = settings.addSetting(widgetsinglecolumn,
				"Fire Cannon / Arrow", "Hoverboat.KeyFireProjectile", 42);
		settingKeyFireTnt = settings.addSetting(widgetsinglecolumn, "Drop",
				"Hoverboat.KeyDrop", 29);
		settingKeyShiftModeLeft = settings.addSetting(widgetsinglecolumn,
				"Shift Mode Left", "Hoverboat.KeySML", 24);
		settingKeyShiftModeRight = settings.addSetting(widgetsinglecolumn,
				"Shift Mode Right", "Hoverboat.KeySMR", 25);
		settingKeyShiftArrowLeft = settings.addSetting(widgetsinglecolumn,
				"Shift Arrow Left", "Hoverboat.KeySAL", 37);
		settingKeyShiftArrowRight = settings.addSetting(widgetsinglecolumn,
				"Shift Arrow Right", "Hoverboat.KeySAR", 38);
		settingKeyShiftDropLeft = settings.addSetting(widgetsinglecolumn,
				"Shift Drop Left", "Hoverboat.KeySDL", 35);
		settingKeyShiftDropRight = settings.addSetting(widgetsinglecolumn,
				"Shift Drop Right", "Hoverboat.KeySDR", 36);
		settingKeySelectBoat = settings.addSetting(widgetsinglecolumn,
				"Select Boat", "Hoverboat.KeySelBoat", 47);
		settingKeySelectHover = settings.addSetting(widgetsinglecolumn,
				"Select Hover", "Hoverboat.KeySelHover", 48);
		settingKeySelectFlight = settings.addSetting(widgetsinglecolumn,
				"Select Flight", "Hoverboat.KeySelFlight", 49);
		settingKeySteeringLeft = settings.addSetting(widgetsinglecolumn,
				"Shift Steer Mode Left", "Hoverboat.KeySteerLeft", 51);
		settingKeySteeringRight = settings.addSetting(widgetsinglecolumn,
				"Shift Steer Mode Right", "Hoverboat.KeySteerRight", 52);
		modscreen.append(GuiApiHelper.makeButton("Key Binding Settings",
				"show", GuiModScreen.class, true, new Class[] { Widget.class },
				new WidgetSimplewindow(widgetsinglecolumn,
						"Key Binding Settings")));

		settingDefaultProjectile = new SettingText(
				"Hoverboat.DefaultProjectile", "NULL");
		settingDefaultDrop = new SettingText("Hoverboat.DefaultDrop", "NULL");
		settings.append(settingDefaultProjectile);
		settings.append(settingDefaultDrop);
	}

	@Override
	public boolean OnTickInGame(float partialtick, Minecraft minecraft) {
		if (minecraft.currentScreen != null) {
			return true;
		}

		if (settingBoolShowHUD.get()
				&& (minecraft.thePlayer.ridingEntity != null)
				&& (minecraft.thePlayer.ridingEntity instanceof Hoverboat)) {
			Hoverboat hoverboat = (Hoverboat) minecraft.thePlayer.ridingEntity;
			minecraft.fontRenderer
					.drawStringWithShadow(
							String.format(
									"Hoverboat: %s Mode (%s)",
									new Object[] {
											hoverboat.IsParked ? "Park"
													: Double.isNaN(hoverboat.depth)
															|| (hoverboat.depth <= 1.0D)
															|| (hoverboat.CurrentMode != 0) ? settingMultiBoatMode.labelValues[hoverboat.CurrentMode]
															: "Sub",
											settingMultiDefKeyboardSteering.labelValues[hoverboat.KeyboardSteering] }),
							2, 12, 0xffffff);
			if ((AllowedProjectiles.size() > 0)
					&& settingBoolEnableArrows.get()) {
				minecraft.fontRenderer.drawStringWithShadow(String.format(
						"Hoverboat Projectile: %s Selected",
						new Object[] { hoverboat.SelectedArrow.GetName() }), 2,
						24, 0xffffff);
			}
			if ((AllowedDrops.size() > 0) && settingBoolEnableTNT.get()) {
				minecraft.fontRenderer.drawStringWithShadow(String.format(
						"Hoverboat Drop: %s Selected",
						new Object[] { hoverboat.SelectedDrop.GetName() }), 2,
						36, 0xffffff);
			}
			int i = 48;
			if (hoverboat.Debugger != "") {
				String as[];
				int k = (as = hoverboat.Debugger.split("\n")).length;
				for (int j = 0; j < k; j++) {
					String s = as[j];
					minecraft.fontRenderer.drawStringWithShadow(
							(new StringBuilder("Hoverboat: Debugging: "))
									.append(s).toString(), 2, i, 0xffffff);
					i += 12;
				}

			}
		}
		return true;
	}

	@Override
	public void RegisterAnimation(Minecraft minecraft) {
		CraftingManager.getInstance().addRecipe(
				new ItemStack(mod_Hoverboat.hoverboatitem, 1),
				new Object[] { "#B#", "###", Character.valueOf('#'),
						Block.blockGold, Character.valueOf('B'),
						Block.blockDiamond });
		InitializeProjectiles();
		InitDropSettings();
		InitArrowSettings();
		ReloadAllowedProjectiles();
		ReloadAllowedDrops();
		settings.load();
	}

	private void ReloadAllowedDrops() {
		AllowedDrops.clear();
		for (Iterator<?> iterator = mod_Hoverboat.projectiles.iterator(); iterator
				.hasNext();) {
			HoverboatProjectileType hoverboatprojectiletype = (HoverboatProjectileType) iterator
					.next();
			if (hoverboatprojectiletype.AllowForDrops) {
				AllowedDrops.add(hoverboatprojectiletype);
				if (settingDefaultProjectile.get() == hoverboatprojectiletype
						.GetName()) {
					hoverboatprojectiletype.MakeDefaultArrow();
				}
			}
		}

	}

	private void ReloadAllowedProjectiles() {
		if (!Loaded) {
			return;
		}
		AllowedProjectiles.clear();
		for (Iterator<?> iterator = mod_Hoverboat.projectiles.iterator(); iterator
				.hasNext();) {
			HoverboatProjectileType hoverboatprojectiletype = (HoverboatProjectileType) iterator
					.next();
			if (hoverboatprojectiletype.AllowForShoot) {
				AllowedProjectiles.add(hoverboatprojectiletype);
				if (settingDefaultProjectile.get() == hoverboatprojectiletype
						.GetName()) {
					hoverboatprojectiletype.MakeDefaultArrow();
				}
			}
		}

	}

	public void SetDefaultArrow(HoverboatProjectileType hoverboatprojectiletype) {
		if (!hoverboatprojectiletype.AllowForShoot) {
			return;
		}
		settingDefaultProjectile.set(hoverboatprojectiletype.GetName(),
				ModSettings.currentContext);
		GuiModScreen.clicksound();
		return;
	}

	public void SetDefaultDrop(HoverboatProjectileType hoverboatprojectiletype) {
		if (!hoverboatprojectiletype.AllowForDrops) {
			return;
		}
		settingDefaultDrop.set(hoverboatprojectiletype.GetName(),
				ModSettings.currentContext);
		GuiModScreen.clicksound();
		return;
	}

	@SuppressWarnings("unused")
	private void showArrowSelectionScreen() {
		GuiModScreen.show(subscreenDropsSelection);
	}

	@SuppressWarnings("unused")
	private void showDropSelectionScreen() {
		GuiModScreen.show(subscreenDropsSelection);
	}

	@Override
	public String getVersion() {
		return "1.0.0";
	}

	@Override
	public void load() {
		ModLoader.RegisterEntityID(Hoverboat.class, "HoverBoat",
				ModLoader.getUniqueEntityId());
		mod_Hoverboat.ProjectileBlacklist = new Vector<Class<?>>();
		mod_Hoverboat.ProjectileBlacklist.add(EntityItem.class);
		mod_Hoverboat.ProjectileBlacklist.add(EntityPainting.class);
		mod_Hoverboat.ProjectileBlacklist.add(EntityLiving.class);
		mod_Hoverboat.ProjectileBlacklist.add(EntityMob.class);
		mod_Hoverboat.ProjectileBlacklist.add(EntityPlayerSP.class);
		mod_Hoverboat.ProjectileBlacklist.add(EntityPlayer.class);
		mod_Hoverboat.ProjectileWhitelist = new Vector<Class<?>>();
		mod_Hoverboat.ProjectileWhitelist.add(EntityMinecart.class);
		mod_Hoverboat.ProjectileWhitelist.add(EntityBoat.class);
		mod_Hoverboat.ProjectileWhitelist.add(EntityFireball.class);
		mod_Hoverboat.ProjectileWhitelist.add(EntityEgg.class);

		try {
			mod_Hoverboat.isMCP = Hoverboat.class.getSuperclass().getName() == "net.minecraft.src.Entity";
		} catch (Throwable x) {
			mod_Hoverboat.isMCP = false;
		}
		
		Loaded = false;
		InitSettings();
		AddDefaultsButton();
		mod_Hoverboat.projectiles = new Vector<HoverboatProjectileType>();
		AllowedProjectiles = new LinkedList<HoverboatProjectileType>();
		AllowedDrops = new LinkedList<HoverboatProjectileType>();
		mod_Hoverboat.Instance = this;
		ModLoader.SetInGameHook(this, true, false);
		ModLoader.AddName(mod_Hoverboat.hoverboatitem, "Hoverboat");
	}
}
