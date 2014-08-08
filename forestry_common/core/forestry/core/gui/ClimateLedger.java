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
package forestry.core.gui;

import forestry.api.core.EnumTemperature;
import forestry.core.config.Defaults;
import forestry.core.genetics.ClimateHelper;
import forestry.core.interfaces.IClimatised;
import forestry.core.utils.StringUtil;

/**
 * A ledger containing climate information.
 */
public class ClimateLedger extends Ledger {

	IClimatised tile;

	public ClimateLedger(LedgerManager manager, IClimatised tile) {
		super(manager);
		this.tile = tile;
		maxHeight = 72;
		overlayColor = manager.gui.fontColor.get("ledger.climate.background");
	}

	@Override
	public void draw(int x, int y) {

		EnumTemperature temperature = tile.getTemperature();

		// Draw background
		drawBackground(x, y);

		// Draw icon
		drawIcon(Defaults.TEXTURE_HABITATS, temperature.getIconIndex(), x + 3, y + 4);

		if (!isFullyOpened())
			return;

		manager.minecraft.fontRenderer.drawStringWithShadow(StringUtil.localize("gui.climate"), x + 22, y + 8,
				manager.gui.fontColor.get("ledger.climate.header"));
		manager.minecraft.fontRenderer.drawStringWithShadow(StringUtil.localize("gui.temperature") + ":", x + 22, y + 20,
				manager.gui.fontColor.get("ledger.climate.subheader"));
		manager.minecraft.fontRenderer.drawString(ClimateHelper.toDisplay(temperature) + " " + StringUtil.floatAsPercent(tile.getExactTemperature()), x + 22, y + 32,
				manager.gui.fontColor.get("ledger.climate.text"));
		manager.minecraft.fontRenderer.drawStringWithShadow(StringUtil.localize("gui.humidity") + ":", x + 22, y + 44,
				manager.gui.fontColor.get("ledger.climate.subheader"));
		manager.minecraft.fontRenderer.drawString(
				ClimateHelper.toDisplay(tile.getHumidity()) + " " + StringUtil.floatAsPercent(tile.getExactHumidity()), x + 22, y + 56,
				manager.gui.fontColor.get("ledger.climate.text"));
	}

	@Override
	public String getTooltip() {
		return "T: " + ClimateHelper.toDisplay(tile.getTemperature()) + " / H: "
				+ ClimateHelper.toDisplay(tile.getHumidity());
	}

}
