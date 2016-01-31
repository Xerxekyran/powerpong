package de.noobisoft.powerpong.audio;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javazoom.jl.player.Player;

import org.apache.log4j.Logger;

import de.noobisoft.powerpong.config.ClientConfiguration;
import de.noobisoft.powerpong.util.SplashScreen;

/**
 * A singleton manager class to perform audio data
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class AudioManager
{
	static Logger								logger					= Logger.getLogger(AudioManager.class);

	private HashMap<ESoundEffects, AudioClip>	loadedSoundFiles		= new HashMap<ESoundEffects, AudioClip>();
	private static AudioManager					instance				= null;
	private int									currentAudioFilesIndex	= -1;
	private AudioThread							currentAudioThread		= null;
	private List<File>							audioFiles				= null;

	/**
	 * Gets a singleton instance of the AudioManager
	 * 
	 * @return the singleton instance
	 */
	public static AudioManager getInstance()
	{
		if (instance == null)
			instance = new AudioManager();

		return instance;
	}

	/**
	 * private ctor for singleton pattern
	 */
	private AudioManager()
	{
		try
		{
			loadFile("audio/aaaah.wav", ESoundEffects.Aaaah);
			loadFile("audio/blob.wav", ESoundEffects.Blob);
			loadFile("audio/blobftsch.wav", ESoundEffects.Blobftsch);
			loadFile("audio/frrrr.wav", ESoundEffects.Frrrr);
			loadFile("audio/refereesball.wav", ESoundEffects.RefereesBall);
			loadFile("audio/team_blue_point.wav", ESoundEffects.BluePoint);
			loadFile("audio/team_red_point.wav", ESoundEffects.RedPoint);
			loadFile("audio/team_blue_wins.wav", ESoundEffects.BlueWin);
			loadFile("audio/team_red_wins.wav", ESoundEffects.RedWin);
			loadFile("audio/schwup.wav", ESoundEffects.Schwup);
			loadFile("audio/schranke.wav", ESoundEffects.Trainbell);
			loadFile("audio/zugfahrt.wav", ESoundEffects.Trainmoving);
			this.readUserMP3Files();

		}
		catch (Exception e)
		{
			logger.error(e);
		}

	}

	private void loadFile(String file, ESoundEffects type)
	{
		try
		{
			SplashScreen.setLoadingText(file);
			logger.info("Loading Sound [" + file + "]");
			File f = new File(file);
			AudioClip soundclip = Applet.newAudioClip(f.toURI().toURL());
			loadedSoundFiles.put(type, soundclip);
		}
		catch (Exception e)
		{
			logger.error(e);
		}
	}

	/**
	 * stops the currently played music
	 */
	public void stopMusic()
	{
		currentAudioThread.stopAudio();
	}

	/**
	 * set the current audio thread on play
	 */
	public void playNextMusicFile()
	{
		if (!ClientConfiguration.musikOn || this.currentAudioFilesIndex < 0)
			return;

		try
		{
			// first stop the old one
			if (currentAudioThread != null)
				currentAudioThread.stopAudio();

			File f = this.audioFiles.get(currentAudioFilesIndex);
			FileInputStream fis = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(fis);

			Player newSoundPlayer = new Player(bis);
			AudioThread thread = new AudioThread(newSoundPlayer);
			thread.setDaemon(true);
			thread.start();
			currentAudioThread = thread;

			// set the index to the next valid one
			currentAudioFilesIndex = (currentAudioFilesIndex + 1)
					% this.audioFiles.size();
		}
		catch (Exception e)
		{
			logger.error("Could not play the music. " + e.toString());
		}
	}

	/**
	 * 
	 * @param sound
	 *            the soundeffect that should be played
	 */
	public void playSoundEffect(ESoundEffects sound)
	{
		if (!ClientConfiguration.soundOn)
			return;

		try
		{
			AudioClip soundclip = null;

			soundclip = loadedSoundFiles.get(sound);
			soundclip.play();

		}
		catch (Exception e)
		{
			logger.error(e.toString());
		}
	}

	/**
	 * refreshes the array of mp3 files
	 */
	public void readUserMP3Files()
	{
		this.audioFiles = new LinkedList<File>();

		File f = new File("mp3");
		File[] files = f.listFiles();

		for (File mp3File : files)
		{
			if (mp3File.getName().endsWith(".mp3"))
			{
				SplashScreen.setLoadingText(mp3File.getName());

				logger.info("Loading custom music file [" + mp3File.getName()
						+ "]");
				audioFiles.add(mp3File);
				currentAudioFilesIndex = 0;
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{

			// AudioManager.getInstance().playNextMusicFile();

			// AudioManager.getInstance().playSoundFileMP3("audio/testSound1.mp3");

			// System.out.println("blobben");
			// AudioManager.getInstance().playSoundEffect(ESoundEffects.BluePoint);
			// AudioManager.getInstance().playSoundEffect(ESoundEffects.RefereesBall);
			// Thread.sleep(4000);
			AudioManager.getInstance().playSoundEffect(ESoundEffects.Trainbell);
			Thread.sleep(4000);
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}

	}

}
