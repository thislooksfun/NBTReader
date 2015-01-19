/**
 * @author thislooksfun
 */

package com.tlf.nbtreader.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

/** Provides easy XYZ coord handling, as well as some methods to make world manipulation easier */
public class Coords
{
	/** The world object */
	public final World world;
	/** The x coord */
	public final int x;
	/** The y coord */
	public final int y;
	/** The z coord */
	public final int z;
	
	public Coords(World world, int x, int y, int z)
	{
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/** Returns the block at this location */
	public Block getBlock()
	{
		return this.world.getBlock(this.x, this.y, this.z);
	}
	/** Returns the block at this location */
	public Block getBlockInDir(ForgeDirection dir)
	{
		return this.world.getBlock(this.x + dir.offsetX, this.y + dir.offsetY, this.z + dir.offsetZ);
	}
	/** Returns the neighbors of this block */
	public Block[][][] getNeighbors()
	{
		Block[][][] blocks = new Block[3][3][3];
		
		for (int i = -1; i < 2; i++)
		{
			for (int j = -1; j < 2; j++)
			{
				for (int k = -1; k < 2; k++)
				{
					blocks[i + 1][j + 1][k + 1] = this.world.getBlock(this.x + i, this.y + j, this.z + k);
				}
			}
		}
		
		return blocks;
	}
	/** Sets the block at this location to the specified block */
	public void setBlock(Block block)
	{
		this.setBlockWithMetadata(block, 0);
	}
	/** Sets the block and metadata at this location to the specified values */
	public void setBlockWithMetadata(Block block, int meta)
	{
		this.world.setBlock(this.x, this.y, this.z, block, meta, 2);
	}
	/** */
	public boolean isBlockSideSolid(ForgeDirection dir)
	{
		return this.getBlock().isSideSolid(this.world, this.x, this.y, this.z, dir);
	}
	/** Sets the block at this location to air */
	public void setAir()
	{
		this.world.setBlockToAir(this.x, this.y, this.z);
	}
	/** Returns the metadata at this location */
	public int getMetadata()
	{
		return this.world.getBlockMetadata(this.x, this.y, this.z);
	}
	/** Returns the TileEntity at this location */
	public TileEntity getTileEntity()
	{
		return this.world.getTileEntity(this.x, this.y, this.z);
	}
	/** Returns true if this location has a tileentity */
	public boolean hasTileEntity()
	{
		return this.getTileEntity() != null;
	}
	/** Removes the TileEntity at this location */
	public void removeTileEntity()
	{
		this.world.removeTileEntity(this.x, this.y, this.z);
	}
	/** Removes the TileEntity at this location, and returns it */
	public TileEntity getAndRemoveTileEntity()
	{
		TileEntity te = this.getTileEntity();
		this.removeTileEntity();
		te.validate();
		return te;
	}
	/** Marks the block as requiring an update */
	public void markForUpdate(boolean requireServerSide)
	{
		if (!requireServerSide || !this.world.isRemote)
		{
			this.world.markBlockForUpdate(this.x, this.y, this.z);
		}
	}
	/** Marks the block as requiring an update */
	public void notifyChange()
	{
		this.world.notifyBlockChange(this.x, this.y, this.z, this.getBlock());
		LogHelper.info("Changed!");
	}
	/** Marks the block as requiring a re-render (Only works on the client side [world.isRemote == true]) */
	public void markForRenderUpdate()
	{
		if (this.world.isRemote)
		{
			Minecraft.getMinecraft().renderGlobal.markBlockForRenderUpdate(this.x, this.y, this.z);
			LogHelper.info("Marked for re-render at " + this.toString());
		}
	}
	
	public Coords getCoordsInDir(ForgeDirection dir)
	{
		return new Coords(this.world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
	}
	
	public Coords getCoordsOppositeDir(ForgeDirection dir)
	{
		return this.getCoordsInDir(dir.getOpposite());
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Coords)
		{
			Coords compare = (Coords)obj;
			if (compare.world.provider.dimensionId == this.world.provider.dimensionId && compare.x == this.x && compare.y == this.y && compare.z == this.z)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/** Returns a copy of this object */
	public Coords copy()
	{
		return new Coords(this.world, this.x, this.y, this.z);
	}
	
	@Override
	public String toString()
	{
		return String.format("(%s: %s, %s, %s)", this.world.provider.dimensionId, this.x, this.y, this.z);
	}
	
	public static Coords fromString(String s)
	{
		if (!s.matches("\\(\\d+: \\d+, \\d+, \\d+\\)")) return null;
		
		s = s.substring(1, s.length() - 1);
		
		int index = s.indexOf(":");
		int id = Integer.parseInt(s.substring(0, index));
		
		World world = DimensionManager.getWorld(id);
		int x = Integer.parseInt(s.substring(index + 1, index = s.indexOf(",", index + 1)).trim());
		int y = Integer.parseInt(s.substring(index + 1, index = s.indexOf(",", index + 1)).trim());
		int z = Integer.parseInt(s.substring(index + 1).trim());
		
		return new Coords(world, x, y, z);
	}
}