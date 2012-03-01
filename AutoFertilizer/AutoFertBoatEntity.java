package net.minecraft.src;

import java.awt.geom.Point2D;

public class AutoFertBoatEntity extends Entity {

	public static double Degreetonormalizedradian(double d) {
		return AutoFertBoatEntity.NormalizeDegree(d) * 0.017453292519943295D;
	}

	public static double NormalizeDegree(double d) {
		for (; d > 180D; d -= 360D) {
			mod_AutoFertilizer.BitGetOn(0);
		}
		for (; d < -180D; d += 360D) {
			mod_AutoFertilizer.BitGetOn(0);
		}
		if ((d == -180D) || (d == 180D)) {
			d = 0.0D;
		}
		return d;
	}

	public static Point2D RotatePoint(Point2D point2d, Point2D point2d1,
			double d) {
		return AutoFertBoatEntity.RotatePoint(point2d, point2d1, Math.sin(d),
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

	public int CurrentDamage;

	public boolean IsSpeedToggleOn = false;

	private boolean LastToggleState = false;

	public int RockDirection = 1;

	int tickcount = 0;

	public int TimeSinceHit;

	public AutoFertBoatEntity(World world) {
		super(world);
		preventEntitySpawning = true;
		setSize(1.5F, 0.6F);
		yOffset = (height / 2.0F);
	}

	public AutoFertBoatEntity(World world, double d, double d1, double d2) {
		this(world);
		setPosition(d, d1 + yOffset, d2);
		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;
		prevPosX = d;
		prevPosY = d1;
		prevPosZ = d2;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, int damage) {
		if (isDead) {
			return true;
		}
		RockDirection = -RockDirection;
		TimeSinceHit = 10;
		CurrentDamage += damage * 10;
		setBeenAttacked();
		if (CurrentDamage > 40) {
			dropItemWithOffset(mod_AutoFertilizer.afertitem.shiftedIndex, 1,
					0.0F);
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
		return false;
	}

	protected boolean CanFert() {

		int ticks = mod_AutoFertilizer.GetFertBoatTicksPerSecond();
		if (ticks == 20) {
			return true;
		}
		tickcount++;
		if (tickcount >= (20 - ticks)) {
			tickcount = 0;
			return true;
		}
		return false;
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void fall(float var1) {
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return boundingBox;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity) {
		return entity.boundingBox;
	}

	@Override
	public double getMountedYOffset() {
		return (height * 0.0D) - 0.30000001192092901D;
	}

	@Override
	public float getShadowSize() {
		return 0.0F;
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
			boundingBox.maxY = boundingBox.minY + 0.6D;
			entityplayer.setPosition(posX, boundingBox.maxY
					+ entityplayer.height, posZ);
		} else {
			boundingBox.maxY = boundingBox.minY + calculated;
			entityplayer.rotationYaw = rotationYaw + 90F;
		}
		return true;
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
		double currentheight = RecalcHeight();
		double CurrentSpeed = 0.0D;
		double maxspeed = mod_AutoFertilizer.GetFertBoatSpeedWorking();
		boolean fert = true;
		double rotate = 0;
		if ((riddenByEntity != null)
				&& (riddenByEntity instanceof EntityPlayerSP)) {
			MovementInput movementinput = ((EntityPlayerSP) riddenByEntity).movementInput;
			float PlayerLR = movementinput.moveStrafe;
			float PlayerFB = movementinput.moveForward;
			if (movementinput.sneak) {
				PlayerFB *= (10D / 3D);
				PlayerLR *= (10D / 3D);
			}
			int mult = 4;
			if (movementinput.jump && !LastToggleState) {
				IsSpeedToggleOn = !IsSpeedToggleOn;
				LastToggleState = true;
			}
			if (!movementinput.jump && LastToggleState) {
				LastToggleState = false;
			}
			if (IsSpeedToggleOn) {
				mult = 8;
				maxspeed = mod_AutoFertilizer.GetFertBoatSpeedBoost();
			}
			rotate = AutoFertBoatEntity.NormalizeDegree(rotationYaw
					- (riddenByEntity.rotationYaw - 90D))
					* (movementinput.jump ? 0.04 : 0.1);

			if ((rotate < 0.5) && (rotate > 0)) {
				rotate = 0;
			}
			if ((rotate > -0.5) && (rotate < 0)) {
				rotate = 0;
			}
			Point2D point2d3 = AutoFertBoatEntity.RotatePoint(
					new java.awt.geom.Point2D.Double(motionX, motionZ),
					new java.awt.geom.Point2D.Double(),
					AutoFertBoatEntity.Degreetonormalizedradian(-rotate));
			motionX = point2d3.getX();
			motionZ = point2d3.getY();
			if (PlayerFB != 0.0F) {
				Point2D point2d4 = AutoFertBoatEntity.RotatePoint(
						new java.awt.geom.Point2D.Double(-(PlayerFB * 0.01D),
								0.0D), new java.awt.geom.Point2D.Double(),
						AutoFertBoatEntity.Degreetonormalizedradian(rotationYaw
								- rotate));
				motionX += point2d4.getX() * mult;
				motionZ += point2d4.getY() * mult;
			}
			if (PlayerLR != 0.0F) {
				Point2D point2d5 = AutoFertBoatEntity.RotatePoint(
						new java.awt.geom.Point2D.Double(-(PlayerLR * 0.01D),
								0.0D), new java.awt.geom.Point2D.Double(),
						AutoFertBoatEntity.Degreetonormalizedradian(rotationYaw
								- rotate - 90D));
				motionX += point2d5.getX() * mult;
				motionZ += point2d5.getY() * mult;
			}
		}
		fert = !IsSpeedToggleOn;
		CurrentSpeed = Math.sqrt((motionX * motionX) + (motionZ * motionZ));

		double wobble = (Math
				.sin((System.currentTimeMillis() % 0x57e40L) / 200D) - 1.0D) * 0.0078125D;
		double working = 0.0D;
		double height = mod_AutoFertilizer.GetFertBoatHoverHeight() + 0.5D;
		if (currentheight < height) {
			working = 0.20000000000000001D * ((height - currentheight) * 0.80000000000000004D);
		}
		if (Math.abs(working * 100000D) > 0.0D) {
			motionY = working;
			if (Math.abs(motionY) < 0.10000000000000001D) {
				motionY += wobble;
			}
		} else {
			motionY -= 0.040000000000000001D;
		}

		if (maxspeed < CurrentSpeed) {
			double speedmult = maxspeed / CurrentSpeed;
			motionX *= speedmult;
			motionZ *= speedmult;
		}

		moveEntity(motionX, motionY, motionZ);

		motionX *= 0.85D;
		motionY *= 0.95D;
		motionZ *= 0.85D;
		rotationPitch = 0.0F;

		double Rotation = rotationYaw;
		double XChange = prevPosX - posX;
		double ZChange = prevPosZ - posZ;
		if ((CurrentSpeed > 0.001D) && (riddenByEntity == null)) {
			XChange *= 1000D;
			ZChange *= 1000D;
			if ((XChange != 0.0D) && (ZChange != 0.0D)) {
				Rotation = Math.atan2(ZChange, XChange) * 57.295779513082323D;
			}
		}
		Rotation = AutoFertBoatEntity.NormalizeDegree(Rotation - rotationYaw
				- rotate);

		double limiter = 10D;
		if (Rotation < -limiter) {
			Rotation = -limiter;
		}
		if (Rotation > limiter) {
			Rotation = limiter;
		}
		rotationYaw += (float) Rotation;
		float temp = (float) AutoFertBoatEntity.NormalizeDegree(rotationYaw);
		if (temp > rotationYaw) {
			prevRotationYaw += 360;
		}
		if (temp < rotationYaw) {
			prevRotationYaw -= 360;
		}
		rotationYaw = temp;
		Rotation = -(rotationYaw * 0.017453292519943295D);

		if (fert && CanFert()) {
			if (mod_AutoFertilizer.Fert(worldObj,
					MathHelper.floor_double(posX + 0.5),
					MathHelper.floor_double(posY + 0.5),
					MathHelper.floor_double(posZ + 0.5),
					mod_AutoFertilizer.GetFertBoatTicksPerSecond(), 0,
					(int) Math.ceil(4 + height))) {
				if (rand.nextInt(6) == 0) {
					Point2D point2d3 = AutoFertBoatEntity.RotatePoint(
							new java.awt.geom.Point2D.Double(
									(rand.nextDouble() - 0.5D) * 1.7D, (rand
											.nextDouble() - 0.5D) * 1.7D),
							new java.awt.geom.Point2D.Double(), Math
									.sin(Rotation), Math.cos(Rotation));
					worldObj.spawnParticle("explode",
							prevPosX + point2d3.getX(), prevPosY - 0.3D,
							prevPosZ + point2d3.getY(),
							point2d3.getX() * 0.25D, -0.25D,
							point2d3.getY() * 0.25D);
				}
				worldObj.spawnParticle("splash", prevPosX
						+ ((rand.nextDouble() - 0.5D) * 2), prevPosY - 0.5D,
						prevPosZ + ((rand.nextDouble() - 0.5D) * 2),
						(rand.nextDouble() - 0.5D) * 6.0D, -0.25D,
						(rand.nextDouble() - 0.5D) * 6.0D);
			}
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
	public void updateRiderPosition() {
		if (riddenByEntity == null) {
			return;
		}
		riddenByEntity
				.setPosition(
						posX
								+ (Math.cos((rotationYaw * 3.1415926535897931D) / 180D) * 0.4),
						posY + getMountedYOffset()
								+ riddenByEntity.getYOffset(),
						posZ
								+ (Math.sin((rotationYaw * 3.1415926535897931D) / 180D) * 0.4));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
	}

}
