package de.noobisoft.powerpong.core;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.uniforms.UniformBool;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.core.uniforms.UniformInt;
import de.bht.jvr.core.uniforms.UniformVector2;
import de.bht.jvr.math.Vector2;
import de.bht.jvr.renderer.NewtRenderWindow;
import de.bht.jvr.renderer.RenderWindow;
import de.bht.jvr.renderer.Viewer;
import de.noobisoft.powerpong.config.ClientConfiguration;
import de.noobisoft.powerpong.domain.Game;
import de.noobisoft.powerpong.domain.player.Player;
import de.noobisoft.powerpong.domain.player.weapon.ObjectGunObject;
import de.noobisoft.powerpong.effects.ClientEffectManager;
import de.noobisoft.powerpong.network.MinaClient;
import de.noobisoft.powerpong.physics.PhysicManager;
import de.noobisoft.powerpong.usercontrol.ControlManager;
import de.noobisoft.powerpong.usercontrol.IControlManager;
import de.noobisoft.powerpong.usercontrol.SimpleKeyListener;
import de.noobisoft.powerpong.usercontrol.SimpleMouseListener;
import de.noobisoft.powerpong.usercontrol.SimpleWiimoteListener;
import de.noobisoft.powerpong.usercontrol.SimpleWindowListener;
import de.noobisoft.powerpong.util.MaterialGenerator;

/**
 * This basic sample demonstrates the following features: - MouseListener -
 * KeyListener
 * 
 * @author Marc Roßbach
 * @author Henrik Tramberend
 * @author Lars George
 * 
 */

public class Renderer
{
	static Logger					logger						= Logger.getLogger(Renderer.class);
	private static boolean			withInversion				= false;
	private static boolean			withBloom					= false;
	private static boolean			withNightVision				= false;
	private static boolean			withTextOverlay				= false;

	private IControlManager			controlManager				= null;
	private GroupNode				rootSceneNode				= null;
	private Game					game						= null;
	private Viewer					v							= null;
	private Pipeline				pipeline					= null;

	private static ShaderMaterial	textOverlayMaterial			= null;
	private static ShaderMaterial	pixelationMaterial			= null;
	private static ShaderMaterial	shaderMaterialInversion		= null;
	private static ShaderMaterial	shaderMaterialBloom			= null;
	private static ShaderMaterial	shaderMaterialNightVision	= null;

	private double					nightVisionTimer			= -1;

	private long					textOverlayTimer			= 0;
	private String					textOverlayText				= null;

	private long					pixelationTimer				= -1;
	private float					pixelation_width			= -1;
	private float					pixelation_height			= -1;

	/**
	 * Construct the example application object and run the simulation and
	 * rendering loop.
	 * 
	 * @throws Exception
	 */
	public Renderer(MinaClient networkClient) throws Exception
	{
		// create the game
		game = new Game(false, new Player(ClientConfiguration.playerName,
				ClientConfiguration.playerTeam, false));

		// create the control manager
		this.controlManager = new ControlManager();

		// Load a scene and generate the pipeline.
		this.pipeline = makePipeline();

		// Create a render window to render the pipeline.

		RenderWindow win = null;
		if (ClientConfiguration.isFullscreen)
		{
			win = new NewtRenderWindow(pipeline, true);
			// win = new AwtRenderWindow(pipeline, true);
		}
		else
		{
			win = new NewtRenderWindow(pipeline,
					ClientConfiguration.screenWidth,
					ClientConfiguration.screenHeight);

			// win = new AwtRenderWindow(pipeline,
			// ClientConfiguration.screenWidth,
			// ClientConfiguration.screenHeight);
		}

		win.setWindowTitle("PowerPong by Lars George and Chris Krauss");

		// Set the key listener for the window.
		win.addKeyListener(new SimpleKeyListener(controlManager, game,
				networkClient));

		// Set the mouse listener for the window.
		win.addMouseListener(new SimpleMouseListener(controlManager, game, win));

		// a window listener so we get those events too
		win.addWindowListener(new SimpleWindowListener());

		// wii
		new SimpleWiimoteListener(controlManager, game);

		// Create a viewer. The viewer manages all render windows.
		v = new Viewer(win);

		startPixelation(15, 15, 5);
	}

