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
package forestry.energy.gadgets;

import ic2.api.Direction;
import ic2.api.ElectricItem;
import ic2.api.IElectricItem;
import ic2.api.energy.EnergyNet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.core.ForestryAPI;
import forestry.core.EnumErrorCode;
import forestry.core.TemperatureState;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.gadgets.Engine;
import forestry.core.gadgets.TileBase;
import forestry.core.interfaces.ISocketable;
import forestry.core.network.GuiId;
import forestry.core.utils.DelayTimer;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.GenericInventoryAdapter;
import forestry.plugins.PluginIC2;

public class EngineTin extends Engine implements ISocketable, IInventory {

	protected static class EuConfig {
		public int euForCycle;
		public int mjPerCycle;
		public int euStorage;
		public int euMaxAccept = 512;

		public EuConfig(int euForCycle, int mjPerCycle, int euStorage) {
			this.euForCycle = euForCycle;
			this.mjPerCycle = mjPerCycle;
			this.euStorage = euStorage;
		}
	}

	private short batterySlot = 0;
	private GenericInventoryAdapter inventory = new GenericInventoryAdapter(1, "electrical");
	private GenericInventoryAdapter sockets = new GenericInventoryAdapter(1, "sockets");
	private boolean isAddedToEnergyNet = false;
	private int euStored = 0;
	private EuConfig euConfig = new EuConfig(Defaults.ENGINE_TIN_EU_FOR_CYCLE, Defaults.ENGINE_TIN_ENERGY_PER_CYCLE, Defaults.ENGINE_TIN_MAX_EU_STORED);

	private DelayTimer delayUpdateTimer = new DelayTimer();

