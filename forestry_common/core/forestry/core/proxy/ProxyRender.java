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

import net.minecraft.world.World;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.interfaces.IBlockRenderer;

public class ProxyRender {

	public int getNextAvailableRenderId() {
		return 0;
	}

	public boolean fancyGraphicsEnabled() {
		return false;
	}
	
	public boolean hasRendering() {
		return false;
	}

	public void registerTESR(MachineDefinition definition) {}
	
	public IBlockRenderer getRenderBlock(String gfxBase) {
		return null;
	}

	public IBlockRenderer getRenderDefaultMachine(String gfxBase) {
		return null;
	}

	public IBlockRenderer getRenderDefaultMachine(String gfxBase, boolean resourceLevel, boolean productLevel) {
		return null;
	}

	public IBlockRenderer getRenderMill(String gfxBase) {
		return null;
	}

	public IBlockRenderer getRenderMill(String gfxBase, byte charges) {
		return null;
	}

	public void registerTextureFX(Object textureFX) {
	}

	public void addSnowFX(World world, double xCoord, double yCoord, double zCoord, int color, int areaX, int areaY, int areaZ) {
	}

}
