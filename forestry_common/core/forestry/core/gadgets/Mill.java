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
package forestry.core.gadgets;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import forestry.core.network.EntityNetData;

public abstract class Mill extends Machine {

	protected TileMill tileMill;
	@EntityNetData
	public int charge = 0;
	@EntityNetData
	public float speed;
	@EntityNetData
	public int stage = 0;
	public float progress;

	public Mill(TileMill tile) {
		super(tile);
		tileMill = tile;
		speed = 0.01F;
	}

	@Override
	public void updateClientSide() {
		update(false);
	}

	@Override
	public void updateServerSide() {
		update(true);
	}

	private void update(boolean isSimulating) {

		// Stop gracefully if discharged.
		if (charge <= 0) {
			if (stage > 0) {
				progress += speed;
			}
			if (progress > 0.5) {
				stage = 2;
			}
			if (progress > 1) {
				progress = 0;
				stage = 0;
			}
			return;
		}

		// Update blades
		progress += speed;
		if (stage <= 0) {
			stage = 1;
		}

		if (progress > 0.5 && stage == 1) {
			stage = 2;
			if (charge < 7 && isSimulating) {
				charge++;
				tileMill.sendNetworkUpdate();
			}
		}
		if (progress > 1) {
			progress = 0;
			stage = 0;

			// Fully charged! Do something!
			if (charge >= 7) {
				activate();
			}
		}

	}

	protected abstract void activate();

	@Override
	public boolean isWorking() {
		return charge != 0;
	}

	@Override
	public void getGUINetworkData(int i, int j) {
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
	}

}
