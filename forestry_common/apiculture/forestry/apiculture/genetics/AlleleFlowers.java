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
package forestry.apiculture.genetics;

import forestry.api.apiculture.IAlleleFlowers;
import forestry.api.apiculture.IFlowerProvider;
import forestry.core.genetics.Allele;

public class AlleleFlowers extends Allele implements IAlleleFlowers {

	IFlowerProvider provider;

	public AlleleFlowers(String uid, IFlowerProvider provider) {
		this(uid, provider, false);
	}

	public AlleleFlowers(String uid, IFlowerProvider provider, boolean isDominant) {
		super(uid, isDominant);
		this.provider = provider;
	}

	@Override
	public IFlowerProvider getProvider() {
		return provider;
	}

}
