package nbtreader.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import nbtreader.client.gui.GuiReader;
import nbtreader.client.gui.GuiSorter;
import nbtreader.inventory.container.ContainerNBTBase;
import nbtreader.tileentity.TileEntityNBTReader;
import nbtreader.tileentity.TileEntityNBTSorter;

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