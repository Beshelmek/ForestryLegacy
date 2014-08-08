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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import forestry.api.core.Tabs;
import forestry.core.config.Defaults;
import forestry.core.items.ItemForestry;
import forestry.core.utils.StringUtil;

public class ItemHoneycomb extends ItemForestry {

	private static class CombInfo {

		public String name;
		public int primaryColor;
		public int secondaryColor;
		public boolean isSecret = false;

		public CombInfo(String name, int primaryColor, int secondaryColor) {
			this.name = name;
			this.primaryColor = primaryColor;
			this.secondaryColor = secondaryColor;
		}

		public CombInfo setIsSecret() {
			this.isSecret = true;
			return this;
		}
	}

	public CombInfo[] combs = new CombInfo[] { new CombInfo("honey", 0xe8d56a, 0xffa12b), // 0
			new CombInfo("cocoa", 0x674016, 0xffb62b).setIsSecret(), // 1
			new CombInfo("simmering", 0x981919, 0xffb62b), // 2
			new CombInfo("stringy", 0xc8be67, 0xbda93e), // 3
			new CombInfo("frozen", 0xf9ffff, 0xa0ffff), // 4
			new CombInfo("dripping", 0xdc7613, 0xffff00), // 5
			new CombInfo("silky", 0x508907, 0xddff00), // 6
			new CombInfo("parched", 0xdcbe13, 0xffff00), // 7
			new CombInfo("mysterious", 0x161616, 0xe099ff).setIsSecret(), // 8
			new CombInfo("irradiated", 0xeafff3, 0xeeff00).setIsSecret(), // 9
			new CombInfo("powdery", 0xe4e4e4, 0xffffff).setIsSecret(), // 10
			new CombInfo("reddened", 0x4b0000, 0x6200e7).setIsSecret(), // 11
			new CombInfo("darkened", 0x353535, 0x33ebcb).setIsSecret(), // 12
			new CombInfo("omega", 0x191919, 0x6dcff6).setIsSecret(), // 13
			new CombInfo("wheaten", 0xfeff8f, 0xffffff).setIsSecret(), // 14
			new CombInfo("mossy", 0x2a3313, 0x7e9939), // 15
			new CombInfo("mellow", 0x886000, 0xfff960) // 16
	// new CombInfo("", 0xd7bee5, 0xfd58ab), // kindof pinkish
	};

	public ItemHoneycomb(int i) {
		super(i);
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		if (itemstack.getItemDamage() < 0 || itemstack.getItemDamage() >= combs.length)
			return null;

		return StringUtil.localize("item.comb." + combs[itemstack.getItemDamage()].name);
	}

	// Return true to enable color overlay
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {
		if (j == 0)
			return combs[itemstack.getItemDamage()].primaryColor;
		else
			return combs[itemstack.getItemDamage()].secondaryColor;
	}

	// Return texture index for color overlay - client side only
	@Override
	public int getIconFromDamageForRenderPass(int i, int j) {
		if (j > 0)
			return 108;
		else
			return 107;
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < combs.length; i++)
			if (!combs[i].isSecret || Defaults.DEBUG) {
				itemList.add(new ItemStack(this, 1, i));
			}
	}

	public int getCombTypeCount() {
		return combs.length;
	}

	public int getRandomCombType(Random random, boolean includeSecret) {
		List<Integer> validCombs = new ArrayList<Integer>(getCombTypeCount());
		for (int i = 0; i < combs.length; i++)
			if (!combs[i].isSecret || includeSecret) {
				validCombs.add(i);
			}

		if (validCombs.isEmpty())
			return 0;
		else
			return validCombs.get(random.nextInt(validCombs.size()));
	}
}
