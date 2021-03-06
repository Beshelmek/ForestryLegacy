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
package forestry.arboriculture.gadgets;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodType;
import forestry.core.config.Defaults;

public class BlockPlanks extends Block implements IWoodTyped {

	public BlockPlanks(int id) {
		super(id, Material.wood);
		setResistance(5.0F);
		setStepSound(soundWoodFootstep);
		setBlockName("wood");
		setRequiresSelfNotify();
		setTextureFile(Defaults.TEXTURE_ARBORICULTURE);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < 16; i++) {
			itemList.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		return meta;
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	/* PROPERTIES */
	@Override
    public float getBlockHardness(World world, int x, int y, int z) {
		return getWoodType(world.getBlockMetadata(x, y, z)).getHardness();
    }
    
	@Override
	public boolean isWood(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
		return 20;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
		return true;
	}

	@Override
	public int getFireSpreadSpeed(World world, int x, int y, int z, int metadata, ForgeDirection face) {
		return 5;
	}

	@Override
	public WoodType getWoodType(int meta) {
		return WoodType.values()[meta];
	}

}
