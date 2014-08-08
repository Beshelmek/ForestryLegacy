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
package forestry.core.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import forestry.core.config.Defaults;
import forestry.core.network.ForestryPacket;

public class ProxyNetwork {

	public void sendNetworkPacket(ForestryPacket packet, int x, int y, int z) {
		if (packet == null)
			return;

		World[] worlds = DimensionManager.getWorlds();
		for (int i = 0; i < worlds.length; i++) {
			for (int j = 0; j < worlds[i].playerEntities.size(); j++) {
				EntityPlayerMP player = (EntityPlayerMP) worlds[i].playerEntities.get(j);

				if (Math.abs(player.posX - x) <= Defaults.NET_MAX_UPDATE_DISTANCE && Math.abs(player.posY - y) <= Defaults.NET_MAX_UPDATE_DISTANCE
						&& Math.abs(player.posZ - z) <= Defaults.NET_MAX_UPDATE_DISTANCE) {
					player.playerNetServerHandler.sendPacketToPlayer(packet.getPacket());
				}
			}
		}
	}

	public void sendToPlayer(ForestryPacket packet, EntityPlayer entityplayer) {
		if(!(entityplayer instanceof EntityPlayerMP))
			return;
		
		EntityPlayerMP player = (EntityPlayerMP) entityplayer;
		player.playerNetServerHandler.sendPacketToPlayer(packet.getPacket());
	}

	public void sendToServer(ForestryPacket packet) {
	}

	public void inventoryChangeNotify(EntityPlayer player) {
		if(player instanceof EntityPlayerMP)
			((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
	}
}
