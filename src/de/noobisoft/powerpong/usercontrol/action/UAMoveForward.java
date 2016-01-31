package de.noobisoft.powerpong.usercontrol.action;

import de.noobisoft.powerpong.domain.player.Player;
import de.noobisoft.powerpong.usercontrol.EUserInput;

/**
 * a user action for a movement forward
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class UAMoveForward implements IUserAction
{
	private Player	player;

	/**
	 * 
	 * @param moverNode
	 *            the scene node that should be moved
	 */
	public UAMoveForward(Player player)
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
		if (player != null)
			this.player.moveForward();

		return false;
	}

	@Override
	public EUserInput getType()
	{
		return EUserInput.MoveForward;
	}
}
