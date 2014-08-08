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
package forestry.apiculture.gadgets;

public class TileAlvearyHeater extends TileAlvearyClimatiser {

	public static final int TEXTURE_OFF = 57;
	public static final int TEXTURE_ON = 58;

	public TileAlvearyHeater() {
		super(new ClimateControl(0.01f, 0.0f, 2.5f), TEXTURE_OFF, TEXTURE_ON, 4);
	}

}
