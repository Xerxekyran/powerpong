package de.noobisoft.powerpong.config;

import de.noobisoft.powerpong.domain.player.ETeam;

/**
 * The settings of the user
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class ClientConfiguration
{
	public static String	playerName		= "Default Player";

	public static ETeam		playerTeam		= ETeam.RED;

	public static String	serverIP		= "127.0.0.1";

	public static String	serverPort		= "9090";

	public static int		screenWidth		= 1024;

	public static int		screenHeight	= 768;

	public static boolean	isFullscreen	= false;

	public static String	world			= "Grimm Night";

	public static boolean	soundOn			= true;

	public static boolean	musikOn			= true;

	public static boolean	isHolodeck		= false;

}
