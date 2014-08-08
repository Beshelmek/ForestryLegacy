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

import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import buildcraft.api.gates.ITrigger;
import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.core.IStructureLogic;
import forestry.api.core.ITileStructure;
import forestry.core.config.ForestryBlock;
import forestry.core.gadgets.Gadget;
import forestry.core.gadgets.TileForestry;
import forestry.core.network.PacketPayload;
import forestry.core.proxy.Proxies;
import forestry.core.triggers.ForestryTrigger;
import forestry.core.utils.TileInventoryAdapter;
import forestry.core.utils.Utils;

public abstract class TileAlveary extends TileForestry implements IAlvearyComponent {

	protected TileInventoryAdapter inventory;
	protected final int componentBlockMeta;

	public TileAlveary(int componentBlockMeta) {
		this.structureLogic = new StructureLogicAlveary(this);
		this.componentBlockMeta = componentBlockMeta;
	}

	/* UPDATING */
	@Override
	public void initialize() {
		int blockid = worldObj.getBlockId(xCoord, yCoord, zCoord);
		if(blockid != ForestryBlock.alveary.blockID) {
			Proxies.log.info("Updating alveary block at %s/%s/%s.", xCoord, yCoord, zCoord);
			worldObj.setBlockAndMetadata(xCoord, yCoord, zCoord, ForestryBlock.alveary.blockID, componentBlockMeta);
			validate();
			worldObj.setBlockTileEntity(xCoord, yCoord, zCoord, this);
		}
	}
	
	@Override
	public void updateEntity() {		
		if (!Proxies.common.isSimulating(worldObj)) {
			updateClientSide();
			
		} else {
			if (!isInited) {
				initialize();
				isInited = true;
			}

			// Periodic validation if needed
			if (worldObj.getWorldTime() % 200 == 0 && (!isIntegratedIntoStructure() || isMaster())) {
				validateStructure();
			}

			updateServerSide();
		}
	}

	protected void updateServerSide() {}
	protected void updateClientSide() {}

	/* TEXTURES */
	public int getBlockTexture(int side, int metadata) {
		return 25;
	}
	
	/* LOADING & SAVING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		this.isMaster = nbttagcompound.getBoolean("IsMaster");
		this.masterX = nbttagcompound.getInteger("MasterX");
		this.masterY = nbttagcompound.getInteger("MasterY");
		this.masterZ = nbttagcompound.getInteger("MasterZ");

		// Init for master state
		if (isMaster) {
			makeMaster();
		}

		structureLogic.readFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setBoolean("IsMaster", isMaster);
		nbttagcompound.setInteger("MasterX", masterX);
		nbttagcompound.setInteger("MasterY", masterY);
		nbttagcompound.setInteger("MasterZ", masterZ);

		structureLogic.writeToNBT(nbttagcompound);
	}

	protected void createInventory() {
		this.inventory = new TileInventoryAdapter(this, 9, "Items");
	}

	@Override
	public Gadget getMachine() {
		return null;
	}

	@Override public void fromPacketPayload(PacketPayload payload) {}
	@Override public PacketPayload getPacketPayload() { return null; }

	/* ITILESTRUCTURE */
	IStructureLogic structureLogic;

	private boolean isMaster;
	protected int masterX, masterZ;
	protected int masterY = -99;

	@Override
	public String getTypeUID() {
		return structureLogic.getTypeUID();
	}

	@Override
	public void validateStructure() {
		structureLogic.validateStructure();
	}

	@Override
	public void makeMaster() {
		setCentralTE(null);
		isMaster = true;

		if (inventory == null) {
			createInventory();
		}
	}

	@Override
	public void onStructureReset() {
		setCentralTE(null);
		if(worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 1)
			worldObj.setBlockMetadata(xCoord, yCoord, zCoord, 0);
		isMaster = false;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public ITileStructure getCentralTE() {

		if (!isIntegratedIntoStructure())
			return null;

		if(!isMaster()) {
			TileEntity tile = worldObj.getBlockTileEntity(masterX, masterY, masterZ);
			if (tile instanceof ITileStructure) {
				ITileStructure master = (ITileStructure) worldObj.getBlockTileEntity(masterX, masterY, masterZ);
				if(master.isMaster())
					return master;
				else {
					return null;
				}
			} else
				return null;
		} else
			return this;

	}

	private boolean isSameTile(TileEntity tile) {
		return tile.xCoord == xCoord && tile.yCoord == yCoord
				&& tile.zCoord == zCoord;
	}
	
	@Override
	public void setCentralTE(TileEntity tile) {
		if (tile == null || tile == this || isSameTile(tile)) {
			this.masterX = this.masterZ = 0;
			this.masterY = -99;
			return;
		}
			
		this.isMaster = false;
		this.masterX = tile.xCoord;
		this.masterY = tile.yCoord;
		this.masterZ = tile.zCoord;
	}

	@Override
	public boolean isMaster() {
		return isMaster;
	}

	protected boolean hasMaster() {
		return masterY >= 0;
	}

	@Override
	public boolean isIntegratedIntoStructure() {
		return isMaster || masterY >= 0;
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	/* IALVEARY COMPONENT */
	@Override
	public boolean hasFunction() {
		return false;
	}

	@Override public void addTemperatureChange(float change, float boundaryDown, float boundaryUp) {}
	@Override public void addHumidityChange(float change, float boundaryDown, float boundaryUp) {}
	@Override public void registerBeeModifier(IBeeModifier modifier) {}
	@Override public void removeBeeModifier(IBeeModifier modifier) {}
	@Override public void registerBeeListener(IBeeListener event) {}
	@Override public void removeBeeListener(IBeeListener event) {}
	
	// / ITRIGGERPROVIDER
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		LinkedList<ITrigger> res = new LinkedList<ITrigger>();
		res.add(ForestryTrigger.missingQueen);
		res.add(ForestryTrigger.missingDrone);
		return res;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return Utils.isUseableByPlayer(player, this, worldObj, xCoord, yCoord, zCoord);
	}

}
