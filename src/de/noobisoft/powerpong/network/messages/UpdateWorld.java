package de.noobisoft.powerpong.network.messages;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.domain.player.ETeam;
import de.noobisoft.powerpong.domain.player.Player;
import de.noobisoft.powerpong.util.TransformHelper;

/**
 * The network message containing the new positions for the main objects in the
 * game
 * 
 * @author Lars George
 * @autho Chris Krauﬂ
 * 
 */
public class UpdateWorld extends AbstractNetworkMessage
{
	static Logger				logger			= Logger.getLogger(UpdateWorld.class);
	private Collection<Player>	players;
	private Vector3				paddle1Position	= new Vector3();
	private Vector3				trainPosition	= new Vector3();
	private Vector3				paddle2Position	= new Vector3();
	private Matrix4				ballTransform	= null;

	@Override
	public ENetworkMessages getType()
	{
		return ENetworkMessages.UpdateWorld;
	}

	public Collection<Player> getPlayers()
	{
		return players;
	}

	public void setPlayers(Collection<Player> players)
	{
		this.players = players;
	}

	public Matrix4 getBallTransform()
	{
		return ballTransform;
	}

	public void setBallTransform(Matrix4 ballTransform)
	{
		this.ballTransform = ballTransform;
	}

	public void setPaddle1Position(Vector3 pos)
	{
		this.paddle1Position = pos;
	}

	public void setPaddle2Position(Vector3 pos)
	{
		this.paddle2Position = pos;
	}

	public Vector3 getPaddle1Position()
	{
		return this.paddle1Position;
	}

	public Vector3 getPaddle2Position()
	{
		return this.paddle2Position;
	}

	@Override
	public void setData(String msgStr)
	{
		try
		{
			msgStr = msgStr.substring(1);

			String[] chunks = msgStr.split(";");

			// paddle 1
			String[] values = chunks[0].split(",");
			paddle1Position = new Vector3(Float.parseFloat(values[0]),
					Float.parseFloat(values[1]), Float.parseFloat(values[2]));

			// paddle 2
			values = chunks[1].split(",");
			paddle2Position = new Vector3(Float.parseFloat(values[0]),
					Float.parseFloat(values[1]), Float.parseFloat(values[2]));
			
			// train
			values = chunks[2].split(",");
			trainPosition = new Vector3(Float.parseFloat(values[0]),
					Float.parseFloat(values[1]), Float.parseFloat(values[2]));

			// ball
			ballTransform = TransformHelper.NetworkStringToMatrix(chunks[3]);

			// players
			players = new LinkedList<Player>();
			for (int i = 4; i < chunks.length; i++)
			{
				values = chunks[i].split(",");

				ETeam team = ETeam.RED;
				if (values[1] == "b")
					team = ETeam.BLUE;
				if (values[1] == "r")
					team = ETeam.RED;

				Player p = new Player(values[0], team, false);
				p.setWeaponEnergy(Double.parseDouble(values[2]));
				p.setPosition(new Vector3(Float.parseFloat(values[3]),
						Float.parseFloat(values[4]),
						Float.parseFloat(values[5])));

				p.setRotationY(Float.parseFloat(values[6]));

				players.add(p);
			}

		}
		catch (Exception e)
		{
			logger.error(e.toString() + " :: " + msgStr);
		}
	}

	public String getNetworkData()
	{
		StringBuilder playersString = new StringBuilder();

		if (players != null)
		{
			for (Player p : players)
			{
				String teamStr = "";
				if (p.getTeam() == ETeam.BLUE)
					teamStr = "b";
				else if (p.getTeam() == ETeam.RED)
					teamStr = "r";

				playersString.append(";" + p.getId() + "," + teamStr + ","
						+ p.getWeaponEnergy() + "," + p.getPosition().x() + ","
						+ p.getPosition().y() + "," + p.getPosition().z() + ","
						+ p.getRotationY());
			}
		}

		return ("u" + paddle1Position.x() + "," + paddle1Position.y() + ","
				+ paddle1Position.z() + ";" + paddle2Position.x() + ","
				+ paddle2Position.y() + "," + paddle2Position.z() + ";"
				+ trainPosition.x() + "," + trainPosition.y() + ","
				+ trainPosition.z() + ";"
				+ TransformHelper.MatrixToNetworkString(ballTransform) + playersString);
	}

	/**
	 * @return the trainPosition
	 */
	public Vector3 getTrainPosition()
	{
		return trainPosition;
	}

	/**
	 * @param trainPosition
	 *            the trainPosition to set
	 */
	public void setTrainPosition(Vector3 trainPosition)
	{
		this.trainPosition = trainPosition;
	}
}
