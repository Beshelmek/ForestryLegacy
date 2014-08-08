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

import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import buildcraft.api.inventory.ISpecialInventory;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.ITileStructure;
import forestry.apiculture.gui.ContainerAlveary;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.ClimateHelper;
import forestry.core.interfaces.IClimatised;
import forestry.core.interfaces.IErrorSource;
import forestry.core.interfaces.IHintSource;
import forestry.core.network.GuiId;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketInventoryStack;
import forestry.core.proxy.Proxies;

public class TileAlvearyPlain extends TileAlveary implements ISpecialInventory, ISidedInventory, IBeeHousing, IClimatised, IHintSource {

	// / CONSTANTS
	public static final int SLOT_QUEEN = 0;
	public static final int SLOT_DRONE = 1;
	public static final int SLOT_PRODUCT_1 = 2;
	public static final int SLOT_PRODUCTION_COUNT = 7;

	// / MEMBERS
	protected IBeekeepingLogic beekeepingLogic;

	protected int biomeId;

	protected float temperature;
	protected float humidity;

	protected float tempChange = 0.0f;
	protected float humidChange = 0.0f;

	private int displayHealthMax = 0;
	private int displayHealth = 0;

	public TileAlvearyPlain() {
		super(0);
	}

	@Override
	public void openGui(EntityPlayer player) {
		if (isMaster()) {
			player.openGui(ForestryAPI.instance, GuiId.AlvearyGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
		} else if (this.hasMaster()) {
			player.openGui(ForestryAPI.instance, GuiId.AlvearyGUI.ordinal(), worldObj, masterX, masterY, masterZ);
		}
	}

	/* LOADING & SAVING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		this.tempChange = nbttagcompound.getFloat("TempChange");
		this.humidChange = nbttagcompound.getFloat("HumidChange");

		if (inventory != null) {
			inventory.readFromNBT(nbttagcompound);
		}
		if (beekeepingLogic != null) {
			beekeepingLogic.readFromNBT(nbttagcompound);
		}

	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setFloat("TempChange", tempChange);
		nbttagcompound.setFloat("HumidChange", humidChange);

		if (inventory != null) {
			inventory.writeToNBT(nbttagcompound);
		}
		if (beekeepingLogic != null) {
			beekeepingLogic.writeToNBT(nbttagcompound);
		}

	}
	
	
	/* UPDATING */
	@Override
	public void initialize() {
		super.initialize();
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(xCoord, zCoord);
		this.biomeId = biome.biomeID;
		this.temperature = biome.temperature;
		this.humidity = biome.rainfall;
		setErrorState(EnumErrorCode.OK);
	}

	protected void updateServerSide() {

		if (beekeepingLogic == null)
			return;
		if(!isMaster())
			return;

		beekeepingLogic.update();

		// Equalize humidity and temperature
		equalizeTemperature();
		equalizeHumidity();

		IBee queen = beekeepingLogic.getQueen();
		if (queen == null)
			return;

		// Add swarm effects
		if (worldObj.getWorldTime() % 200 * 10 == 0) {
			onQueenChange(inventory.getStackInSlot(SLOT_QUEEN));
		}
		if (getErrorState() == EnumErrorCode.OK) {
			queen.doFX(beekeepingLogic.getEffectData(), this);
		}

		if (getErrorState() == EnumErrorCode.OK && worldObj.getWorldTime() % 50 == 0) {
			float f = xCoord + 0.5F;
			float f1 = yCoord + 0.0F + (worldObj.rand.nextFloat() * 6F) / 16F;
			float f2 = zCoord + 0.5F;
			float f3 = 0.52F;
			float f4 = worldObj.rand.nextFloat() * 0.6F - 0.3F;

			Proxies.common.addEntitySwarmFX(worldObj, (f - f3), f1, (f2 + f4), 0F, 0F, 0F);
			Proxies.common.addEntitySwarmFX(worldObj, (f + f3), f1, (f2 + f4), 0F, 0F, 0F);
			Proxies.common.addEntitySwarmFX(worldObj, (f + f4), f1, (f2 - f3), 0F, 0F, 0F);
			Proxies.common.addEntitySwarmFX(worldObj, (f + f4), f1, (f2 + f3), 0F, 0F, 0F);
		}

	}

	protected void updateClientSide() {

		if (!isMaster())
			return;

		if(inventory == null)
			return;
		
		// / Multiplayer FX
		if (BeeManager.beeInterface.isMated(inventory.getStackInSlot(SLOT_QUEEN))) {
			if (getErrorState() == EnumErrorCode.OK && worldObj.getWorldTime() % 2 == 0) {
				IBee displayQueen = BeeManager.beeInterface.getBee(inventory.getStackInSlot(SLOT_QUEEN));
				displayQueen.doFX(beekeepingLogic.getEffectData(), this);
			}
		}
		return;

	}

	private void equalizeTemperature() {
		if (tempChange == 0)
			return;

		tempChange -= 0.05f * tempChange;
		if (tempChange <= 0.001f && tempChange >= -0.001f) {
			tempChange = 0;
		}
	}

	private void equalizeHumidity() {
		if (humidChange == 0)
			return;

		humidChange -= 0.05f * humidChange;
		if (humidChange <= 0.001f && humidChange >= 0.001f) {
			humidChange = 0;
		}
	}

	/* STATE INFORMATION */
	private int getHealthDisplay() {
		if (inventory == null || inventory.getStackInSlot(SLOT_QUEEN) == null)
			return 0;

		if (BeeManager.beeInterface.isMated(inventory.getStackInSlot(SLOT_QUEEN)))
			return BeeManager.beeInterface.getBee(inventory.getStackInSlot(SLOT_QUEEN)).getHealth();
		else if (!BeeManager.beeInterface.isDrone(inventory.getStackInSlot(SLOT_QUEEN)))
			return displayHealth;
		else
			return 0;
	}

	private int getMaxHealthDisplay() {
		if (inventory == null || inventory.getStackInSlot(SLOT_QUEEN) == null)
			return 0;

		if (BeeManager.beeInterface.isMated(inventory.getStackInSlot(SLOT_QUEEN)))
			return BeeManager.beeInterface.getBee(inventory.getStackInSlot(SLOT_QUEEN)).getMaxHealth();
		else if (!BeeManager.beeInterface.isDrone(inventory.getStackInSlot(SLOT_QUEEN)))
			return displayHealthMax;
		else
			return 0;
	}

	public int getHealthScaled(int i) {
		if (getMaxHealthDisplay() == 0)
			return 0;

		return (getHealthDisplay() * i) / getMaxHealthDisplay();
	}

	@Override
	public boolean allowsInteraction(EntityPlayer player) {
		if (!super.allowsInteraction(player))
			return false;

		return this.isIntegratedIntoStructure();
	}

	/* STRUCTURE MANAGMENT */
	@Override
	public void makeMaster() {
		super.makeMaster();
		if (beekeepingLogic == null) {
			this.beekeepingLogic = BeeManager.breedingManager.createBeekeepingLogic(this);
		}
	}
	
	@Override
	public void onStructureReset() {
		super.onStructureReset();
		modifiers.clear();
		eventHandlers.clear();
	}
	
	/* IALVEARYCOMPONENT */
	private Set<IBeeModifier> modifiers = new LinkedHashSet<IBeeModifier>();
	private Set<IBeeListener> eventHandlers = new LinkedHashSet<IBeeListener>();
	
	@Override
	public void registerBeeModifier(IBeeModifier modifier) {
		modifiers.add(modifier);
	}

	@Override
	public void removeBeeModifier(IBeeModifier modifier) {
		modifiers.remove(modifier);
	}
	
	@Override
	public void registerBeeListener(IBeeListener modifier) {
		eventHandlers.add(modifier);
	}

	@Override
	public void removeBeeListener(IBeeListener modifier) {
		eventHandlers.remove(modifier);
	}

	@Override
	public void addTemperatureChange(float change, float boundaryDown, float boundaryUp) {
		tempChange = Math.min(boundaryUp - temperature, Math.max(boundaryDown - temperature, tempChange + change));
	}

	@Override
	public void addHumidityChange(float change, float boundaryDown, float boundaryUp) {
		humidChange = Math.min(boundaryUp - humidity, Math.max(boundaryDown - humidity, humidChange + change));
	}

	/* IBEEHOUSING */
	@Override
	public int getXCoord() {
		return xCoord;
	}

	@Override
	public int getYCoord() {
		return yCoord;
	}

	@Override
	public int getZCoord() {
		return zCoord;
	}

	@Override
	public ItemStack getQueen() {
		return getStackInSlot(SLOT_QUEEN);
	}

	@Override
	public ItemStack getDrone() {
		return getStackInSlot(SLOT_DRONE);
	}

	@Override
	public void setQueen(ItemStack itemstack) {
		setInventorySlotContents(SLOT_QUEEN, itemstack);
	}

	@Override
	public void setDrone(ItemStack itemstack) {
		setInventorySlotContents(SLOT_DRONE, itemstack);
	}

	@Override
	public int getBiomeId() {
		return this.biomeId;
	}

	@Override
	public EnumTemperature getTemperature() {
		if(this.biomeId == BiomeGenBase.hell.biomeID
				&& tempChange >= 0)
			return EnumTemperature.HELLISH;
		
		return ClimateHelper.getTemperature(getExactTemperature());
	}

	@Override
	public EnumHumidity getHumidity() {
		return ClimateHelper.getHumidity(getExactHumidity());
	}

	@Override
	public float getTerritoryModifier(IBeeGenome genome) {
		float mod = 2.0f;
		for (IBeeModifier modifier : modifiers) {
			mod *= modifier.getTerritoryModifier(genome);
		}
		return mod;
	}

	@Override
	public float getProductionModifier(IBeeGenome genome) {
		float mod = 1.0f;
		for (IBeeModifier modifier : modifiers) {
			mod *= modifier.getProductionModifier(genome);
		}
		return mod;
	}

	@Override
	public float getMutationModifier(IBeeGenome genome, IBeeGenome mate) {
		float mod = 1.0f;
		for (IBeeModifier modifier : modifiers) {
			mod *= modifier.getMutationModifier(genome, mate);
		}
		return mod;
	}

	@Override
	public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate) {
		float mod = 1.0f;
		for (IBeeModifier modifier : modifiers) {
			mod *= modifier.getLifespanModifier(genome, mate);
		}
		return mod;
	}
	
