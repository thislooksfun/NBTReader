package com.tlf.nbtreader.common;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.tlf.nbtreader.block.BlockNBTBase;
import com.tlf.nbtreader.handler.GuiHandler;
import com.tlf.nbtreader.item.ItemNBTBlock;
import com.tlf.nbtreader.network.PacketReaderInfo;
import com.tlf.nbtreader.tileentity.TileEntityNBTReader;
import com.tlf.nbtreader.tileentity.TileEntityNBTSorter;
import com.tlf.nbtreader.util.LogHelper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * @author thislooksfun
 */
@Mod(name = NBTReader.NAME, modid = NBTReader.MODID, version = NBTReader.VERSION)
public class NBTReader
{
	public static final String NAME = "NBT Reader";
	public static final String MODID = "com/tlf/nbtreader";
	public static final String VERSION = "1.0.0";
	
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
		
		GameRegistry.registerTileEntity(TileEntityNBTReader.class, "nbtreader:NBTTE");
		GameRegistry.registerTileEntity(TileEntityNBTSorter.class, "nbtreader:NBTTESorter");
		
		GameRegistry.addRecipe(new ItemStack(nbtBlocks, 1, 0), "srs", "rbr", "srs", 's', Blocks.stone, 'r', Items.redstone, 'b', Items.book);
		GameRegistry.addRecipe(new ItemStack(nbtBlocks, 1, 1), "h", "r", "h", 'h', Blocks.hopper, 'r', new ItemStack(nbtBlocks, 1, 0));
		
		this.network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		this.network.registerMessage(PacketReaderInfo.Handler.class, PacketReaderInfo.class, 0, Side.SERVER);
	}
	
	@Mod.EventHandler
	@SuppressWarnings("UnusedParameters")
	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
	}
	
	@Mod.EventHandler
	@SuppressWarnings({"UnusedParameters", "EmptyMethod"})
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