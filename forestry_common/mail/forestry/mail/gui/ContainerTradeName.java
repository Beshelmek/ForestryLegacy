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
package forestry.mail.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import forestry.core.gui.ContainerForestry;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.utils.GenericInventoryAdapter;
import forestry.mail.gadgets.MachineTrader;

public class ContainerTradeName extends ContainerForestry {

	boolean isLinked;
	MachineTrader machine;

	public ContainerTradeName(InventoryPlayer player, MachineTrader tile) {
		super(new GenericInventoryAdapter(0, "Empty"));
		machine = tile;
		isLinked = machine.isLinked();
	}

	public String getMoniker() {
		return machine.getMoniker();
	}

	public void setMoniker(String moniker) {

		if (moniker == null || moniker.isEmpty())
			return;

		PacketPayload payload = new PacketPayload(0, 0, 1);
		payload.stringPayload[0] = moniker;

		PacketUpdate packet = new PacketUpdate(PacketIds.TRADING_MONIKER_SET, payload);
		Proxies.net.sendToServer(packet);

		machine.setMoniker(moniker);
	}

	public void handleSetMoniker(PacketUpdate packet) {
		machine.setMoniker(packet.payload.stringPayload[0]);
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
