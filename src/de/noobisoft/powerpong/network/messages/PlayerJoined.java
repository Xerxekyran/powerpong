package de.noobisoft.powerpong.network.messages;

import de.noobisoft.powerpong.domain.player.ETeam;
import de.noobisoft.powerpong.domain.player.Player;

/**
 * A Network Message telling that a player joined the game
 * 
 * @author Lars George
 * 
 */
public class PlayerJoined extends AbstractNetworkMessage
{
	public static double	CALCULATION_TIME_MS	= 8.0;

	private Player			player;

	/**
	 * default ctor
	 */	
	public PlayerJoined()
	{
		setPriority(10);
	}

	@Override
	public ENetworkMessages getType()
	{
		return ENetworkMessages.PlayerJoined;
	}

	@Override
	public void setData(String msgStr)
	{
		msgStr = msgStr.substring(1);

		String[] values = msgStr.split(";");

		ETeam team = ETeam.RED;
		if (values[1].equals("b"))
			team = ETeam.BLUE;
		if (values[1].equals("r"))
			team = ETeam.RED;

		this.player = new Player(values[0], team, false);
	}

	@Override
	public String getNetworkData()
	{
		String teamStr = "b";
		if (player.getTeam() == ETeam.BLUE)
			teamStr = "b";
		else if (player.getTeam() == ETeam.RED)
			teamStr = "r";

		return ("j" + this.player.getId() + ";" + teamStr);
	}


	/**
	 * @return the player
	 */
	public Player getPlayer()
	{
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player)
	{
		this.player = player;
	}

}
