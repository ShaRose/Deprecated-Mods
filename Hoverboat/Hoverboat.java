package net.minecraft.src;

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.awt.geom.Point2D;

import org.lwjgl.input.Keyboard;

public class Hoverboat extends Entity implements IInventory {

	public static double Degreetonormalizedradian(double d) {
		return Hoverboat.NormalizeDegree(d) * 0.017453292519943295D;
	}

	public static double NormalizeDegree(double d) {
		for (; d > 180D; d -= 360D) {
		}
		for (; d < -180D; d += 360D) {
		}
		if ((d == -180D) || (d == 180D)) {
			d = 0.0D;
		}
		return d;
	}

	public static Point2D RotatePoint(Point2D point2d, Point2D point2d1,
			double d) {
		return Hoverboat.RotatePoint(point2d, point2d1, Math.sin(d),
				Math.cos(d));
	}

	public static Point2D RotatePoint(Point2D point2d, Point2D point2d1,
			double d, double d1) {
		return new java.awt.geom.Point2D.Double(
				((d1 * (point2d.getX() - point2d1.getX())) - (d * (point2d.getY() - point2d1.getY())))
						+ point2d1.getY(),
				(d * (point2d.getX() - point2d1.getX()))
						+ (d1 * (point2d.getY() - point2d1.getY()))
						+ point2d1.getY());
	}

	protected int chestSize;

	public int CurrentDamage;

	double currentheight;

	private int currentItems;

	public int CurrentMode;

	public String Debugger;

	double depth;

	protected Object dropPoints[][];

	private double floatSpeed;

	protected ItemStack inventory[];

	protected boolean isDiving;

	protected boolean isDivingInLava;

	protected boolean isOnline;

	public boolean IsParked;

	protected boolean jumpedLastFrame;

	protected boolean jumpedLastFrameHigh;

	public int KeyboardSteering;

	protected boolean keyDown;

	private double KeySteeringLast;

	protected long lastDive;

	private int lastPressedKey;

	public float Lean;

	protected long nextProjectile;

	private float oldRotation;

	public float PlayerFB;

	public boolean PlayerJumping;

	public float PlayerLR;

	public int RockDirection;

	public HoverboatProjectileType SelectedArrow;

	public HoverboatProjectileType SelectedDrop;

	public int TimeSinceHit;

	public Hoverboat(World world) {
		super(world);
		CurrentDamage = 0;
		TimeSinceHit = 0;
		RockDirection = 1;
		KeyboardSteering = 0;
		Lean = 0.0F;
		currentheight = 0.0D;
		depth = (0.0D / 0.0D);
		lastPressedKey = -1;
		chestSize = 2;
		inventory = new ItemStack[54];
		jumpedLastFrame = false;
		jumpedLastFrameHigh = false;
		CurrentMode = 1;
		keyDown = false;
		dropPoints = new Object[0][2];
		nextProjectile = 0L;
		isOnline = false;
		lastDive = 0L;
		isDiving = false;
		isDivingInLava = false;
		SelectedArrow = null;
		SelectedDrop = null;
		IsParked = false;
		floatSpeed = 0.0D;
		currentItems = 0;
		Debugger = "";
		PlayerJumping = false;
		PlayerLR = 0.0F;
		PlayerFB = 0.0F;
		KeySteeringLast = 0.0D;
		oldRotation = (0.0F / 0.0F);
		if (world instanceof WorldClient) {
			CrashDueToOnline();
		}
		preventEntitySpawning = true;
		setSize(1.5F, 0.6F);
		yOffset = (height / 2.0F);
		LoadSettings();
	}

	public Hoverboat(World world, double d, double d1, double d2) {
		this(world);
		setPosition(d, d1 + yOffset, d2);
		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;
		prevPosX = d;
		prevPosY = d1;
		prevPosZ = d2;
	}

