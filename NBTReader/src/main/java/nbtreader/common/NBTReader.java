package nbtreader.common;

import net.minecraft.block.Block;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import nbtreader.block.BlockNBTBase;
import nbtreader.handler.GuiHandler;
import nbtreader.item.ItemNBTBlock;
import nbtreader.network.PacketReaderInfo;
import nbtreader.tileentity.TileEntityNBTBase;
import nbtreader.tileentity.TileEntityNBTSorter;
import nbtreader.util.LogHelper;

/**
 * @author thislooksfun
 */
@Mod(name = NBTReader.NAME, modid = NBTReader.MODID, version = NBTReader.VERSION)
public class NBTReader
{
	public static final String NAME = "NBT Reader";
	public static final String MODID = "nbtreader";
	public static final String VERSION = "0.0.1";
	
	public static Block nbtBlocks;
	
	@Mod.Instance(NBTReader.MODID)
	private static NBTReader instance;
	
	private SimpleNetworkWrapper network;
	
	@Mod.EventHandler
	@SuppressWarnings("UnusedParameters")
	public void preInit(FMLPreInitializationEvent event)
	{
		LogHelper.init(event.getModLog());
		
		nbtBlocks = new BlockNBTBase().setBlockName("nbtreader:nbtblock");
		
		GameRegistry.registerBlock(nbtBlocks, ItemNBTBlock.class, "nbtreader.tile.nbtblock");
		
		GameRegistry.registerTileEntity(TileEntityNBTBase.class, "nbtreader:NBTTE");
		GameRegistry.registerTileEntity(TileEntityNBTSorter.class, "nbtreader:NBTTESorter");
		
		this.network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		this.network.registerMessage(PacketReaderInfo.Handler.class, PacketReaderInfo.class, 0, Side.SERVER);
		this.network.registerMessage(PacketReaderInfo.Handler.class, PacketReaderInfo.class, 1, Side.CLIENT);
	}
	
	@Mod.EventHandler
	@SuppressWarnings("UnusedParameters")
	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
	}
	
	@Mod.EventHandler
	@SuppressWarnings("UnusedParameters")
	public void postInit(FMLPostInitializationEvent event) {}
	
	public static NBTReader instance()
	{
		return instance;
	}
	public static SimpleNetworkWrapper network()
	{
		return instance.network;
	}
}