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

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;
import forestry.core.utils.Vect;

public abstract class FarmLogicHomogenous extends FarmLogic {

	protected final ItemStack[] resource;
	protected final ItemStack[] ground;
	protected final ItemStack[] waste;
	protected final IFarmable[] germlings;
	
	ArrayList<ItemStack> produce = new ArrayList<ItemStack>();
	
	public FarmLogicHomogenous(IFarmHousing housing, ItemStack[] resource, ItemStack[] ground, ItemStack[] waste, IFarmable[] germlings) {
		super(housing);
		this.resource = resource;
		this.ground = ground;
		this.waste = waste;
		this.germlings = germlings;
	}


	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return resource[0].isItemEqual(itemstack);
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		for(IFarmable germling : germlings)
			if(germling.isGermling(itemstack))
				return true;
		return false;
	}

	public boolean isWindfall(ItemStack itemstack) {
		for(IFarmable germling : germlings)
			if(germling.isWindfall(itemstack))
				return true;
		return false;
	}
	
	@Override
	public boolean cultivate(int x, int y, int z, ForgeDirection direction, int extent) {
		world = housing.getWorld();

		if(maintainSoil(x, y, z, direction, extent))
			return true;
		
		if(maintainGermlings(x, y + 1, z, direction, extent))
			return true;
		
		return false;
	}

	private boolean maintainSoil(int x, int yGround, int z, ForgeDirection direction, int extent) {

		if(!housing.hasResources(resource))
			return false;
		
		cycle: for(int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(x, yGround, z, direction, i);

			if(!isAirBlock(position)
					&& !Utils.isReplaceableBlock(world, position.x, position.y, position.z)) {
			
				ItemStack block = getAsItemStack(position);

				for(ItemStack grnd : ground) {
					if(StackUtils.isIdenticalItem(grnd, block)) {
						continue cycle;
					}
				}
			
				if(waste.length > 0 && waste[0].isItemEqual(block))
					produce.add(waste[0].copy());
				else
					return false;
			}

			setBlock(position, ground[0].itemID, ground[0].getItemDamage());
			housing.removeResources(resource);
			return true;
			
		}
		
		return false;
	}
	
	protected abstract boolean maintainGermlings(int x, int ySaplings, int z, ForgeDirection direction, int extent);
}
