package nbtreader.client.gui;

import net.minecraft.client.gui.Gui;

/**
 * @author thislooksfun
 */
public class GuiSelectGroup extends Gui
{
	private GuiSelectGroupMini miniGroup;
	private GuiSelectGroupFull fullGroup;
	
	public GuiSelectGroup(int height)
	{
		this.miniGroup = new GuiSelectGroupMini(height);
		this.fullGroup = new GuiSelectGroupFull();
	}
	
	public void renderFull(int top, int left, int height, int width)
	{
		this.fullGroup.render(top, left, height, width);
	}
	public void renderSub(int top, int left, int width)
	{
		this.miniGroup.render(top, left, width);
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
}