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
package forestry.apiculture.gui;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.apiculture.items.ItemBeeGE;
import forestry.apiculture.items.ItemImprinter.ImprinterInventory;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.ItemGE;
import forestry.core.gui.GuiForestry;
import forestry.core.proxy.Proxies;

public class GuiImprinter extends GuiForestry {

	private ImprinterInventory inventory;
	private ContainerImprinter container;

	private int startX;
	private int startY;

	private HashMap<String, ItemStack> iconStacks = new HashMap<String, ItemStack>();

	public GuiImprinter(InventoryPlayer inventoryplayer, ImprinterInventory inventory) {
		super(Defaults.TEXTURE_PATH_GUI + "/imprinter.png", new ContainerImprinter(inventoryplayer, inventory), inventory, 1, inventory.getSizeInventory());

		this.inventory = inventory;
		this.container = (ContainerImprinter) inventorySlots;

		xSize = 176;
		ySize = 185;
		
		ArrayList<ItemStack> beeList = new ArrayList<ItemStack>();
		((ItemBeeGE) ForestryItem.beeDroneGE).addCreativeItems(beeList, false);
		for (ItemStack beeStack : beeList) {
			iconStacks.put(BeeManager.beeInterface.getBee(beeStack).getIdent(), beeStack);
		}

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

		container.updateContainer(Proxies.common.getRenderWorld());

		drawBackground();

		int offset = (138 - fontRenderer.getStringWidth("GENOME IMPRINTER")) / 2;
		fontRenderer.drawString("GENOME IMPRINTER", startX + 8 + offset, startY + 16, fontColor.get("gui.screen"));

		IAlleleBeeSpecies primary = inventory.getPrimary();
		drawBeeSpeciesIcon(primary, startX + 12, startY + 32);
		fontRenderer.drawString(primary.getName(), startX + 32, startY + 36, fontColor.get("gui.screen"));

		IAlleleBeeSpecies secondary = inventory.getSecondary();
		drawBeeSpeciesIcon(secondary, startX + 12, startY + 52);
		fontRenderer.drawString(secondary.getName(), startX + 32, startY + 56, fontColor.get("gui.screen"));

		String youCheater = "(You Cheater!)";
		offset = (138 - fontRenderer.getStringWidth(youCheater)) / 2;
		fontRenderer.drawString(youCheater, startX + 8 + offset, startY + 76, fontColor.get("gui.screen"));

	}

	private void drawBeeSpeciesIcon(IAlleleBeeSpecies bee, int x, int y) {

		int tex = this.mc.renderEngine.getTexture(Defaults.TEXTURE_BEES);
		GL11.glDisable(GL11.GL_LIGHTING);
		mc.renderEngine.bindTexture(tex);

		for (int i = 0; i < ForestryItem.beeDroneGE.getRenderPasses(0); ++i) {

			int iconIndex = ForestryItem.beeDroneGE.getIconIndex(iconStacks.get(bee.getUID()), i);
			int color = ((ItemGE) ForestryItem.beeDroneGE).getColorFromItemStack(iconStacks.get(bee.getUID()), i);
			float colorR = (color >> 16 & 255) / 255.0F;
			float colorG = (color >> 8 & 255) / 255.0F;
			float colorB = (color & 255) / 255.0F;

			GL11.glColor4f(colorR, colorG, colorB, 1.0F);
			drawTexturedModalRect(x, y, iconIndex % 16 * 16, iconIndex / 16 * 16, 16, 16);

		}
		GL11.glEnable(GL11.GL_LIGHTING);

	}

	private int getHabitatSlotAtPosition(int i, int j) {
		int[] xPos = new int[] { 12, 12 };
		int[] yPos = new int[] { 32, 52 };

		for (int l = 0; l < xPos.length; l++) {
			if (i >= xPos[l] && i <= xPos[l] + 16 && j >= yPos[l] && j <= yPos[l] + 16)
				return l;
		}

		return -1;
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);

		int cornerX = (width - xSize) / 2;
		int cornerY = (height - ySize) / 2;

		int slot = 0;
		if ((slot = getHabitatSlotAtPosition(i - cornerX, j - cornerY)) < 0)
			return;

		if (k == 0) {
			container.advanceSelection(slot, Proxies.common.getRenderWorld());
		} else {
			container.regressSelection(slot, Proxies.common.getRenderWorld());
		}
	}

	@Override
	public void initGui() {
		super.initGui();

		startX = (this.width - this.xSize) / 2;
		startY = (this.height - this.ySize) / 2;
	}
}
