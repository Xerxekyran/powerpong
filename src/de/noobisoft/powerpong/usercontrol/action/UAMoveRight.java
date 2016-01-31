package de.noobisoft.powerpong.usercontrol.action;

import de.noobisoft.powerpong.domain.player.Player;
import de.noobisoft.powerpong.usercontrol.EUserInput;

/**
 * a user action for a movement to the right
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class UAMoveRight implements IUserAction
{
	private Player	player;

	/**
	 * 
	 * @param the
	 *            player that should move to the right
	 */
	public UAMoveRight(Player player)
	{
		this.player = player;
	}

	public void setPlayer(Player player)
	{
		this.player = player;
	}

	@Override
	public boolean execute(double timeElapsed)
	{
		this.player.strafeRight();

		return false;
	}

	@Override
	public EUserInput getType()
	{
		return EUserInput.MoveStrafeRight;
	}
}
