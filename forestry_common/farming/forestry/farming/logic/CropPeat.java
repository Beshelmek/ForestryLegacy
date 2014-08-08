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
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Vect;

public class CropPeat extends Crop {

	public CropPeat(World world, Vect position) {
		super(world, position);
	}

	@Override
	protected boolean isCrop(Vect pos) {
		return getBlockId(pos) == ForestryBlock.soil.blockID && (getBlockMeta(pos) & 0x03) == 1;
	}

	@Override
	protected Collection<ItemStack> harvestBlock(Vect pos) {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		list.add(new ItemStack(ForestryItem.peat));
		
		Proxies.common.addBlockDestroyEffects(world, pos.x, pos.y, pos.z, world.getBlockId(pos.x, pos.y, pos.z), 0);
		setBlock(pos, Block.dirt.blockID, 0);
		return list;
	}

}
