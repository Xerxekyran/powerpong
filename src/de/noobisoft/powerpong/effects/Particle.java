package de.noobisoft.powerpong.effects;

import de.bht.jvr.math.Vector4;

/**
 * data class representing tha attributes of an particle
 * 
 * @author Lars George
 * 
 */
public class Particle
{
	public Vector4	velocity	= null;
	public double	ageSeconds	= 0;
	public boolean	initialized	= false;

	public Particle()
	{
		velocity = new Vector4();
		ageSeconds = 0;
		initialized = false;
	}
}
