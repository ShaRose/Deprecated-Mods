package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class AutoFertCartRender extends Render {
	protected ModelBase modelMinecart;

	public AutoFertCartRender() {
		shadowSize = 0.5F;
		modelMinecart = new ModelMinecart();
	}

	@Override
	public void doRender(Entity entitybase, double d, double d1, double d2,
			float f, float f1) {
		AutoFertCartEntity entity = (AutoFertCartEntity) entitybase;
		GL11.glPushMatrix();
		double d3 = entity.lastTickPosX
				+ ((entity.posX - entity.lastTickPosX) * f1);
		double d4 = entity.lastTickPosY
				+ ((entity.posY - entity.lastTickPosY) * f1);
		double d5 = entity.lastTickPosZ
				+ ((entity.posZ - entity.lastTickPosZ) * f1);
		double d6 = 0.30000001192092896D;
		Vec3D vec3d = entity.func_514_g(d3, d4, d5);
		float f2 = entity.prevRotationPitch
				+ ((entity.rotationPitch - entity.prevRotationPitch) * f1);
		if (vec3d != null) {
			Vec3D vec3d1 = entity.func_515_a(d3, d4, d5, d6);
			Vec3D vec3d2 = entity.func_515_a(d3, d4, d5, -d6);
			if (vec3d1 == null) {
				vec3d1 = vec3d;
			}
			if (vec3d2 == null) {
				vec3d2 = vec3d;
			}
			d += vec3d.xCoord - d3;
			d1 += ((vec3d1.yCoord + vec3d2.yCoord) / 2D) - d4;
			d2 += vec3d.zCoord - d5;
			Vec3D vec3d3 = vec3d2.addVector(-vec3d1.xCoord, -vec3d1.yCoord,
					-vec3d1.zCoord);
			if (vec3d3.lengthVector() != 0.0D) {
				vec3d3 = vec3d3.normalize();
				f = (float) ((Math.atan2(vec3d3.zCoord, vec3d3.xCoord) * 180D) / 3.1415926535897931D);
				f2 = (float) (Math.atan(vec3d3.yCoord) * 73D);
			}
		}
		GL11.glTranslatef((float) d, (float) d1, (float) d2);
		GL11.glRotatef(180F - f, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-f2, 0.0F, 0.0F, 1.0F);
		float f3 = entity.minecartTimeSinceHit - f1;
		float f4 = entity.minecartCurrentDamage - f1;
		if (f4 < 0.0F) {
			f4 = 0.0F;
		}
		if (f3 > 0.0F) {
			GL11.glRotatef(((MathHelper.sin(f3) * f3 * f4) / 10F)
					* entity.minecartRockDirection, 1.0F, 0.0F, 0.0F);
		}
		loadTexture("/terrain.png");
		float f5 = 1.2F;
		GL11.glScalef(f5, f5, f5);
		GL11.glTranslatef(0.0F, 0.3125F, 0.0F);
		GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
		(new RenderBlocks()).renderBlockOnInventory(
				mod_AutoFertilizer.afertblock, 0, 1);
		GL11.glRotatef(-90F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(0.0F, -0.3125F, 0.0F);
		GL11.glScalef(1.0F / f5, 1.0F / f5, 1.0F / f5);

		loadTexture("/item/cart.png");
		GL11.glScalef(-1F, -1F, 1.0F);
		modelMinecart
				.render(entitybase, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}
}
