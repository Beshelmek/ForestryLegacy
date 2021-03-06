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
package forestry.cultivation.gui;

import net.minecraft.entity.player.InventoryPlayer;
import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.core.utils.StringUtil;
import forestry.cultivation.gadgets.TilePlanter;
import forestry.cultivation.planters.PlanterMushroom;

public class GuiArboretum extends GuiForestry {

	private String name;

	public GuiArboretum(InventoryPlayer inventory, TilePlanter arboretum) {
		super(Defaults.TEXTURE_PATH_GUI + "/arboretum.png", new ContainerArboretum(inventory, arboretum), arboretum);

		if (arboretum.machine.getClass().equals(PlanterMushroom.class)) {
			name = StringUtil.localize("tile.planter.5");
			textureFile = Defaults.TEXTURE_PATH_GUI + "/mushroomfarm.png";
		} else {
			name = StringUtil.localize("tile.planter.0");
		}

	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		super.drawGuiContainerForegroundLayer();
		this.fontRenderer.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
		this.fontRenderer.drawString("Inventory", 8, this.ySize - 96 + 2, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int i, int j) {
		drawBackground();
	}
}
