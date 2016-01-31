package de.noobisoft.powerpong.usercontrol.action;

import de.noobisoft.powerpong.domain.player.Player;
import de.noobisoft.powerpong.network.OutgoingMessageQueue;
import de.noobisoft.powerpong.network.messages.PlayerShot;
import de.noobisoft.powerpong.usercontrol.EUserInput;

/**
 * a user action for a shot
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class UAShoot implements IUserAction
{
	private Player	player;

	public UAShoot(Player player)
	{
		this.player = player;
	}

	@Override
	public EUserInput getType()
	{
		return EUserInput.Shoot;
	}

	@Override
	public boolean execute(double timeElapsed)
	{
		// send the message to the server
		PlayerShot msg = new PlayerShot();
		msg.setPlayerID(player.getId());
		msg.setDirection(player.getShootDirection());
		msg.setPosition(player.getPosition());
		msg.setWeaponType(player.getActiveWeapon().getWeaponType());
		OutgoingMessageQueue.getInstance().addMessage(msg);

		return true;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer()
	{
		return player;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(Player player)
	{
		this.player = player;
	}

}
