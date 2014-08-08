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

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;

/**
 * Hermits will not produce if there are any other living creatures nearby. 
 */
public class JubilanceProviderHermit extends JubilanceDefault {

	@Override
	public boolean isJubilant(IAlleleBeeSpecies species, IBeeGenome genome, IBeeHousing housing) {
		AxisAlignedBB bounding = this.getBounding(genome, housing, 1.0f);
		
		List list = housing.getWorld().getEntitiesWithinAABB(EntityLiving.class, bounding);
		if(list.size() > 0)
			return false;

		return true;
	}
	
}