	public boolean attackEntityFrom(DamageSource source, int damage) {
		if (isDead) {
			return true;
		}
		RockDirection = -RockDirection;
		TimeSinceHit = 10;
		if (damage < 10) {
			damage = 10;
		}
		CurrentDamage += damage;
		setBeenAttacked();
		if (CurrentDamage > 80) {
			if (riddenByEntity != null) {
				riddenByEntity.isImmuneToFire = false;
			}
			if (source.getSourceOfDamage() instanceof EntityPlayer) {
				if (!((EntityPlayer) source.getSourceOfDamage()).inventory
						.addItemStackToInventory(new ItemStack(
								mod_Hoverboat.hoverboatitem.shiftedIndex, 1, 0))) {
					dropItemWithOffset(
							mod_Hoverboat.hoverboatitem.shiftedIndex, 1, 0.0F);
				}
			} else {
				dropItemWithOffset(mod_Hoverboat.hoverboatitem.shiftedIndex, 1,
						0.0F);
			}
			for (int j = 0; j < inventory.length; j++) {
				if (inventory[j] != null) {
					dropItemWithOffset(inventory[j].itemID,
							inventory[j].stackSize, 0.0F);
					inventory[j] = null;
				}
			}
			setEntityDead();
		}
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	private void CheckKeys() {
		if (!ModLoader.isGUIOpen(null)) {
			return;
		}
		if (!mod_Hoverboat.Instance.GetValidProjectile(SelectedArrow)) {
			SelectedArrow = mod_Hoverboat.Instance
					.GetNextProjectile(SelectedArrow);
		}
		if (!mod_Hoverboat.Instance.GetValidDrop(SelectedDrop)) {
			SelectedDrop = mod_Hoverboat.Instance.GetNextDrop(SelectedDrop);
		}
		
		if (mod_Hoverboat.Instance.settingKeyBrake.isKeyDown()) {
			motionX *= mod_Hoverboat.Instance.settingFloatBrakeMult.get();
			motionZ *= mod_Hoverboat.Instance.settingFloatBrakeMult.get();
		}
		if (GetKeyLocked(mod_Hoverboat.Instance.settingKeyShiftModeLeft)) {
			CurrentMode--;
			if (CurrentMode < 0) {
				CurrentMode = 2;
			}
		}
		if (GetKeyLocked(mod_Hoverboat.Instance.settingKeyShiftModeRight)) {
			CurrentMode++;
			if (CurrentMode > 2) {
				CurrentMode = 0;
			}
		}
		if (GetKeyLocked(mod_Hoverboat.Instance.settingKeyShiftArrowLeft)) {
			SelectedArrow = mod_Hoverboat.Instance
					.GetPrevProjectile(SelectedArrow);
		}
		if (GetKeyLocked(mod_Hoverboat.Instance.settingKeyShiftArrowRight)) {
			SelectedArrow = mod_Hoverboat.Instance
					.GetNextProjectile(SelectedArrow);
		}
		if (GetKeyLocked(mod_Hoverboat.Instance.settingKeyShiftDropLeft)) {
			SelectedDrop = mod_Hoverboat.Instance.GetPrevDrop(SelectedDrop);
		}
		if (GetKeyLocked(mod_Hoverboat.Instance.settingKeyShiftDropRight)) {
			SelectedDrop = mod_Hoverboat.Instance.GetNextDrop(SelectedDrop);
		}
		if (GetKeyLocked(mod_Hoverboat.Instance.settingKeySteeringLeft)) {
			prevRotationYaw = rotationYaw;
			KeyboardSteering--;
			if (KeyboardSteering < 0) {
				KeyboardSteering = 3;
			}
		}
		if (GetKeyLocked(mod_Hoverboat.Instance.settingKeySteeringRight)) {
			prevRotationYaw = rotationYaw;
			KeyboardSteering++;
			if (KeyboardSteering > 3) {
				KeyboardSteering = 0;
			}
		}
		if (mod_Hoverboat.Instance.settingKeySelectBoat.isKeyDown()) {
			CurrentMode = 0;
		}
		if (mod_Hoverboat.Instance.settingKeySelectHover.isKeyDown()) {
			CurrentMode = 1;
		}
		if (mod_Hoverboat.Instance.settingKeySelectFlight.isKeyDown()) {
			CurrentMode = 2;
		}
		if (mod_Hoverboat.Instance.settingKeyFireArrow.isKeyDown()) {
			FireArrow();
		}
		if (mod_Hoverboat.Instance.settingKeyFireTnt.isKeyDown()
				&& (CurrentMode == 2)) {
			DropTnT();
		}
		if (GetKeyLocked(mod_Hoverboat.Instance.settingKeyPark)) {
			IsParked = !IsParked;
		}
		if ((lastPressedKey != -1) && !Keyboard.isKeyDown(lastPressedKey)
				&& keyDown) {
			lastPressedKey = -1;
			keyDown = false;
		}
	}

	private void CompressChest(int i) {
		int j = 0;
		for (int k = 0; k < inventory.length; k++) {
			if (inventory[k] != null) {
				j++;
			}
		}

		if (j > i) {
			for (int l = 0; l < inventory.length; l++) {
				if (inventory[l] != null) {
					int j1 = inventory[l].getItem().maxStackSize;
					if (j1 != 1) {
						int l1 = 0;
						do {
							if (l1 == l) {
								break;
							}
							l1++;
							l1 = GetFirstSlotOfItem(inventory[l].itemID, l1);
						} while ((l1 == -1) || (inventory[l1].stackSize >= j1)
								|| (l1 == l));
						if (l1 != l) {
							int i2 = inventory[l1].stackSize
									+ inventory[l].stackSize;
							if (i2 > j1) {
								inventory[l1].stackSize = j1;
								inventory[l].stackSize = i2 - j1;
							} else {
								inventory[l1].stackSize = i2;
								inventory[l] = null;
							}
						}
					}
				}
			}

		}
		int i1 = GetEmptySlot();
		for (int k1 = 0; k1 < inventory.length; k1++) {
			if (inventory[k1] != null) {
				if ((i1 != -1) && (i1 < i)) {
					inventory[i1] = inventory[k1];
					inventory[k1] = null;
					i1 = GetEmptySlot();
				} else {
					dropItemWithOffset(inventory[k1].itemID,
							inventory[k1].stackSize, 0.0F);
					inventory[k1] = null;
				}
			}
		}

		currentItems = i;
	}

	private void CompressChestIfNeeded() {
		int i = mod_Hoverboat.Instance.settingMultiChestSize.get() * 27;
		if (currentItems > i) {
			CompressChest(i);
		} else {
			currentItems = i;
		}
	}

	private static void CrashDueToOnline() {
		throw new RuntimeException(
				"Hoverboat has been disabled on online multiplayer to curb griefing. You should not see this unless you try and spawn it with java yourself.");
	}

	private void DropTnT() {
		if (!mod_Hoverboat.Instance.settingBoolEnableTNT.get()) {
			return;
		}
		MakeDrops(mod_Hoverboat.Instance.settingIntTntLines.get());
		boolean useammo = mod_Hoverboat.Instance.settingBoolUseAmmo.get();
		int tntFireRate = mod_Hoverboat.Instance.settingIntTNTFireRate.get();
		double rotation = Hoverboat.Degreetonormalizedradian(rotationYaw) + 1.5707963267948966D;
		java.awt.geom.Point2D.Double double1 = new java.awt.geom.Point2D.Double();
		double cos = Math.cos(rotation);
		double sin = Math.sin(rotation);
		long time = System.currentTimeMillis();
		for (int j = 0; j < dropPoints.length; j++) {
			if (((Long) dropPoints[j][1]).longValue() <= time) {
				Point2D point2d = Hoverboat.RotatePoint(
						(Point2D) dropPoints[j][0], double1, sin, cos);
				if (useammo && !SelectedDrop.UseItem(this)) {
					return;
				}
				double SpeedLevel = (rand.nextDouble() * 0.6D) + 1.0D;
				dropPoints[j][1] = Long.valueOf(time
						+ (long) (tntFireRate * SpeedLevel));
				SelectedDrop
						.DropItem(
								worldObj,
								(EntityLiving) riddenByEntity,
								point2d.getX()
										+ posX
										+ (float) (rand.nextGaussian() * mod_Hoverboat.Instance.settingFloatDropAccuracy
												.get()),
								posY - 1.5D,
								point2d.getY()
										+ posZ
										+ (float) (rand.nextGaussian() * mod_Hoverboat.Instance.settingFloatDropAccuracy
												.get()), rotationYaw);
			}
		}

	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (inventory[i] != null) {
			if (inventory[i].stackSize <= j) {
				ItemStack itemstack = inventory[i];
				inventory[i] = null;
				return itemstack;
			}
			ItemStack itemstack1 = inventory[i].splitStack(j);
			if (inventory[i].stackSize == 0) {
				inventory[i] = null;
			}
			return itemstack1;
		}
		return null;
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void fall(float var1) {
	}

	private void FireArrow() {
		if ((nextProjectile >= System.currentTimeMillis())
				|| !mod_Hoverboat.Instance.settingBoolEnableArrows.get()) {
			return;
		}
		if (mod_Hoverboat.Instance.settingBoolUseAmmo.get()
				&& !SelectedArrow.UseItem(this)) {
			return;
		}
		double x = posX;
		double y = (riddenByEntity.posY + riddenByEntity.getEyeHeight()) - 0.3D;
		double z = posZ;
		Point2D point2d = Hoverboat
				.RotatePoint(
						new java.awt.geom.Point2D.Double(0.0D, 3.5D),
						new java.awt.geom.Point2D.Double(),
						Hoverboat
								.Degreetonormalizedradian(riddenByEntity.rotationPitch));
		y += point2d.getX();
		point2d = Hoverboat
				.RotatePoint(
						new java.awt.geom.Point2D.Double(0.0D, point2d
								.getY()),
						new java.awt.geom.Point2D.Double(),
						Hoverboat
								.Degreetonormalizedradian(riddenByEntity.rotationYaw));
		x += point2d.getX();
		z += point2d.getY();
		SelectedArrow
				.FireItem(
						worldObj,
						(EntityLiving) riddenByEntity,
						x,
						y,
						z,
						riddenByEntity.rotationYaw
								+ (float) (rand.nextGaussian() * mod_Hoverboat.Instance.settingFloatCannonAccuracy
										.get()),
						riddenByEntity.rotationPitch
								+ (float) (rand.nextGaussian() * mod_Hoverboat.Instance.settingFloatCannonAccuracy
										.get()));
		nextProjectile = System.currentTimeMillis()
				+ (long) mod_Hoverboat.Instance.settingIntArrowFireRate
						.get();
		return;
	}

	@Override
	public void closeChest() {
	}

	@Override
	public void openChest() {
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return boundingBox;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity) {
		return entity.boundingBox;
	}

	private double GetDepthStack() {
		isDivingInLava = false;
		int i = -1;
		int j = MathHelper.floor_double(posX);
		int k = MathHelper.floor_double(posY - 0.14999999999999999D);
		int l = MathHelper.floor_double(posZ);
		boolean flag = false;
		boolean flag2 = false;
		do {
			i++;
			flag = MaterialOfBlockIsWaterLava(j, k + i, l);
			flag2 = flag || flag2;
		} while (flag && (i < 3));
		if (!flag2) {
			boolean flag1;
			do {
				i--;
				flag1 = MaterialOfBlockIsWaterLava(j, k + i, l);
				flag2 = flag1 || flag2;
			} while (flag1 && (i > -3));
			if (!flag2) {
				return (0.0D / 0.0D);
			}
		}
		return (k + i) - (posY - 0.11D);
	}

	private int GetEmptySlot() {
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] == null) {
				return i;
			}
		}

