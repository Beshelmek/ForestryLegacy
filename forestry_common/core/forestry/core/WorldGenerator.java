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

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;
import forestry.api.core.IPlugin;
import forestry.core.config.Config;
import forestry.core.config.ForestryBlock;
import forestry.core.proxy.Proxies;
import forestry.core.worldgen.WorldGenMinableMeta;
import forestry.plugins.PluginManager;

public class WorldGenerator implements IWorldGenerator {

	private WorldGenMinableMeta apatiteGenerator;
	private WorldGenMinableMeta copperGenerator;
	private WorldGenMinableMeta tinGenerator;
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		if(apatiteGenerator == null) {
			apatiteGenerator = new WorldGenMinableMeta(ForestryBlock.resources.blockID, 0, 36);
			copperGenerator = new WorldGenMinableMeta(ForestryBlock.resources.blockID, 1, 6);
			tinGenerator = new WorldGenMinableMeta(ForestryBlock.resources.blockID, 2, 6);
		}
		
		// shift to world coordinates
		chunkX = chunkX << 4;
		chunkZ = chunkZ << 4;

		// / APATITE
		if (Config.generateApatiteOre) {
			
			if(random.nextFloat() < 0.75f) {
				int randPosX = chunkX + random.nextInt(16);
				int randPosY = random.nextInt(world.getActualHeight() - 72) + 64; // Does not generate below y = 64
				int randPosZ = chunkZ + random.nextInt(16);
				if(apatiteGenerator.generate(world, random, randPosX, randPosY, randPosZ))
					Proxies.log.finest("Generated apatite vein around %s/%s/%s", randPosX, randPosY, randPosZ);
			}
			
		}

		// / COPPER
		if (Config.generateCopperOre) {
			for (int i = 0; i < 20; i++) {
				int randPosX = chunkX + random.nextInt(16);
				int randPosY = random.nextInt(76) + 32;
				int randPosZ = chunkZ + random.nextInt(16);
				copperGenerator.generate(world, random, randPosX, randPosY, randPosZ);
			}
		}

		// / TIN
		if (Config.generateTinOre) {
			for (int i = 0; i < 18; i++) {
				int randPosX = chunkX + random.nextInt(16);
				int randPosY = random.nextInt(76) + 16;
				int randPosZ = chunkZ + random.nextInt(16);
				tinGenerator.generate(world, random, randPosX, randPosY, randPosZ);
			}
		}

		// / PLUGIN WORLD GENERATION
		for (IPlugin plugin : PluginManager.plugins)
			if (plugin.isAvailable()) {
				plugin.generateSurface(world, random, chunkX, chunkZ);
			}
	}

}
