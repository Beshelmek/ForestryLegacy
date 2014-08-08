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

import net.minecraft.entity.player.InventoryPlayer;
import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.mail.gadgets.MachineMailbox;

public class GuiMailbox extends GuiForestry {

	public GuiMailbox(InventoryPlayer player, MachineMailbox tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/mailbox.png", new ContainerMailbox(player, tile), tile);
		this.xSize = 230;
		this.ySize = 227;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		drawBackground();
	}

}
