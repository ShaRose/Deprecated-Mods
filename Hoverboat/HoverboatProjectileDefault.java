package net.minecraft.src;

import java.lang.reflect.InvocationTargetException;

public class HoverboatProjectileDefault extends HoverboatProjectileType {

	@SuppressWarnings("rawtypes")
	public HoverboatProjectileDefault(Class class1, int i) {
		ClassType = class1;
		ItemID = i;
		try {
			ClassType.getConstructor(new Class[] { World.class,
					EntityLiving.class });
			throwable = true;
		} catch (Exception exception) {
			throwable = false;
		}
		GetName();
	}

	@SuppressWarnings("rawtypes")
	public HoverboatProjectileDefault(Class class1, int i, String s) {
		ClassType = class1;
		ItemID = i;
		try {
			ClassType.getConstructor(new Class[] { World.class,
					EntityLiving.class });
			throwable = true;
		} catch (Exception exception) {
			throwable = false;
		}
		name = s;
	}

	@Override
	protected Entity CreateItem(World world, EntityLiving entityliving,
			double d, double d1, double d2) throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		Entity entity = (Entity) ClassType.getConstructor(
				new Class[] { World.class })
				.newInstance(new Object[] { world });
		return entity;
	}

	@Override
	protected Entity ThrowItem(World world, EntityLiving entityliving,
			double d, double d1, double d2) throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		Entity entity = (Entity) ClassType.getConstructor(
				new Class[] { World.class, EntityLiving.class }).newInstance(
				new Object[] { world, entityliving });
		return entity;
	}
}
