package nbtreader.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import nbtreader.tileentity.TileEntityNBTReader;

/**
 * @author thislooksfun
 */
public class ContainerNBTBase extends Container
{
	public final TileEntityNBTReader tile;
	
	public ContainerNBTBase(TileEntityNBTReader te, EntityPlayer player)
	{
		this.tile = te;
		this.addSlotToContainer(new CustomSlot(te, 0, 21, 154)); //Inventory slot
		
		for (int i = 0; i < 9; i++)
		{
			this.addSlotToContainer(new Slot(player.inventory, i, 56 + (i * 18), 209)); //Hotbar
		}
		
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				this.addSlotToContainer(new Slot(player.inventory, (i * 9) + j + 9, 56 + (j * 18), 151 + (i * 18))); //Main player inventory
			}
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return this.tile.isUseableByPlayer(player);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
	{
		ItemStack stack = null;
		Slot slot = this.getSlot(slotIndex);
		
		if (slot != null && slot.getHasStack())
		{
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();
			
			if (slotIndex == 0)
			{
				if (!this.mergeItemStack(stack1, 1, 37, false))
				{
					return null;
				}
			} else if (slotIndex >= 1 && slotIndex <= 36)
			{
				if (!this.mergeItemStack(stack1, 0, 1, false))
				{
					return null;
				}
			}
			
			if (stack1.stackSize == 0)
			{
				slot.putStack(null);
			}
			else
			{
				slot.onSlotChanged();
			}
			
			if (stack1.stackSize == stack.stackSize)
			{
				return null;
			}
			
			slot.onPickupFromSlot(player, stack);
		}
		
		return stack;
	}
	
	public static class CustomSlot extends Slot
	{
		public CustomSlot(IInventory inv, int invPos, int x, int y)
		{
			super(inv, invPos, x, y);
		}
		
		@Override
		public void putStack(ItemStack p_75215_1_)
		{
			super.putStack(p_75215_1_);
			((TileEntityNBTReader)this.inventory).triggerBool = true;
		}
		
		@Override
		public void onSlotChanged()
		{
			super.onSlotChanged();
			((TileEntityNBTReader)this.inventory).triggerBool = true;
		}
	}
}