package nbtreader.tileentity;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;

import cpw.mods.fml.common.FMLCommonHandler;
import nbtreader.network.PacketReaderInfo;
import nbtreader.util.Coords;
import nbtreader.util.LogHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author thislooksfun
 */
public class TileEntityNBTSorter extends TileEntity
{
	private int nextTick = 0;
	
	private Coords pos;
	
	public ForgeDirection in;
	public ForgeDirection out;
	
	public ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
	
	private int stackPullSize = 64;
	
	/** True if matching all conditions, false if matching any */
	public boolean matchType = true;
	
	public TileEntityNBTSorter()
	{
		this.yCoord = -1; //To help with knowing if the position has been set - y can't be <0 in the world.
		
		this.in = ForgeDirection.SOUTH;
		this.out = ForgeDirection.NORTH;
		
//		ArrayList<String> arr = new ArrayList<String>();
//		arr.add("pages");
//		arr.add("0");
//		arr.add("abcd");
//		this.data.add(arr);
		
		ArrayList<String> arr = new ArrayList<String>();
		arr.add("asd");
		arr.add("list");
		arr.add("0");
		arr.add("var");
		arr.add("32.5");
		this.data.add(arr);
	}
	
	public Coords getPos()
	{
		if (this.pos == null && this.worldObj != null && this.yCoord > -1)
		{
			this.pos = new Coords(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		}
		
		return this.pos;
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		if (!this.worldObj.isRemote && this.nextTick-- <= 0)
		{
			this.nextTick = 5;
			this.pullItems();
		}
	}
	
	private void pullItems()
	{
		Coords c = this.getPos();
		if (c == null) return; //The position hasn't been set!
		
		TileEntity teIn = c.getCoordsInDir(this.in).getTileEntity();
		if (teIn == null || !(teIn instanceof IInventory)) return; //No TE or isn't an inventory!
		IInventory invIn = ((IInventory)teIn);
		
		ItemStack pulled = null;
		int pullSlot = -1;
		
		if (invIn instanceof ISidedInventory)
		{
			for (int slot : ((ISidedInventory)invIn).getAccessibleSlotsFromSide(this.in.getOpposite().ordinal()))
			{
				ItemStack s = invIn.getStackInSlot(slot);
				if (s != null && this.stackMatches(s))
				{
					pulled = s.copy();
					pullSlot = slot;
					break;
				}
			}
		} else
		{
			for (int slot = 0; slot < invIn.getSizeInventory(); slot++)
			{
				ItemStack s = invIn.getStackInSlot(slot);
				if (s != null && this.stackMatches(s))
				{
					pulled = s.copy();
					pullSlot = slot;
					break;
				}
			}
		}
		
		if (pulled == null)
		{
			return;
		}
		
		
		TileEntity teOut = c.getCoordsInDir(this.out).getTileEntity();
		if (teOut == null || !(teOut instanceof IInventory)) return; //No TE or isn't an inventory!
		IInventory invOut = ((IInventory)teOut);
		
		int size = pulled.stackSize > this.stackPullSize ? this.stackPullSize : pulled.stackSize;
		pulled.stackSize = size;
		
		if (mergeStacks(invOut, pulled))
		{
			invIn.decrStackSize(pullSlot, size);
		}
	}
	
	private boolean mergeStacks(IInventory inv, ItemStack stack)
	{
		if (stack == null) return false;
		
		if (inv instanceof ISidedInventory)
		{
			int[] availSlots = ((ISidedInventory)inv).getAccessibleSlotsFromSide(this.out.getOpposite().ordinal());
			for (int slot : availSlots)
			{
				ItemStack s = inv.getStackInSlot(slot);
				if (s != null && s.getItem() == stack.getItem() && s.stackSize + stack.stackSize <= s.getMaxStackSize())
				{
					s.stackSize += stack.stackSize;
					return true;
				}
			}
			
			for (int slot : availSlots)
			{
				ItemStack s = inv.getStackInSlot(slot);
				if (s == null)
				{
					inv.setInventorySlotContents(slot, stack);
					return true;
				}
			}
		} else
		{
			for (int i = 0; i < inv.getSizeInventory(); i++)
			{
				ItemStack s = inv.getStackInSlot(i);
				if (s != null && s.getItem() == stack.getItem() && s.stackSize + stack.stackSize <= s.getMaxStackSize())
				{
					s.stackSize += stack.stackSize;
					return true;
				}
			}
			
			for (int i = 0; i < inv.getSizeInventory(); i++)
			{
				ItemStack s = inv.getStackInSlot(i);
				if (s == null)
				{
					inv.setInventorySlotContents(i, stack);
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean stackMatches(ItemStack stack)
	{
		if (!stack.hasTagCompound()) return false; //No tag == nothing to compare
		
		for (int i = 0; i < this.data.size(); i++)
		{
			NBTBase currentTag = stack.getTagCompound().copy();
			ArrayList<String> arr = this.data.get(i);
			for (int j = 0; j < arr.size(); j++)
			{
				if (currentTag instanceof NBTBase.NBTPrimitive || currentTag instanceof NBTTagString)
				{
					if (j + 1 < arr.size()) return false; //Expected one more tag, found at least two
					
					if (currentTag instanceof NBTTagString)
						return ((NBTTagString)currentTag).func_150285_a_().equals(arr.get(j));
					
					try
					{
						switch (currentTag.getId())
						{
							case Constants.NBT.TAG_BYTE:
								return ((NBTTagByte)currentTag).func_150290_f() == Byte.parseByte(arr.get(j));
							case Constants.NBT.TAG_SHORT:
								return ((NBTTagShort)currentTag).func_150289_e() == Short.parseShort(arr.get(j));
							case Constants.NBT.TAG_LONG:
								return ((NBTTagLong)currentTag).func_150291_c() == Long.parseLong(arr.get(j));
							case Constants.NBT.TAG_FLOAT:
								return ((NBTTagFloat)currentTag).func_150288_h() == Float.parseFloat(arr.get(j));
							case Constants.NBT.TAG_DOUBLE:
								return ((NBTTagDouble)currentTag).func_150286_g() == Double.parseDouble(arr.get(j));
						}
					} catch (IllegalArgumentException e)
					{
						return false; //Wrong format (Probably not a number)
					}
				} else if (currentTag instanceof NBTTagByteArray)
				{
					if (j + 1 < arr.size()) return false;
					try
					{
						return ((NBTTagByteArray)currentTag).func_150292_c()[Integer.parseInt(arr.get(j))] == Byte.parseByte(arr.get(j + 1));
					} catch (IllegalArgumentException e)
					{
						return false;
					}
				} else if (currentTag instanceof NBTTagIntArray)
				{
					if (j + 1 < arr.size()) return false;
					try
					{
						return ((NBTTagIntArray)currentTag).func_150302_c()[Integer.parseInt(arr.get(j))] == Integer.parseInt(arr.get(j + 1));
					} catch (IllegalArgumentException e)
					{
						return false;
					}
				} else if (currentTag instanceof NBTTagList)
				{
					if (j + 1 >= arr.size()) return false;
					
					try
					{
						currentTag = ((NBTTagList)currentTag).removeTag(Integer.parseInt(arr.get(j)));
					} catch (IllegalArgumentException e)
					{
						return false;
					}
				} else if (currentTag instanceof NBTTagCompound)
				{
					currentTag = ((NBTTagCompound)currentTag).getTag(arr.get(j));
				} else
				{
					return false;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		
		tag.setBoolean("matchType", this.matchType);
		tag.setInteger("inDir", this.in.ordinal());
		tag.setInteger("outDir", this.out.ordinal());
		
		NBTTagList matches = new NBTTagList();
		
		for (ArrayList<String> arr : this.data)
		{
			NBTTagList list = new NBTTagList();
			for (String s : arr)
				list.appendTag(new NBTTagString(s));
			
			if (list.tagCount() > 0)
				matches.appendTag(list);
		}
		
		tag.setTag("matches", matches);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		
		this.matchType = tag.getBoolean("matchType");
		this.in = ForgeDirection.getOrientation(tag.getInteger("inDir"));
		this.out = ForgeDirection.getOrientation(tag.getInteger("outDir"));
		
		NBTTagList matches = tag.getTagList("matches", 9);
		
		this.data.clear();
		for (int i = 0; i < matches.tagCount(); i++)
		{
			NBTTagList l = (NBTTagList)matches.removeTag(0);
			ArrayList<String> arr = new ArrayList<String>();
			
			for (int j = 0; j < l.tagCount(); j++)
			{
				arr.add(l.getStringTagAt(j));
			}
			
			if (arr.size() > 0)
			{
				this.data.add(arr);
			}
		}
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, -1, tag);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		super.onDataPacket(net, pkt);
		this.readFromNBT(pkt.func_148857_g());
		LogHelper.info("Got packet on side " + FMLCommonHandler.instance().getSide());
		this.markDirty();
	}
	
	public void onClientPacket(PacketReaderInfo pkt)
	{
		this.readFromNBT(pkt.tag());
		this.markDirty();
	}
}