	/**
	 * 
	 * @param camNode
	 */
	public void switchCamera(CameraNode camNode)
	{
		this.pipeline.switchCamera(camNode);
	}

	/**
	 * Generate a scene and a pipeline.
	 * 
	 * @return
	 * @throws Exception
	 */
	public Pipeline makePipeline() throws Exception
	{
		// Now generate the scene graph.
		rootSceneNode = (GroupNode) game.getNode();

		// To render the scene we need a rendering pipeline.
		Pipeline pipeline = new Pipeline(rootSceneNode);

		// Clear the depth and the color buffer and set the clear color to
		// black.
		pipeline.clearBuffers(true, true, new Color(0, 0, 0));

		// create the FBOs for the postprocessing that is done late
		// the 3d scene will be rendered into one of these first
		pipeline.createFrameBufferObject("PostprocessingFBO1", true, 1, 1.0f, 4);
		pipeline.createFrameBufferObject("PostprocessingFBO2", true, 1, 1.0f, 4);

		// ******************************************************************************
		// MIRROR RENDERING 1/2
		// ******************************************************************************
		pipeline.createFrameBufferObject("MirrorFBO", true, 1, 1.0f, 4);
		pipeline.setDepthTest(true);
		// render to texture
		pipeline.switchFrameBufferObject("MirrorFBO");
		pipeline.switchCamera(game.getPortal1());
		pipeline.clearBuffers(true, true, new Color(12, 18, 25));
		pipeline.drawGeometry("SKY", null);
		pipeline.drawGeometry("SKYMIRROR1", "(?!PortalClass).*");
		pipeline.drawGeometry("AMBIENT", "(?!PortalClass)(?!Weapon).*");
		pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null);// "(?!MirrorClass).*");

		// switch back to normal cam
		pipeline.switchCamera(game.getCurrentPlayer().getCamera());

		// ******************************************************************************
		// Write into a Postprocessing FBO to alter that image later on
		// ******************************************************************************
		// switch the fbo
		pipeline.switchFrameBufferObject("PostprocessingFBO1");
		// clear fbo buffers
		pipeline.clearBuffers(true, true, new Color(121, 188, 255));

		// ******************************************************************************
		// SKY BOX
		// ******************************************************************************
		pipeline.drawGeometry("SKY", null);

		// ******************************************************************************
		// AMBIENT
		// ******************************************************************************
		// we have to render the ambient pass
		pipeline.drawGeometry("AMBIENT", "(?!Weapon)(?!MirrorClass).*");
		pipeline.setDepthTest(true);

		// ******************************************************************************
		// PARTICLES
		// ******************************************************************************
		pipeline.drawGeometry("PARTICLES", null);

		// ******************************************************************************
		// LIGHT
		// ******************************************************************************
		// pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null);
		Pipeline lp = pipeline.doLightLoop(false, true);
		lp.switchLightCamera();
		// create a depth map (1024x1024)
		lp.createFrameBufferObject("ShadowMap", true, 0, 1024, 1024, 0);
		// switch to depth map
		lp.switchFrameBufferObject("ShadowMap");
		// clear the depth map
		lp.clearBuffers(true, false, null);
		// render to the depth map		
		lp.drawGeometry("AMBIENT", "(?!MirrorClass).*");
		lp.setDepthTest(true);
		// switch back to screen buffer
		lp.switchFrameBufferObject("PostprocessingFBO1");
		// switch back to normal camera
		lp.switchCamera(game.getCurrentPlayer().getCamera());
		// bind the depth map to the uniform jvr_ShadowMap
		lp.bindDepthBuffer("jvr_ShadowMap", "ShadowMap");
		// render the geometry for the active light
		lp.drawGeometry("LIGHTING", "(?!Weapon)");

		// ******************************************************************************
		// MIRROR RENDERING 2/2
		// ******************************************************************************	
		pipeline.bindColorBuffer("jvr_MirrorTexture", "MirrorFBO", 0);
		pipeline.drawGeometry("AMBIENT", "PortalClass");
		pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", "PortalClass");

