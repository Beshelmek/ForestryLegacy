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
package forestry.plugins;

import java.util.Random;

import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;

import net.minecraft.command.ICommand;
import net.minecraft.world.World;
import forestry.api.core.IPacketHandler;
import forestry.api.core.IPickupHandler;
import forestry.api.core.IPlugin;
import forestry.api.core.IResupplyHandler;

public abstract class NativePlugin implements IPlugin {

	@Override
	public void preInit() {
		registerItems();
	}

	@Override
	public void doInit() {
		registerBackpackItems();
		registerCrates();
	}

	@Override
	public void postInit() {
		registerPackages();
		registerRecipes();
	}

	public boolean processIMCMessage(IMCMessage message) {
		return false;
	}
	
	@Override
	public void generateSurface(World world, Random rand, int chunkX, int chunkZ) {
	}

	@Override
	public IPacketHandler getPacketHandler() {
		return null;
	}

	@Override
	public IPickupHandler getPickupHandler() {
		return null;
	}

	@Override
	public IResupplyHandler getResupplyHandler() {
		return null;
	}

	@Override
	public ICommand[] getConsoleCommands() {
		return null;
	}

	protected abstract void registerPackages();

	protected abstract void registerItems();

	protected abstract void registerBackpackItems();

	protected abstract void registerCrates();

	protected abstract void registerRecipes();

}
