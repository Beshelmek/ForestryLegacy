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
package forestry.factory.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import forestry.core.gadgets.TileMachine;
import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.SlotLiquidContainer;
import forestry.factory.gadgets.MachineStill;

public class ContainerStill extends ContainerLiquidTanks {
	protected TileMachine processor;

	public ContainerStill(InventoryPlayer player, TileMachine tile) {
		super(tile, tile);

		this.processor = tile;
		this.addSlot(new Slot(tile, MachineStill.SLOT_OUTPUT, 150, 54));
		this.addSlot(new SlotLiquidContainer(tile, MachineStill.SLOT_CAN, 150, 18, true));
		this.addSlot(new SlotLiquidContainer(tile, MachineStill.SLOT_INPUT, 10, 36));

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(player, i, 8 + i * 18, 142));
		}

	}

	// @Override client side only
	public void updateProgressBar(int i, int j) {
		processor.machine.getGUINetworkData(i, j);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++) {
			processor.machine.sendGUINetworkData(this, (ICrafting) crafters.get(i));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return processor.isUseableByPlayer(entityplayer);
	}
}
