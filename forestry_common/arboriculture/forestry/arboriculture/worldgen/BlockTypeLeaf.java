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
package forestry.arboriculture.worldgen;

import net.minecraft.world.World;
import forestry.api.world.ITreeGenData;
import forestry.core.config.ForestryBlock;
import forestry.core.worldgen.BlockType;

public class BlockTypeLeaf extends BlockType {

	public BlockTypeLeaf() {
		super(ForestryBlock.leaves.blockID, 0);
	}

	@Override
	public void setBlock(World world, ITreeGenData tree, int x, int y, int z) {
		tree.setLeaves(world, x, y, z);
	}
}
