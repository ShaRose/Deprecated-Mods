package net.minecraft.src;

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

public class HoverboatProjectileTNT extends HoverboatProjectileType {

	@SuppressWarnings("rawtypes")
	public HoverboatProjectileTNT(Class class1, int i, String s) {
		ClassType = class1;
		name = s;
		ItemID = i;
		throwable = false;
	}

	@Override
	protected Entity CreateItem(World world, EntityLiving entityliving,
			double d, double d1, double d2) throws Throwable {
		EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, d, d1, d2);
		ModLoader.setPrivateValue(EntityTNTPrimed.class, entitytntprimed,
				(mod_Hoverboat.isMCP ? "fuse" : "a"), Integer
						.valueOf(mod_Hoverboat.Instance.settingIntTntTicks
								.get()));
		return entitytntprimed;
	}

	@Override
	protected Entity ThrowItem(World world, EntityLiving entityliving,
			double d, double d1, double d2) throws Throwable {
		return CreateItem(world, entityliving, d, d1, d2);
	}
}
