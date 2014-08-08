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
package forestry.farming.gadgets;

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import forestry.api.core.ITileStructure;
import forestry.api.farming.IFarmComponent;
import forestry.core.gadgets.BlockStructure.EnumStructureState;
import forestry.core.gadgets.StructureLogic;
import forestry.core.utils.Schemata;
import forestry.core.utils.Schemata.EnumStructureBlock;
import forestry.core.utils.Vect;

public class StructureLogicFarm extends StructureLogic {

	/* CONSTANTS */
	public static final String UID_FARM = "farm"; 
	public static final Schemata SCHEMATA_FARM_3x3 = new Schemata("farm3x3", 5, 6, 5,
			"FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF",
			"FFFFF", "FAAAF", "FAAAF", "FBBBF", "FAAAF", "FFFFF",
			"FFFFF", "FAAAF", "FAAAF", "FBMBF", "FAAAF", "FFFFF",
			"FFFFF", "FAAAF", "FAAAF", "FBBBF", "FAAAF", "FFFFF",
			"FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF"
			).setOffsets(-2, -3, -2);
	public static final Schemata SCHEMATA_FARM_4x3 = new Schemata("farm3x4", 6, 6, 5,
			"FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF",
			"FFFFF", "FAAAF", "FAAAF", "FBBBF", "FAAAF", "FFFFF",
			"FFFFF", "FAAAF", "FAAAF", "FBMBF", "FAAAF", "FFFFF",
			"FFFFF", "FAAAF", "FAAAF", "FBABF", "FAAAF", "FFFFF",
			"FFFFF", "FAAAF", "FAAAF", "FBBBF", "FAAAF", "FFFFF",
			"FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF"
			).setOffsets(-2, -3, -3);
	public static final Schemata SCHEMATA_FARM_4x4 = new Schemata("farm4x4", 6, 6, 6,
			"FFFFFF", "FFFFFF", "FFFFFF", "FFFFFF", "FFFFFF", "FFFFFF",
			"FFFFFF", "FAAAAF", "FAAAAF", "FBBBBF", "FAAAAF", "FFFFFF",
			"FFFFFF", "FAAAAF", "FAAAAF", "FBAMBF", "FAAAAF", "FFFFFF",
			"FFFFFF", "FAAAAF", "FAAAAF", "FBAABF", "FAAAAF", "FFFFFF",
			"FFFFFF", "FAAAAF", "FAAAAF", "FBBBBF", "FAAAAF", "FFFFFF",
			"FFFFFF", "FFFFFF", "FFFFFF", "FFFFFF", "FFFFFF", "FFFFFF"
			).setOffsets(-3, -3, -3);
	public static final Schemata SCHEMATA_FARM_5x3 = new Schemata("farm3x5", 7, 6, 5,
			"FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF",
			"FFFFF", "FAAAF", "FAAAF", "FBBBF", "FAAAF", "FFFFF",
			"FFFFF", "FAAAF", "FAAAF", "FBABF", "FAAAF", "FFFFF",
			"FFFFF", "FAAAF", "FAAAF", "FBMBF", "FAAAF", "FFFFF",
			"FFFFF", "FAAAF", "FAAAF", "FBABF", "FAAAF", "FFFFF",
			"FFFFF", "FAAAF", "FAAAF", "FBBBF", "FAAAF", "FFFFF",
			"FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF"
			).setOffsets(-3, -3, -2);
	public static final Schemata SCHEMATA_FARM_5x5 = new Schemata("farm5x5", 7, 6, 7,
			"FFFFFFF", "FFFFFFF", "FFFFFFF", "FFFFFFF", "FFFFFFF", "FFFFFFF",
			"FFFFFFF", "FAAAAAF", "FAAAAAF", "FBBBBBF", "FAAAAAF", "FFFFFFF",
			"FFFFFFF", "FAAAAAF", "FAAAAAF", "FBAAABF", "FAAAAAF", "FFFFFFF",
			"FFFFFFF", "FAAAAAF", "FAAAAAF", "FBAMABF", "FAAAAAF", "FFFFFFF",
			"FFFFFFF", "FAAAAAF", "FAAAAAF", "FBAAABF", "FAAAAAF", "FFFFFFF",
			"FFFFFFF", "FAAAAAF", "FAAAAAF", "FBBBBBF", "FAAAAAF", "FFFFFFF",
			"FFFFFFF", "FFFFFFF", "FFFFFFF", "FFFFFFF", "FFFFFFF", "FFFFFFF"
			).setOffsets(-3, -3, -3);
	
	public static HashSet<Integer> brickIds = new HashSet<Integer>();
	static {
		brickIds.add(Block.brick.blockID);
		brickIds.add(Block.stoneBrick.blockID);
		brickIds.add(Block.sandStone.blockID);
		brickIds.add(Block.netherBrick.blockID);
	}
	
	public StructureLogicFarm(ITileStructure structure) {
		super(UID_FARM, structure);
		schematas = new Schemata[] { SCHEMATA_FARM_3x3, SCHEMATA_FARM_4x3, SCHEMATA_FARM_5x3, SCHEMATA_FARM_5x5, SCHEMATA_FARM_4x4 };
		metaOnValid.put(EnumStructureBlock.BLOCK_B, 1);		
	}

	@Override
	protected EnumStructureState determineMasterState(Schemata schemata, boolean rotate) {
		
		Vect dimensions = schemata.getDimensions(rotate);
		int offsetX = schemata.getxOffset();
		int offsetZ = schemata.getzOffset();
		if(rotate) {
			offsetX = schemata.getzOffset();
			offsetZ = schemata.getxOffset();
		}
		
		for(int i = 0; i < dimensions.x; i++)
			for(int j = 0; j < schemata.getHeight(); j++)
				for(int k = 0; k < dimensions.z; k++) {
					
					int x = structureTile.xCoord + i + offsetX;
					int y = structureTile.yCoord + j + schemata.getyOffset();
					int z = structureTile.zCoord + k + offsetZ;
					
					if(!structureTile.worldObj.blockExists(x, y, z))
						return EnumStructureState.INDETERMINATE;
					
					EnumStructureBlock required = schemata.getAt(i, j, k, rotate);
						
					if(required == EnumStructureBlock.ANY)
						continue;
					
					TileEntity tile = structureTile.worldObj.getBlockTileEntity(x, y, z);
					int blockid = structureTile.worldObj.getBlockId(x, y, z);
					
					switch(required) {
					case AIR:
						if(blockid != 0)
							return EnumStructureState.INVALID;
						break;
					case BLOCK_A:
						if(tile == null || !(tile instanceof IFarmComponent))
							return EnumStructureState.INVALID;
						if(!((ITileStructure)tile).getTypeUID().equals(UID_FARM))
							return EnumStructureState.INVALID;
						break;
					case BLOCK_B:
					case MASTER:
						if(tile == null || !(tile instanceof TileFarm))
							return EnumStructureState.INVALID;
						if(((TileFarm)tile).hasFunction())
							return EnumStructureState.INVALID;
						break;
					case BLOCK_C:
						if(!brickIds.contains(blockid))
							return EnumStructureState.INVALID;
						break;
					case FOREIGN:
						if(tile instanceof ITileStructure)
							return EnumStructureState.INVALID;
						break;
					default:
						return EnumStructureState.INDETERMINATE;
					}
				}
		
		return EnumStructureState.VALID;
	}

}
