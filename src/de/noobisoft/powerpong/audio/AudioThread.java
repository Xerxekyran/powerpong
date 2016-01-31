package de.noobisoft.powerpong.audio;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import org.apache.log4j.Logger;

/**
 * A class to control the playback of an audio file
 * 
 * @author Lars George
 * 
 */
public class AudioThread extends Thread
{
	static Logger	logger		= Logger.getLogger(AudioThread.class);	

	private Player	audioPlayer	= null;

	/**
	 * 
	 * @param audioPlayer
	 *            the player instance associated with this thread
	 */
	public AudioThread(Player audioPlayer)
	{
		this.audioPlayer = audioPlayer;
	}

	public void play()
	{
		try
		{
			audioPlayer.play();
		}
		catch (JavaLayerException e)
		{
			logger.error(e.toString());
		}
	}
	
	public void stopAudio()
	{
		audioPlayer.close();
	}

	@Override
	public void run()
	{
		try
		{
			audioPlayer.play();
		}
		catch (JavaLayerException e)
		{
			logger.error(e.toString());
		}
	}
}
