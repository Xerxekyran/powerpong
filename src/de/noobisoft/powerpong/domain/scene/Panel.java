package de.noobisoft.powerpong.domain.scene;

import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.domain.AbstractSceneObject;
import de.noobisoft.powerpong.domain.Game;
import de.noobisoft.powerpong.domain.player.TeamStatistics;
import de.noobisoft.powerpong.util.ResourceLoader;

public class Panel extends AbstractSceneObject
{
	private GroupNode panelNode;
	private PanelSign signBlueDez;
	private PanelSign signBlueEin;
	private PanelSign signRedDez;
	private PanelSign signRedEin;
	
	public Panel()
	{		
		panelNode = new GroupNode();
		
		signBlueDez = new PanelSign();
		signBlueDez.getNode().setTransform(Transform.translate(new Vector3(0,-4,0)));
		panelNode.addChildNode(signBlueDez.getNode());
		signBlueDez.setNumber(0);
		
		signBlueEin = new PanelSign();
		signBlueEin.getNode().setTransform(Transform.translate(new Vector3(0,-2,0)));
		panelNode.addChildNode(signBlueEin.getNode());
		signBlueEin.setNumber(1);
		
		signRedDez = new PanelSign();
		signRedDez.getNode().setTransform(Transform.translate(new Vector3(0,2,0)));
		panelNode.addChildNode(signRedDez.getNode());
		signRedDez.setNumber(2);
		
		signRedEin = new PanelSign();
		signRedEin.getNode().setTransform(Transform.translate(new Vector3(0,4,0)));
		panelNode.addChildNode(signRedEin.getNode());
		signRedEin.setNumber(3);
		
		panelNode.addChildNode(ResourceLoader.getCollada("meshes/panel.DAE"));
		panelNode.addChildNode(ResourceLoader.getCollada("meshes/panel_signs/doublepoint.DAE"));
	}
	
	@Override
	public SceneNode getNode()
	{
		return panelNode;
	}
	
	@Override
	public void update(double elapsedTime, Game game, boolean inServerMode)
	{
		TeamStatistics blue = game.getTeam1();
		TeamStatistics red = game.getTeam2();
		
		if(blue.getScore()%10 != signBlueEin.getNumber())
			signBlueEin.setNumber(blue.getScore()%10);
		
		if(red.getScore()%10 != signRedEin.getNumber())
			signRedEin.setNumber(red.getScore()%10);
		
		if((int)(blue.getScore()/10) != signBlueDez.getNumber())
			signBlueDez.setNumber((int)(blue.getScore()/10));
		
		if((int)(red.getScore()/10) != signRedDez.getNumber())
			signRedDez.setNumber((int)(red.getScore()/10));
		
//		signBlueDez.update(elapsedTime, game, inServerMode);
//		signBlueEin.update(elapsedTime, game, inServerMode);
//		signRedDez.update(elapsedTime, game, inServerMode);
//		signRedEin.update(elapsedTime, game, inServerMode);
		
		//super.update(elapsedTime, game, inServerMode);
	}

}
