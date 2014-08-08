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
import forestry.core.gui.SlotClosed;
import forestry.core.gui.SlotLiquidContainer;
import forestry.factory.gadgets.MachineBottler;

public class ContainerBottler extends ContainerLiquidTanks {

	protected TileMachine fermenter;

	public ContainerBottler(InventoryPlayer player, TileMachine fermenter) {
		super(fermenter, fermenter);

		this.fermenter = fermenter;
		this.addSlot(new SlotLiquidContainer(fermenter, MachineBottler.SLOT_RESOURCE, 116, 19, true));
		this.addSlot(new SlotClosed(fermenter, MachineBottler.SLOT_PRODUCT, 116, 55));
		this.addSlot(new SlotLiquidContainer(fermenter, MachineBottler.SLOT_CAN, 26, 38));

		int var3;
		for (var3 = 0; var3 < 3; ++var3) {
			for (int var4 = 0; var4 < 9; ++var4) {
				this.addSlot(new Slot(player, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
			}
		}

		for (var3 = 0; var3 < 9; ++var3) {
			this.addSlot(new Slot(player, var3, 8 + var3 * 18, 142));
		}

	}

	// @Override client side only
	public void updateProgressBar(int i, int j) {
		fermenter.machine.getGUINetworkData(i, j);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++) {
			fermenter.machine.sendGUINetworkData(this, (ICrafting) crafters.get(i));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return fermenter.isUseableByPlayer(entityplayer);
	}
}
