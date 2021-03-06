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

import net.minecraft.util.AxisAlignedBB;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.config.Defaults;
import forestry.core.genetics.Allele;
import forestry.core.genetics.EffectData;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Vect;
import forestry.plugins.PluginForestryApiculture;

public abstract class AlleleEffectThrottled extends Allele implements IAlleleBeeEffect {

	private String name;
	private boolean isCombinable = false;
	private int throttle;
	
	public AlleleEffectThrottled(String uid, String name, boolean isDominant, int throttle, boolean isCombinable) {
		super(uid, isDominant);
		this.name = "apiculture.effect." + name;
		this.throttle = throttle;
		this.isCombinable = isCombinable;
	}

	@Override
	public String getIdentifier() {
		return StringUtil.localize(name);
	}

	public int getThrottle() {
		return throttle;
	}
	
	@Override
	public boolean isCombinable() {
		return isCombinable;
	}

	@Override
	public IEffectData validateStorage(IEffectData storedData) {
		if (storedData instanceof EffectData)
			return storedData;

		return new EffectData(1, 0);
	}

	public boolean isThrottled(IEffectData storedData) {

		int throttle = storedData.getInteger(0);
		throttle++;
		storedData.setInteger(0, throttle);

		if (throttle < getThrottle())
			return true;

		// Reset since we are done throttling.
		storedData.setInteger(0, 0);
		return false;
	}

	@Override
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		int[] area = getModifiedArea(genome, housing);

		PluginForestryApiculture.proxy.addBeeHiveFX(Defaults.TEXTURE_PARTICLES_BEE, housing.getWorld(), housing.getXCoord(), housing.getYCoord(),
				housing.getZCoord(), genome.getPrimaryAsBee().getPrimaryColor(), area[0], area[1], area[2]);
		return storedData;
	}

	@Override
	public String getIconTextureFile() {
		return null;
	}

	@Override
	public int getIconIndex() {
		return -1;
	}

	protected int[] getModifiedArea(IBeeGenome genome, IBeeHousing housing) {
		int[] area = genome.getTerritory();
		area[0] *= housing.getTerritoryModifier(genome)*3;
		area[1] *= housing.getTerritoryModifier(genome)*3;
		area[2] *= housing.getTerritoryModifier(genome)*3;

		if (area[0] < 1) {
			area[0] = 1;
		}
		if (area[1] < 1) {
			area[1] = 1;
		}
		if (area[2] < 1) {
			area[2] = 1;
		}

		return area;
	}
	
	protected AxisAlignedBB getBounding(IBeeGenome genome, IBeeHousing housing, float modifier) {
		int[] areaAr = genome.getTerritory();
		Vect area = new Vect(areaAr[0], areaAr[1], areaAr[2]).multiply(modifier);
		Vect offset = new Vect(-Math.round(area.x / 2), -Math.round(area.y / 2), -Math.round(area.z / 2));

		// Radioactivity hurts players and mobs
		Vect min = new Vect(housing.getXCoord() + offset.x, housing.getYCoord() + offset.y, housing.getZCoord() + offset.z);
		Vect max = new Vect(housing.getXCoord() + offset.x + area.x, housing.getYCoord() + offset.y + area.y, housing.getZCoord() + offset.z + area.z);

		return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(min.x, min.y, min.z, max.x, max.y, max.z);
	}
}
