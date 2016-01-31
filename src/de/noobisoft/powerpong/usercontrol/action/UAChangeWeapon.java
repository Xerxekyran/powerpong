package de.noobisoft.powerpong.usercontrol.action;

import org.apache.log4j.Logger;

import de.noobisoft.powerpong.domain.player.Player;
import de.noobisoft.powerpong.usercontrol.EUserInput;

/**
 * a user action for choosing an other active weapon
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class UAChangeWeapon implements IUserAction
{
	static Logger	logger			= Logger.getLogger(UAChangeWeapon.class);

	private Player	player;
	private int		newWeaponIndex	= 0;

	public UAChangeWeapon(Player player)
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
			this.player.changeWeapon(newWeaponIndex);
		}

		return true;
	}

	@Override
	public EUserInput getType()
	{
		return EUserInput.ChangeWeapon;
	}

	/**
	 * @return the newWeaponIndex
	 */
	public int getNewWeaponIndex()
	{
		return newWeaponIndex;
	}

	/**
	 * @param newWeaponIndex
	 *            the newWeaponIndex to set
	 */
	public void setNewWeaponIndex(int newWeaponIndex)
	{
		this.newWeaponIndex = newWeaponIndex;
	}

}
