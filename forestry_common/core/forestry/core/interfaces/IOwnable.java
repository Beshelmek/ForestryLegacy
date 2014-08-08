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
package forestry.core.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import forestry.core.utils.EnumAccess;

public interface IOwnable {

	boolean isOwnable();

	boolean isOwned();

	String getOwnerName();

	void setOwner(EntityPlayer player);

	boolean isOwner(EntityPlayer player);

	boolean switchAccessRule(EntityPlayer player);

	EnumAccess getAccess();

	boolean allowsRemoval(EntityPlayer player);

	boolean allowsInteraction(EntityPlayer player);
}
