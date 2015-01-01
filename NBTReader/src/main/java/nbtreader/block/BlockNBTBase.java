package nbtreader.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

import nbtreader.common.NBTReader;
import nbtreader.tileentity.TileEntityNBTBase;
import nbtreader.tileentity.TileEntityNBTSorter;

/**
 * @author thislooksfun
 */
public class BlockNBTBase extends BlockContainer
{
	private final IIcon[][] icons = new IIcon[2][];
	
	public BlockNBTBase()
	{
		super(Material.iron);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i, float f1, float f2, float f3)
	{
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		
		if (tileEntity == null || player.isSneaking())
		{
			return false;
		}
		
		player.openGui(NBTReader.instance(), world.getBlockMetadata(x, y, z), world, x, y, z);
		return true;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		dropItems(world, x, y, z);
		super.breakBlock(world, x, y, z, block, meta);
	}
	
	private void dropItems(World world, int x, int y, int z)
	{
		Random rand = new Random();
		
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (!(tileEntity instanceof IInventory))
		{
			return;
		}
		IInventory inventory = (IInventory)tileEntity;
		
		for (int i = 0; i < inventory.getSizeInventory(); i++)
		{
			ItemStack item = inventory.getStackInSlot(i);
			
			if (item != null && item.stackSize > 0)
			{
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;
				
				EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz, item.copy());
				
				float factor = 0.05F;
				entityItem.motionX = rand.nextGaussian() * factor;
				entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
				entityItem.motionZ = rand.nextGaussian() * factor;
				world.spawnEntityInWorld(entityItem);
				item.stackSize = 0;
			}
		}
	}
	
	@Override
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side)
	{
		switch (access.getBlockMetadata(x, y, z))
		{
			case 0:
				return this.icons[0][0];
			case 1:
				TileEntityNBTSorter te = (TileEntityNBTSorter)access.getTileEntity(x, y, z);
				if (side == te.in.ordinal())
				{
					return this.icons[1][1];
				} else if (side == te.out.ordinal())
				{
					return this.icons[1][2];
				} else
				{
					return this.icons[1][0];
				}
		}
		
		return this.icons[0][0];
	}
	
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item)
	{
		super.onBlockPlacedBy(world, x, y, z, entity, item);
		
		world.setBlock(x, y, z, ((ItemBlock)item.getItem()).field_150939_a, item.getItemDamage(), 3);
	}
	@Override
	public IIcon getIcon(int side, int meta)
	{
		switch (meta)
		{
			case 0:
				return this.icons[0][0];
			case 1:
				switch (side)
				{
					case 4:
						return this.icons[1][1];
					case 5:
						return this.icons[1][2];
					default:
						return this.icons[1][0];
				}
		}
		
		return this.icons[0][0];
	}
	
	@Override
	public int damageDropped(int meta)
	{
		return meta;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		list.add(new ItemStack(item, 1, 0)); //Reader
		list.add(new ItemStack(item, 1, 1)); //Sorter
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		switch (meta)
		{
			case 0:
				return new TileEntityNBTBase("NBT Reader");
			case 1:
				return new TileEntityNBTSorter();
		}
		return null;
	}
	
	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		this.icons[0] = new IIcon[1];
		this.icons[0][0] = register.registerIcon(NBTReader.MODID + ":reader.main");
		
		this.icons[1] = new IIcon[3];
		this.icons[1][0] = register.registerIcon(NBTReader.MODID + ":sorter.main");
		this.icons[1][1] = register.registerIcon(NBTReader.MODID + ":sorter.in");
		this.icons[1][2] = register.registerIcon(NBTReader.MODID + ":sorter.out");
	}
}