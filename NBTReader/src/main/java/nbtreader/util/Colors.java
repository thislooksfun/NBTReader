package nbtreader.util;

/**
 * @author thislooksfun
 */
public class Colors
{
	public static final int BLACK = rgb(0, 0, 0);
	public static final int WHITE = rgb(255, 255, 255);
	public static final int TEXT_COLOR = rgb(244, 244, 244);
	public static final int DISABLED_TEXT_COLOR = rgb(112, 112, 112);
	
	public static int rgba(int r, int g, int b, int a)
	{
		a = (a > 255 ? 255 : a);
		return (a << 24) | rgb(r, g, b);
	}
	public static int rgb(int r, int g, int b)
	{
		r = (r > 255 ? 255 : r);
		g = (g > 255 ? 255 : g);
		b = (b > 255 ? 255 : b);
		return (r << 16) | (g << 8) | b;
	}
	
	@SuppressWarnings("SameParameterValue")
	public static int toRgba(int i)
	{
		return toRgba(i, 255);
	}
	@SuppressWarnings({"SameParameterValue", "WeakerAccess"})
	public static int toRgba(int i, int alpha)
	{
		return (alpha << 24) | i;
	}
	@SuppressWarnings("UnusedDeclaration")
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