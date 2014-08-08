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

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.utils.Utils;
import forestry.core.utils.Vect;

public class FarmableGenericCrop implements IFarmable {

	private ItemStack seed;
	private int blockid;
	private int mature;
	
	public FarmableGenericCrop(ItemStack seed, int blockid, int mature) {
		this.seed = seed;
		this.blockid = blockid;
		this.mature = mature;
	}
	
	@Override
	public boolean isSaplingAt(World world, int x, int y, int z) {
		
		if(world.isAirBlock(x, y, z))
			return false;

		if(world.getBlockId(x, y, z) != blockid)
			return false;
		else
			return true;
	}

	@Override
	public ICrop getCropAt(World world, int x, int y, int z) {
		if(world.getBlockId(x, y, z) != blockid)
			return null;
		if(world.getBlockMetadata(x, y, z) != mature) {
			return null;
		}
		
		return new CropBlock(world, blockid, mature, new Vect(x, y, z));
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		if(seed.itemID != itemstack.itemID)
			return false;
		
		if(seed.getItemDamage() >= 0)
			return seed.getItemDamage() == itemstack.getItemDamage();
		else
			return true;
	}

	@Override
	public boolean plantSaplingAt(ItemStack germling, World world, int x, int y, int z) {
		return germling.copy().tryPlaceItemIntoWorld(Utils.getForestryPlayer(world, x, y, z), world, x, y - 1, z, 1, 0, 0, 0);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

}
