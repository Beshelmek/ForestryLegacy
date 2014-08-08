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
package forestry.farming.logic;

import java.util.Collection;
import java.util.Stack;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import forestry.api.farming.Farmables;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.config.Defaults;
import forestry.core.utils.Vect;

public class FarmLogicGourd extends FarmLogic {

	private IFarmable[] seeds;

	public FarmLogicGourd(IFarmHousing housing) {
		super(housing);
		seeds = Farmables.farmables.get("farmGourd").toArray(new IFarmable[0]);
	}

	@Override
	public int getIconIndex() {
		return Item.melon.getIconFromDamage(0);
	}

	@Override
	public String getTextureFile() {
		return Defaults.TEXTURE_ICONS_MINECRAFT;
	}

	@Override
	public String getName() {
		return "Gourd Farm";
	}

	@Override
	public int getFertilizerConsumption() {
		return 10;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int)(40 * hydrationModifier);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		return false;
	}

	@Override
	public Collection<ItemStack> collect() {
		return null;
	}

	@Override
	public boolean cultivate(int x, int y, int z, ForgeDirection direction, int extent) {
		return false;
	}

	@Override
	public Collection<ICrop> harvest(int x, int y, int z, ForgeDirection direction, int extent) {
		
		world = housing.getWorld();

		Stack<ICrop> crops = new Stack<ICrop>();
		for(int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(x, y + 1, z, direction, i);
			for(IFarmable seed : seeds) {
				ICrop crop = seed.getCropAt(world, position.x, position.y, position.z);
				if(crop != null)
					crops.push(crop);
			}			
		}
		return crops;
	}


}