		return -1;
	}

	private int GetFirstSlotOfItem(int i) {
		return GetFirstSlotOfItem(i, 0);
	}

	private int GetFirstSlotOfItem(int i, int j) {
		if (j >= inventory.length) {
			return -1;
		}
		for (int k = j; k < inventory.length; k++) {
			if ((inventory[k] != null) && (inventory[k].itemID == i)) {
				return k;
			}
		}

		return -1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public String getInvName() {
		return "Hoverboat Storage Center";
	}

	private boolean GetKeyLocked(SettingKey settingkey) {
		if (keyDown || !settingkey.isKeyDown()) {
			return false;
		}
		lastPressedKey = settingkey.get();
		keyDown = true;
		return true;
	}

	@Override
	public double getMountedYOffset() {
		return (height * 0.0D) - 0.30000001192092901D;
	}

	@Override
	public float getShadowSize() {
		return 0.0F;
	}

	@Override
	public int getSizeInventory() {
		return mod_Hoverboat.Instance.settingMultiChestSize.get() * 27;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory[i];
	}

	private boolean IgnoreBlock(int x, int y, int z) {
		Block block = Block.blocksList[worldObj.getBlockId(x, y, z)];
		if (block == null) {
			return true;
		}
		if (block instanceof BlockFluid) {
			return false;
		}
		AxisAlignedBB bb = block.getCollisionBoundingBoxFromPool(worldObj, x,
				y, z);
		if ((bb == null) || ((bb.maxY - bb.minY) < 0.001D)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean interact(EntityPlayer entityplayer) {
		CompressChestIfNeeded();
		if (getSizeInventory() != 0) {

			if (entityplayer.isSneaking() && (riddenByEntity != entityplayer)) {
				((EntityPlayerSP) entityplayer).displayGUIChest(this);
				return true;
			}
		}
		if ((riddenByEntity != null)
				&& (riddenByEntity instanceof EntityPlayer)
				&& (riddenByEntity != entityplayer)) {
			return true;
		}
		entityplayer.mountEntity(this);
		double calculated = entityplayer.boundingBox.maxY
				- entityplayer.boundingBox.minY;
		if (calculated < 0.6D) {
			calculated = 0.6D;
		}
		if (riddenByEntity == null) {
			entityplayer.isImmuneToFire = false;
			boundingBox.maxY = boundingBox.minY + 0.6D;
		} else {
			boundingBox.maxY = boundingBox.minY + calculated;
			entityplayer.rotationYaw = rotationYaw + 90F;
		}
		return true;
	}

	private void LoadSettings() {
		CurrentMode = mod_Hoverboat.Instance.settingMultiBoatMode.get();
		currentItems = mod_Hoverboat.Instance.settingMultiChestSize.get();
		KeyboardSteering = mod_Hoverboat.Instance.settingMultiDefKeyboardSteering
				.get();
		SelectedArrow = mod_Hoverboat.Instance.GetDefaultProjectile();
		SelectedDrop = mod_Hoverboat.Instance.GetDefaultDrop();

	}

	private void MakeDrops(int i) {
		if ((i < 0) || (dropPoints.length == i)) {
			return;
		}
		dropPoints = new Object[i][2];
		double dropwidth = 7D;
		double part = (1.0D / dropPoints.length) * dropwidth;
		double x = (-dropwidth / 2D) - (part / 2D);
		for (int j = 0; j < dropPoints.length; j++) {
			x += part;
			dropPoints[j][0] = new java.awt.geom.Point2D.Double(x,
					-(2D + (Math.abs(x) / 2.5D)));
			dropPoints[j][1] = Long.valueOf(0L);
		}

	}

	private boolean MaterialOfBlockIsWaterLava(int i, int j, int k) {
		int l = worldObj.getBlockId(i, j, k);
		if (l != 0) {
			if (Block.blocksList[l].blockMaterial == Material.lava) {
				isDivingInLava = true;
				return true;
			}
			if (Block.blocksList[l].blockMaterial == Material.water) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onInventoryChanged() {
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (TimeSinceHit > 0) {
			TimeSinceHit--;
		}
		if (CurrentDamage > 0) {
			CurrentDamage--;
		}
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		currentheight = RecalcHeight();
		depth = Double.NaN;
		if (CurrentMode == 0) {
			depth = GetDepthStack();
		}
		double hoverheight = mod_Hoverboat.Instance.settingFloatDefaultHover
				.get();
		double extrajump = 0.0D;
		double CurrentSpeed = 0.0D;
		double SpeedLevel = 0.0D;
		double MaxSpeed = mod_Hoverboat.Instance.settingFloatMaxSpeed.get();
		boolean SubEnabled = mod_Hoverboat.Instance.settingBoolSub.get();
		isImmuneToFire = mod_Hoverboat.Instance.settingBoolFireProof.get();
		boolean SlowDecent = CurrentMode == 2;
		double rotate = 0.0D;
		if (riddenByEntity != null) {
			UpdateKeys();
			
			double AccelerationMultiplier = mod_Hoverboat.Instance.settingFloatAcceleration
					.get();
			riddenByEntity.isImmuneToFire = isImmuneToFire;
			double AltTurnSpeed = mod_Hoverboat.Instance.settingFloatMaxAltTurnSpeed
					.get();
			double AltTurnSpeedDamper = mod_Hoverboat.Instance.settingFloatAltTurnSpeed
					.get();
			PlayerFB *= 1.3;
			PlayerLR *= 1.3;
			switch (KeyboardSteering) {
			default:
				break;

			case 0: // Minecraft Standard
			{
				motionX += riddenByEntity.motionX * AccelerationMultiplier;
				motionZ += riddenByEntity.motionZ * AccelerationMultiplier;
				break;
			}
			case 1: // Free View
			{
				rotate = (KeySteeringLast - (PlayerLR * AltTurnSpeed))
						* (PlayerLR != 0.0F ? 0.1F : 0.3F);
				KeySteeringLast -= rotate;
				rotate = KeySteeringLast;
				if (KeySteeringLast > AltTurnSpeed) {
					KeySteeringLast = AltTurnSpeed;
				}
				if (KeySteeringLast < -AltTurnSpeed) {
					KeySteeringLast = -AltTurnSpeed;
				}
				Point2D point2d = Hoverboat.RotatePoint(
						new java.awt.geom.Point2D.Double(motionX, motionZ),
						new java.awt.geom.Point2D.Double(),
						Hoverboat.Degreetonormalizedradian(-rotate));
				motionX = point2d.getX();
				motionZ = point2d.getY();
				if (PlayerFB != 0.0F) {
					Point2D point2d1 = Hoverboat.RotatePoint(
							new java.awt.geom.Point2D.Double(
									-(PlayerFB * 0.01D), 0.0D),
							new java.awt.geom.Point2D.Double(), Hoverboat
									.Degreetonormalizedradian(rotationYaw
											- rotate));
					double X = point2d1.getX() * AccelerationMultiplier;
					double Y = point2d1.getY() * AccelerationMultiplier;
					motionX += X;
					motionZ += Y;
				}
				break;
			}
			case 2: // Flight Style
			{
				if (PlayerFB == 0.0F) {
					break;
				}
				rotate = Hoverboat.NormalizeDegree(rotationYaw
						- (riddenByEntity.rotationYaw - 90D))
						* AltTurnSpeedDamper;
				if (rotate > AltTurnSpeed) {
					rotate = AltTurnSpeed;
				}
				if (rotate < -AltTurnSpeed) {
					rotate = -AltTurnSpeed;
				}
				Point2D point2d2 = Hoverboat.RotatePoint(
						new java.awt.geom.Point2D.Double(motionX, motionZ),
						new java.awt.geom.Point2D.Double(),
						Hoverboat.Degreetonormalizedradian(-rotate));
				motionX = point2d2.getX();
				motionZ = point2d2.getY();
				point2d2 = Hoverboat
						.RotatePoint(new java.awt.geom.Point2D.Double(
								-(PlayerFB * 0.01D), 0.0D),
								new java.awt.geom.Point2D.Double(), Hoverboat
										.Degreetonormalizedradian(rotationYaw
												- rotate));
				motionX += point2d2.getX() * AccelerationMultiplier;
				motionZ += point2d2.getY() * AccelerationMultiplier;
				rotationYaw -= rotate;
				break;
			}
			case 3: // beta style
			{
				if ((PlayerFB == 0.0F) && (PlayerLR == 0.0F)) {
					break;
				}
				rotate = Hoverboat.NormalizeDegree(rotationYaw
						- (riddenByEntity.rotationYaw - 90D))
						* AltTurnSpeedDamper;
				if (rotate > AltTurnSpeed) {
					rotate = AltTurnSpeed;
				}
				if (rotate < -AltTurnSpeed) {
					rotate = -AltTurnSpeed;
				}
				Point2D point2d3 = Hoverboat.RotatePoint(
						new java.awt.geom.Point2D.Double(motionX, motionZ),
						new java.awt.geom.Point2D.Double(),
						Hoverboat.Degreetonormalizedradian(-rotate));
				motionX = point2d3.getX();
				motionZ = point2d3.getY();

				if (PlayerFB != 0.0F) {
					Point2D point2d4 = Hoverboat.RotatePoint(
							new java.awt.geom.Point2D.Double(
									-(PlayerFB * 0.01D), 0.0D),
							new java.awt.geom.Point2D.Double(), Hoverboat
									.Degreetonormalizedradian(rotationYaw
											- rotate));
					motionX += point2d4.getX() * AccelerationMultiplier;
					motionZ += point2d4.getY() * AccelerationMultiplier;
				}
				if (PlayerLR != 0.0F) {
					Point2D point2d5 = Hoverboat.RotatePoint(
							new java.awt.geom.Point2D.Double(
									-(PlayerLR * 0.01D), 0.0D),
							new java.awt.geom.Point2D.Double(), Hoverboat
									.Degreetonormalizedradian(rotationYaw
											- rotate - 90D));
					motionX += point2d5.getX() * AccelerationMultiplier;
					motionZ += point2d5.getY() * AccelerationMultiplier;
				}
				rotationYaw -= rotate;
				break;
			}
			}
			CurrentSpeed = Math.sqrt((motionX * motionX) + (motionZ * motionZ));
			SpeedLevel = CurrentSpeed / MaxSpeed;
			if (SpeedLevel > 0.7D) {
				hoverheight = mod_Hoverboat.Instance.settingFloatMaxHover.get();
			}
			if(mod_Hoverboat.Instance.settingBoolAllowFastFov.get())
			{
				if(CurrentSpeed > 0.2)
				{
					((EntityPlayer)riddenByEntity).speedOnGround = (float) SpeedLevel;
				}
			}
			if (SubEnabled) {
				riddenByEntity.dataWatcher.updateObject(1, Short.valueOf((short) 300));
			}

			if (PlayerJumping) {
				if (CurrentMode != 0) {
					if ((currentheight < (hoverheight + 0.5D))
							|| ((CurrentMode == 2) && (CurrentSpeed > (MaxSpeed * 0.5D)))) {
						extrajump = 0.6D;
						jumpedLastFrameHigh = true;
						SlowDecent = false;
					} else if ((currentheight < (hoverheight + mod_Hoverboat.Instance.settingFloatMaxJump
							.get())) && jumpedLastFrameHigh) {
						extrajump = 0.6D;
					} else {
						jumpedLastFrameHigh = false;
						extrajump = -(0.1D - (SpeedLevel * 0.1D));
					}
				} else if (!java.lang.Double.isNaN(depth) && SubEnabled
						&& (depth > 0.0D)) {
					lastDive = System.currentTimeMillis();
					if (!isDiving) {
						isDiving = true;
					}
				} else {
					if (isDiving) {
						isDiving = false;
					}
					if ((currentheight < 0.5D) && java.lang.Double.isNaN(depth)) {
						motionY = 0.4D;
					}
				}
				jumpedLastFrame = true;
			} else {
				if (CurrentMode == 2) {
					SlowDecent = true;
				}
				isDiving = false;
				jumpedLastFrame = false;
			}
			if (SlowDecent && (currentheight > (hoverheight + 2D))
					&& (extrajump == 0.0D)) {
				extrajump = -(0.4D - (SpeedLevel * 0.4D));
			}
			if (!jumpedLastFrame && jumpedLastFrameHigh) {
				jumpedLastFrameHigh = false;
			}
			CheckKeys();
		} else {
			CurrentSpeed = Math.sqrt((motionX * motionX) + (motionZ * motionZ));
			SpeedLevel = CurrentSpeed / MaxSpeed;
			isDiving = false;
			lastDive = 0L;
			CurrentMode = mod_Hoverboat.Instance.settingMultiBoatMode.get();
			hoverheight = mod_Hoverboat.Instance.settingFloatMinHover.get();
			motionX *= 0.8D;
			motionZ *= 0.8D;
		}
		double wobble = (Math
				.sin((System.currentTimeMillis() % 360000) / 200D) - 1.0D) * 0.0078125D;
		if (CurrentMode != 0) {
			double working = 0.0D;
			if (currentheight < hoverheight) {
				working = 0.2D * ((hoverheight - currentheight) * 0.8D);
			}
			if ((Math.abs(working * 100000D) > 0.0D)
					|| (Math.abs(extrajump * 100000D) > 0.0D)) {
				motionY = working + extrajump;
				if (Math.abs(motionY) < 0.1D) {
					motionY += wobble;
				}
			} else {
				motionY -= 0.04D;
			}
		} else if (!java.lang.Double.isNaN(depth) && (depth > 0.0D)) {
			if (depth < 0.0D) {
				lastDive = 0L;
			}
			double limiter = (lastDive + 2000L) <= System.currentTimeMillis() ? 0.09D
					: 0.013D;
			floatSpeed = isDiving ? -depth : depth;
			floatSpeed *= 0.15D;
			if (floatSpeed > limiter) {
				floatSpeed = limiter;
			}
			if (floatSpeed < -limiter) {
				floatSpeed = -limiter;
			}
			motionY += floatSpeed;
			if (Math.abs(depth) < 0.2D) {
				motionY += wobble / 8D;
			}
		} else {
			motionY -= java.lang.Double.isNaN(depth) ? 0.04D
					: 0.005D;
		}
		if (MaxSpeed < CurrentSpeed) {
			double speedmult = MaxSpeed / CurrentSpeed;
			motionX *= speedmult;
			motionZ *= speedmult;
		}

		if ((CurrentSpeed < (double) mod_Hoverboat.Instance.settingFloatBrakeThreshold
				.get())
				&& (0.0F > PlayerFB)
				&& (KeyboardSteering != 3)
				&& (KeyboardSteering != 2)) {
			motionX = motionZ = 0.0D;
			if (Float.isNaN(oldRotation)) {
				oldRotation = rotationYaw;
			}
		} else {
			oldRotation = (0.0F / 0.0F);
		}
		if ((posY > 126D) && (motionY > 0.0D)) {
			motionY = 0.0D;
		}
		if (!IsParked) {
			moveEntity(motionX, motionY, motionZ);
		} else {
			motionX = motionY = motionZ = 0.0D;
		}
		if (!Float.isNaN(oldRotation)) {
			rotationYaw = oldRotation;
		}

		motionX *= mod_Hoverboat.Instance.settingFloatMaxFriction.get();
		motionY *= 0.95D;
		motionZ *= mod_Hoverboat.Instance.settingFloatMaxFriction.get();
		rotationPitch = 0.0F;

		double Rotation = rotationYaw;
		double XChange = prevPosX - posX;
		double ZChange = prevPosZ - posZ;
		if ((CurrentSpeed > 0.001D)
				&& (((KeyboardSteering != 3) && (KeyboardSteering != 2)) || (riddenByEntity == null))) {
			XChange *= 1000D;
			ZChange *= 1000D;
			if ((XChange != 0.0D) && (ZChange != 0.0D)) {
				Rotation = Math.atan2(ZChange, XChange) * 57.295779513082323D;
			}
		}
		Rotation = Hoverboat.NormalizeDegree(Rotation - rotationYaw);
		if ((Rotation == 0.0D) && !IsParked) {
			Rotation = -rotate;
		}
		double limiter = 10D;
		if (Rotation < -limiter) {
			Rotation = -limiter;
		}
		if (Rotation > limiter) {
			Rotation = limiter;
		}
		float lean = 0.0F;
		if (Rotation < 0.0D) {
			lean = (float) (-Math.pow(Math.pow(-Rotation * 20D, SpeedLevel),
					SpeedLevel));
		} else {
			lean = (float) Math.pow(Math.pow(Rotation * 20D, SpeedLevel),
					SpeedLevel);
		}
		float LeanBias = IsParked ? 0.0F : lean;
		LeanBias = (Lean - LeanBias) * 0.1F;
		Lean -= LeanBias;
		float leanclamp = 20F;
		if (Lean > leanclamp) {
			Lean = leanclamp;
		}
		if (Lean < -leanclamp) {
			Lean = -leanclamp;
		}
		rotationYaw += Rotation;
		float temp = (float) Hoverboat.NormalizeDegree(rotationYaw);
		if (temp > rotationYaw) {
			prevRotationYaw += 360;
		}
		if (temp < rotationYaw) {
			prevRotationYaw -= 360;
		}
		rotationYaw = temp;
		Rotation = -(rotationYaw * 0.017453292519943295D);
		java.awt.geom.Point2D.Double double1 = new java.awt.geom.Point2D.Double();
		double sinrot = Math.sin(Rotation);
		double cosrot = Math.cos(Rotation);
		for (int i = 0; i < ((1.0D + (SpeedLevel * (java.lang.Double
				.isNaN(depth) ? 10D : -1D))) * (double) mod_Hoverboat.Instance.settingFloatParticles
				.get()); i++) {
			Point2D point2d3 = Hoverboat.RotatePoint(
					new java.awt.geom.Point2D.Double(
							(rand.nextDouble() - 0.5D) * 1.3D, (rand
									.nextDouble() - 0.5D) * 1.3D), double1,
					sinrot, cosrot);
			String s = (java.lang.Double.isNaN(depth) || isDivingInLava ? (rand
					.nextBoolean() && (SpeedLevel > 0.98) ? "explode" : "smoke")
					: "splash");
			worldObj.spawnParticle(s, prevPosX + point2d3.getX(),
					prevPosY - 0.3D, prevPosZ + point2d3.getY(),
					point2d3.getX() * 0.02D * (1.0D + SpeedLevel),
					!isDivingInLava && (currentheight >= 0.5D) ? -0.04D
							* hoverheight : 0.01D, point2d3.getY() * 0.02D
							* (1.0D + SpeedLevel));
		}

		if ((riddenByEntity != null) && riddenByEntity.isDead) {
			riddenByEntity = null;
		}
	}

	@Override
	public void performHurtAnimation() {
		RockDirection = -RockDirection;
		TimeSinceHit = 10;
		CurrentDamage += CurrentDamage * 10;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
		inventory = new ItemStack[54];
		for (int i = 0; i < 54; i++) {
			try {
				NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist
						.tagAt(i);
				int j = nbttagcompound1.getByte("Slot") & 0xff;
				if ((j >= 0) && (j < inventory.length)) {
					inventory[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
				}
			} catch (Throwable throwable) {
			}
		}

		IsParked = nbttagcompound.getBoolean("Parked");
		currentItems = nbttagcompound.getInteger("NumItems");

		SelectedArrow = mod_Hoverboat.Instance
				.GetProjectileFromString(nbttagcompound
						.getString("SelectedProj"));
		if (SelectedArrow == null) {
			SelectedArrow = mod_Hoverboat.Instance.GetDefaultProjectile();
		}

		SelectedDrop = mod_Hoverboat.Instance.GetDropFromString(nbttagcompound
				.getString("SelectedDrop"));
		if (SelectedDrop == null) {
			SelectedDrop = mod_Hoverboat.Instance.GetDefaultDrop();
		}

		CurrentMode = nbttagcompound.getInteger("Mode");
		KeyboardSteering = nbttagcompound.getInteger("Steering");
		CompressChestIfNeeded();
	}

	private double RecalcHeight() {
		int i = 1;
		int j = MathHelper.floor_double(posX);
		int k = MathHelper.floor_double(posY);
		for (int l = MathHelper.floor_double(posZ); IgnoreBlock(j, k - i, l)
				&& ((k - i) >= 1) && (i < 20); i++) {
		}
		return posY - ((k - i) + 1);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory[i] = itemstack;
		if ((itemstack != null)
				&& (itemstack.stackSize > getInventoryStackLimit())) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	private void UpdateKeys() {
		if (riddenByEntity instanceof EntityPlayerSP) {
			MovementInput movementinput = ((EntityPlayerSP) riddenByEntity).movementInput;
			PlayerJumping = movementinput.jump;
			PlayerLR = movementinput.moveStrafe;
			PlayerFB = movementinput.moveForward;
			if (movementinput.sneak) {
				PlayerFB *= (10D / 3D);
				PlayerLR *= (10D / 3D);
			}
		} else {
			PlayerJumping = false;
			PlayerLR = 0.0F;
			PlayerFB = 0.0F;
		}
	}

	@Override
	public void updateRiderPosition() {
		if (isImmuneToFire) {
			func_40045_B();
		}
		if (riddenByEntity == null) {
			return;
		}
		if (isImmuneToFire) {
			riddenByEntity.func_40045_B();
		}
		double shift = mod_Hoverboat.Instance.settingFloatViewShift.get();
		double x = 0.0D;
		double z = 0.0D;
		if (shift != 0.0D) {
			x = Math.cos((rotationYaw * 3.1415926535897931D) / 180D) * shift;
			z = Math.sin((rotationYaw * 3.1415926535897931D) / 180D) * shift;
		}
		riddenByEntity.setPosition(posX + x, posY + getMountedYOffset()
				+ riddenByEntity.getYOffset(), posZ + z);
	}

	public boolean UseOneItem(int i) {
		int j = GetFirstSlotOfItem(i);
		if (j < 0) {
			return false;
		}
		if (--inventory[j].stackSize <= 0) {
			inventory[j] = null;
		}
		return true;
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); i++) {
			if (inventory[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				inventory[i].writeToNBT(nbttagcompound1);
				nbttaglist.setTag(nbttagcompound1);
			}
		}

		nbttagcompound.setTag("Items", nbttaglist);
		nbttagcompound.setBoolean("Parked", IsParked);
		nbttagcompound.setInteger("NumItems", getSizeInventory());
		nbttagcompound.setString("SelectedProj", SelectedArrow.toString());
		nbttagcompound.setString("SelectedDrop", SelectedDrop.toString());
		nbttagcompound.setInteger("Mode", CurrentMode);
		nbttagcompound.setInteger("Steering", KeyboardSteering);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (isCollided) {
			return false;
		}
		return entityplayer.getDistanceToEntity(this) <= 64D;
	}
}
