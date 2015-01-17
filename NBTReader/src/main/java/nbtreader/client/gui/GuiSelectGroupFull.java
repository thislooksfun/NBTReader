package nbtreader.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;

import java.util.ArrayList;

import nbtreader.util.Colors;
import org.lwjgl.input.Mouse;

/**
 * @author thislooksfun
 */
public class GuiSelectGroupFull extends Gui
{
	private static final int PERPAGE = 11;
	private FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
	
	private int top;
	private int left;
	private int width;
	private int height;
	
	private int start = 0;
	
	private GuiOtherButton done;
	private GuiOtherButton add;
	
	private ArrayList<GuiTextField> fields = new ArrayList<GuiTextField>();
	private ArrayList<GuiOtherButton> rmvButtons = new ArrayList<GuiOtherButton>();
	
	public GuiSelectGroupFull(int top, int left, int width, int height, ArrayList<String> data)
	{
		this.width = width;
		this.height = height;
		
		this.done = new GuiOtherButton(30, 11, "Done").setColor(Colors.rgba(0, 200, 0, 255));
		this.add = new GuiOtherButton(30, 11, "+").setColor(Colors.rgba(0, 200, 0, 255));
		this.updatePos(top, left);
		
		for (String s : data)
		{
			GuiTextField f = new GuiTextField(this.fr, this.left + 16, 0, this.width - 28, 12);
			f.setText(s);
			this.fields.add(f);
			this.genButton();
		}
	}
	
	private void genButton()
	{
		GuiOtherButton b = new GuiOtherButton(8, 8, "-").setColor(Colors.rgba(200, 0, 0, 255));
		b.left = this.left + 6;
		this.rmvButtons.add(b);
	}
	
	private void remove(int i)
	{
		this.rmvButtons.remove(i);
		this.fields.remove(i);
		this.checkStart();
	}
	
	public void updatePos(int top, int left)
	{
		this.top = top;
		this.left = left;
		this.done.setPos(left + this.width - 36, top+6);
		this.add.setPos(left + (this.width - this.add.width) / 2, 0);
	}
	
	public void render()
	{
		this.renderBackground();
		this.renderForeground();
	}
	
	private void renderBackground()
	{
		this.rect(5, 5, this.width - 10, this.height - 10, Colors.rgba(150, 100, 100, 255));
		this.done.render();
		this.add.render();
	}
	
	private void renderForeground()
	{
		this.drawCenteredString(this.fr, "Edit NBT tags", this.left + (this.width / 2), this.top + 6, Colors.TEXT_COLOR);
		
		int max = (this.fields.size() - this.start > PERPAGE ? PERPAGE : this.fields.size() - this.start);
		for (int i = 0; i < max; i++)
		{
			GuiTextField f = this.fields.get(this.start + i);
			GuiOtherButton b = this.rmvButtons.get(this.start + i);
			
			f.setVisible(true);
			f.yPosition = this.top + 19 + (i * 16);
			b.top = f.yPosition + 2;
			f.drawTextBox();
			b.render();
		}
		
		if (this.start + PERPAGE > this.fields.size())
		{
			this.add.top = this.top + 19 + (max * 16);
			this.add.visible = true;
		} else
			this.add.visible = false;
		
		if (this.fields.size() + 1 > PERPAGE)
			this.drawScrollBar();
	}
	
	private void drawScrollBar()
	{
		this.rect(this.width - 9, 24, 4, this.height - 29, Colors.rgba(0, 150, 0, 255));
		
		int height = 15;
		float percent = ((float)this.start / (this.fields.size() + 1 - PERPAGE));
		int offset = (int)(percent * (this.height - 31 - height));
		
		this.rect(this.width - 8, 25 + offset, 2, height, Colors.rgba(0, 255, 124, 255));
	}
	
	private void rect(int left, int top, int width, int height, int color)
	{
		drawRect(this.left + left, this.top + top, this.left + left + width, this.top + top + height, color);
	}
	
	public void mouseInput()
	{
		int i = Mouse.getEventDWheel();
		
		if (i != 0 && (this.fields.size() + 1) > PERPAGE)
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
		if (this.start > (this.fields.size() + 1) - PERPAGE)
			this.start = (this.fields.size() + 1) - PERPAGE;
		
		if (this.start < 0)
			this.start = 0;
	}
	
	public void keyPress(char ch, int key)
	{
		for (GuiTextField f : this.fields)
			f.textboxKeyTyped(ch, key);
	}
	
	public void onClick(int x, int y, int button)
	{
		if (this.add.wasClicked(x, y, button))
		{
			this.fields.add(new GuiTextField(this.fr, this.left + 16, 0, this.width - 28, 12));
			this.genButton();
		}
		else
		{
			int max = (this.rmvButtons.size() - this.start > PERPAGE ? PERPAGE : this.rmvButtons.size() - this.start);
			int rmv = -1;
			for (int i = 0; i < max; i++)
			{
				GuiOtherButton b = this.rmvButtons.get(this.start + i);
				if (b.wasClicked(x, y, button))
					rmv = this.start + i;
			}
			
			if (rmv > -1)
				this.remove(rmv);
			
			for (GuiTextField f : this.fields)
				f.mouseClicked(x, y, button);
		}
	}
	
	public boolean closeClick(int x, int y, int button)
	{
		return this.done.wasClicked(x, y, button);
	}
	
	public ArrayList<String> toArray()
	{
		ArrayList<String> arr = new ArrayList<String>();
		
		for (GuiTextField f : this.fields)
			arr.add(f.getText());
		
		return arr;
	}
}