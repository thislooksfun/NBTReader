package nbtreader.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;

import java.util.ArrayList;

import nbtreader.util.Colors;

/**
 * @author thislooksfun
 */
public class GuiSelectGroupFull extends Gui
{
	private static final int PERPAGE = 5;
	private FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
	
	private int top;
	private int left;
	private int width;
	private int height;
	
	private int start;
	
	private GuiOtherButton close;
	
	private ArrayList<GuiTextField> fields = new ArrayList<GuiTextField>();
	
	public GuiSelectGroupFull(int top, int left, int width, int height)
	{
		this.close = new GuiOtherButton(8, 10, "x").setColor(Colors.rgba(0, 200, 0, 255)).setTextColor(Colors.TEXT_COLOR);
	}
	
	public void updatePos(int top, int left)
	{
		this.top = top;
		this.left = left;
		this.close.setPos(left + width - 14, top + 6);
	}
	
	public void render()
	{
		this.renderBackground();
		this.renderForeground();
	}
	
	private void renderBackground()
	{
		drawRect(this.left + 5, this.top + 5, this.left + this.width - 5, this.top + this.height - 5, Colors.rgba(150, 100, 100, 255));
		this.close.render();
	}
	
	private void renderForeground()
	{
		//TODO Help bubble (explain empty text boxes get removed)
		int max = (this.fields.size() - this.start > PERPAGE ? PERPAGE : this.fields.size() - this.start);
		for (int i = 0; i < max; i++)
		{
			GuiTextField f = this.fields.get(this.start + i);
			f.yPosition = i * 10;
			f.drawTextBox();
		}
	}
	
	public boolean closeClick(int x, int y, int button)
	{
		return this.close.wasClicked(x, y, button);
	}
}