package com.tlf.nbtreader.util;

import net.minecraft.client.Minecraft;

import java.util.HashSet;
import java.util.Set;

/**
 * @author thislooksfun
 */
public class ColorHelper
{
	public static String format(String s)
	{
		return s.replace("&", "\u00a7");
	}
	public static String unformat(String s)
	{
		return s.replace("\u00a7", "&");
	}
	
	@SuppressWarnings("UnusedDeclaration")
	public static String removeCodes(String s)
	{
		return s.replaceAll("&.", "");
	}
	
	public static String limitToLengthExcludingCodes(String s, int length)
	{
		if (length <= 0 || s == null) return "";
		
		s = format(s);
		int end = s.length();
		while (Minecraft.getMinecraft().fontRenderer.getStringWidth((s.substring(0, end)) + "\u00a7r...") > length)
			end--;
		
		return end < s.length() ? getRenderedSubstring(s, end) + "\u00a7r..." : s;
	}
	
	private static String getRenderedSubstring(String s, int end)
	{
		Set<Integer> poses = getCodePositions(s);
		
		for (int i : poses)
		{
			if (i < end)
			{
				end += 2;
			}
		}
		
		return s.substring(0, end);
	}
	
	public static String getEffectiveEndCodes(String s)
	{
		String color = "";
		String style = "";
		
		Set<Integer> poses = getCodePositions(s);
		
		int cnt = 0;
		String[] codes = new String[poses.size()];
		for (int i : poses)
		{
			if (s.length() >= i + 2)
			{
				codes[cnt++] = s.substring(i, i + 2);
			}
		}
		
		for (int i = 0; i < cnt; i++)
		{
			String str = codes[i];
			if (str.equals("&r"))
			{
				color = "";
				style = "";
			} else if (isColorCode(str))
			{
				color = str;
			} else
			{
				style += str;
			}
		}
		
		return color + style;
	}
	
	private static Set<Integer> getCodePositions(String s)
	{
		String findStr = "&";
		int lastIndex = 0;
		
		Set<Integer> poses = new HashSet<Integer>();
		
		while (lastIndex > -1)
		{
			lastIndex = s.indexOf(findStr, lastIndex);
			
			if (lastIndex > -1)
			{
				poses.add(lastIndex);
				lastIndex += findStr.length();
			}
		}
		
		return poses;
	}
	
	private static boolean isColorCode(String s)
	{
		return s.equalsIgnoreCase("&0") || s.equalsIgnoreCase("&1") || s.equalsIgnoreCase("&2") || s.equalsIgnoreCase("&3") || s.equalsIgnoreCase("&4") || s.equalsIgnoreCase("&5") || s.equalsIgnoreCase("&6") || s.equalsIgnoreCase("&7") || s.equalsIgnoreCase("&8") || s.equalsIgnoreCase("&9") || s.equalsIgnoreCase("&a") || s.equalsIgnoreCase("&b") || s.equalsIgnoreCase("&c") || s.equalsIgnoreCase("&d") || s.equalsIgnoreCase("&e") || s.equalsIgnoreCase("&f");
	}
}