package de.noobisoft.powerpong.usercontrol.action;

import de.noobisoft.powerpong.usercontrol.EUserInput;

/**
 * Interface for an user action.
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public interface IUserAction
{
	public EUserInput getType();

	/**
	 * performs the action itself
	 * 
	 * @param timeElapsed
	 *            the time passed since the last update cylce
	 * @return if the execution returns true, that means the action is not
	 *         repatable
	 */
	public boolean execute(double timeElapsed);
}