	@Override
	public float getFloweringModifier(IBeeGenome genome) {
		float mod = 1.0f;
		for (IBeeModifier modifier : modifiers) {
			mod *= modifier.getFloweringModifier(genome);
		}
		return mod;
	}

	@Override
	public World getWorld() {
		return worldObj;
	}

	@Override
	public void setErrorState(int state) {
		setErrorState(EnumErrorCode.values()[state]);
	}

	@Override
	public int getErrorOrdinal() {
		return getErrorState().ordinal();
	}

	@Override
	public boolean canBreed() {
		return true;
	}

	@Override
	public boolean addProduct(ItemStack product, boolean all) {
		if (inventory == null)
			return false;

		return inventory.tryAddStack(product, SLOT_PRODUCT_1, inventory.getSizeInventory() - SLOT_PRODUCT_1, all);
	}

	@Override
	public void wearOutEquipment(int amount) {
		for (IBeeListener eventHandler : eventHandlers) {
			eventHandler.wearOutEquipment(amount);
		}
	}

	@Override
	public void onQueenChange(ItemStack queenStack) {
		if (!Proxies.common.isSimulating(worldObj))
			return;

		Proxies.net.sendNetworkPacket(new PacketInventoryStack(PacketIds.IINVENTORY_STACK, xCoord, yCoord, zCoord, SLOT_QUEEN, queenStack), xCoord, yCoord,
				zCoord);
		
		for (IBeeListener eventHandler : eventHandlers) {
			eventHandler.onQueenChange(queenStack);
		}
	}
	
