package de.noobisoft.powerpong.domain;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.examples.shader.SkyBoxCreator;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.config.ClientConfiguration;
import de.noobisoft.powerpong.domain.player.AIPaddle;
import de.noobisoft.powerpong.domain.player.ETeam;
import de.noobisoft.powerpong.domain.player.Player;
import de.noobisoft.powerpong.domain.player.TeamStatistics;
import de.noobisoft.powerpong.domain.player.weapon.GluePaste;
import de.noobisoft.powerpong.domain.player.weapon.ObjectGunObject;
import de.noobisoft.powerpong.domain.pong.Ball;
import de.noobisoft.powerpong.domain.pong.GameRules;
import de.noobisoft.powerpong.domain.scene.DummySceneElement;
import de.noobisoft.powerpong.domain.scene.Light;
import de.noobisoft.powerpong.domain.scene.Panel;
import de.noobisoft.powerpong.domain.scene.SceneEnvironment;
import de.noobisoft.powerpong.domain.scene.Train;
import de.noobisoft.powerpong.effects.ClientEffectManager;
import de.noobisoft.powerpong.physics.PhysicManager;
import de.noobisoft.powerpong.util.MaterialGenerator;
import de.noobisoft.powerpong.util.ResourceLoader;

/**
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class Game extends AbstractSceneObject
{
	static Logger								logger						= Logger.getLogger(Game.class);
	private static int							MAX_NUM_OBJECTGUNOBJECTS	= 10;

	private boolean								isGameActive				= true;
	private GroupNode							rootNode;

	private Player								currentPlayer;
	private ConcurrentHashMap<String, Player>	players						= new ConcurrentHashMap<String, Player>();
	private List<Light>							lights						= new ArrayList<Light>();
	private List<SceneEnvironment>				environments				= new Vector<SceneEnvironment>();
	private List<DummySceneElement>				dummyElements				= new Vector<DummySceneElement>();
	private Ball								ball;
	private Panel								panel1;

	private SceneNode							skyMirror1					= null;

	private Train								ghostTrain					= null;

	private AIPaddle							paddle1;
	private AIPaddle							paddle2;

	private CameraNode							portal1;
	// private CameraNode portal2;
	private ShapeNode							portal2Shape;
	// private boolean portalMoveUp = true;
	private SceneNode							portal1Plane;
	private SceneNode							portal2Plane;
	private ShaderMaterial						portal1GlossyMat;
	private ShaderMaterial						portal1Mat;
	private ShaderMaterial						portal2Mat;

	private TeamStatistics						team1;
	private TeamStatistics						team2;
	private boolean								isServermode				= false;
	private GameRules							gameRules					= null;

	private List<ObjectGunObject>				objectGunObjects			= new LinkedList<ObjectGunObject>();
	private List<GluePaste>						gluePastes					= new LinkedList<GluePaste>();

	/**
	 * 
	 */
	public Game(boolean isServermode, Player startPlayer)
	{
		this.isServermode = isServermode;
		initPongGame(startPlayer);
	}

	/**
	 * 
	 */
	private void initPongGame(Player startPlayer)
	{
		// root node
		rootNode = new GroupNode("PowerPongRoot");

		// Teams
		team1 = new TeamStatistics(ETeam.BLUE);
		team2 = new TeamStatistics(ETeam.RED);

		if (startPlayer != null)
		{
			currentPlayer = addPlayer(startPlayer.getId(),
					startPlayer.getTeam());
			currentPlayer.changeWeapon(0);
		}

		// lights
		Color lightColorRed = new Color(0.5f, 0.3f, 0.3f, 0.3f);
		Color lightColorBlue = new Color(0.3f, 0.3f, 0.5f, 0.3f);
		Color lightColorWhite = new Color(0.3f, 0.3f, 0.3f, 0.6f);

		Light light1 = new Light(new Vector3(0, 30, -45), lightColorRed);
		lights.add(light1);
		rootNode.addChildNode(light1.getNode());

		Light light2 = new Light(new Vector3(0, 50, 0), lightColorWhite);
		light2.getNode().setName("TopLight");
		lights.add(light2);
		rootNode.addChildNode(light2.getNode());

		Light light3 = new Light(new Vector3(0, 30, 45), lightColorBlue);
		lights.add(light3);
		rootNode.addChildNode(light3.getNode());

		// Light light4 = new Light(new Vector3(-30, 25, 45), lightColorBlue);
		// lights.add(light4);
		// rootNode.addChildNode(light4.getNode());

		// ground
		if (!isServermode())
		{
			SceneEnvironment ground = new SceneEnvironment("meshes/ground.DAE",
					new Vector3(0, 0, 0));
			ground.getNode().setTransform(Transform.translate(new Vector3(0, 0,
					0)).mul(Transform.rotateX(-(float) (Math.PI / 2))));

			// ShapeNode groundShape = new ShapeNode(
			// "GroundShape",
			// Finder.findGeometry(ground.getNode(), null),
			// MaterialGenerator.makeParallaxMaterial("textures/ground",
			// 0.1f,
			// 0.0f,
			// 0.01f));
			// groundShape.setTransform(Transform.translate(new Vector3(0f, 0,
			// 0)).mul(Transform.rotateX(-(float) (Math.PI / 2))));

			// rootNode.addChildNode(groundShape);
			rootNode.addChildNode(ground.getNode());

			// lines
			SceneEnvironment lines = new SceneEnvironment("meshes/pplines.DAE",
					new Vector3(0, 0, 0));
			lines.getNode().setTransform(Transform.translate(new Vector3(0, 0,
					0)).mul(Transform.rotateX(-(float) (Math.PI / 2))));
			rootNode.addChildNode(lines.getNode());

			// wall1
			SceneEnvironment wall1 = new SceneEnvironment("meshes/wall1.DAE",
					new Vector3(0, 0, 0));
			wall1.getNode().setTransform(Transform.translate(new Vector3(0, 0,
					0)).mul(Transform.rotateX(-(float) (Math.PI / 2))));
			ShapeNode wall1Shape = new ShapeNode(
					"Wall1Shape",
					Finder.findGeometry(wall1.getNode(), null),
					MaterialGenerator.makeParallaxMaterial("textures/brick-floor-tileable",
							0.01f,
							0f,
							30.0f));
			wall1Shape.setTransform(Transform.translate(new Vector3(-32.5f, 0,
					0)).mul(Transform.rotateX(-(float) (Math.PI / 2))));
			// rootNode.addChildNode(wall1.getNode());
			rootNode.addChildNode(wall1Shape);

			// wall2
			SceneEnvironment wall2 = new SceneEnvironment("meshes/wall2.DAE",
					new Vector3(0, 0, 0));
			wall2.getNode().setTransform(Transform.translate(new Vector3(0, 0,
					0)).mul(Transform.rotateX(-(float) (Math.PI / 2))));

			ShapeNode wall2Shape = new ShapeNode(
					"Wall2Shape",
					Finder.findGeometry(wall2.getNode(), null),
					MaterialGenerator.makeParallaxMaterial("textures/brick-floor-tileable",
							0.01f,
							0f,
							30.0f));
			wall2Shape.setTransform(Transform.translate(new Vector3(32.5f, 0, 0)).mul(Transform.rotateX(-(float) (Math.PI / 2))));
			// rootNode.addChildNode(wall2.getNode());
			rootNode.addChildNode(wall2Shape);

			// portal1
			SceneEnvironment portal1InWall = new SceneEnvironment(
					"meshes/portal1.DAE", new Vector3(0, 0, 0));
			portal1InWall.getNode().setTransform(Transform.translate(new Vector3(
					0, 0, 0)).mul(Transform.rotateX(-(float) (Math.PI / 2))));
			rootNode.addChildNode(portal1InWall.getNode());

			// portal2
			SceneEnvironment portal2InWall = new SceneEnvironment(
					"meshes/portal2.DAE", new Vector3(0, 0, 0));
			portal2InWall.getNode().setTransform(Transform.translate(new Vector3(
					0, 0, 0)).mul(Transform.rotateX(-(float) (Math.PI / 2))));
			rootNode.addChildNode(portal2InWall.getNode());

			// ***************
			// portals
			try
			{
				// portal plane
				portal1Plane = ResourceLoader.getCollada("meshes/plane.dae");
				portal1Plane.setTransform(Transform.translate(-31.9f, 3, 0).mul(Transform.rotateYDeg(90).mul(Transform.scale(15,
						15,
						15))));
				rootNode.addChildNode(portal1Plane);

				// portal camera
				portal1 = new CameraNode("portal1", -4f / 3f, 60f);
				// portal1.setTransform(Transform.translate(31.9f, 2, 0));
				rootNode.addChildNode(portal1);

				portal1Mat = MaterialGenerator.makeViewablePortalMaterial("textures/portal",
						0.05f,
						0.01f,
						0.9f);
				
				portal1Mat.setMaterialClass("PortalClass");
				
				ShapeNode mirrorShape = Finder.find(portal1Plane,
						ShapeNode.class,
						null);
				mirrorShape.setMaterial(portal1Mat);

//				// portal1 glossy
//				SceneNode portal1GlossyPlane = ResourceLoader.getCollada("meshes/plane.dae");
//
//				portal1GlossyPlane.setTransform(Transform.translate(32f, 3, 0).mul(Transform.rotateYDeg(90).mul(Transform.scale(15,
//						15,
//						15))));
//
//				portal1GlossyMat = MaterialGenerator.makeGlossyPortalMaterial("textures/portal",
//						0.9f,
//						0.15f,
//						0.01f);
//
//				ShapeNode glossyShape = new ShapeNode(
//						"GlossyPlaneShape",
//						Finder.findGeometry(portal1GlossyPlane, null),
//						portal1GlossyMat,
//						Transform.translate(-30.0f, 3, 0).mul(Transform.rotateYDeg(90).mul(Transform.scale(15,
//								15,
//								15))));
//				rootNode.addChildNode(glossyShape);

				// porta2 plane
				portal2Plane = ResourceLoader.getCollada("meshes/plane.dae");
				// portal2Plane.setTransform(Transform.translate(31.9f, 3,
				// 0).mul(Transform.rotateYDeg(90).mul(Transform.scale(100,15,50))));
				portal2Mat = MaterialGenerator.makePortalMaterial("textures/portal",
						0.9f,
						0.15f,
						0.01f);

				portal2Mat.setMaterialClass("PortalClass");
				portal2Shape = new ShapeNode("Portal2Shape",
						Finder.findGeometry(portal2Plane, null), portal2Mat);

				portal2Shape.setTransform(Transform.translate(new Vector3(
						32.01f, 3, 0)).mul(Transform.rotateY(-(float) (Math.PI / 2)).mul(Transform.scale(15,
						15,
						15))));
				rootNode.addChildNode(portal2Shape);
				

			}
			catch (Exception e)
			{
				logger.error(e);
			}

		}

		// panel1
		panel1 = new Panel();
		panel1.getNode().setTransform(Transform.translate(new Vector3(0, 0, 0)).mul(Transform.rotateX(-(float) (Math.PI / 2))));
		rootNode.addChildNode(panel1.getNode());

		// //panel2
		// Panel panel2 = new Panel();
		// panel2.getNode().setTransform(Transform.translate(new Vector3(0, 0,
		// 0)).mul(Transform.rotateX(-(float) (Math.PI /
		// 2)).mul(Transform.rotateYDeg(180))));
		// rootNode.addChildNode(panel2.getNode());

		// ball
		this.ball = new Ball();
		ball.getNode().setName("Ballnode");
		rootNode.addChildNode(ball.getNode());

		// test object
		// FirstPhysicsObject fi = new FirstPhysicsObject();
		// rootNode.addChildNode(fi.getNode());

		// paddle creation
		this.paddle1 = new AIPaddle(ETeam.BLUE);
		this.paddle2 = new AIPaddle(ETeam.RED);
		rootNode.addChildNode(paddle1.getNode());
		rootNode.addChildNode(paddle2.getNode());

		// create the ghosttrain
		ghostTrain = new Train();

		// only create the skybox on the client
		if (!isServermode)
		{
			try
			{
				String texture = "textures/skybox/mountain_ring";

				if (ClientConfiguration.world.equals("Grimm Night"))
					texture = "textures/skybox/grimmnight";
				else if (ClientConfiguration.world.equals("Interstellar"))
					texture = "textures/skybox/interstellar";
				else if (ClientConfiguration.world.equals("Miramar"))
					texture = "textures/skybox/miramar";
				else if (ClientConfiguration.world.equals("Stormy Days"))
					texture = "textures/skybox/stormydays";
				else if (ClientConfiguration.world.equals("Violent Days"))
					texture = "textures/skybox/violentdays";

				// Tell the MaterialGenerator about the skybox decision
				MaterialGenerator.setEnvironmentCubeTexture(texture);

				// Create the Skybox
				SkyBoxCreator skyCreator = new SkyBoxCreator(texture);
				skyMirror1 = skyCreator.getSkyBox("SKYMIRROR1");
				SceneNode sky = skyCreator.getSkyBox("SKY").setTransform(Transform.scale(1000));
				rootNode.addChildNodes(sky, skyMirror1);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * a second initialisation for objects that need a working game object
	 */
	public void initAfterCreation()
	{
		ClientEffectManager.getInstance().setGame(this);

		// ********************
		// particle systems
		// ********************
		ClientEffectManager.getInstance().createFirePole(new Vector3(-31.464f,
				10f, -30f));
		ClientEffectManager.getInstance().createFirePole(new Vector3(31.464f,
				10f, -30f));
		ClientEffectManager.getInstance().createFirePole(new Vector3(-31.464f,
				10f, 30f));
		ClientEffectManager.getInstance().createFirePole(new Vector3(31.464f,
				10f, 30f));

	}

	/**
	 * 
	 * @return
	 */
	public Ball getBall()
	{
		return this.ball;
	}

	/**
	 * 
	 * @return
	 */
	public AIPaddle getPaddle1()
	{
		return this.paddle1;
	}

	/**
	 * 
	 * @return
	 */
	public AIPaddle getPaddle2()
	{
		return this.paddle2;
	}

	/**
	 * 
	 * @return
	 */
	public Player getCurrentPlayer()
	{
		return this.currentPlayer;
	}

	/**
	 * 
	 * @param currentPlayer
	 */
	public void setCurrentPlayer(Player currentPlayer)
	{
		this.currentPlayer = currentPlayer;
	}

	/**
	 * 
	 * @param id
	 * @param team
	 * @return
	 */
	public Player addPlayer(String id, ETeam team)
	{
		logger.debug("Adding player: " + id + " for team: " + team.toString());

		Player player = new Player(id, team, true);
		if (!isServermode)
		{
			player.setCameraRatio((float) ClientConfiguration.screenWidth
					/ (float) ClientConfiguration.screenHeight);
		}
		rootNode.addChildNodes(player.getNode());
		players.put(id, player);
		return player;
	}

	/**
	 * 
	 * @return the hashmap of players. keys are the players ids
	 */
	public ConcurrentHashMap<String, Player> getAllPlayers()
	{
		return this.players;
	}

	/**
	 * updates the players position and orientation (Y-rotation)
	 * 
	 * @param id
	 *            the id of the player
	 * @param newPosition
	 *            the new position
	 * @param rotationY
	 *            new rotation value
	 * 
	 */
	public void changePositionOfPlayer(	String id,
										Vector3 newPosition,
										float rotationY)
	{
		Player p = players.get(id);
		if (p != null)
		{
			p.setPosition(newPosition);
			p.setRotationY(rotationY);
		}
	}

	/**
	 * updates the players energy bar if he is found in the list of active
	 * players
	 * 
	 * @param id
	 *            the id of the player
	 * @param energy
	 *            the new energy value
	 */
	public void changeWeaponEnergyOfPlayer(String id, double energy)
	{
		Player p = players.get(id);
		if (p != null)
		{
			p.setWeaponEnergy(energy);
		}
	}

	/**
	 * 
	 * @param id
	 */
	public boolean removePlayer(String id)
	{
		Player player = players.get(id);
		if (player != null)
		{
			logger.info("Removed the following player: " + id);
			rootNode.removeChildNode(player.getNode());
			players.remove(id);
		}

		if (player == null)
			return false;
		else
			return true;
	}

	/**
	 * @return the team1
	 */
	public TeamStatistics getTeam1()
	{
		return team1;
	}

	/**
	 * @return the team2
	 */
	public TeamStatistics getTeam2()
	{
		return team2;
	}

	/**
	 * updates each object. things like movement ai thinking and so on
	 */
	public void update(double elapsedTime, Game game)
	{
		super.update(elapsedTime, game, this.isServermode);

		for (Player player : players.values())
		{
			player.update(elapsedTime, this, this.isServermode);
		}

		if (ghostTrain != null)
			ghostTrain.update(elapsedTime, game, this.isServermode);

		if (ball != null)
			ball.update(elapsedTime, this, this.isServermode);

		if (paddle1 != null)
			paddle1.update(elapsedTime, this, this.isServermode);

		if (paddle2 != null)
			paddle2.update(elapsedTime, this, this.isServermode);

		if (panel1 != null)
			panel1.update(elapsedTime, this, this.isServermode);

		// check the game rules if we are in server mode
		if (isServermode && gameRules != null)
		{
			gameRules.update();
		}

		for (DummySceneElement element : dummyElements)
		{
			element.update(elapsedTime, this, this.isServermode);
		}

		if (!isServermode)
		{
			for (Light element : lights)
			{
				if (!element.getNode().getName().equals("TopLight"))
				{
					float xPos = game.getBall().getNode().getTransform().getMatrix().getData()[3];
					element.setPosition(new Vector3(xPos,
							element.getPosition().y(),
							element.getPosition().z()));
				}

				element.update(elapsedTime, this, this.isServermode);
			}
		}

		for (SceneEnvironment element : environments)
		{
			element.update(elapsedTime, this, this.isServermode);
		}

		for (ObjectGunObject gunObj : objectGunObjects)
		{
			gunObj.update(elapsedTime, this, this.isServermode);
		}

		for (GluePaste p : gluePastes)
		{
			p.update(elapsedTime, this, this.isServermode);
		}

		// update portal camera
		// update mirror camera transformation
		if (getCurrentPlayer() != null
				&& getCurrentPlayer().getCamera() != null
				&& portal1Plane != null)
		{
			Transform camTrans = Transform.translate(getCurrentPlayer().getPosition().x()
					+ (31.9f * 2),
					getCurrentPlayer().getPosition().y(),// - 1.0f,
					getCurrentPlayer().getPosition().z()).mul(Transform.rotateY(getCurrentPlayer().getRotationY())).mul(Transform.rotateX(getCurrentPlayer().getRotationX()));
			portal1.setTransform(camTrans);

			// animate portal 2
			/*
			 * if (portal2Shape.getTransform().getMatrix().translation().y() <
			 * -5)
			 * portal2Shape.setTransform(portal2Shape.getTransform().mul(Transform
			 * .translate(0, 0.5f, 0)));
			 * portal2Shape.setTransform(portal2Shape.getTransform
			 * ().mul(Transform.translate(0, -(float) elapsedTime * 0.005f,
			 * 0)));
			 */

		}

		if (!isServermode())
		{
			skyMirror1.setTransform(portal1.getTransform().extractTranslation());

			// update portal shader
			float yflow = -(float) ((new Date().getTime() % 2000000.0) * 0.000005);
			portal1Mat.setUniform("AMBIENT", "yflow", new UniformFloat(yflow));

			portal2Mat.setUniform("AMBIENT", "yflow", new UniformFloat(yflow));
			// portal1GlossyMat.setUniform("AMBIENT", "yflow", new
			// UniformFloat(yflow));
			// portal1GlossyMat.setUniform("LIGHTING", "yflow", new
			// UniformFloat(yflow));
			// System.out.println("YFlow: "+yflow);
		}
	}

	/**
	 * return the team of the player with the given name. if the player is not
	 * found, null is returned
	 * 
	 * @param name
	 *            the name of the player
	 * @return the team for the player
	 */
	public ETeam getTeamForName(String name)
	{
		ETeam ret = null;

		for (Player p : this.players.values())
		{
			if (p.getId().equals(name))
			{
				ret = p.getTeam();
				break;
			}
		}

		return ret;
	}

	@Override
	public SceneNode getNode()
	{
		return this.rootNode;
	}

	/**
	 * @return the isGameActive
	 */
	public boolean isGameActive()
	{
		return isGameActive;
	}

	/**
	 * @param isGameActive
	 *            the isGameActive to set
	 */
	public void setGameActive(boolean isGameActive)
	{
		this.isGameActive = isGameActive;
	}

	/**
	 * @param gameRules
	 *            the gameRules to set
	 */
	public void setGameRules(GameRules gameRules)
	{
		this.gameRules = gameRules;
	}

	/**
	 * @return the isServermode
	 */
	public boolean isServermode()
	{
		return isServermode;
	}

	/**
	 * @return the portal1
	 */
	public CameraNode getPortal1()
	{
		return portal1;
	}

	/**
	 * adds a new objectgun object
	 * 
	 * @param obj
	 */
	public void addObjectGunObject(ObjectGunObject obj)
	{
		if (objectGunObjects.size() >= MAX_NUM_OBJECTGUNOBJECTS)
		{
			ObjectGunObject removeObj = objectGunObjects.remove(0);
			this.rootNode.removeChildNode(removeObj.getNode());
			PhysicManager.getInstance().removeRigidBody(removeObj.getRigidBody());
		}

		this.objectGunObjects.add(obj);
	}

	/**
	 * @return the objectGunObjects
	 */
	public List<ObjectGunObject> getObjectGunObjects()
	{
		return objectGunObjects;
	}

	public void setObjectGunObjectsVisible(boolean visible)
	{
		// make the train un/visible
		if (visible)
		{
			rootNode.addChildNode(this.ghostTrain.getNode());
		}
		else
		{
			rootNode.removeChildNode(this.ghostTrain.getNode());
		}

		// set the ObjectGunObjects un/visible
		for (ObjectGunObject o : objectGunObjects)
		{
			o.setVisible(visible);
		}
	}

	/**
	 * 
	 * @param objects
	 */
	public void updateObjectGunObjects(List<ObjectGunObject> objects)
	{
		// for all objects to be update
		for (ObjectGunObject o : objects)
		{
			boolean found = false;

			// search the corresponding item
			for (ObjectGunObject obj : this.objectGunObjects)
			{
				if (o.getId() == obj.getId())
				{
					found = true;
					obj.setMatrixFromServer(o.getMatrixFromServer());
					break;
				}
			}
			// if we should update an object that is not there, create a new one
			if (!found)
			{
				ObjectGunObject newObjectGunShot = new ObjectGunObject(
						o.getPosition(), new Vector3(), o.getId(), true);

				// add it to the game
				((GroupNode) getNode()).addChildNode(newObjectGunShot.getNode());

				addObjectGunObject(newObjectGunShot);
			}
		}
	}

	/**
	 * adds a glue paste object to be animated
	 * 
	 * @param p
	 *            the gluepaste
	 */
	public void addGluePaste(GluePaste p)
	{
		this.gluePastes.add(p);
	}

	/**
	 * removes a gluepaste object from the animation list
	 * 
	 * @param p
	 *            the gluepaste
	 */
	public void removeGluePaste(GluePaste p)
	{
		if (!this.gluePastes.remove(p))
		{
			if (!isServermode())
				logger.warn("Could not remove the given GluePaste object");
		}
	}

	/**
	 * @return the ghostTrain
	 */
	public Train getGhostTrain()
	{
		return ghostTrain;
	}
}
