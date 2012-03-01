package net.minecraft.src;

public class AutoFertCartEntity extends EntityMinecart {

	private boolean Operating = false;

	int tickcount = 0;

	public AutoFertCartEntity(World world) {
		super(world);
	}

	public AutoFertCartEntity(World world, double d, double d1, double d2) {
		super(world);
		setPosition(d, d1 + yOffset, d2);
		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;
		prevPosX = d;
		prevPosY = d1;
		prevPosZ = d2;
		minecartType = 255;
	}

	@Override
	public boolean attackEntityFrom(DamageSource damageSource, int i) {
		boolean returnvalue = super.attackEntityFrom(damageSource, i);
		if (isDead) {
			dropItemWithOffset(mod_AutoFertilizer.afertblock.blockID, 1, 0.0F);
		}
		return returnvalue;
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
	public boolean interact(EntityPlayer entityplayer) {
		Operating = !Operating;
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (Operating && CanFert()) {
			if (mod_AutoFertilizer.Fert(worldObj,
					MathHelper.floor_double(posX + 0.5),
					MathHelper.floor_double(posY + 0.5),
					MathHelper.floor_double(posZ + 0.5),
					mod_AutoFertilizer.GetFertBoatTicksPerSecond(), 0,
					(int) Math.ceil(4 + height))) {
				worldObj.spawnParticle("splash", prevPosX
						+ ((rand.nextDouble() - 0.5D) * 0.4), prevPosY + 0.7D,
						prevPosZ + ((rand.nextDouble() - 0.5D) * 0.4),
						(rand.nextDouble() - 0.5D) * 20.0D, 20D,
						(rand.nextDouble() - 0.5D) * 20.0D);
			}
		}
	}
}
