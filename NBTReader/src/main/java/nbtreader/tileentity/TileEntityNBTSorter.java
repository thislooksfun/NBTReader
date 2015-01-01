package nbtreader.tileentity;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import nbtreader.common.NBTReader;
import nbtreader.network.PacketReaderInfo;
import nbtreader.util.Coords;
import nbtreader.util.LogHelper;
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
	
	private ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
	
	/** True if matching all conditions, false if matching any */
	public boolean matchType = true;
	
	public TileEntityNBTSorter()
	{
		this.yCoord = -1; //To help with knowing if the position has been set - y can't be <0 in the world.
		
		this.in = ForgeDirection.SOUTH;
		this.out = ForgeDirection.NORTH;
		
		ArrayList<String> arr = new ArrayList<String>();
		arr.add("camelPackFill");
		arr.add("100");
		this.data.add(arr);
	}
	
	public Coords getPos()
	{
		if (this.pos == null && this.worldObj != null && this.yCoord > -1)
		{
			this.pos = new Coords(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			this.updateClients();
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
					pulled = s;
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
					pulled = s;
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
		
		if (mergeStacks(invOut, pulled))
		{
			invIn.decrStackSize(pullSlot, 1);
		}
	}
	
	private boolean mergeStacks(IInventory inv, ItemStack stack)
	{
		if (stack == null)
		{
			return false;
		}
		
		if (inv instanceof ISidedInventory)
		{
			int[] availSlots = ((ISidedInventory)inv).getAccessibleSlotsFromSide(this.out.getOpposite().ordinal());
			for (int slot : availSlots)
			{
				ItemStack s = inv.getStackInSlot(slot);
				if (s != null && s.getItem() == stack.getItem() && s.stackSize + stack.stackSize <= s.getMaxStackSize())
				{
					s.stackSize += stack.stackSize;
					inv.setInventorySlotContents(slot, s);
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
					inv.setInventorySlotContents(i, s);
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
		NBTTagCompound tag = stack.getTagCompound();
		
		for (int j = 0; j < this.data.size(); j++)
		{
			ArrayList<String> arr = this.data.get(j);
			for (int i = 0; i < arr.size(); j++)
			{
				NBTBase b = tag.getTag(arr.get(j));
				if (b != null)
				{
					if (b instanceof NBTBase.NBTPrimitive || b instanceof NBTTagString)
					{
						if (i + 1 >= arr.size()) return false; //Deeper tags expected, but not found
						
						if (b instanceof NBTTagString)
						{
							return arr.get(arr.size() - 1).equals(((NBTTagString)b).func_150285_a_());
						}
						
						try
						{
							switch (b.getId())
							{
								case 1:
									//Byte
									return Byte.parseByte(arr.get(arr.size() - 1)) == ((NBTTagByte)b).func_150290_f();
								case 2:
									//Short
									return Short.parseShort(arr.get(arr.size() - 1)) == ((NBTTagShort)b).func_150289_e();
								case 3:
									//Int
									return Integer.parseInt(arr.get(arr.size() - 1)) == ((NBTTagInt)b).func_150287_d();
								case 4:
									//Long
									return Long.parseLong(arr.get(arr.size() - 1)) == ((NBTTagLong)b).func_150291_c();
								case 5:
									//Float
									return Float.parseFloat(arr.get(arr.size() - 1)) == ((NBTTagFloat)b).func_150288_h();
								case 6:
									//Double
									return Double.parseDouble(arr.get(arr.size() - 1)) == ((NBTTagDouble)b).func_150286_g();
							}
						} catch (IllegalArgumentException e)
						{
							e.printStackTrace();
							return false;
						}
					} else
					{
						if (i + 1 >= arr.size()) return false; //Deeper tags found, but not expected
						
						switch (b.getId())
						{
							case 7:
								//ByteArray
								break;
							case 9:
								//TagList
								break;
							case 10:
								//Compound
								break;
							case 11:
								//IntArray
								break;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public void updateClients()
	{
		if (this.client()) return;
		NBTReader.network().sendToAll(new PacketReaderInfo(this.getPos(), this.in, this.out, this.matchType, this.data));
	}
	public void updateAll()
	{
		if (this.server()) return;
		NBTReader.network().sendToServer(new PacketReaderInfo(this.getPos(), this.in, this.out, this.matchType, this.data));
		this.markDirty();
	}
	public void updateDirs()
	{
		if (this.server()) return;
		NBTReader.network().sendToServer(new PacketReaderInfo(this.getPos(), this.in, this.out));
		this.markDirty();
	}
	public void updateMatchType()
	{
		if (this.server()) return;
		NBTReader.network().sendToServer(new PacketReaderInfo(this.getPos(), this.matchType));
		this.markDirty();
	}
	
	private boolean server()
	{
		Coords pos = this.getPos();
		return pos != null && !pos.world.isRemote;
	}
	private boolean client()
	{
		Coords pos = this.getPos();
		return pos != null && pos.world.isRemote;
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
		
		this.updateAll();
		this.updateClients();
	}
	
	public void onPacket(PacketReaderInfo p, Side side)
	{
		if (p.type() == -1)
		{
			LogHelper.error("Invalid type");
			return;
		}
		
		
		if (p.type() == 0 || p.type() == 1)
		{
			this.in = p.in();
			this.out = p.out();
		}
		if (p.type() == 0 || p.type() == 2)
		{
			this.matchType = p.matchType();
		}
		if (p.type() == 0 || p.type() == 3)
		{
			this.data = p.data();
		}
		
		if (side.isServer()) NBTReader.network().sendToAll(p);
		
		this.getPos().markForUpdate(false);
		this.getPos().markForRenderUpdate();
		this.getPos().notifyChange();
	}
}