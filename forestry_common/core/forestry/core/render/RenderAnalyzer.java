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
package forestry.core.render;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import forestry.apiculture.render.ModelAnalyzer;
import forestry.core.gadgets.MachineAnalyzer;
import forestry.core.interfaces.IBlockRenderer;

public class RenderAnalyzer extends TileEntitySpecialRenderer implements IBlockRenderer {

	private ModelAnalyzer model;
	private final EntityItem dummyEntityItem = new EntityItem (null);
	private final RenderItem customRenderItem;
	private long lastTick;

	public RenderAnalyzer() {
	    customRenderItem = new RenderItem() {
	        @Override public boolean shouldBob() { return true; }
	    	@Override public boolean shouldSpreadItems() { return false; };
	    };
	    customRenderItem.setRenderManager(RenderManager.instance);	    
	}

	public RenderAnalyzer(String baseTexture) {
		this();
		this.model = new ModelAnalyzer(baseTexture);
	}

	@Override
	public void inventoryRender(double x, double y, double z, float f, float f1) {
		render(null, null, ForgeDirection.WEST, x, y, z);
	}

	@Override
	public void preloadTextures() {
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double d, double d1, double d2, float f) {

		MachineAnalyzer analyzer = (MachineAnalyzer) tile;
		render(analyzer.getBeeOnDisplay(), tile.worldObj, analyzer.getOrientation(), d, d1, d2);
	}

	private void render(ItemStack itemstack, World world, ForgeDirection orientation, double x, double y, double z) {

		dummyEntityItem.worldObj = world;
		
		model.render(orientation, (float) x, (float) y, (float) z);
		if (itemstack == null)
			return;
		float renderScale = 0.7f;
		
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glTranslatef(0.5f, 0.35f, 0.5f);
        GL11.glScalef(renderScale, renderScale, renderScale);
        dummyEntityItem.func_92058_a(itemstack);
        
        if(world.getWorldTime() != lastTick) {
        	lastTick = world.getWorldTime();
        	dummyEntityItem.onUpdate();
        }
        customRenderItem.doRenderItem(dummyEntityItem, 0, 0, 0, 0, 0);
		GL11.glPopMatrix();

	}

}
