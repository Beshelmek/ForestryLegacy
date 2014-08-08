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
import forestry.core.gui.ReservoirSlot;
import forestry.core.utils.StringUtil;
import forestry.factory.gadgets.MachineFabricator;

public class GuiFabricator extends GuiForestry {

	public GuiFabricator(InventoryPlayer player, TileMachine tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/fabricator.png", new ContainerFabricator(player, tile), tile);
		this.ySize = 211;
		slotManager.add(new ReservoirSlot(this.slotManager, 26, 48, tile, 0));
	}

	protected void drawGuiContainerForegroundLayer() {
		super.drawGuiContainerForegroundLayer();
		String name = StringUtil.localize(tile.getInvName());
		this.fontRenderer.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		drawBackground();

		MachineFabricator fabricator = (MachineFabricator) tile.getMachine();
		int heatScaled = fabricator.getHeatScaled(52);
		if (heatScaled > 0) {
			drawTexturedModalRect(guiLeft + 55, guiTop + 17 + 52 - heatScaled, 192, 0 + 52 - heatScaled, 4, heatScaled);
		}

		int meltingPointScaled = fabricator.getMeltingPointScaled(52);
		if (meltingPointScaled > 0) {
			drawTexturedModalRect(guiLeft + 52, guiTop + 15 + 52 - meltingPointScaled, 196, 0, 10, 5);
		}
	}

}