		// ******************************************************************************
		// WEAPONS (they need to be on top all the time)
		// ******************************************************************************
		pipeline.createFrameBufferObject("WeaponFBO", true, 1, 1.0f, 4);
		pipeline.switchFrameBufferObject("WeaponFBO");
		pipeline.clearBuffers(true, true, new Color(0, 0, 0));
		pipeline.drawGeometry("AMBIENT", "Weapon");
		pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", "Weapon");
		pipeline.switchFrameBufferObject("PostprocessingFBO1");
		ShaderProgram spWeapon = new ShaderProgram(new File(
				"pipeline_shader/quad.vs"), new File(
				"pipeline_shader/default_with_alpha.fs"));
		ShaderMaterial shaderMaterialWeapon = new ShaderMaterial("WeaponPass",
				spWeapon);

		// ******************************************************************************
		// TEXT OVERLAY
		// ******************************************************************************
		ShaderProgram prog = new ShaderProgram(new File(
				"pipeline_shader/quad.vs"), new File(
				"pipeline_shader/text_overlay.fs"));
		textOverlayMaterial = new ShaderMaterial("OVERLAY", prog);
		textOverlayMaterial.setTexture("OVERLAY",
				"jvr_Texture0",
				new Texture2D(new File("textures/fonts.png")));
		textOverlayMaterial.setUniform("OVERLAY",
				"withTextOverlay",
				new UniformBool(withTextOverlay));

		// draw the text overlay
		pipeline.drawQuad(textOverlayMaterial, "OVERLAY");

		// --------------------------------------------------------------------------------------------
		// POSTPROCESSING (the 3D image is rendered, now do some special things)
		// --------------------------------------------------------------------------------------------

		// --------------------------------------------------------------------------------------------
		// PHASE 1 : Shader creations
		// --------------------------------------------------------------------------------------------

		// ***********************************************
		// DEPTH OF FIELD
		// ***********************************************
		ShaderProgram spDOF = new ShaderProgram(new File(
				"pipeline_shader/quad.vs"), new File("pipeline_shader/dof.fs"));
		ShaderMaterial shaderMaterialDOF = new ShaderMaterial("DOFPass", spDOF);
		// set the blur intensity
		shaderMaterialDOF.setUniform("DOFPass", "intensity", new UniformFloat(
				1.5f));

		// ***********************************************
		// COLOR INVERSION
		// ***********************************************
		ShaderProgram spInversion = new ShaderProgram(new File(
				"pipeline_shader/quad.vs"), new File(
				"pipeline_shader/invert.fs"));
		shaderMaterialInversion = new ShaderMaterial("InversionPass",
				spInversion);
		shaderMaterialInversion.setUniform("InversionPass",
				"withInvert",
				new UniformBool(withInversion));

		// ***********************************************
		// BLOOM
		// ***********************************************
		ShaderProgram spBloom = new ShaderProgram(new File(
				"pipeline_shader/quad.vs"),
				new File("pipeline_shader/bloom.fs"));
		shaderMaterialBloom = new ShaderMaterial("BloomPass", spBloom);
		shaderMaterialBloom.setUniform("BloomPass",
				"withBloom",
				new UniformBool(withBloom));

		// ***********************************************
		// PIXELATION
		// ***********************************************
		ShaderProgram spPixelation = new ShaderProgram(new File(
				"pipeline_shader/quad.vs"), new File(
				"pipeline_shader/pixelation.fs"));
		pixelationMaterial = new ShaderMaterial("PixelationPass", spPixelation);
		pixelationMaterial.setUniform("PixelationPass",
				"rt_width",
				new UniformFloat(ClientConfiguration.screenWidth));
		pixelationMaterial.setUniform("PixelationPass",
				"rt_height",
				new UniformFloat(ClientConfiguration.screenHeight));
		pixelationMaterial.setUniform("PixelationPass",
				"pixel_w",
				new UniformFloat(15));
		pixelationMaterial.setUniform("PixelationPass",
				"pixel_h",
				new UniformFloat(10));
		pixelationMaterial.setUniform("PixelationPass",
				"pixelIt",
				new UniformBool(false));

