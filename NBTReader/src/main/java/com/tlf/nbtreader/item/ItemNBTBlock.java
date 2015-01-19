package com.tlf.nbtreader.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * @author thislooksfun
 */
public class ItemNBTBlock extends ItemBlock
{
	public ItemNBTBlock(Block b)
	{
		super(b);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return super.getUnlocalizedName(stack)+"."+(stack.getItemDamage() == 0 ? "reader" : "sorter");
	}
}