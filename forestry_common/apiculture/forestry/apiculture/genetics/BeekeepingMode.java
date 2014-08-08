/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.apiculture.genetics;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import net.minecraft.world.World;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeekeepingMode;

public class BeekeepingMode implements IBeekeepingMode {

	public static IBeekeepingMode easy = new BeekeepingMode("EASY", 2.0f, 1.0f, 1.0f, false, false);
	public static IBeekeepingMode normal = new BeekeepingMode("NORMAL", 1.0f, 1.0f, 1.0f, false, true);
	public static IBeekeepingMode hard = new BeekeepingMode("HARD", 0.75f, 1.5f, 1.0f, false, true);
	public static IBeekeepingMode hardcore = new BeekeepingMode("HARDCORE", 0.5f, 5.0f, 0.8f, true, true);
	public static IBeekeepingMode insane = new BeekeepingMode("INSANE", 0.2f, 10.0f, 0.6f, true, true);

	final Random rand;
	final String name;
	final float mutationModifier;
	final float lifespanModifier;
	final float speedModifier;
	final boolean reducesFertility;
	final int fatigueArtifical = 50;
	final boolean canFatigue;

	public BeekeepingMode(String name, float mutationModifier, float lifespanModifier, float speedModifier, boolean reducesFertility, boolean canFatigue) {
		this.rand = new Random();
		this.name = name;
		this.mutationModifier = mutationModifier;
		this.lifespanModifier = lifespanModifier;
		this.speedModifier = speedModifier;
		this.reducesFertility = reducesFertility;
		this.canFatigue = canFatigue;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public ArrayList<String> getDescription() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add("beemode." + name.toLowerCase(Locale.ENGLISH) + ".desc");
		return ret;
	}

	@Override
	public float getWearModifier() {
		return 1.0f;
	}

	@Override
	public float getMutationModifier(IBeeGenome genome, IBeeGenome mate) {
		return this.mutationModifier;
	}

	@Override
	public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate) {
		return this.lifespanModifier;
	}

	@Override
	public float getProductionModifier(IBeeGenome genome) {
		return this.speedModifier;
	}

	@Override
	public float getTerritoryModifier(IBeeGenome genome) {
		return 1.0f;
	}
	
	@Override
	public float getFloweringModifier(IBeeGenome genome) {
		return 1.0f;
	}


	@Override
	public int getFinalFertility(IBee queen, World world, int x, int y, int z) {
		int toCreate = queen.getGenome().getFertility();

		if (reducesFertility) {
			toCreate = new Random().nextInt(toCreate);
		}

		return toCreate;
	}

	@Override
	public boolean isFatigued(IBee queen) {
		if (!canFatigue)
			return false;

		if (queen.isNatural())
			return false;

		if (rand.nextInt(fatigueArtifical) < queen.getGeneration())
			return true;

		return false;
	}

	@Override
	public boolean isNaturalOffspring(IBee queen) {
		if (!queen.isNatural())
			return false;

		if (queen.isIrregularMating() && rand.nextBoolean())
			return false;

		return true;
	}

	@Override
	public boolean mayMultiplyPrincess(IBee queen) {
		return true;
	}

	@Override
	public boolean isSealed() {
		return false;
	}

	@Override
	public boolean isSelfLighted() {
		return false;
	}

	@Override
	public boolean isSunlightSimulated() {
		return false;
	}

	@Override
	public boolean isHellish() {
		return false;
	}

	
}
