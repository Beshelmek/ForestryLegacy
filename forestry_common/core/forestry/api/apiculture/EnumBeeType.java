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
package forestry.api.apiculture;

public enum EnumBeeType {
	PRINCESS("bees.princess"), QUEEN("bees.queen"), DRONE("bees.drone");

	String name;

	private EnumBeeType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
