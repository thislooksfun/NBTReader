package com.tlf.nbtreader.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import com.tlf.nbtreader.util.Colors;

/**
 * @author thislooksfun
 */
public class GuiOtherButton extends Gui
{
	protected float centerX;
	protected float centerY;
	
	protected int left;
	protected int top;
	
	protected int width;
	protected int height;
	
	protected int color;
	protected int textColor = Colors.TEXT_COLOR;
	
	protected String str;
	
	public boolean visible = true;
	
	private FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
	
	public GuiOtherButton(int width, int height, String s)
	{
		this.width = width;
		this.height = height;
		this.str = s;
	}
	
	@SuppressWarnings("UnusedReturnValue")
	public GuiOtherButton setPos(int left, int top)
	{
		this.left = left;
		this.top = top;
		return this;
	}
	
	public GuiOtherButton setColor(int color)
	{
		this.color = color;
		return this;
	}
	public GuiOtherButton setTextColor(int color)
	{
		this.textColor = color;
		return this;
	}
	
	public int top()
	{
		return this.top;
	}
	public int bottom()
	{
		return this.top + this.height;
	}
	public int width()
	{
		return this.width;
	}
	public int height()
	{
		return this.height;
	}
	
	public void render()
	{
		if (!this.visible) return;
		drawRect(this.left, this.top, this.left + this.width, this.top + this.height, this.color);
		this.drawCenteredString(this.fontRenderer, this.str, this.left + (this.width / 2), this.top + (this.height / 2) - (this.fontRenderer.FONT_HEIGHT / 2), this.textColor);
	}
	
	public boolean wasClicked(int mouseX, int mouseY, int button)
	{
		return button == 0 && mouseX >= this.left && mouseY >= this.top && mouseX < this.left + this.width && mouseY < this.top + height;
	}
}