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

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import forestry.api.genetics.IFruitBearer;
import forestry.api.genetics.IFruitFamily;
import forestry.core.network.PacketFXSignal;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Vect;

public class CropFruit extends Crop {

	IFruitFamily family;
	
	public CropFruit(World world, Vect position, IFruitFamily family) {
		super(world, position);
		this.family = family;
	}

	@Override
	protected boolean isCrop(Vect pos) {
		TileEntity tile = world.getBlockTileEntity(pos.x, pos.y, pos.z);
		if(!(tile instanceof IFruitBearer))
			return false;
		IFruitBearer bearer = (IFruitBearer)tile;
		if(!bearer.hasFruit())
			return false;
		if(bearer.getRipeness() < 0.9f)
			return false;
		
		return true;
	}

	@Override
	protected Collection<ItemStack> harvestBlock(Vect pos) {
		TileEntity tile = world.getBlockTileEntity(pos.x, pos.y, pos.z);
		if(!(tile instanceof IFruitBearer))
			return new ArrayList<ItemStack>();
		
		Proxies.common.sendFXSignal(PacketFXSignal.VisualFXType.BLOCK_DESTROY, PacketFXSignal.SoundFXType.LEAF, world, pos.x, pos.y, pos.z, world.getBlockId(pos.x, pos.y, pos.z), 0);
		return ((IFruitBearer)tile).pickFruit(null);
	}

}
