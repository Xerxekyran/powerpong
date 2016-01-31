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
 * This weapon creates new objects in the world
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class ObjectGun extends AbstractWeapon
{
	/** this value is used to create the physical effect of moving other objects **/
	public static int	POWER	= 30;

	private SceneNode	node	= null;

	public ObjectGun(ETeam team)
	{
		SceneNode gunNode = ResourceLoader.getCollada("meshes/mod_weapon/models/flame.dae");

		// set the material class to weapon (we need to render it differently)
		Collection<Material> mats = Finder.findAllMaterials(gunNode, null);
		for (Material mat : mats)
		{
			((ShaderMaterial) mat).setMaterialClass("Weapon");
		}

		gunNode.setTransform(Transform.translate(0.5f, -0.2f, -2.0f).mul(Transform.rotateYDeg(-90)).mul(Transform.rotateXDeg(90)).mul(Transform.scale(0.03f)));
		node = new GroupNode();

		SceneNode crosshairNode = null;
		if (team == ETeam.BLUE)
			crosshairNode = ResourceLoader.getCollada("meshes/crosshairs/ch_pointBlue.dae");
		else
			crosshairNode = ResourceLoader.getCollada("meshes/crosshairs/ch_pointRed.dae");
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
		return 15;
	}

	@Override
	public Integer getFireRateTimeInMs()
	{
		return 1;
	}

	@Override
	public SceneNode getNode()
	{
		return node;
	}

	@Override
	public EWeapons getWeaponType()
	{
		return EWeapons.ObjectActivator;
	}
}
