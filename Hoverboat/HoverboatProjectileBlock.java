package net.minecraft.src;

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.HashMap;
import java.util.Vector;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class HoverboatProjectileBlock extends HoverboatProjectileType {

	private static Vector _BlockBlacklistHashMap;

	private static HashMap _NameBlockExceptionsHashMap;

	static {
		HoverboatProjectileBlock._NameBlockExceptionsHashMap = new HashMap();
		HoverboatProjectileBlock._BlockBlacklistHashMap = new Vector();
		HoverboatProjectileBlock._NameBlockExceptionsHashMap.put(
				Integer.valueOf(93), "Redstone Repeater");
		HoverboatProjectileBlock._NameBlockExceptionsHashMap.put(
				Integer.valueOf(70), "Stone Plate");
		HoverboatProjectileBlock._NameBlockExceptionsHashMap.put(
				Integer.valueOf(72), "Wood Plate");
		HoverboatProjectileBlock._NameBlockExceptionsHashMap.put(
				Integer.valueOf(43), "Double Slab");
		HoverboatProjectileBlock._NameBlockExceptionsHashMap.put(
				Integer.valueOf(44), "Single Slab");
		HoverboatProjectileBlock._NameBlockExceptionsHashMap.put(
				Integer.valueOf(39), "Brown Mushroom");
		HoverboatProjectileBlock._NameBlockExceptionsHashMap.put(
				Integer.valueOf(40), "Red Mushroom");
		HoverboatProjectileBlock._NameBlockExceptionsHashMap.put(
				Integer.valueOf(104), "Pumpkin Stem");
		HoverboatProjectileBlock._NameBlockExceptionsHashMap.put(
				Integer.valueOf(105), "Melon Stem");
		HoverboatProjectileBlock._BlockBlacklistHashMap
				.add(Integer.valueOf(74));
		HoverboatProjectileBlock._BlockBlacklistHashMap.add(Integer.valueOf(9));
		HoverboatProjectileBlock._BlockBlacklistHashMap
				.add(Integer.valueOf(11));
		HoverboatProjectileBlock._BlockBlacklistHashMap
				.add(Integer.valueOf(68));
		HoverboatProjectileBlock._BlockBlacklistHashMap
				.add(Integer.valueOf(94));
		HoverboatProjectileBlock._BlockBlacklistHashMap
				.add(Integer.valueOf(76));
		HoverboatProjectileBlock._BlockBlacklistHashMap
				.add(Integer.valueOf(62));
		HoverboatProjectileBlock._BlockBlacklistHashMap
				.add(Integer.valueOf(31));
		HoverboatProjectileBlock._BlockBlacklistHashMap
				.add(Integer.valueOf(32));
		HoverboatProjectileBlock._BlockBlacklistHashMap
				.add(Integer.valueOf(36));
		HoverboatProjectileBlock._BlockBlacklistHashMap
				.add(Integer.valueOf(34));
	}

	public static boolean IsAllowed(int i) {
		return !HoverboatProjectileBlock._BlockBlacklistHashMap
				.contains(Integer.valueOf(i));
	}

	public HoverboatProjectileBlock(int i) {
		ItemID = i;
		throwable = false;
		if (!HoverboatProjectileBlock._NameBlockExceptionsHashMap
				.containsKey(Integer.valueOf(i))) {
			name = (new StringBuilder("Block ")).append(
					StringTranslate.getInstance().translateNamedKey(
							Block.blocksList[i].getBlockName())).toString();
		} else {
			name = (new StringBuilder("Block "))
					.append((String) HoverboatProjectileBlock._NameBlockExceptionsHashMap
							.get(Integer.valueOf(i))).toString();
		}
	}

	@Override
	protected Entity CreateItem(World world, EntityLiving entityliving,
			double d, double d1, double d2) {
		EntityFallingSand entityfallingsand = new EntityFallingSand(world, d,
				d1, d2, ItemID);
		entityfallingsand.preventEntitySpawning = false;
		return entityfallingsand;
	}

	@Override
	protected Entity ThrowItem(World world, EntityLiving entityliving,
			double d, double d1, double d2) {
		return CreateItem(world, entityliving, d, d1, d2);
	}
}
