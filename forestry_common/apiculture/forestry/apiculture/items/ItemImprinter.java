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
package forestry.apiculture.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.apiculture.genetics.Bee;
import forestry.apiculture.genetics.BreedingManager;
import forestry.core.items.ItemForestry;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;

public class ItemImprinter extends ItemForestry {

	public static class ImprinterInventory implements IInventory {

		private ItemStack[] inventoryStacks = new ItemStack[2];
		private short specimenSlot = 0;
		private short imprintedSlot = 1;

		private int primaryIndex = 0;
		private int secondaryIndex = 0;

		EntityPlayer player;

		public ImprinterInventory(EntityPlayer player) {
			this.player = player;
		}

		public void advancePrimary() {
			if (primaryIndex < BreedingManager.beeTemplates.size() - 1) {
				primaryIndex++;
			} else {
				primaryIndex = 0;
			}
		}

		public void advanceSecondary() {
			if (secondaryIndex < BreedingManager.beeTemplates.size() - 1) {
				secondaryIndex++;
			} else {
				secondaryIndex = 0;
			}
		}

		public void regressPrimary() {
			if (primaryIndex > 0) {
				primaryIndex--;
			} else {
				primaryIndex = BreedingManager.beeTemplates.size() - 1;
			}
		}

		public void regressSecondary() {
			if (secondaryIndex > 0) {
				secondaryIndex--;
			} else {
				secondaryIndex = BreedingManager.beeTemplates.size() - 1;
			}
		}

		public IAlleleBeeSpecies getPrimary() {
			return (IAlleleBeeSpecies) BreedingManager.speciesTemplates.get(BreedingManager.beeTemplates.get(primaryIndex).getIdent())[EnumBeeChromosome.SPECIES
					.ordinal()];
		}

		public IAlleleBeeSpecies getSecondary() {
			return (IAlleleBeeSpecies) BreedingManager.speciesTemplates.get(BreedingManager.beeTemplates.get(secondaryIndex).getIdent())[EnumBeeChromosome.SPECIES
					.ordinal()];
		}

		public IBee getSelectedBee() {
			return new Bee(BeeManager.beeInterface.templateAsGenome(
					BreedingManager.speciesTemplates.get(BreedingManager.beeTemplates.get(primaryIndex).getIdent()),
					BreedingManager.speciesTemplates.get(BreedingManager.beeTemplates.get(secondaryIndex).getIdent())));
		}

		public int getPrimaryIndex() {
			return primaryIndex;
		}

		public int getSecondaryIndex() {
			return secondaryIndex;
		}

		public void setPrimaryIndex(int index) {
			primaryIndex = index;
		}

		public void setSecondaryIndex(int index) {
			secondaryIndex = index;
		}

		private void tryImprint() {

			if (inventoryStacks[specimenSlot] == null)
				return;

			// Only imprint bees
			if (!BeeManager.beeInterface.isBee(inventoryStacks[specimenSlot]))
				return;

			// Needs space
			if (inventoryStacks[imprintedSlot] != null)
				return;

			IBee imprint = getSelectedBee();
			if (imprint == null)
				return;

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			imprint.writeToNBT(nbttagcompound);
			inventoryStacks[specimenSlot].setTagCompound(nbttagcompound);

			inventoryStacks[imprintedSlot] = inventoryStacks[specimenSlot];
			inventoryStacks[specimenSlot] = null;
		}

		@Override
		public ItemStack decrStackSize(int i, int j) {
			if (inventoryStacks[i] == null)
				return null;

			ItemStack product;
			if (inventoryStacks[i].stackSize <= j) {
				product = inventoryStacks[i];
				inventoryStacks[i] = null;
				return product;
			} else {
				product = inventoryStacks[i].splitStack(j);
				if (inventoryStacks[i].stackSize == 0) {
					inventoryStacks[i] = null;
				}

				return product;
			}
		}

		@Override
		public void onInventoryChanged() {
			if (!Proxies.common.isSimulating(player.worldObj))
				return;
			tryImprint();
		}

		@Override
		public void setInventorySlotContents(int i, ItemStack itemstack) {
			inventoryStacks[i] = itemstack;
		}

		@Override
		public ItemStack getStackInSlot(int i) {
			return inventoryStacks[i];
		}

		@Override
		public int getSizeInventory() {
			return inventoryStacks.length;
		}

		@Override
		public String getInvName() {
			return "Imprinter";
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer entityplayer) {
			return true;
		}

		@Override
		public void openChest() {
		}

		@Override
		public void closeChest() {
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int slot) {
			if (inventoryStacks[slot] == null)
				return null;
			ItemStack toReturn = inventoryStacks[slot];
			inventoryStacks[slot] = null;
			return toReturn;
		}

	}

	public ItemImprinter(int i) {
		super(i);
		setCreativeTab(Tabs.tabApiculture);
		setMaxStackSize(1);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (Proxies.common.isSimulating(world)) {
			entityplayer.openGui(ForestryAPI.instance, GuiId.ImprinterGUI.ordinal(), world, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);
		}

		return itemstack;
	}

}