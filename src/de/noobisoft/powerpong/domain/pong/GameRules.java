package de.noobisoft.powerpong.domain.pong;

import java.util.Date;
import java.util.Random;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.Transform;

import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.domain.Game;
import de.noobisoft.powerpong.network.MinaServer;
import de.noobisoft.powerpong.network.messages.ScoreBoard;

/**
 * 
 * @author Lars George
 * 
 */
public class GameRules
{
	private MinaServer	networkLayer	= null;
	private Game		game			= null;

	/**
	 * 
	 * @param networkLayer
	 *            needs to know how to send messages
	 */
	public GameRules(Game game, MinaServer networkLayer)
	{
		this.networkLayer = networkLayer;
		this.game = game;
	}

	/**
	 * checks all game rule belonging things, like counting the score
	 */
	public void update()
	{
		Ball ball = game.getBall();
		Vector3 ballpos = ball.getNode().getTransform().getMatrix().translation();

		// is the ball in the portal?
		// teleport
		if (ballpos.z() < 6.8f && ballpos.z() > -6.8f && ballpos.x() < -32f)
		{
			Vector3f ballForce = new Vector3f();
			ballForce = ball.getRigidBody().getLinearVelocity(ballForce);
			ballForce.y = 0f;
			ball.getRigidBody().setLinearVelocity(ballForce);

			ball.getRigidBody().proceedToTransform(new com.bulletphysics.linearmath.Transform(
					new Matrix4f(Matrix4.translate(new Vector3(31.5f, 4,
							ball.getPosition().z())).getData())));
		}
		else if (ballpos.z() < 6.8f && ballpos.z() > -6.8f && ballpos.x() > 32f)
		{
			Vector3f ballForce = new Vector3f();
			ballForce = ball.getRigidBody().getLinearVelocity(ballForce);
			ballForce.y = 0f;
			ball.getRigidBody().setLinearVelocity(ballForce);

			ball.getRigidBody().proceedToTransform(new com.bulletphysics.linearmath.Transform(
					new Matrix4f(Matrix4.translate(new Vector3(-31.5f, 4,
							ball.getPosition().z())).getData())));
		}

		// if there was a goal
		if (ballpos.z() < -53 && ballpos.x() > -35 && ballpos.x() < 35
				&& game.isGameActive())
		{
			game.getTeam1().increaseScore();
			game.setGameActive(false);

			// send the new score to all clients
			ScoreBoard sb = new ScoreBoard();
			sb.setScoreTeam1(game.getTeam1().getScore());
			sb.setScoreTeam2(game.getTeam2().getScore());
			networkLayer.broadcast(sb.getNetworkData());

		}
		else if (ballpos.z() > 53 && ballpos.x() > -35 && ballpos.x() < 35
				&& game.isGameActive())
		{
			game.getTeam2().increaseScore();
			game.setGameActive(false);

			// send the new score to all clients
			ScoreBoard sb = new ScoreBoard();
			sb.setScoreTeam1(game.getTeam1().getScore());
			sb.setScoreTeam2(game.getTeam2().getScore());
			networkLayer.broadcast(sb.getNetworkData());
		}

		// if the ball is under the field
		if (ballpos.y() < -15)
		{
			Random rand = new Random(new Date().getTime());

			// spawn it at the team1 pos
			if (ballpos.z() < 0)
			{
				ball.getRigidBody().proceedToTransform(new Transform(
						new Matrix4f(
								Matrix4.translate(new Vector3(
										game.getPaddle1().getNode().getTransform().getMatrix().translation().x(),
										-10f, -70f)).getData())));

				ball.getRigidBody().setLinearVelocity(new Vector3f(
						rand.nextFloat() * 2 - 1, 30.0f, 20.0f));
				ball.getRigidBody().setAngularVelocity(new Vector3f(0f, 0f, 0f));
			}
			// spwan it at the team2 pos
			else
			{
				ball.getRigidBody().proceedToTransform(new Transform(
						new Matrix4f(
								Matrix4.translate(new Vector3(
										game.getPaddle1().getNode().getTransform().getMatrix().translation().x(),
										-10f, 70f)).getData())));

				ball.getRigidBody().setLinearVelocity(new Vector3f(
						rand.nextFloat() * 2 - 1, 30f, -20.0f));
				ball.getRigidBody().setAngularVelocity(new Vector3f(0f, 0f, 0f));
			}
		}

		// reactivate game if ball is in field
		if (!game.isGameActive() && ballpos.z() < 45 && ballpos.z() > -45
				&& ballpos.x() < 30 && ballpos.x() > -30)
		{
			game.setGameActive(true);
		}
	}
}
