package net.minecraft.src;

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

public class HoverboatProjectileSpiderJockey extends HoverboatProjectileType {

	@SuppressWarnings("rawtypes")
	public HoverboatProjectileSpiderJockey(Class class1, int i, String s) {
		ClassType = class1;
		name = s;
		ItemID = i;
		throwable = false;
	}

	@Override
	protected Entity CreateItem(World world, EntityLiving entityliving,
			double d, double d1, double d2) {
		EntitySpider entityspider = new EntitySpider(world);
		entityspider.setPosition(d, d1, d2);
		EntitySkeleton entityskeleton = new EntitySkeleton(world);
		entityskeleton.setPosition(d, d1, d2);
		entityskeleton.mountEntity(entityspider);
		return entityspider;
	}

	@Override
	protected Entity ThrowItem(World world, EntityLiving entityliving,
			double d, double d1, double d2) {
		return CreateItem(world, entityliving, d, d1, d2);
	}
}
