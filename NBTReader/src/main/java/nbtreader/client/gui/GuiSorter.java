package nbtreader.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

import nbtreader.common.NBTReader;
import nbtreader.network.PacketReaderInfo;
import nbtreader.tileentity.TileEntityNBTSorter;
import nbtreader.util.ColorHelper;
import nbtreader.util.Colors;
import nbtreader.util.LogHelper;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * @author thislooksfun
 */
public class GuiSorter extends GuiScreen
{
	private static final int elementsPerPage = 10;
	
	private static final ResourceLocation img = new ResourceLocation(NBTReader.MODID, "textures/gui/sorter.png");
	
	private int start = 0;
	
	private TileEntityNBTSorter te;
	
	private int xSize;
	private int ySize;
	
	private int top;
	private int left;
	
	GuiCustomButton buttonMatchType;
	GuiCustomButton buttonInDir;
	GuiCustomButton buttonOutDir;
	GuiCustomButton addGroup;
	
	private ArrayList<GuiSelectGroup> groups = new ArrayList<GuiSelectGroup>();
	
	private GuiSelectGroup currentGroup = null;
	
	public GuiSorter(TileEntityNBTSorter te)
	{
		this.te = te;
		
		this.xSize = 224;
		this.ySize = 200;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void initGui()
	{
		super.initGui();
		
		this.buttonList.add(this.buttonMatchType = new GuiCustomButton(0, 60, 15, this.te.matchType ? "Match: All" : "Match: Any"));
		this.buttonList.add(this.buttonInDir = new GuiCustomButton(1, 60, 15, "In: " + this.te.in.name().substring(0, 1) + this.te.in.name().substring(1).toLowerCase()));
		this.buttonList.add(this.buttonOutDir = new GuiCustomButton(2, 60, 15, "Out: " + this.te.out.name().substring(0, 1) + this.te.out.name().substring(1).toLowerCase()));
		this.buttonList.add(this.addGroup = new GuiCustomButton(3, 60, 11, "+"));
	}
	
	@Override
	public void drawScreen(int i1, int i2, float f1)
	{
		this.drawBackground();
		
		if (this.currentGroup != null)
			this.currentGroup.renderFull();
		else
		{
			this.drawForeground();
			super.drawScreen(i1, i2, f1);
		}
	}
	
	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height)
	{
		super.setWorldAndResolution(mc, width, height);
		this.left = (this.width - this.xSize) / 2;
		this.top = (this.height - this.ySize) / 2;
		
		this.buttonMatchType.xPosition = this.left + 17;
		this.buttonMatchType.yPosition = this.top + 7;
		
		this.buttonInDir.xPosition = this.left + 82;
		this.buttonInDir.yPosition = this.top + 7;
		
		this.buttonOutDir.xPosition = this.left + 147;
		this.buttonOutDir.yPosition = this.top + 7;
		
		this.addGroup.xPosition = this.left + (this.xSize - this.addGroup.width) / 2;
		
		this.loadData(this.te.data);
	}
	
	private void loadData(ArrayList<ArrayList<String>> data)
	{
		LogHelper.info("Loading "+data.size()+" items");
		for (ArrayList<String> arr : data)
			this.groups.add(new GuiSelectGroup(this.top, this.left, this.xSize, this.ySize, 14, arr));
	}
	
