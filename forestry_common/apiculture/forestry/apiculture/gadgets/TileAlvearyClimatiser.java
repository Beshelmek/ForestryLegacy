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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerFramework;
import forestry.api.apiculture.IAlvearyComponent;
import forestry.core.network.PacketPayload;

public abstract class TileAlvearyClimatiser extends TileAlveary implements IPowerReceptor {

	public static class ClimateControl {
		final float changePerTransfer;
		final float boundaryUp;
		final float boundaryDown;

		public ClimateControl(float changePerTransfer, float boundaryDown, float boundaryUp) {
			this.changePerTransfer = changePerTransfer;
			this.boundaryDown = boundaryDown;
			this.boundaryUp = boundaryUp;
		}
	}

	IPowerProvider powerProvider;
	ClimateControl climateControl;
	private int transferTime = 0;
	private int animationDelay = 0;

	private int textureOff;
	private int textureOn;
	
	public TileAlvearyClimatiser(ClimateControl control, int textureOff, int textureOn, int componentBlockMeta) {
		super(componentBlockMeta);
		this.climateControl = control;
		powerProvider = PowerFramework.currentFramework.createPowerProvider();
		powerProvider.configure(1, 10, 100, 25, 200);
		this.textureOff = textureOff;
		this.textureOn = textureOn;
	}

	@Override
	public void openGui(EntityPlayer player) {
	}

	/* UPDATING */
	@Override
	protected void updateServerSide() {
		super.updateServerSide();

		// BC Power
		if (getPowerProvider() != null) {
			IPowerProvider powerProvider = getPowerProvider();
			powerProvider.update(this);
		}

		if (!this.hasMaster())
			return;

		if (transferTime > 0) {
			transferTime--;
			IAlvearyComponent component = (IAlvearyComponent) this.getCentralTE();
			if (component != null) {
				component.addTemperatureChange(climateControl.changePerTransfer, climateControl.boundaryDown, climateControl.boundaryUp);
			}
		}

		if (animationDelay > 0) {
			animationDelay--;
			if(animationDelay <= 0)
				sendNetworkUpdate();
		}
	}

	@Override
	public boolean hasFunction() {
		return true;
	}

	/* TEXTURES */
	@Override
	public int getBlockTexture(int side, int metadata) {
		if(animationDelay > 0)
			return textureOn;
		else
			return textureOff;
	}
	
	/* IPOWERRECEPTOR */
	@Override
	public void doWork() {
		if (!this.hasMaster())
			return;

		if (powerProvider.useEnergy(powerProvider.getActivationEnergy(), powerProvider.getEnergyStored(), false) < powerProvider.getActivationEnergy())
			return;

		transferTime = Math.round(powerProvider.useEnergy(powerProvider.getActivationEnergy(), powerProvider.getEnergyStored(), true));

		if(animationDelay <= 0) {
			animationDelay = 100;
			sendNetworkUpdate();
		} else
			animationDelay = 100;			
	}

	@Override
	public void setPowerProvider(IPowerProvider provider) {
		powerProvider = provider;
	}

	@Override
	public IPowerProvider getPowerProvider() {
		return powerProvider;
	}

	@Override
	public int powerRequest() {
		return powerProvider.getActivationEnergy();
	}

	/* LOADING & SAVING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		PowerFramework.currentFramework.loadPowerProvider(this, nbttagcompound);
		transferTime = nbttagcompound.getInteger("Heating");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		PowerFramework.currentFramework.savePowerProvider(this, nbttagcompound);
		nbttagcompound.setInteger("Heating", transferTime);
	}
	
	@Override
	public String getInvName() {
		return "tile.alveary.climatiser";
	}

	/* NETWORK */
	@Override
	public void fromPacketPayload(PacketPayload payload) {
		animationDelay = payload.shortPayload[0];
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public PacketPayload getPacketPayload() {
		PacketPayload payload = new PacketPayload(0, 1);
		payload.shortPayload[0] = (short)animationDelay;
		return payload;
	}

}
