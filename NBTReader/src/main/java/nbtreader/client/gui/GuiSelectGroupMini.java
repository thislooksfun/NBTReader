package nbtreader.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import nbtreader.util.ColorHelper;
import nbtreader.util.Colors;

/**
 * @author thislooksfun
 */
public class GuiSelectGroupMini extends Gui
{
	private FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
	
	private int color = Colors.rgba(255, 0, 0);
	
	private int top;
	private int left;
	private int width;
	private int height;
	
	private GuiOtherButton removeThis;
	
	private String display;
	
	public GuiSelectGroupMini(int height)
	{
		this.height = height;
		
		this.removeThis = new GuiOtherButton(8, 10, "-").setColor(Colors.rgba(0, 200, 0)).setTextColor(Colors.TEXT_COLOR);
	}
	
	public void setString(String s)
	{
		this.display = s;
	}
	
	public void render(int top, int left, int width)
	{
		this.removeThis.setPos(left + 2, top + 2);
		
		String s = ColorHelper.limitToLengthExcludingCodes(this.display, width - 15);
		
		this.top = top;
		this.left = left;
		this.width = width;
		
		drawRect(left, top, left + width, top + this.height, this.color);
		this.drawString(this.fr, s, left + 13, top + 3, Colors.TEXT_COLOR);
		
		this.removeThis.render();
	}
	
	public boolean removeClick(int x, int y, int button)
	{
		return this.removeThis.wasClicked(x, y, button);
	}
	public boolean clicked(int x, int y, int button)
	{
		return button == 0 && x >= this.left && y >= this.top && x < this.left + this.width && y < this.top + height;
	}
}