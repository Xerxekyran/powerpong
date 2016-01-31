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
 * This weapon creates a small object, that interfers physicall with the ball in
 * the direction the weapon is fired (if it hits the ball)
 * 
 * @author Lars George
 * 
 */
public class PeaGun extends AbstractWeapon
{
	/** this value is used to create the physical effect of moving other objects **/
	public static int	POWER		= 70;

	/** time in ms for a shot to be alive in the world before deletion **/
	public static int	SHOT_TTL	= 4000;

	private SceneNode	node		= null;

	public PeaGun(ETeam team)
	{
		SceneNode gunNode = ResourceLoader.getCollada("meshes/fuel_rod/models/fuel rod.dae");

		// set the material class to weapon (we need to render it differently)
		Collection<Material> mats = Finder.findAllMaterials(gunNode, null);
		for (Material mat : mats)
		{
			((ShaderMaterial) mat).setMaterialClass("Weapon");
		}

		gunNode.setTransform(Transform.translate(0.5f, 0, -1).mul(Transform.rotateYDeg(-90)).mul(Transform.rotateXDeg(90)).mul(Transform.scale(0.03f)));

		node = new GroupNode();

		SceneNode crosshairNode = null;
		if (team == ETeam.BLUE)
			crosshairNode = ResourceLoader.getCollada("meshes/crosshairs/ch_peaBlue.dae");
		else
			crosshairNode = ResourceLoader.getCollada("meshes/crosshairs/ch_peaRed.dae");
		crosshairNode.setTransform(Transform.translate(new Vector3(0, 0.37f, -2)).mul(Transform.rotateXDeg(-90)));

		// set the material class to weapon (we need to render it differently)
		mats = Finder.findAllMaterials(crosshairNode, null);
		for (Material mat : mats)
		{
			((ShaderMaterial) mat).setMaterialClass("Weapon");
		}

		((GroupNode) node).addChildNode(crosshairNode);
		((GroupNode) node).addChildNode(gunNode);
	}

	@Override
	public Integer getNeededEnegery()
	{
		return 20;
	}

	@Override
	public Integer getFireRateTimeInMs()
	{
		return 500;
	}

	@Override
	public SceneNode getNode()
	{
		return node;
	}

	@Override
	public EWeapons getWeaponType()
	{
		return EWeapons.PeaGun;
	}
}