	protected void drawBackground()
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(img);
		this.drawTexturedModalRect(this.left, this.top, 0, 0, this.xSize, this.ySize);
	}
	
	protected void drawForeground()
	{
		this.rect(5, 5, this.xSize - 10, this.ySize - 10, Colors.rgba(0, 11, 121));
		this.rect(5, 5, this.xSize - 10, 19, Colors.rgba(50, 81, 121));
		
		int max = (this.groups.size() - this.start > elementsPerPage ? elementsPerPage : this.groups.size() - this.start);
		for (int i = 0; i < max; i++)
		{
			GuiSelectGroup g = this.groups.get(start + i);
			g.renderSub((this.top + 26 + (i * 17)), this.left + 7, this.xSize - 7 - ((this.groups.size() + 1 > elementsPerPage) ? 11 : 7));
		}
		
		if (this.start + elementsPerPage > this.groups.size())
		{
			this.addGroup.yPosition = this.top + 27 + (max * 17);
			this.addGroup.visible = true;
		} else
			this.addGroup.visible = false;
		
		if (this.groups.size() + 1 > elementsPerPage)
			this.drawScrollBar();
	}
	
	private void rect(int left, int top, int width, int height, int color)
	{
		drawRect(this.left + left, this.top + top, this.left + left + width, this.top + top + height, color);
	}
	
	private void drawScrollBar()
	{
		this.rect(this.xSize - 9, 24, 4, this.ySize - 29, Colors.rgba(0, 150, 0));
		
		int height = 15;
		float percent = ((float)this.start / (this.groups.size() + 1 - elementsPerPage));
		int offset = (int)(percent * (this.ySize - 31 - height));
		
		this.rect(this.xSize - 8, 25 + offset, 2, height, Colors.rgba(0, 255, 124));
	}
	
	private void drawString(String s, int left, int top)
	{
		this.drawString(s, left, top, this.xSize - 20);
	}
	private void drawString(String s, int left, int top, int maxWidth)
	{
		this.drawString(this.fontRendererObj, ColorHelper.limitToLengthExcludingCodes(s, maxWidth), left, top, Colors.WHITE);
	}
	
	private void addNewGroup()
	{
		this.groups.add(new GuiSelectGroup(this.top, this.left, this.xSize, this.ySize, 14));
	}
	private void removeGroup(int id)
	{
		this.groups.remove(id);
		this.checkStart();
	}
	
	@Override
	public void handleMouseInput()
	{
		super.handleMouseInput();
		
		if (this.currentGroup != null)
		{
			this.currentGroup.mouseInput();
			return;
		}
		
		int i = Mouse.getEventDWheel();
		
		if (i != 0 && (this.groups.size() + 1) > elementsPerPage)
		{
			if (i > 0)
				this.start--;
			else if (i < 0)
				this.start++;
			
			this.checkStart();
		}
	}
	
	private void checkStart()
	{
		if (this.start > (this.groups.size() + 1) - elementsPerPage)
			this.start = (this.groups.size() + 1) - elementsPerPage;
		
		if (this.start < 0)
			this.start = 0;
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button)
	{
		if (this.currentGroup != null)
		{
			if (this.currentGroup.closeClick(x, y, button))
			{
				this.currentGroup.onClose();
				this.currentGroup = null;
			} else
				this.currentGroup.onClick(x, y, button);
			
			return;
		}
		
		super.mouseClicked(x, y, button);
		
		int toRemove = -1;
		
		int max = (this.groups.size() - this.start > elementsPerPage ? elementsPerPage : this.groups.size() - this.start);
		for (int i = 0; i < max; i++)
		{
			GuiSelectGroup g = this.groups.get(start + i);
			if (g.removeClick(x, y, button))
				toRemove = i;
			else if (g.clicked(x, y, button))
				this.currentGroup = g;
		}
		
		if (toRemove > -1) this.removeGroup(toRemove);
	}
	
	@Override
	protected void keyTyped(char ch, int key)
	{
		if (this.currentGroup != null)
		{
			if (key == 1)
			{
				this.currentGroup.onClose();
				this.currentGroup = null;
			} else
				this.currentGroup.keyPress(ch, key);
		} else
			super.keyTyped(ch, key);
	}
	
	@Override
	protected void actionPerformed(GuiButton button)
	{
		super.actionPerformed(button);
		
		switch (button.id)
		{
			case 0:
				//MatchType button
				this.te.matchType = !this.te.matchType;
				this.buttonMatchType.displayString = this.te.matchType ? "Match: All" : "Match: Any";
				
				break;
			case 1:
				//In button
				int inDir = this.te.in.ordinal() + 1;
				
				if (ForgeDirection.getOrientation(inDir) == ForgeDirection.UNKNOWN) inDir = 0;
				if (inDir == this.te.out.ordinal()) inDir++;
				if (ForgeDirection.getOrientation(inDir) == ForgeDirection.UNKNOWN) inDir = 0;
				
				this.te.in = ForgeDirection.getOrientation(inDir);
				this.buttonInDir.displayString = "In: " + this.te.in.name().substring(0, 1) + this.te.in.name().substring(1).toLowerCase();
				
				break;
			case 2:
				//Out button
				int outDir = this.te.out.ordinal() + 1;
				
				if (ForgeDirection.getOrientation(outDir) == ForgeDirection.UNKNOWN) outDir = 0;
				if (outDir == this.te.in.ordinal()) outDir++;
				if (ForgeDirection.getOrientation(outDir) == ForgeDirection.UNKNOWN) outDir = 0;
				
				this.te.out = ForgeDirection.getOrientation(outDir);
				this.buttonOutDir.displayString = "Out: " + this.te.out.name().substring(0, 1) + this.te.out.name().substring(1).toLowerCase();
				
				break;
			case 3:
				//Add group button
				this.addNewGroup();
				if (this.groups.size() >= elementsPerPage)
					this.start++;
				break;
		}
	}
	
	@Override
	public void onGuiClosed()
	{
		//Re-render
		this.te.getPos().markForUpdate(false);
		
		//Update the client tile entity's matching rules
		this.te.data.clear();
		for (GuiSelectGroup g : this.groups)
			this.te.data.add(g.toArray());
		
		//Write the client tile entity to a NBT tag, and send it to the server
		NBTTagCompound tag = new NBTTagCompound();
		this.te.writeToNBT(tag);
		NBTReader.network().sendToServer(new PacketReaderInfo(this.te.getPos(), tag));
		
		//Close the GUI
		super.onGuiClosed();
	}
}