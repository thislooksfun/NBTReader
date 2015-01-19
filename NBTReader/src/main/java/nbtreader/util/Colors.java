package nbtreader.util;

import net.minecraft.util.MathHelper;

/**
 * @author thislooksfun
 */
@SuppressWarnings({"UnusedDeclaration", "SameParameterValue", "WeakerAccess"})
public class Colors
{
	public static final int BLACK = rgb(0, 0, 0);
	public static final int WHITE = rgb(255, 255, 255);
	public static final int TEXT_COLOR = rgb(244, 244, 244);
	public static final int DISABLED_TEXT_COLOR = rgb(112, 112, 112);
	
	public static int argb(int r, int g, int b)
	{
		return argb(255, r, g, b, 0);
	}
	public static int argb(int a, int r, int g, int b, int x)
	{
		a = MathHelper.clamp_int(a, 0, 255);
		return (a << 24) | rgb(r, g, b);
	}
	public static int rgb(int r, int g, int b)
	{
		r = MathHelper.clamp_int(r, 0, 255);
		g = MathHelper.clamp_int(g, 0, 255);
		b = MathHelper.clamp_int(b, 0, 255);
		return (r << 16) | (g << 8) | b;
	}
	
	public static int toRgba(int i)
	{
		return toRgba(i, 255);
	}
	public static int toRgba(int i, int alpha)
	{
		return (alpha << 24) | i;
	}
	public static int toRgb(int i)
	{
		return i & ~(255 << 24);
	}
	
	public static int hexToA(int i)
	{
		return (i >> 24 & 255);
	}
	public static int hexToR(int i)
	{
		return (i >> 16 & 255);
	}
	public static int hexToG(int i)
	{
		return (i >> 8 & 255);
	}
	public static int hexToB(int i)
	{
		return (i & 255);
	}
}