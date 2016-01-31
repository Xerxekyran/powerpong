package de.noobisoft.powerpong.domain.player.weapon;

import de.noobisoft.powerpong.domain.AbstractSceneObject;

/**
 * The abstract class for all kinds of weapons.
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public abstract class AbstractWeapon extends AbstractSceneObject
{
	/**
	 * gives a value for the energy costs of the weapon
	 * 
	 * @return the energy that is needed to shoot with this weapon
	 */
	public abstract Integer getNeededEnegery();

	/**
	 * 
	 * @return the rate of fire in ms (min time between two shots)
	 */
	public abstract Integer getFireRateTimeInMs();

	/**
	 * 
	 * @return the type of the weapon
	 */
	public abstract EWeapons getWeaponType();
}
