package nbtreader.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

/**
 * A base class for a single-slot TileEntity
 *
 * @author thislooksfun
 */
public class TileEntityNBTReader extends TileEntity implements ISidedInventory
{
	private static final int[] slotAccess = new int[]{0};
	private ItemStack item;
	public boolean triggerBool = false;
	
	@Override
	public int getSizeInventory()
	{
		return 1;
	}
	
	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return (slot == 0 ? this.item : null);
	}
	
	@Override
	public String getInventoryName()
	{
		return "NBT Reader";
	}
	
	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int amnt)
	{
		if (slot != 0)
		{
			return null;
		}
		
		ItemStack stack;
		if (this.item == null)
		{
			return null;
		}
		stack = this.item.copy();
		
		if (amnt >= stack.stackSize)
		{
			this.item = null;
		} else
		{
			stack.stackSize = amnt;
			this.item.stackSize -= amnt;
		}
		
		return stack;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		return slot == 0 ? this.item : null;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		if (slot == 0)
		{
			this.item = stack;
		}
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && player.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}
	
	@Override
	public void openInventory()
	{
	}
	
	@Override
	public void closeInventory()
	{
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack)
	{
		return true;
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		return slotAccess;
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side)
	{
		return true;
	}
	
	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side)
	{
		return true;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		NBTTagList nbttaglist = tag.getTagList("Items", 10);
		
		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte slot = nbttagcompound1.getByte("Slot");
			
			if (slot == 0)
			{
				this.item = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		NBTTagList nbttaglist = new NBTTagList();
		
		if (this.item != null)
		{
			NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setByte("Slot", (byte)0);
			this.item.writeToNBT(nbttagcompound1);
			nbttaglist.appendTag(nbttagcompound1);
		}
		
		tag.setTag("Items", nbttaglist);
	}
}