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

import java.util.Random;

import forestry.core.config.Defaults;
import forestry.core.interfaces.IHintSource;
import forestry.core.utils.StringUtil;

public class HintLedger extends Ledger {

	int position;
	String[] hints;

	public HintLedger(LedgerManager manager, IHintSource tile) {
		super(manager);
		this.hints = tile.getHints();
		maxHeight = 96;
		position = new Random().nextInt(hints.length);
		overlayColor = manager.gui.fontColor.get("ledger.hint.background");
	}

	@Override
	public void draw(int x, int y) {

		// Draw background
		drawBackground(x, y);

		// Draw icon
		drawIcon(Defaults.TEXTURE_ICONS_MISC, 1, x + 3, y + 4);

		if (!isFullyOpened())
			return;

		manager.minecraft.fontRenderer.drawStringWithShadow(StringUtil.localize("gui.didyouknow") + "?", x + 22, y + 8,
				manager.gui.fontColor.get("ledger.hint.header"));
		manager.minecraft.fontRenderer.drawSplitString(StringUtil.localize("hints." + hints[position] + ".desc"), x + 22, y + 20, maxWidth - 28,
				manager.gui.fontColor.get("ledger.hint.text"));

	}

	@Override
	public String getTooltip() {
		return StringUtil.localize("hints." + hints[position] + ".tag");
	}

}
