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
package forestry.core.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.block.Block;

import forestry.core.proxy.Proxies;

public class PacketFXSignal extends ForestryPacket {

	public static enum VisualFXType {
		NONE, BLOCK_DESTROY, SAPLING_PLACE
	};

	public static enum SoundFXType {
		NONE(""), BLOCK_DESTROY(""), BLOCK_PLACE(""), LEAF("step.grass"), LOG("dig.wood"), DIRT("dig.gravel"); 
		
		public final String soundFile;
		public final float volume = 1.0f;
		public final float pitch = 1.0f;
		
		private SoundFXType(String soundFile) {
			this.soundFile = soundFile;
		}
	};
	
	private VisualFXType visualFX;
	private SoundFXType soundFX;
	
	private int xCoord;
	private int yCoord;
	private int zCoord;
	private int blockid;
	private int meta;

	public PacketFXSignal() {
	}

	public PacketFXSignal(VisualFXType type, int xCoord, int yCoord, int zCoord, int blockid, int meta) {
		this(type, SoundFXType.NONE, xCoord, yCoord, zCoord, blockid, meta);
	}

	public PacketFXSignal(SoundFXType type, int xCoord, int yCoord, int zCoord, int blockid, int meta) {
		this(VisualFXType.NONE, type, xCoord, yCoord, zCoord, blockid, meta);
	}

	public PacketFXSignal(VisualFXType visualFX, SoundFXType soundFX, int xCoord, int yCoord, int zCoord, int blockid, int meta) {
		super(PacketIds.FX_SIGNAL);
		this.visualFX = visualFX;
		this.soundFX = soundFX;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;
		this.blockid = blockid;
		this.meta = meta;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeShort(visualFX.ordinal());
		data.writeShort(soundFX.ordinal());
		data.writeInt(xCoord);
		data.writeInt(yCoord);
		data.writeInt(zCoord);
		data.writeInt(blockid);
		data.writeInt(meta);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.visualFX = VisualFXType.values()[data.readShort()];
		this.soundFX = SoundFXType.values()[data.readShort()];
		this.xCoord = data.readInt();
		this.yCoord = data.readInt();
		this.zCoord = data.readInt();
		this.blockid = data.readInt();
		this.meta = data.readInt();
	}

	public void executeFX() {
		if(visualFX != VisualFXType.NONE)
			Proxies.common.addBlockDestroyEffects(Proxies.common.getRenderWorld(), xCoord, yCoord, zCoord, blockid, meta);
		if(soundFX != SoundFXType.NONE) {
			if(soundFX == SoundFXType.BLOCK_DESTROY)
				Proxies.common.playBlockBreakSoundFX(Proxies.common.getRenderWorld(), xCoord, yCoord, zCoord, Block.blocksList[blockid]);
			else if (soundFX == SoundFXType.BLOCK_PLACE)
				Proxies.common.playBlockPlaceSoundFX(Proxies.common.getRenderWorld(), xCoord, yCoord, zCoord, Block.blocksList[blockid]);
			else
				Proxies.common.playSoundFX(Proxies.common.getRenderWorld(), xCoord, yCoord, zCoord, soundFX.soundFile, soundFX.volume, soundFX.pitch);
		}
	}

}
