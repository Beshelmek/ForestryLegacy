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
package forestry.farming.triggers;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import buildcraft.api.gates.ITriggerParameter;
import forestry.api.core.ITileStructure;
import forestry.core.triggers.Trigger;
import forestry.core.utils.StringUtil;
import forestry.farming.gadgets.TileFarmPlain;
import forestry.farming.gadgets.TileHatch;

public class TriggerLowSoil extends Trigger {

	private int threshold = 64;

	public TriggerLowSoil(int id, int threshold) {
		super(id, 7);
		this.threshold = threshold;
	}

	@Override
	public String getDescription() {
		return StringUtil.localize("trigger.lowSoil") + " < " + threshold;
	}

	@Override
	public boolean hasParameter() {
		return true;
	}

	/**
	 * Return true if the tile given in parameter activates the trigger, given the parameters.
	 */
	@Override
	public boolean isTriggerActive(TileEntity tile, ITriggerParameter parameter) {
		if(!(tile instanceof TileHatch))
			return false;
		
		ITileStructure central = ((TileHatch)tile).getCentralTE();
		if(central == null || !(central instanceof TileFarmPlain))
			return false;
		
		ItemStack filter;
		if(parameter == null || parameter.getItemStack() == null) {
			filter = new ItemStack(-1, threshold, -1);
		} else {
			filter = parameter.getItemStack().copy();			
			filter.stackSize = threshold;
		}
		
		return !((TileFarmPlain)central).hasResources(new ItemStack[] { filter });
	}
}
