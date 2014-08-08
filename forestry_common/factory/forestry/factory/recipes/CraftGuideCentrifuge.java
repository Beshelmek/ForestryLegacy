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
package forestry.factory.recipes;

import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.factory.gadgets.MachineCentrifuge;
import uristqwerty.CraftGuide.api.ChanceSlot;
import uristqwerty.CraftGuide.api.ItemSlot;
import uristqwerty.CraftGuide.api.RecipeGenerator;
import uristqwerty.CraftGuide.api.RecipeProvider;
import uristqwerty.CraftGuide.api.RecipeTemplate;
import uristqwerty.CraftGuide.api.Slot;
import uristqwerty.CraftGuide.api.SlotType;

public class CraftGuideCentrifuge implements RecipeProvider {

    private final Slot[] slots = new Slot[11];

    public CraftGuideCentrifuge() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                slots[i + j * 3] = new ChanceSlot(i * 18 + 24, j * 18 + 3, 16, 16).setRatio(100).setSlotType(SlotType.OUTPUT_SLOT).drawOwnBackground();
            }
        }
        slots[9] = new ItemSlot(4, 31, 16, 16, true).drawOwnBackground();
        slots[10] = new ItemSlot(4, 11, 16, 16).setSlotType(SlotType.MACHINE_SLOT);
    }

    @Override
    public void generateRecipes(RecipeGenerator generator) {
    	
    	if(ForestryBlock.machine == null)
    		return;
    	
    	ItemStack machine = new ItemStack(ForestryBlock.machine, 1, Defaults.ID_PACKAGE_MACHINE_CENTRIFUGE);
    	RecipeTemplate template = generator.createRecipeTemplate(slots, machine);

    	for(MachineCentrifuge.Recipe recipe : MachineCentrifuge.RecipeManager.recipes) {
    		Object[] array = new Object[11];
    		Entry[] entries = recipe.products.entrySet().toArray(new Entry[0]);
    		for(int i = 0; i < Math.min(entries.length, 9); i++) 
    			array[i] = (Integer)entries[i].getValue() > 0 ? new Object[] {entries[i].getKey(), entries[i].getValue()} : null;
    		array[9] = recipe.resource;
    		array[10] = machine;
    		generator.addRecipe(template, array);
    	}
    }
}
