package nbtreader.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

import nbtreader.common.NBTReader;
import nbtreader.tileentity.TileEntityNBTSorter;
import nbtreader.util.ColorHelper;
import nbtreader.util.Colors;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * @author thislooksfun
 */
public class GuiSorter extends GuiScreen
{
	private static final ResourceLocation img = new ResourceLocation(NBTReader.MODID, "textures/gui/sorter.png");
	
	private ArrayList<String> displayStrings = new ArrayList<String>();
	private int start = 0;
	
	private TileEntityNBTSorter te;
	
	private int xSize;
	private int ySize;
	
	private int top;
	private int left;
	
	GuiCustomButton buttonMatchType;
	GuiCustomButton buttonInDir;
	GuiCustomButton buttonOutDir;
	
	public GuiSorter(TileEntityNBTSorter te)
	{
		this.te = te;
		
		this.xSize = 224;
		this.ySize = 151;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void initGui()
	{
		super.initGui();
		
		this.buttonList.add(this.buttonMatchType = new GuiCustomButton(0, this.left, this.top, 60, 15, this.te.matchType ? "Match: All" : "Match: Any"));
		this.buttonList.add(this.buttonInDir = new GuiCustomButton(1, this.left, this.top + 30, 60, 15, "In: " + this.te.in.name().substring(0, 1) + this.te.in.name().substring(1).toLowerCase()));
		this.buttonList.add(this.buttonOutDir = new GuiCustomButton(2, this.left, this.top + 60, 60, 15, "Out: " + this.te.out.name().substring(0, 1) + this.te.out.name().substring(1).toLowerCase()));
	}
	
	@Override
	public void drawScreen(int i1, int i2, float f1)
	{
		this.drawBackground();
		this.drawForeground();
		super.drawScreen(i1, i2, f1);
	}
	
	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height)
	{
		super.setWorldAndResolution(mc, width, height);
		this.left = (this.width - this.xSize) / 2;
		this.top = (this.height - this.ySize) / 2;
		
		this.buttonMatchType.xPosition = this.left + 15;
		this.buttonMatchType.yPosition = this.top + 10;
		
		this.buttonInDir.xPosition = this.left + 80;
		this.buttonInDir.yPosition = this.top + 10;
		
		this.buttonOutDir.xPosition = this.left + 145;
		this.buttonOutDir.yPosition = this.top + 10;
	}
	
	protected void drawBackground()
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(img);
		this.drawTexturedModalRect(this.left, this.top, 0, 0, this.xSize, this.ySize);
	}
	
	protected void drawForeground()
	{
		this.rect(5, 5, 214, 141, Colors.rgba(0, 11, 121, 255));
		
		this.drawScrollBar();
	}
	
	private void rect(int left, int top, int width, int height, int color)
	{
		drawRect(this.left + left, this.top + top, this.left + left + width, this.top + top + height, color);
	}
	
	private void drawScrollBar()
	{
		this.rect(this.xSize - 9, 5, 4, 141, Colors.rgba(0, 150, 0, 255));
		
		int height = 15;
		float percent = ((float)this.start / (this.displayStrings.size() - 14));
		int offset = (int)(percent * (127 - height));
		
		this.rect(this.xSize - 8, 6 + offset, 2, 6 + offset + height, Colors.rgba(0, 255, 124, 255));
	}
	
	private void drawString(String s, int left, int top)
	{
		this.drawString(s, left, top, this.xSize - 20);
	}
	private void drawString(String s, int left, int top, int maxWidth)
	{
		this.drawString(this.fontRendererObj, ColorHelper.limitToLengthExcludingCodes(s, maxWidth), left, top, Colors.WHITE);
	}
	
	public void handleMouseInput()
	{
		super.handleMouseInput();
		int i = Mouse.getEventDWheel();
		
		if (i != 0 && this.displayStrings.size() > 14)
		{
			if (i > 0)
			{
				start -= 1;
			}
			if (i < 0)
			{
				start += 1;
			}
			
			if (this.start < 0)
			{
				this.start = 0;
			}
			
			if (this.start > this.displayStrings.size() - 14)
			{
				this.start = this.displayStrings.size() - 14;
			}
		}
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
				this.te.updateMatchType();
				break;
			case 1:
				//In button
				int inDir = this.te.in.ordinal() + 1;
				
				if (ForgeDirection.getOrientation(inDir) == ForgeDirection.UNKNOWN) inDir = 0;
				if (inDir == this.te.out.ordinal()) inDir++;
				if (ForgeDirection.getOrientation(inDir) == ForgeDirection.UNKNOWN) inDir = 0;
				
				this.te.in = ForgeDirection.getOrientation(inDir);
				this.buttonInDir.displayString = "In: " + this.te.in.name().substring(0, 1) + this.te.in.name().substring(1).toLowerCase();
				
				this.te.updateDirs();
				
				break;
			case 2:
				//Out button
				int outDir = this.te.out.ordinal() + 1;
				
				if (ForgeDirection.getOrientation(outDir) == ForgeDirection.UNKNOWN) outDir = 0;
				if (outDir == this.te.in.ordinal()) outDir++;
				if (ForgeDirection.getOrientation(outDir) == ForgeDirection.UNKNOWN) outDir = 0;
				
				this.te.out = ForgeDirection.getOrientation(outDir);
				this.buttonOutDir.displayString = "Out: " + this.te.out.name().substring(0, 1) + this.te.out.name().substring(1).toLowerCase();
				
				this.te.updateDirs();
				
				break;
		}
	}
}