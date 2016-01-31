package de.noobisoft.powerpong.usercontrol.action;

import org.apache.log4j.Logger;

import de.noobisoft.powerpong.domain.player.Player;
import de.noobisoft.powerpong.usercontrol.EUserInput;

/**
 * a user action for changing the active camera
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class UAChangeCam implements IUserAction
{
	static Logger	logger	= Logger.getLogger(UAChangeCam.class);

	private Player	player;

	public UAChangeCam(Player player)
	{
		this.player = player;
	}

	/**
	 * 
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(Player player)
	{
		this.player = player;
	}

	@Override
	public boolean execute(double timeElapsed)
	{
		if (player == null)
		{
			logger.error("Can not be executed, player is not set.");
		}
		else
		{
			this.player.setNextCam();
		}

		return true;
	}

	@Override
	public EUserInput getType()
	{
		return EUserInput.ChangeCam;
	}

}
