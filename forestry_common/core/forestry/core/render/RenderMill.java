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

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import forestry.core.ForestryClient;
import forestry.core.gadgets.Mill;
import forestry.core.gadgets.TileMill;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.proxy.Proxies;

public class RenderMill implements IBlockRenderer {

	private ModelBase model = new ModelBase() {
	};
	private String gfxBase;
	private byte charges = 1;

	private ModelRenderer pedestal;
	private ModelRenderer column;
	private ModelRenderer extension;
	private ModelRenderer blade1;
	private ModelRenderer blade2;

	public RenderMill() {
		pedestal = new ModelRenderer(model, 0, 0);
		pedestal.addBox(-8F, -8F, -8F, 16, 1, 16);
		pedestal.rotationPointX = 8;
		pedestal.rotationPointY = 8;
		pedestal.rotationPointZ = 8;

		column = new ModelRenderer(model, 0, 0);
		column.addBox(-2, -7F, -2, 4, 15, 4);
		column.rotationPointX = 8;
		column.rotationPointY = 8;
		column.rotationPointZ = 8;

		extension = new ModelRenderer(model, 0, 0);
		extension.addBox(1F, 8F, 7F, 14, 2, 2);
		extension.rotationPointX = 0;
		extension.rotationPointY = 0;
		extension.rotationPointZ = 0;

		blade1 = new ModelRenderer(model, 0, 0);
		blade1.addBox(-4F, -5F, -3F, 8, 12, 1);
		blade1.rotationPointX = 8;
		blade1.rotationPointY = 8;
		blade1.rotationPointZ = 8;

		blade2 = new ModelRenderer(model, 0, 0);
		blade2.addBox(-4F, -5F, 2F, 8, 12, 1);
		blade2.rotationPointX = 8;
		blade2.rotationPointY = 8;
		blade2.rotationPointZ = 8;

	}

	public RenderMill(String baseTexture) {
		this();
		this.gfxBase = baseTexture;
	}

	public RenderMill(String baseTexture, byte charges) {
		this(baseTexture);
	}

	@Override
	public void preloadTextures() {
		ForestryClient.preloadTexture(gfxBase + "pedestal.png");
		ForestryClient.preloadTexture(gfxBase + "extension.png");
		ForestryClient.preloadTexture(gfxBase + "blade1.png");
		ForestryClient.preloadTexture(gfxBase + "blade2.png");

		for (int i = 0; i < charges; i++) {
			ForestryClient.preloadTexture(gfxBase + "column_" + i + ".png");
		}
	}

	@Override
	public void inventoryRender(double x, double y, double z, float f, float f1) {
		byte charge = 0;
		render(0.0f, charge, ForgeDirection.WEST, gfxBase, x, y, z);
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
		Mill tile = (Mill) ((TileMill) tileentity).machine;
		render(tile.progress, tile.charge, ForgeDirection.WEST, gfxBase, d, d1, d2);
	}

	private void render(float progress, int charge, ForgeDirection orientation, String gfxBase, double x, double y, double z) {

		GL11.glPushMatrix();
		GL11.glDisable(2896 /* GL_LIGHTING */);

		GL11.glTranslatef((float) x, (float) y, (float) z);

		float step;

		if (progress > 0.5) {
			step = 3.99F - (progress - 0.5F) * 2F * 3.99F;
		} else {
			step = progress * 2F * 3.99F;
		}

		float[] angle = { 0, 0, 0 };
		float[] translate = { 0, 0, 0 };
		float tfactor = step / 16;

		if (orientation == null) {
			orientation = ForgeDirection.WEST;
		}
		switch (orientation) {
		case EAST:
			// angle [2] = (float) Math.PI / 2;
			angle[1] = (float) Math.PI;
			angle[2] = (float) -Math.PI / 2;
			translate[0] = 1;
			break;
		case WEST:
			// 2, -PI/2
			angle[2] = (float) Math.PI / 2;
			translate[0] = -1;
			break;
		case UP:
			translate[1] = 1;
			break;
		case DOWN:
			angle[2] = (float) Math.PI;
			translate[1] = -1;
			break;
		case SOUTH:
			angle[0] = (float) Math.PI / 2;
			angle[2] = (float) Math.PI / 2;
			translate[2] = 1;
			break;
		case NORTH:
		default:
			angle[0] = (float) -Math.PI / 2;
			angle[2] = (float) Math.PI / 2;
			translate[2] = -1;
			break;
		}

		pedestal.rotateAngleX = angle[0];
		pedestal.rotateAngleY = angle[2];
		pedestal.rotateAngleZ = angle[1];

		column.rotateAngleX = angle[0];
		column.rotateAngleY = angle[2];
		column.rotateAngleZ = angle[1];

		blade1.rotateAngleX = angle[0];
		blade1.rotateAngleY = angle[2];
		blade1.rotateAngleZ = angle[1];

		blade2.rotateAngleX = angle[0];
		blade2.rotateAngleY = angle[2];
		blade2.rotateAngleZ = angle[1];

		float factor = (float) (1.0 / 16.0);

		Proxies.common.bindTexture(gfxBase + "pedestal.png");
		pedestal.render(factor);

		Proxies.common.bindTexture(gfxBase + "column_" + charge + ".png");
		column.render(factor);

		Proxies.common.bindTexture(gfxBase + "extension.png");
		extension.render(factor);

		Proxies.common.bindTexture(gfxBase + "blade1.png");
		GL11.glTranslatef(translate[0] * tfactor, translate[1] * tfactor, translate[2] * tfactor);
		blade1.render(factor);

		// Reset
		GL11.glTranslatef(-translate[0] * tfactor, -translate[1] * tfactor, -translate[2] * tfactor);

		Proxies.common.bindTexture(gfxBase + "blade2.png");
		GL11.glTranslatef(-translate[0] * tfactor, translate[1] * tfactor, -translate[2] * tfactor);
		blade2.render(factor);

		GL11.glEnable(2896 /* GL_LIGHTING */);
		GL11.glPopMatrix();

	}

}
