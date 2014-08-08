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

import net.minecraft.entity.player.InventoryPlayer;
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileMachine;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.LiquidTankSlot;
import forestry.factory.gadgets.MachineMoistener;

public class GuiMoistener extends GuiForestry {

	public GuiMoistener(InventoryPlayer inventory, TileMachine tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/moistener.png", new ContainerMoistener(inventory, tile), tile);
		slotManager.add(new LiquidTankSlot(this.slotManager, 16, 16, tile, 0));
	}

	protected void drawGuiContainerForegroundLayer() {
		super.drawGuiContainerForegroundLayer();
		String name = tile.getInvName();
		this.fontRenderer.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
	}

	protected void drawGuiContainerBackgroundLayer(float var1, int i, int j) {

		drawBackground();
		MachineMoistener machine = (MachineMoistener) tile.getMachine();

		// Mycelium production progress
		if (machine.isProducing()) {
			int i1 = machine.getProductionProgressScaled(16);
			drawTexturedModalRect(guiLeft + 124, guiTop + 36, 176, 74, 16 - i1, 16);
		}

		// Resource consumption progress
		if (machine.isWorking()) {
			int i1 = machine.getConsumptionProgressScaled(54);
			drawTexturedModalRect(guiLeft + 93, guiTop + 18 + i1, 176, 92 + i1, 29, 54 - i1);
		}
	}

}
