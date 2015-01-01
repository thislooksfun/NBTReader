package nbtreader.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import nbtreader.util.Colors;

/**
 * @author thislooksfun
 */
public class GuiCustomButton extends GuiButton
{
	private int color = Colors.rgba(70, 150, 25, 255);
	private int textColor = Colors.rgb(224, 224, 224);
	private int colorHover = Colors.rgba(90, 200, 40, 255);
	private int textColorHover = Colors.rgb(255, 255, 255);
	
	public GuiCustomButton(int id, int x, int y, String str)
	{
		this(id, x, y, 200, 20, str);
	}
	public GuiCustomButton(int id, int x, int y, int width, int height, String str)
	{
		super(id, x, y, width, height, str);
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (this.visible)
		{
			boolean hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			
			drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, (hovered ? this.colorHover : this.color));
			
			this.mouseDragged(mc, mouseX, mouseY);
			
			this.drawCenteredString(mc.fontRenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, (hovered ? this.textColorHover : this.textColor));
		}
	}
}