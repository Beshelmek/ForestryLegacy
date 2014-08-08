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
package forestry.arboriculture.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import forestry.arboriculture.WoodType;
import forestry.arboriculture.gadgets.TileStairs;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.items.ItemForestryBlock;
import forestry.core.utils.StringUtil;

public class ItemStairs extends ItemForestryBlock {

	public ItemStairs(int i, String name) {
		super(i, name);
		setTextureFile(Defaults.TEXTURE_ARBORICULTURE);
	}

	@Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {

		WoodType type = WoodType.getFromCompound(stack.getTagCompound());		
		return placeStairs(world, player, type, x, y, z);
		
	}

	private boolean placeStairs(World world, EntityPlayer player, WoodType type, int x, int y, int z) {
		
		boolean placed = world.setBlockAndMetadataWithNotify(x, y, z, ForestryBlock.stairs.blockID, 0);
		if (!placed)
			return false;

		if (world.getBlockId(x, y, z) != ForestryBlock.stairs.blockID)
			return false;

		Block.blocksList[world.getBlockId(x, y, z)].onBlockPlacedBy(world, x, y, z, player);
		
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (!(tile instanceof TileStairs)) {
			world.setBlockAndMetadataWithNotify(x, y, z, 0, 0);
			return false;
		}

		((TileStairs)tile).setType(type);
		return true;
	}
	
	private String getWoodNameIS(WoodType type) {
		return StringUtil.localize("wood." + type.ordinal());
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {

		WoodType type = WoodType.getFromCompound(itemstack.getTagCompound());
		
		return getWoodNameIS(type) + " " + StringUtil.localize("tile." + name);
	}

}