		// ***********************************************
		// NIGHT VISION
		// ***********************************************
		ShaderProgram spNightVision = new ShaderProgram(new File(
				"pipeline_shader/quad.vs"), new File(
				"pipeline_shader/nightvision.fs"));

		Texture2D noiseTexture = MaterialGenerator.loadTexture2D("textures/noise_texture_0008.jpg");
		Texture2D maskTexture = MaterialGenerator.loadTexture2D("textures/nightVisionMask.png");

		shaderMaterialNightVision = new ShaderMaterial("NightVisionPass",
				spNightVision);

		shaderMaterialNightVision.setTexture("NightVisionPass",
				"noiseTex",
				noiseTexture);
		shaderMaterialNightVision.setTexture("NightVisionPass",
				"maskTex",
				maskTexture);
		shaderMaterialNightVision.setUniform("NightVisionPass",
				"colorAmplification",
				new UniformFloat(4.0f));
		shaderMaterialNightVision.setUniform("NightVisionPass",
				"luminanceThreshold",
				new UniformFloat(0.2f));
		shaderMaterialNightVision.setUniform("NightVisionPass",
				"elapsedTime",
				new UniformFloat(0));
		shaderMaterialNightVision.setUniform("NightVisionPass",
				"withNightVision",
				new UniformBool(withNightVision));

		// --------------------------------------------------------------------------------------------
		// PHASE 2 : Render alternating into postprocessing FBOs with each
		// postprocessing shader
		// --------------------------------------------------------------------------------------------

		// ***********************************************
		// render with DEPTH OF FIELD
		// ***********************************************
		pipeline.switchFrameBufferObject("PostprocessingFBO2");
		pipeline.clearBuffers(true, true, new Color(0, 0, 0));

		pipeline.bindColorBuffer("jvr_Texture1", "PostprocessingFBO1", 0); // color
		pipeline.bindDepthBuffer("jvr_Texture0", "PostprocessingFBO1"); // depth
		pipeline.drawQuad(shaderMaterialDOF, "DOFPass");

		// ***********************************************
		// render weapons
		// ***********************************************
		pipeline.bindColorBuffer("jvr_Texture0", "WeaponFBO", 0); // bind color
		pipeline.drawQuad(shaderMaterialWeapon, "WeaponPass");

		// ***********************************************
		// render bloom (on / off)
		// ***********************************************
		pipeline.switchFrameBufferObject("PostprocessingFBO1");
		pipeline.clearBuffers(true, true, new Color(0, 0, 0));

		pipeline.bindColorBuffer("jvr_Texture0", "PostprocessingFBO2", 0); // color
		pipeline.drawQuad(shaderMaterialBloom, "BloomPass");

		// ***********************************************
		// render inversion (on / off)
		// ***********************************************
		pipeline.switchFrameBufferObject("PostprocessingFBO2");
		pipeline.clearBuffers(true, true, new Color(0, 0, 0));

		pipeline.bindColorBuffer("jvr_Texture0", "PostprocessingFBO1", 0); // color
		pipeline.drawQuad(shaderMaterialInversion, "InversionPass");

		// ***********************************************
		// render night vision (on / off)
		// ***********************************************
		pipeline.switchFrameBufferObject("PostprocessingFBO1");
		pipeline.clearBuffers(true, true, new Color(0, 0, 0));

		pipeline.bindColorBuffer("jvr_Texture0", "PostprocessingFBO2", 0); // color
		pipeline.drawQuad(shaderMaterialNightVision, "NightVisionPass");

		// ***********************************************
		// render with pixelation (on / off)
		// ***********************************************
		// switch to p-buffer
		pipeline.switchFrameBufferObject(null);
		pipeline.clearBuffers(true, true, new Color(0, 0, 0));
		pipeline.bindColorBuffer("jvr_Texture0", "PostprocessingFBO1", 0); // color
		pipeline.drawQuad(pixelationMaterial, "PixelationPass");

