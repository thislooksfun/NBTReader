package nbtreader.client.gui;

import net.minecraft.client.gui.Gui;

import java.util.ArrayList;

/**
 * @author thislooksfun
 */
public class GuiSelectGroup extends Gui
{
	private GuiSelectGroupFull fullGroup;
	private GuiSelectGroupMini miniGroup;
	
	public GuiSelectGroup(int top, int left, int width, int height, int miniHeight)
	{
		this(top, left, width, height, miniHeight, new ArrayList<String>());
	}
	public GuiSelectGroup(int top, int left, int width, int height, int miniHeight, ArrayList<String> data)
	{
		this.fullGroup = new GuiSelectGroupFull(top, left, width, height, data);
		this.miniGroup = new GuiSelectGroupMini(miniHeight);
		this.onClose();
	}
	
	public void renderFull()
	{
		this.fullGroup.render();
	}
	public void renderSub(int top, int left, int width)
	{
		this.miniGroup.render(top, left, width);
	}
	
	public ArrayList<String> toArray()
	{
		return this.fullGroup.toArray();
	}
	public void mouseInput()
	{
		this.fullGroup.mouseInput();
	}
	public void keyPress(char ch, int key)
	{
		this.fullGroup.keyPress(ch, key);
	}
	public void onClick(int x, int y, int button)
	{
		this.fullGroup.onClick(x, y, button);
	}
	public boolean removeClick(int x, int y, int button)
	{
		return this.miniGroup.removeClick(x, y, button);
	}
	public boolean closeClick(int x, int y, int button)
	{
		return this.fullGroup.closeClick(x, y, button);
	}
	public boolean clicked(int x, int y, int button)
	{
		return this.miniGroup.clicked(x, y, button);
	}
	public void onClose()
	{
		String disp = "";
		for (String s : this.toArray())
			disp += (disp.equals("") ? s : " : " + s);
		this.miniGroup.setString(disp);
	}
}