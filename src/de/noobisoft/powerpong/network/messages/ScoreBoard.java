package de.noobisoft.powerpong.network.messages;

/**
 * Message that informs about the current scoreboard values
 * 
 * @author Lars George
 * 
 */
public class ScoreBoard extends AbstractNetworkMessage
{
	private Integer	scoreTeam2	= 0;
	private Integer	scoreTeam1	= 0;

	@Override
	public ENetworkMessages getType()
	{
		return ENetworkMessages.ScoreBoard;
	}

	@Override
	public String getNetworkData()
	{
		return "s" + scoreTeam1 + ";" + scoreTeam2;
	}

	@Override
	public void setData(String msgStr)
	{
		msgStr = msgStr.substring(1);

		String[] values = msgStr.split(";");
		scoreTeam1 = Integer.parseInt(values[0]);
		scoreTeam2 = Integer.parseInt(values[1]);
	}

	/**
	 * @return the scoreRed
	 */
	public Integer getScoreTeam2()
	{
		return scoreTeam2;
	}

	/**
	 * @param scoreTeam2
	 *            the scoreRed to set
	 */
	public void setScoreTeam2(Integer scoreTeam2)
	{
		this.scoreTeam2 = scoreTeam2;
	}

	/**
	 * @return the scoreBlue
	 */
	public Integer getScoreTeam1()
	{
		return scoreTeam1;
	}

	/**
	 * @param scoreTeam1
	 *            the scoreBlue to set
	 */
	public void setScoreTeam1(Integer scoreTeam1)
	{
		this.scoreTeam1 = scoreTeam1;
	}

}
