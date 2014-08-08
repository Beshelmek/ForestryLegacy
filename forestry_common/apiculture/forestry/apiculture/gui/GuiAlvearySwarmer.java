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
package forestry.apiculture.gui;

import net.minecraft.entity.player.InventoryPlayer;
import forestry.apiculture.gadgets.TileAlvearySwarmer;
import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;

public class GuiAlvearySwarmer extends GuiForestry {

	public GuiAlvearySwarmer(InventoryPlayer inventory, TileAlvearySwarmer tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/swarmer.png", new ContainerAlvearySwarmer(inventory, tile));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		drawBackground();
	}

}
