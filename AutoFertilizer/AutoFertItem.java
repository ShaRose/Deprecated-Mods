package net.minecraft.src;

public class AutoFertItem extends Item {

	static int fertboaticon = ModLoader.addOverride("/gui/items.png",
			"/gui/autofertboat.png");
	static int fertcarticon = ModLoader.addOverride("/gui/items.png",
			"/gui/autofertcart.png");

	public AutoFertItem(int i) {
		super(i);
		maxStackSize = 1;
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public int getIconFromDamage(int i) {
		switch (i) {
		case 1:
			return AutoFertItem.fertcarticon;
		default:
		case 0:
			return AutoFertItem.fertboaticon;
		}
	}

	@Override
	public String getItemNameIS(ItemStack itemstack) {
		switch (itemstack.getItemDamage()) {
		case 1:
			return "item.afertcart";
		default:
		case 0:
			return "item.afertboat";
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world,
			EntityPlayer entityplayer) {
		float f = 1.0F;
		float f1 = entityplayer.prevRotationPitch
				+ ((entityplayer.rotationPitch - entityplayer.prevRotationPitch) * f);
		float f2 = entityplayer.prevRotationYaw
				+ ((entityplayer.rotationYaw - entityplayer.prevRotationYaw) * f);
		double d = entityplayer.prevPosX
				+ ((entityplayer.posX - entityplayer.prevPosX) * f);
		double d1 = (entityplayer.prevPosY
				+ ((entityplayer.posY - entityplayer.prevPosY) * f) + 1.6200000000000001D)
				- entityplayer.yOffset;
		double d2 = entityplayer.prevPosZ
				+ ((entityplayer.posZ - entityplayer.prevPosZ) * f);
		Vec3D vec3d = null;
		vec3d = Vec3D.createVector(d, d1, d2);
		float f3 = MathHelper.cos((-f2 * 0.01745329F) - 3.141593F);
		float f4 = MathHelper.sin((-f2 * 0.01745329F) - 3.141593F);
		float f5 = -MathHelper.cos(-f1 * 0.01745329F);
		float f6 = MathHelper.sin(-f1 * 0.01745329F);
		float f7 = f4 * f5;
		float f8 = f6;
		float f9 = f3 * f5;
		double d3 = 5D;
		Vec3D vec3d1 = vec3d.addVector(f7 * d3, f8 * d3, f9 * d3);
		MovingObjectPosition movingobjectposition = world.rayTraceBlocks_do(
				vec3d, vec3d1, true);
		if (movingobjectposition == null) {
			return itemstack;
		}
		if (movingobjectposition.typeOfHit == EnumMovingObjectType.TILE) {
			int i = movingobjectposition.blockX;
			int j = movingobjectposition.blockY;
			int k = movingobjectposition.blockZ;

			if (!world.multiplayerWorld) {
				switch (itemstack.getItemDamage()) {
				case 1: {
					world.entityJoinedWorld(new AutoFertCartEntity(world,
							i + 0.5F, j + 1.5F, k + 0.5F));
					break;
				}

				default:
				case 0: {
					world.entityJoinedWorld(new AutoFertBoatEntity(world,
							i + 0.5F, j + 1.5F, k + 0.5F));
					break;
				}
				}
			}
			itemstack.stackSize--;
		}
		return itemstack;
	}

}
