package de.noobisoft.powerpong.domain.scene;

import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.domain.AbstractSceneObject;

/**
 * A dummy scene element.
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class DummySceneElement extends AbstractSceneObject
{
	private SceneNode	node;

	/**
	 * 
	 */
	public DummySceneElement(SceneNode node, Vector3 pos, Vector3 rotationVec)
	{
		// Random rand = new Random(new Date().getTime());

		GroupNode gnode = new GroupNode();
		gnode.addChildNode(node);
		this.node = gnode;

		setPosition(pos);

		// node.setTransform(Transform.translate(this.pos));

		setRotationX(rotationVec.x());
		setRotationY(rotationVec.y());
		setRotationZ(rotationVec.z());
	}

	@Override
	public SceneNode getNode()
	{
		return node;
	}
}
