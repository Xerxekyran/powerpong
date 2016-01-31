package de.noobisoft.powerpong.domain.player;

public class TeamStatistics
{
	private ETeam	team;
	private Integer	score;

	public TeamStatistics(ETeam team)
	{
		this.team = team;
		clearScore();
	}

	/**
	 * @return the team
	 */
	public ETeam getTeam()
	{
		return team;
	}
	
	/**
	 * @return the score
	 */
	public Integer getScore()
	{
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void increaseScore()
	{
		this.score++;
	}	
	
	/**
	 * @param score the score to set
	 */
	public void setScore(Integer score)
	{
		this.score = score;
	}

	/**
	 * @param score the score to set
	 */
	public void clearScore()
	{
		this.score =0;
	}
}