		// ******************************************************************************
		return pipeline;
		// ******************************************************************************
	}

	/**
	 * Simulate the world for one frame.
	 * 
	 * @param elapsed
	 *            Duration of time to simulate.
	 */
	private void simulate(double elapsed)
	{
		controlManager.perfomActiveUserActions(elapsed);
		controlManager.perfomActiveUserActions(elapsed);

		// physics updates
		PhysicManager.getInstance().update(elapsed, this.game);

		// effects updates
		ClientEffectManager.getInstance().update(elapsed, this.game);

		// update the text overlay if needed
		updateTextOverlay(elapsed);

		// update the pixelation effect if needed
		updatePixelation(elapsed);

		// update the night vision effect if needed
		updateNightVision(elapsed);

		// game updates
		game.update(elapsed, this.game);

		if (game.getCurrentPlayer().wantsToRespawn())
		{
			startPixelation(15, 15, 2);
			game.getCurrentPlayer().doRespawn();
		}
	}

	/**
	 * updates the shader uniforms of the night vision shader if active
	 * 
	 * @param elapsed
	 */
	private void updateNightVision(double elapsed)
	{
		if (withNightVision)
		{
			nightVisionTimer += elapsed;

			synchronized (shaderMaterialNightVision)
			{
				shaderMaterialNightVision.setUniform("NightVisionPass",
						"elapsedTime",
						new UniformFloat((float) nightVisionTimer));
			}
		}
	}

	/**
	 * 
	 * @param elapsed
	 */
	private void updatePixelation(double elapsed)
	{
		this.pixelationTimer += elapsed;

		if (this.pixelation_width > -1)
		{
			synchronized (pixelationMaterial)
			{
				if ((System.currentTimeMillis() / 1000) <= this.pixelationTimer)
				{
					double timeTillEnd = (this.pixelationTimer * 1000 - (System.currentTimeMillis())) / 1000.0;
					double widthPixel = pixelation_width * timeTillEnd;
					double heightPixel = pixelation_height * timeTillEnd;
					if (widthPixel < 1)
						widthPixel = 1;
					if (heightPixel < 1)
						heightPixel = 1;

					pixelationMaterial.setUniform("PixelationPass",
							"pixel_w",
							new UniformFloat((float) widthPixel));

					pixelationMaterial.setUniform("PixelationPass",
							"pixel_h",
							new UniformFloat((float) heightPixel));
				}
				else
				{
					pixelationMaterial.setUniform("PixelationPass",
							"pixelIt",
							new UniformBool(false));

					this.pixelation_width = -1;
					this.pixelation_width = -1;
				}
			}
		}
	}

	/**
	 * updates the text overlay uniforms if needed
	 * 
	 * @param elapsed
	 */
	private void updateTextOverlay(double elapsed)
	{
		this.textOverlayTimer += elapsed;
		if (withTextOverlay)
		{
			if ((System.currentTimeMillis() / 1000) <= this.textOverlayTimer)
			{
				this.setScreenText(textOverlayMaterial,
						0.3f,
						0.4f,
						0.05f,
						0.05f,
						textOverlayText);
			}
			else
			{
				// if the time is up, dont show the overlay
				textOverlayMaterial.setUniform("OVERLAY",
						"withTextOverlay",
						new UniformBool(false));
				withTextOverlay = false;
			}
		}
	}

	/**
	 * updates the uniform variables for the text overlay shader
	 * 
	 * @param mat
	 *            the shader material to be altered
	 * @param posX
	 *            x position on the screen (between 0 and 1)
	 * @param posY
	 *            y position on the screen (between 0 and 1)
	 * @param xSize
	 *            the width of the font
	 * @param ySize
	 *            the height of the font
	 * @param text
	 *            the text that should be displayed
	 */
	private void setScreenText(	ShaderMaterial mat,
								float posX,
								float posY,
								float xSize,
								float ySize,
								String text)
	{

		List<Vector2> letters = new ArrayList<Vector2>();
		List<Vector2> positions = new ArrayList<Vector2>();
		List<Vector2> size = new ArrayList<Vector2>();
		Vector2 gridSize = new Vector2(0.0625f, 0.0625f);

		for (int i = 0; i < text.length(); i++)
		{
			char letter = text.charAt(i);
			Vector2 letterV = null;
			letterV = getFontCoord(letter);

			if (letterV != null)
			{
				letterV = new Vector2(letterV.x() * gridSize.x(), letterV.y()
						* gridSize.y());
				letters.add(letterV);
				positions.add(new Vector2((letters.size() - 1) * xSize + posX,
						posY));
				size.add(new Vector2(xSize, ySize));
			}
		}
		mat.setUniform("OVERLAY",
				"lettersCount",
				new UniformInt(letters.size()));
		mat.setUniform("OVERLAY", "letters", new UniformVector2(letters));
		mat.setUniform("OVERLAY", "positions", new UniformVector2(positions));
		mat.setUniform("OVERLAY", "size", new UniformVector2(size));
		mat.setUniform("OVERLAY", "gridSize", new UniformVector2(gridSize));
	}

	/**
	 * helper method to retrieve the coords for a character in the font image
	 * 
	 * @param c
	 *            the character the coords are needed for
	 * @return a vector containing the x and y coords in the font image fot the
	 *         given character
	 */
	private Vector2 getFontCoord(char c)
	{
		int y = c / 16;
		int x = c - (y * 16);

		return new Vector2(x, y);
	}

	/**
	 * 
	 * @return
	 */
	public Viewer getViewer()
	{
		return this.v;
	}

	public Game getGame()
	{
		return this.game;
	}

	/**
	 * 
	 * @param elapsed
	 */
	public void update(double elapsed)
	{
		try
		{
			// perform simulation
			this.simulate(elapsed);

			// Render the scene.
			v.display();

		}
		catch (Exception e)
		{
			logger.error(e);

		}
	}

	/**
	 * sets the overlay text for the given amount of seconds
	 * 
	 * @param text
	 *            the text for the overlay
	 * @param durationInSeconds
	 *            how long should the text be visable
	 */
	public void setTextOverlay(String text, long durationInSeconds)
	{
		withTextOverlay = true;
		textOverlayMaterial.setUniform("OVERLAY",
				"withTextOverlay",
				new UniformBool(withTextOverlay));

		this.textOverlayText = text;
		this.textOverlayTimer = (System.currentTimeMillis() / 1000)
				+ durationInSeconds;
	}

	/**
	 * swaps the inversion shader on / off
	 */
	public static void swapInversionRendering()
	{
		withInversion = !withInversion;

		synchronized (shaderMaterialInversion)
		{
			shaderMaterialInversion.setUniform("InversionPass",
					"withInvert",
					new UniformBool(withInversion));
		}
	}

	/**
	 * swaps the inversion shader on / off
	 */
	public static void swapBloomRendering()
	{
		withBloom = !withBloom;

		synchronized (shaderMaterialBloom)
		{
			shaderMaterialBloom.setUniform("BloomPass",
					"withBloom",
					new UniformBool(withBloom));
		}
	}

	/**
	 * swaps the inversion shader on / off
	 */
	public static boolean swapNightVisionRendering()
	{
		withNightVision = !withNightVision;
		synchronized (shaderMaterialNightVision)
		{
			shaderMaterialNightVision.setUniform("NightVisionPass",
					"withNightVision",
					new UniformBool(withNightVision));
		}

		return withNightVision;
	}

	/**
	 * 
	 * @return if its currently the night vision mode
	 */
	public static boolean isNightVision()
	{
		return withNightVision;
	}

	/**
	 * updates the uniforms of the shadermaterial for the pixelation
	 * 
	 * @param pixelWidth
	 *            the width of the pixelation pixel
	 * @param pixelHeight
	 *            the height of the pixelation pixel
	 * @param durationInSeconds
	 *            the duration time of the pixelation effect
	 */
	public void startPixelation(float pixelWidth,
								float pixelHeight,
								long durationInSeconds)
	{

		this.pixelationTimer = (System.currentTimeMillis() / 1000)
				+ durationInSeconds;

		this.pixelation_width = pixelWidth;
		this.pixelation_height = pixelHeight;

		synchronized (pixelationMaterial)
		{
			pixelationMaterial.setUniform("PixelationPass",
					"pixelIt",
					new UniformBool(true));
		}

	}

	public void addObjectGunObject(ObjectGunObject obj)
	{
		this.game.addObjectGunObject(obj);
	}

}
