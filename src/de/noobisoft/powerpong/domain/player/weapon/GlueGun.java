package de.noobisoft.powerpong.domain.player.weapon;

import java.util.Collection;

import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Material;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.domain.player.ETeam;
import de.noobisoft.powerpong.util.ResourceLoader;

/**
 * This weapon creates a small beam, that pushes objects instantly in firing
 * direction
 * 
 * @author Lars George
 * 
 */
public class GlueGun extends AbstractWeapon
{
	/** this value is used to create the physical effect of moving other objects **/
	public static int	POWER			= 30;

	// how much should the force vector be reduced?
	public static float	movementStopper	= 0.8f;

	/** time in ms for a shot to be alive in the world before deletion **/
	public static int	SHOT_TTL		= 10000;

	private SceneNode	node			= null;

	public GlueGun(ETeam team)
	{
		SceneNode gunNode = ResourceLoader.getCollada("meshes/beam_rifle/models/Beam Rifle.dae");
		
		// set the material class to weapon (we need to render it differently)
		Collection<Material> mats = Finder.findAllMaterials(gunNode, null);		
		for(Material mat : mats)
		{
			((ShaderMaterial)mat).setMaterialClass("Weapon");
		}

		gunNode.setTransform(Transform.translate(0.5f, -0.2f, -2.0f).mul(Transform.rotateYDeg(-90)).mul(Transform.rotateXDeg(90)).mul(Transform.scale(0.028f)));
		node = new GroupNode();
		
		SceneNode crosshairNode = null; 
		if(team == ETeam.BLUE)
			crosshairNode = ResourceLoader.getCollada("meshes/crosshairs/ch_granateBlue.dae");
		else
			crosshairNode = ResourceLoader.getCollada("meshes/crosshairs/ch_granateRed.dae");
		crosshairNode.setTransform(Transform.translate(new Vector3(0, 0.37f, -2)).mul(Transform.rotateXDeg(-90)));
		
		// set the material class to weapon (we need to render it differently)
		mats = Finder.findAllMaterials(crosshairNode, null);		
		for(Material mat : mats)
		{
			((ShaderMaterial)mat).setMaterialClass("Weapon");
		}
		
		((GroupNode)node).addChildNode(crosshairNode);
		((GroupNode)node).addChildNode(gunNode);
	}

	@Override
	public Integer getNeededEnegery()
	{
		return 10;
	}

	@Override
	public Integer getFireRateTimeInMs()
	{
		return 2000;
	}

	@Override
	public SceneNode getNode()
	{
		return node;
	}

	@Override
	public EWeapons getWeaponType()
	{
		return EWeapons.GlueGun;
	}
}
