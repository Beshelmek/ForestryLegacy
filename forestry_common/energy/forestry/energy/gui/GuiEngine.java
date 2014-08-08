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
/**
 * 
 */
package forestry.energy.gui;

import forestry.core.config.Defaults;
import forestry.core.gadgets.Engine;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.Ledger;
import forestry.core.utils.StringUtil;

public abstract class GuiEngine extends GuiForestry {

	protected class EngineLedger extends Ledger {

		private Engine engine;

		public EngineLedger(Engine engine) {
			super(ledgerManager);
			this.engine = engine;
			maxHeight = 94;
			overlayColor = fontColor.get("ledger.power.background");
		}

		@Override
		public void draw(int x, int y) {

			// Draw background
			drawBackground(x, y);

			// Draw icon
			drawIcon(Defaults.TEXTURE_ICONS_MISC, 0, x + 3, y + 4);

			if (!isFullyOpened())
				return;

			fontRenderer.drawStringWithShadow(StringUtil.localize("gui.energy"), x + 22, y + 8, fontColor.get("ledger.power.header"));
			fontRenderer.drawStringWithShadow(StringUtil.localize("gui.currentOutput") + ":", x + 22, y + 20, fontColor.get("ledger.power.subheader"));
			fontRenderer.drawString(engine.getCurrentOutput() + " MJ/t", x + 22, y + 32, fontColor.get("ledger.power.text"));
			fontRenderer.drawStringWithShadow(StringUtil.localize("gui.stored") + ":", x + 22, y + 44, fontColor.get("ledger.power.subheader"));
			fontRenderer.drawString(engine.getEnergyStored() + " MJ", x + 22, y + 56, fontColor.get("ledger.power.text"));
			fontRenderer.drawStringWithShadow(StringUtil.localize("gui.heat") + ":", x + 22, y + 68, fontColor.get("ledger.power.subheader"));
			fontRenderer.drawString(((double) engine.getHeat() / (double) 10) + " C", x + 22, y + 80, fontColor.get("ledger.power.text"));

		}

		@Override
		public String getTooltip() {
			return engine.getCurrentOutput() + " MJ/t";
		}

	}

	public GuiEngine(String texture, ContainerForestry container, Engine tile) {
		super(texture, container, tile);
	}

	@Override
	protected void initLedgers(Object inventory) {
		super.initLedgers(inventory);
		ledgerManager.insert(new EngineLedger((Engine) tile));
	}
}
