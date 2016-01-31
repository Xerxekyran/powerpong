package de.noobisoft.powerpong.usercontrol.action;

import de.noobisoft.powerpong.domain.player.Player;
import de.noobisoft.powerpong.usercontrol.EUserInput;

/**
 * a user action for changing the viewing direction of the player
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class UAChangeViewDirection implements IUserAction
{
	private Player	player;
	private float	deltaX;
	private float	deltaY;

	public UAChangeViewDirection(Player player, float deltaX, float deltaY)
	{
		this.player = player;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}

	public void setPlayer(Player player)
	{
		this.player = player;
	}

	public void setDeltaX(float deltaX)
	{
		this.deltaX = this.deltaX + deltaX;
	}

	public void setDeltaY(float deltaY)
	{
		this.deltaY = this.deltaY + deltaY;
	}

	@Override
	public boolean execute(double timeElapsed)
	{
		this.player.changeViewDirection(deltaX, deltaY);
		deltaX = 0.0f;
		deltaY = 0.0f;

		return true;
	}

	@Override
	public EUserInput getType()
	{
		return EUserInput.ChangeViewDirection;
	}

}
