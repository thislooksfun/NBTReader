package com.tlf.nbtreader.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import com.tlf.nbtreader.client.gui.GuiReader;
import com.tlf.nbtreader.client.gui.GuiSorter;
import com.tlf.nbtreader.inventory.container.ContainerNBTBase;
import com.tlf.nbtreader.tileentity.TileEntityNBTReader;
import com.tlf.nbtreader.tileentity.TileEntityNBTSorter;

/**
 * @author thislooksfun
 */
public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (ID)
		{
			case 0:
				return new ContainerNBTBase((TileEntityNBTReader)world.getTileEntity(x, y, z), player);
		}
		
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (ID)
		{
			case 0:
				return new GuiReader((TileEntityNBTReader)world.getTileEntity(x, y, z), player);
			case 1:
				return new GuiSorter((TileEntityNBTSorter)world.getTileEntity(x, y, z));
		}
		
		return null;
	}
}