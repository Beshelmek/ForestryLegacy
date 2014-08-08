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
package forestry.core;

public enum EnumErrorCode {

	UNKNOWN("unknown", 0),

	OK("ok", 1),

	INVALIDBIOME("invalidBiome", 2),

	ISRAINING("isRaining", 3),

	NOTRAINING("notRaining", 4),

	NOFUEL("noFuel", 5), // Biogas & Peat-fired

	NOHEAT("noHeat", 6), // Biogas engine

	NODISPOSAL("noDisposal", 7),

	NORESOURCE("noResource", 8),

	NOTGLOOMY("notGloomy", 9),

	NOTLUCID("notLucid", 10),

	NOTDAY("notDay", 11),

	NOTNIGHT("notNight", 12),

	NOFLOWER("noFlower", 13),

	NOQUEEN("noQueen", 14),

	NODRONE("noDrone", 15),

	NOSKY("noSky", 16),

	NOSPACE("noSpace", 17),

	NORECIPE("noRecipe", 19),

	NOENERGYNET("noEnergyNet", 20),

	NOTHINGANALYZE("noSpecimen", 15),

	FORCEDCOOLDOWN("forcedCooldown", 21),

	NOHONEY("noHoney", 22),

	NOTPOSTPAID("notPostpaid", 23),

	NORECIPIENT("noRecipient", 24),

	NOTALPHANUMERIC("notAlphaNumeric", 25),

	NOTUNIQUE("notUnique", 26),

	NOSTAMPS("noStamps", 23),
	
	NOCIRCUITBOARD("noCircuitBoard", 28),
	
	WRONGSTACKSIZE("wrongStacksize", 26),
	
	NOFERTILIZER("noFertilizer", 5),
	
	NOFARMLAND("noFarmland", 27),
	
	CIRCUITMISMATCH("circuitMismatch", 26),
	
	NOLIQUID("noLiquid", 29);

	private String name;
	private int iconIndex;

	private EnumErrorCode(String name, int iconIndex) {
		this.name = name;
		this.iconIndex = iconIndex;
	}

	public String getDescription() {
		return "errors." + name + ".desc";
	}

	public String getHelp() {
		return "errors." + name + ".help";
	}

	public int getIconIndex() {
		return iconIndex;
	}
}
