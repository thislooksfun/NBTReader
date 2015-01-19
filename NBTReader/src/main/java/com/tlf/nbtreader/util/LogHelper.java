package com.tlf.nbtreader.util;

import cpw.mods.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

/**
 * @author thislooksfun
 */
@SuppressWarnings("UnusedDeclaration")
public class LogHelper
{
	private static Logger logger = FMLCommonHandler.instance().getFMLLogger();
	
	public static void init(Logger l)
	{
		logger = l;
	}
	
	/** Logs a message at the specified level */
	@SuppressWarnings("WeakerAccess")
	public static void log(Level level, Object msg)
	{
		logger.log(level, msg);
	}
	/** Logs a message at the "fatal" level */
	public static void fatal(Object msg)
	{
		log(Level.FATAL, msg);
	}
	/** Logs a message at the "error" level */
	public static void error(Object msg)
	{
		log(Level.ERROR, msg);
	}
	/** Logs a message at the "warn" level */
	public static void warn(Object msg)
	{
		log(Level.WARN, msg);
	}
	/** Logs a message at the "info" level */
	public static void info(Object msg)
	{
		log(Level.INFO, msg);
	}
	/** Logs a message at the "debug" level */
	public static void debug(Object msg)
	{
		log(Level.DEBUG, msg);
	}
	/** Logs a message at the "trace" level */
	public static void trace(Object msg)
	{
		log(Level.TRACE, msg);
	}
}