	@Override
	public void onQueenDeath(IBee queen) {
		
		for (IBeeListener eventHandler : eventHandlers) {
			eventHandler.onQueenDeath(queen);
		}
		
	}

	@Override
	public void onPostQueenDeath(IBee queen) {
		
		for (IBeeListener eventHandler : eventHandlers) {

			eventHandler.onPostQueenDeath(queen);
		}
		
	}

	@Override
	public boolean isSealed() {
		for (IBeeModifier modifier : modifiers) {
			if(modifier.isSealed()) return true;
		}
		return false;
	}

	@Override
	public boolean isSelfLighted() {
		for (IBeeModifier modifier : modifiers) {
			if(modifier.isSelfLighted()) return true;
		}
		return false;
	}

	@Override
	public boolean isSunlightSimulated() {
		for (IBeeModifier modifier : modifiers) {
			if(modifier.isSunlightSimulated()) return true;
		}
		return false;
	}

	@Override
	public boolean isHellish() {
		for (IBeeModifier modifier : modifiers) {
			if(modifier.isHellish()) return true;
		}
		return false;
	}

	/* IINVENTORY */
	private IInventory getStructureInventory() {
		
		if (inventory != null) {
			if(isMaster() || !Proxies.common.isSimulating(worldObj)) {
				return inventory;
			}
		} else if (hasMaster()) {
			ITileStructure central = getCentralTE();
			if(central != null) {
				return central.getInventory();
			}
		}

		return null;
	}
	
