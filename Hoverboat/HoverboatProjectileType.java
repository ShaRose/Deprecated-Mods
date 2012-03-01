package net.minecraft.src;

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.awt.geom.Point2D;
import java.util.HashMap;

public abstract class HoverboatProjectileType implements
		Comparable<HoverboatProjectileType> {

	protected static HashMap<Class<?>, String> _NameSpecialExceptions;

	static {
		HoverboatProjectileType._NameSpecialExceptions = new HashMap<Class<?>, String>();
		HoverboatProjectileType._NameSpecialExceptions.put(EntityEgg.class,
				"Egg");
		HoverboatProjectileType._NameSpecialExceptions.put(
				EntityFireball.class, "Fireball");
	}

	public boolean AllowForDrops;

	public boolean AllowForShoot;

	protected Class<?> ClassType;

	protected int ItemID;

	protected String name;

	public SettingBoolean settingBoolDropEnabled;

	public SettingBoolean settingBoolProjectileEnabled;

	protected boolean throwable;

	protected boolean usesCustomSounds = false;
	
	protected boolean overridesPosAngs = false;
	
	protected double addMotion = 0; 

	protected HoverboatProjectileType() {
		throwable = false;
		name = "";
		AllowForDrops = true;
		AllowForShoot = true;
	}

	@Override
	public int compareTo(HoverboatProjectileType other) {
		return toString().compareTo(other.toString());
	}

	protected abstract Entity CreateItem(World world,
			EntityLiving entityliving, double d, double d1, double d2)
			throws Throwable;

	public Entity DropItem(World world, EntityLiving entityliving, double d,
			double d1, double d2, float f) {
		try {
			Entity entity = CreateItem(world, entityliving, d, d1, d2);
			if (entity == null) {
				return null;
			}
			if(!overridesPosAngs)
			{
			entity.setPosition(d, d1, d2);
			entity.rotationYaw = f;
			}
			world.entityJoinedWorld(entity);
			if (entity.riddenByEntity != null) {
				world.entityJoinedWorld(entity.riddenByEntity);
			}

			if (usesCustomSounds) {
				PlayCustomSound(true, entity);
			}

			if (entity.motionY == 0D) {
				entity.motionY = -0.01D;
			}
			if (addMotion > 0)
			{
				Entity movingEnt = entityliving.ridingEntity;
				if(movingEnt == null)
					movingEnt = entityliving;
				entity.motionX += movingEnt.motionX * addMotion;
				entity.motionZ += movingEnt.motionZ * addMotion;
			}
			return entity;
		} catch (Throwable throwable1) {
			return null;
		}
	}

	public Entity FireItem(World world, EntityLiving entityliving, double d,
			double d1, double d2, float f, float f1) {
		try {
			Entity entity = throwable ? ThrowItem(world, entityliving, d, d1,
					d2) : CreateItem(world, entityliving, d, d1, d2);
			if(!overridesPosAngs)
			{
			entity.setPosition(d, d1, d2);
			}
			double d3 = 1.0D;
			if (!throwable) {
				// ADD THE RANDOM ACCURACY CRAP
				Point2D point2d = Hoverboat.RotatePoint(
						new java.awt.geom.Point2D.Double(0.0D, 1.0D),
						new java.awt.geom.Point2D.Double(),
						Hoverboat.Degreetonormalizedradian(f1));
				d3 = mod_Hoverboat.Instance.settingFloatTNTVel.get();
				entity.motionY = point2d.getX();
				point2d = Hoverboat.RotatePoint(
						new java.awt.geom.Point2D.Double(0.0D, point2d.getY()),
						new java.awt.geom.Point2D.Double(),
						Hoverboat.Degreetonormalizedradian(f));
				entity.motionX = point2d.getX();
				entity.motionZ = point2d.getY();
				entity.setRotation(f, f1);
				entity.prevRotationYaw = f;
				entity.prevRotationPitch = f1;
				if (usesCustomSounds) {
					PlayCustomSound(false, entity);
				} else {
					world.playSoundAtEntity(entityliving, "random.explode",
							1.0F,
							1.5F + ((world.rand.nextFloat() - 0.5F) * 0.4F));
				}
			} else {
				d3 = mod_Hoverboat.Instance.settingFloatArrowVel.get();
				if (usesCustomSounds) {
					PlayCustomSound(false, entity);
				} else {
					world.playSoundAtEntity(entityliving, "random.bow", 0.3F,
							1.0F / ((world.rand.nextFloat() * 0.6F) + 0.7F));
				}
			}
			entity.motionX *= d3;
			entity.motionY *= d3;
			entity.motionZ *= d3;
			world.entityJoinedWorld(entity);
			if (entity.riddenByEntity != null) {
				world.entityJoinedWorld(entity.riddenByEntity);
			}
			return entity;
		} catch (Throwable throwable1) {
			return null;
		}
	}

	public String GetName() {
		if (name != "") {
			return name;
		}
		try {
			if (HoverboatProjectileType._NameSpecialExceptions
					.containsKey(ClassType)) {
				name = HoverboatProjectileType._NameSpecialExceptions
						.get(ClassType);
				return name;
			}
			if ((Entity.class).isAssignableFrom(ClassType)) {
				name = EntityList
						.getEntityString((Entity) ClassType
								.getConstructor(new Class[] { World.class })
								.newInstance(
										new Object[] { ModLoader
												.getMinecraftInstance().theWorld }));
				return name;
			}
		} catch (Exception exception) {
			exception.equals(null);
		}
		return name = (new StringBuilder("NO NAME FOUND ("))
				.append(ClassType.toString()).append(")").toString();
	}

	public void MakeDefaultArrow() {
		mod_Hoverboat.Instance.SetDefaultArrow(this);
	}

	public void MakeDefaultDrop() {
		mod_Hoverboat.Instance.SetDefaultDrop(this);
	}

	public void PlayCustomSound(boolean IsDrop, Entity ent) {
		
	}

	protected abstract Entity ThrowItem(World world, EntityLiving entityliving,
			double d, double d1, double d2) throws Throwable;

	@Override
	public String toString() {
		return GetName();
	}

	public boolean UseItem(Hoverboat hoverboat) {
		if (ItemID == -1) {
			return false;
		} else {
			return hoverboat.UseOneItem(ItemID);
		}
	}
}
