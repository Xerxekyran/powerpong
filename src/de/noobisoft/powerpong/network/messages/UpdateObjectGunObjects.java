package de.noobisoft.powerpong.network.messages;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.noobisoft.powerpong.domain.player.weapon.ObjectGunObject;
import de.noobisoft.powerpong.util.TransformHelper;

/**
 * This message containts update information about object gun objects in the
 * level
 * 
 * @author Lars George
 * 
 */
public class UpdateObjectGunObjects extends AbstractNetworkMessage
{
	static Logger					logger	= Logger.getLogger(UpdateObjectGunObjects.class);
	private List<ObjectGunObject>	objects	= null;

	@Override
	public String getNetworkData()
	{
		StringBuilder objectString = new StringBuilder("o");

		for (ObjectGunObject obj : objects)
		{
			objectString.append(";"
					+ obj.getId()
					+ ";"
					+ TransformHelper.MatrixToNetworkString(obj.getNode().getTransform().getMatrix()));
		}
		return objectString.toString();
	}

	@Override
	public void setData(String msgStr)
	{
		try
		{
			msgStr = msgStr.substring(2);

			String[] chunks = msgStr.split(";");
			this.objects = new LinkedList<ObjectGunObject>();

			for (int i = 0; i < chunks.length; i += 2)
			{
				int id = Integer.parseInt(chunks[i]);
				ObjectGunObject tmpObj = new ObjectGunObject(null, null, id,
						false);
				tmpObj.setMatrixFromServer(TransformHelper.NetworkStringToMatrix(chunks[i + 1]));

				this.objects.add(tmpObj);
			}
		}
		catch (Exception e)
		{
			logger.error(e.toString() + " :: " + msgStr);
		}
	}

	@Override
	public ENetworkMessages getType()
	{
		return ENetworkMessages.UpdateObjectGunObjects;
	}

	/**
	 * @return the objects
	 */
	public List<ObjectGunObject> getObjects()
	{
		return objects;
	}

	/**
	 * @param objects
	 *            the objects to set
	 */
	public void setObjects(List<ObjectGunObject> objects)
	{
		this.objects = objects;
	}

}