	@Override
	public int getSizeInventory() {
		IInventory inv = getStructureInventory();
		if (inv != null)
			return inv.getSizeInventory();
		else
			return 0;
	}

	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		IInventory inv = getStructureInventory();
		if (inv != null)
			return inv.getStackInSlot(slotIndex);
		else
			return null;
	}

	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) {
		IInventory inv = getStructureInventory();
		if (inv != null)
			return inv.decrStackSize(slotIndex, amount);
		else
			return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slotIndex) {
		IInventory inv = getStructureInventory();
		if (inv != null)
			return inv.getStackInSlotOnClosing(slotIndex);
		else
			return null;
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
		// Client side handling for container synch
		if (inventory == null && !Proxies.common.isSimulating(worldObj)) {
			createInventory();
		}

		IInventory inv = getStructureInventory();
		if (inv != null)
			inv.setInventorySlotContents(slotIndex, itemstack);
	}

	@Override
	public String getInvName() {
		return "tile.alveary.0";
	}

	@Override
	public int getInventoryStackLimit() {
		IInventory inv = getStructureInventory();
		if (inv != null)
			return inv.getInventoryStackLimit();
		else
			return 0;
	}
	
	@Override public void openChest() {}
	@Override public void closeChest() {}

	/* ISPECIALINVENTORY */
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {

		IInventory inv = getStructureInventory();
		if (inv == null)
			return 0;

		// Princesses && Queens
		if (stack.itemID == ForestryItem.beePrincessGE.itemID || stack.itemID == ForestryItem.beeQueenGE.itemID) {
			if (inv.getStackInSlot(SLOT_QUEEN) == null) {
				if (doAdd) {
					inv.setInventorySlotContents(SLOT_QUEEN, stack.copy());
					inv.getStackInSlot(SLOT_QUEEN).stackSize = 1;
				}
				return 1;
			}
		}

		// Drones
		if (stack.itemID == ForestryItem.beeDroneGE.itemID) {
			
			ItemStack droneStack = inv.getStackInSlot(SLOT_DRONE);
			if (droneStack == null) {
				if (doAdd) {
					inv.setInventorySlotContents(SLOT_DRONE, stack.copy());
				}
				return stack.stackSize;
			} else {
				if(!droneStack.isItemEqual(stack))
					return 0;
				if(!ItemStack.areItemStackTagsEqual(droneStack, stack))
					return 0;
				int space = droneStack.getMaxStackSize() - droneStack.stackSize;
				if(space <= 0)
					return 0;
				
				int added = space > stack.stackSize ? stack.stackSize : space;
				if(doAdd)
					droneStack.stackSize += added;
				return added;
			}
		}

		return 0;
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {

		IInventory inv = getStructureInventory();
		if (inv == null) {
			return new ItemStack[0];
		}

		ItemStack product = null;

		for (int i = SLOT_PRODUCT_1; i < inv.getSizeInventory(); i++) {
			if (inv.getStackInSlot(i) == null) {
				continue;
			}

			ItemStack stack = inv.getStackInSlot(i);

			if (doRemove) {
				product = inv.decrStackSize(i, 1);
			} else {
				product = stack.copy();
				product.stackSize = 1;
			}
			break;
		}

		return new ItemStack[] { product };
	}

	// ISIDEDINVENTORY
	@Override
	public int getStartInventorySide(ForgeDirection side) {
		switch(side) {
		case NORTH:
		case SOUTH:
			return SLOT_DRONE;
		case WEST:
		case EAST:
			return SLOT_QUEEN;
		case UP:
		case DOWN:
		default:
			return SLOT_PRODUCT_1;
		}
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		switch(side) {
		case NORTH:
		case SOUTH:
		case WEST:
		case EAST:
			return 1;
		case UP:
		case DOWN:
		default:
			return SLOT_PRODUCTION_COUNT;
		}
	}

	/* SMP GUI */
	public void getGUINetworkData(int i, int j) {
		switch (i) {
		case 0:
			displayHealth = j;
			break;
		case 1:
			displayHealthMax = j;
			break;
		case 2:
			this.temperature = (float) j / 100;
			break;
		case 3:
			this.humidity = (float) j / 100;
			break;
		case 4:
			this.tempChange = (float) j / 100;
			break;
		case 5:
			this.humidChange = (float) j / 100;
			break;
		case 6:
			this.biomeId = j;
			break;
		}

	}

	public void sendGUINetworkData(ContainerAlveary container, ICrafting iCrafting) {
		if (beekeepingLogic == null)
			return;

		iCrafting.sendProgressBarUpdate(container, 0, beekeepingLogic.getBreedingTime());
		iCrafting.sendProgressBarUpdate(container, 1, beekeepingLogic.getTotalBreedingTime());
		iCrafting.sendProgressBarUpdate(container, 2, Math.round(temperature * 100));
		iCrafting.sendProgressBarUpdate(container, 3, Math.round(humidity * 100));
		iCrafting.sendProgressBarUpdate(container, 4, Math.round(tempChange * 100));
		iCrafting.sendProgressBarUpdate(container, 5, Math.round(humidChange * 100));
		iCrafting.sendProgressBarUpdate(container, 6, biomeId);
	}

	/* IERRORSOURCE */
	@Override
	public EnumErrorCode getErrorState() {
		if (hasMaster()) {
			ITileStructure tile = this.getCentralTE();
			if(tile != null)
				return ((IErrorSource) tile).getErrorState();
		}
		
		return errorState;
	}

	/* ICLIMATISED */
	@Override
	public boolean isClimatized() {
		return true;
	}

	@Override
	public float getExactTemperature() {
		return this.temperature + this.tempChange;
	}

	@Override
	public float getExactHumidity() {
		return this.humidity + this.humidChange;
	}

	/* IHINTSOURCE */
	@Override
	public boolean hasHints() {
		return Config.hints.get("alveary").length > 0;
	}

	@Override
	public String[] getHints() {
		return Config.hints.get("alveary");
	}

	/* IOWNABLE */
	@Override
	public boolean isOwnable() {
		return true;
	}

}