	public EngineTin() {
		setHints(Config.hints.get("engine.tin"));

		maxEnergy = 10000;
		maxEnergyExtracted = 400;
		maxHeat = Defaults.ENGINE_TIN_HEAT_MAX;
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.EngineTinGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	// / SAVING / LOADING
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		euStored = nbttagcompound.getInteger("EngineEnergyStored");
		inventory.readFromNBT(nbttagcompound);
		sockets.readFromNBT(nbttagcompound);

		ItemStack chip = sockets.getStackInSlot(0);
		if (chip != null) {
			ICircuitBoard chipset = ItemCircuitBoard.getCircuitboard(chip);
			if (chipset != null) {
				chipset.onLoad(this);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("EngineEnergyStored", euStored);
		inventory.writeToNBT(nbttagcompound);
		sockets.writeToNBT(nbttagcompound);
	}

	// / HEAT MANAGMENT
	@Override
	public int dissipateHeat() {
		if (heat <= 0)
			return 0;

		int loss = 0;

		if (!isBurning() || !isActivated()) {
			loss += 1;
		}

		TemperatureState tempState = getTemperatureState();
		if (tempState == TemperatureState.OVERHEATING || tempState == TemperatureState.OPERATING_TEMPERATURE) {
			loss += 1;
		}

		heat -= loss;
		return loss;
	}

	@Override
	public int generateHeat() {

		int gain = 0;
		if (isActivated() && isBurning()) {
			gain++;
			if (((double) storedEnergy / (double) maxEnergy) > 0.5) {
				gain++;
			}
		}

		addHeat(gain);
		return gain;
	}

	// / WORK
	@Override
	public void updateServerSide() {
		// No work to be done if IC2 is unavailable.
		if (!PluginIC2.instance.isAvailable()) {
			setErrorState(EnumErrorCode.NOENERGYNET);
			return;
		}

		if (!isAddedToEnergyNet) {
			EnergyNet.getForWorld(worldObj).addTileEntity(this);
			this.isAddedToEnergyNet = true;
		}

		super.updateServerSide();

		if (forceCooldown) {
			setErrorState(EnumErrorCode.FORCEDCOOLDOWN);
			return;
		}

		if (inventory.getStackInSlot(batterySlot) != null) {
			replenishFromBattery(batterySlot);
		}

		// Updating of gui delayed to prevent it from going crazy
		if (!delayUpdateTimer.delayPassed(worldObj, 80))
			return;

		if (currentOutput <= 0 && getErrorState() == EnumErrorCode.OK) {
			setErrorState(EnumErrorCode.NOFUEL);
		} else {
			setErrorState(EnumErrorCode.OK);
		}
	}

	@Override
	public void burn() {

		currentOutput = 0;

		if (!isActivated())
			return;

		if (euStored >= euConfig.euForCycle) {
			currentOutput = euConfig.mjPerCycle;
			addEnergy(euConfig.mjPerCycle);
			euStored -= euConfig.euForCycle;
		}

	}

	private void replenishFromBattery(int slot) {

		// Don't accept if we are already full
		if (euStored >= euConfig.euStorage)
			return;

		if (!isActivated())
			return;

		// Only continue if we have an accepted battery
		ItemStack itemstack = inventory.getStackInSlot(slot);
		if (itemstack == null)
			return;

		if (!(itemstack.getItem() instanceof IElectricItem) || !((IElectricItem) itemstack.getItem()).canProvideEnergy())
			return;

		int space = euConfig.euStorage - euStored;
		if (space >= euConfig.euForCycle * 3) {
			space = euConfig.euForCycle * 3;
		}

		euStored += ElectricItem.discharge(itemstack, space, Integer.MAX_VALUE, true, false);
	}

	// / STATE INFORMATION
	@Override
	public boolean isBurning() {
		return mayBurn() && euStored >= euConfig.euForCycle;
	}

	public int getStorageScaled(int i) {
		return (euStored * i) / (euConfig.euStorage + euConfig.euMaxAccept - 1);
	}

	public EnumTankLevel rateLevel(int scaled) {

		if (scaled < 5)
			return EnumTankLevel.EMPTY;
		else if (scaled < 30)
			return EnumTankLevel.LOW;
		else if (scaled < 60)
			return EnumTankLevel.MEDIUM;
		else if (scaled < 90)
			return EnumTankLevel.HIGH;
		else
			return EnumTankLevel.MAXIMUM;
	}

	// / SMP GUI
	@Override
	public void getGUINetworkData(int i, int j) {

		switch (i) {

		case 0:
			currentOutput = j;
			break;
		case 1:
			storedEnergy = j;
			break;
		case 2:
			heat = j;
			break;
		case 3:
			euStored = j;
			break;
		}

	}

	@Override
	public void sendGUINetworkData(Container containerEngine, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(containerEngine, 0, currentOutput);
		iCrafting.sendProgressBarUpdate(containerEngine, 1, storedEnergy);
		iCrafting.sendProgressBarUpdate(containerEngine, 2, heat);
		iCrafting.sendProgressBarUpdate(containerEngine, 3, euStored);
	}

	// / ENERGY CONFIG CHANGE
	public void changeEnergyConfig(int euChange, int mjChange, int storageChange) {
		euConfig.euForCycle += euChange;
		euConfig.mjPerCycle += mjChange;
		euConfig.euStorage += storageChange;
	}

	// / IC2 IMPLEMENTATION
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
		return true;
	}

	@Override
	public boolean isAddedToEnergyNet() {
		return isAddedToEnergyNet;
	}

	@Override
	public int demandsEnergy() {
		return euConfig.euStorage - euStored;
	}

	@Override
	public int injectEnergy(Direction directionFrom, int amount) {
		int input = Math.min(euConfig.euStorage + euConfig.euMaxAccept - 1 - euStored, amount);
		euStored += input;
		return amount - input;
	}

	// / IINVENTORY
	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override public void openChest() {}
	@Override public void closeChest() {}

	// / ISOCKETABLE
	@Override
	public int getSocketCount() {
		return sockets.getSizeInventory();
	}

	@Override
	public ItemStack getSocket(int slot) {
		return sockets.getStackInSlot(slot);
	}

	@Override
	public void setSocket(int slot, ItemStack stack) {

		if (stack != null && !ItemCircuitBoard.isChipset(stack))
			return;

		// Dispose correctly of old chipsets
		if (sockets.getStackInSlot(slot) != null) {
			if (ItemCircuitBoard.isChipset(sockets.getStackInSlot(slot))) {
				ICircuitBoard chipset = ItemCircuitBoard.getCircuitboard(sockets.getStackInSlot(slot));
				if (chipset != null) {
					chipset.onRemoval(this);
				}
			}
		}

		if (stack == null) {
			sockets.setInventorySlotContents(slot, stack);
			return;
		}

		sockets.setInventorySlotContents(slot, stack);

		ICircuitBoard chipset = ItemCircuitBoard.getCircuitboard(stack);
		if (chipset != null) {
			chipset.onInsertion(this);
		}
	}

}
