package nbtreader.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.ArrayList;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import nbtreader.tileentity.TileEntityNBTSorter;
import nbtreader.util.Coords;
import nbtreader.util.LogHelper;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author thislooksfun
 */
public class PacketReaderInfo implements IMessage
{
	/** The type of this packet - -1 = uninitialized properly, 0 = all, 1 = directions, 2 = matchtype, 3 = data array */
	private int type = -1;
	private ForgeDirection in = ForgeDirection.UNKNOWN;
	private ForgeDirection out = ForgeDirection.UNKNOWN;
	private boolean matchType = false;
	private ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
	private Coords pos;
	
	public PacketReaderInfo()
	{
	}
	public PacketReaderInfo(Coords c, ForgeDirection in, ForgeDirection out)
	{
		this(c, in, out, false, new ArrayList<ArrayList<String>>(), 1);
	}
	public PacketReaderInfo(Coords c, boolean matchType)
	{
		this(c, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, false, new ArrayList<ArrayList<String>>(), 2);
	}
	public PacketReaderInfo(Coords c, ArrayList<ArrayList<String>> arr)
	{
		this(c, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, false, arr, 3);
	}
	public PacketReaderInfo(Coords c, ForgeDirection in, ForgeDirection out, boolean matchType, ArrayList<ArrayList<String>> arr)
	{
		this(c, in, out, matchType, arr, 0);
	}
	private PacketReaderInfo(Coords c, ForgeDirection in, ForgeDirection out, boolean matchType, ArrayList<ArrayList<String>> arr, int type)
	{
		this.type = type;
		
		this.pos = c;
		this.in = in;
		this.out = out;
		this.matchType = matchType;
		this.data = arr;
	}
	
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		if (this.type < 0) throw new IllegalStateException("Can't encode an empty packet!");
//		if (this.pos == null) throw new IllegalArgumentException("Unknown position!");
		if (this.pos == null)
		{
			LogHelper.warn("Unknown position!");
			return;
		}
		
		buf.writeInt(this.type);                                //Type
		buf.writeInt(this.in.ordinal());                        //In direction
		buf.writeInt(this.out.ordinal());                       //Out direction
		buf.writeBoolean(this.matchType);                       //Match type
		ByteBufUtils.writeUTF8String(buf, this.pos.toString()); //Position
		
		
		NBTTagCompound tag = new NBTTagCompound();
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
		ByteBufUtils.writeTag(buf, tag);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.type = buf.readInt();                                      //Type
		this.in = ForgeDirection.getOrientation(buf.readInt());         //In direction
		this.out = ForgeDirection.getOrientation(buf.readInt());        //Out direction
		this.matchType = buf.readBoolean();                             //Match type
		this.pos = Coords.fromString(ByteBufUtils.readUTF8String(buf)); //Position
		
		this.data.clear();
		NBTTagList matches = ByteBufUtils.readTag(buf).getTagList("matches", 9);
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
	
	public int type()
	{
		return this.type;
	}
	public ForgeDirection in()
	{
		return this.in;
	}
	public ForgeDirection out()
	{
		return this.out;
	}
	public boolean matchType()
	{
		return this.matchType;
	}
	public ArrayList<ArrayList<String>> data()
	{
		return this.data;
	}
	
	
	public static class Handler implements IMessageHandler<PacketReaderInfo, IMessage>
	{
		@Override
		public IMessage onMessage(PacketReaderInfo message, MessageContext ctx)
		{
			((TileEntityNBTSorter)message.pos.getTileEntity()).onPacket(message, ctx.side);
			return null; //Response
		}
	}
}