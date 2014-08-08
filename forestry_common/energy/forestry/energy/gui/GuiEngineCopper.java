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
package forestry.energy.gui;

import net.minecraft.entity.player.InventoryPlayer;
import forestry.core.config.Defaults;
import forestry.core.utils.StringUtil;
import forestry.energy.gadgets.EngineCopper;

public class GuiEngineCopper extends GuiEngine {

	public GuiEngineCopper(InventoryPlayer inventory, EngineCopper tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/peatengine.png", new ContainerEngineCopper(inventory, tile), tile);
	}

	protected void drawGuiContainerForegroundLayer() {
		super.drawGuiContainerForegroundLayer();
		String name = StringUtil.localize("tile.engine.1");
		this.fontRenderer.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
		this.fontRenderer.drawString("Inventory", 8, this.ySize - 96 + 2, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int i, int j) {

		drawBackground();
		EngineCopper engine = (EngineCopper) tile;

		int progress;
		if (engine.isBurning()) {
			progress = engine.getBurnTimeRemainingScaled(12);
			this.drawTexturedModalRect(guiLeft + 45, guiTop + 27 + 12 - progress, 176, 0 + 12 - progress, 14, progress + 2);
		}
	}
}
