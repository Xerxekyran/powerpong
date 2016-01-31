package de.noobisoft.powerpong.physics;

import java.util.Date;

import com.bulletphysics.dynamics.RigidBody;

/**
 * An object that has a limited time to live
 * 
 * @author Lars George
 * 
 */
public class LifetimeObject
{
	private Date		timeToDelete	= new Date();
	private RigidBody	rigidBody		= null;

	/**
	 * 
	 * @param timeToDelete
	 *            at which timepoint should it be deleted?
	 * @param rb
	 *            the rigidbody that should be deleted at the given time
	 */
	public LifetimeObject(Date timeToDelete, RigidBody rb)
	{
		this.timeToDelete = timeToDelete;
		this.rigidBody = rb;
	}

	/**
	 * 
	 * @return true if its time to delete this objects content
	 */
	public boolean isTimeToDelete()
	{
		Date now = new Date();
		return (now.after(timeToDelete));
	}

	/**
	 * @return the timeToDelete
	 */
	public Date getTimeToDelete()
	{
		return timeToDelete;
	}

	/**
	 * @param timeToDelete the timeToDelete to set
	 */
	public void setTimeToDelete(Date timeToDelete)
	{
		this.timeToDelete = timeToDelete;
	}

	/**
	 * @return the rigidBody
	 */
	public RigidBody getRigidBody()
	{
		return rigidBody;
	}

	/**
	 * @param rigidBody the rigidBody to set
	 */
	public void setRigidBody(RigidBody rigidBody)
	{
		this.rigidBody = rigidBody;
	}
}
