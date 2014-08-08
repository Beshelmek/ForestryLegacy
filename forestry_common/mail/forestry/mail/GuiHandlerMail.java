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
package forestry.mail;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import forestry.core.GuiHandlerBase;
import forestry.core.network.GuiId;
import forestry.mail.gui.ContainerLetter;
import forestry.mail.gui.ContainerMailbox;
import forestry.mail.gui.ContainerPhilatelist;
import forestry.mail.gui.ContainerTradeName;
import forestry.mail.gui.ContainerTrader;
import forestry.mail.gui.GuiLetter;
import forestry.mail.gui.GuiMailbox;
import forestry.mail.gui.GuiPhilatelist;
import forestry.mail.gui.GuiTradeName;
import forestry.mail.gui.GuiTrader;
import forestry.mail.items.ItemLetter.LetterInventory;
import forestry.mail.gadgets.MachineMailbox;
import forestry.mail.gadgets.MachinePhilatelist;
import forestry.mail.gadgets.MachineTrader;

public class GuiHandlerMail extends GuiHandlerBase {

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		switch (GuiId.values()[id]) {
		case LetterGUI:
			return new GuiLetter(player, new LetterInventory(getEquippedItem(player)));
		case MailboxGUI:
			return new GuiMailbox(player.inventory, (MachineMailbox)getTileForestry(world, x, y, z));
		case PhilatelistGUI:
			return new GuiPhilatelist(player.inventory, (MachinePhilatelist)getTileForestry(world, x, y, z));
		case TraderGUI:
			return new GuiTrader(player.inventory, (MachineTrader)getTileForestry(world, x, y, z));
		case TraderNameGUI:
			return new GuiTradeName(player.inventory, (MachineTrader)getTileForestry(world, x, y, z));
		default:
			return null;

		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		switch (GuiId.values()[id]) {
		case LetterGUI:
			return new ContainerLetter(player, new LetterInventory(getEquippedItem(player)));
		case MailboxGUI:
			return new ContainerMailbox(player.inventory, (MachineMailbox)getTileForestry(world, x, y, z));
		case PhilatelistGUI:
			return new ContainerPhilatelist(player.inventory, (MachinePhilatelist)getTileForestry(world, x, y, z));
		case TraderGUI:
			return new ContainerTrader(player.inventory, (MachineTrader)getTileForestry(world, x, y, z));
		case TraderNameGUI:
			return new ContainerTradeName(player.inventory, (MachineTrader)getTileForestry(world, x, y, z));
		default:
			return null;

		}
	}

}
