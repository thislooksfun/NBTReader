package nbtreader.network;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import nbtreader.tileentity.TileEntityNBTSorter;
import nbtreader.util.Coords;
import nbtreader.util.LogHelper;

/**
 * @author thislooksfun
 */
public class PacketReaderInfo implements IMessage
{
	private Coords pos;
	private NBTTagCompound tag;
	
	public PacketReaderInfo()
	{
	}
	public PacketReaderInfo(Coords c, NBTTagCompound tag)
	{
		this.pos = c;
		this.tag = tag;
	}
	
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		if (this.pos == null)
		{
			LogHelper.warn("Unknown position!");
			return;
		}
		
		ByteBufUtils.writeUTF8String(buf, this.pos.toString()); //Position
		ByteBufUtils.writeTag(buf, this.tag); //NBTData
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		
		this.pos = Coords.fromString(ByteBufUtils.readUTF8String(buf)); //Position
		this.tag = ByteBufUtils.readTag(buf); //NBTData
	}
	
	public NBTTagCompound tag()
	{
		return this.tag;
	}
	
	public static class Handler implements IMessageHandler<PacketReaderInfo, IMessage>
	{
		@Override
		public IMessage onMessage(PacketReaderInfo message, MessageContext ctx)
		{
			((TileEntityNBTSorter)message.pos.getTileEntity()).onClientPacket(message);
			return null; //Response
		}
	}
}