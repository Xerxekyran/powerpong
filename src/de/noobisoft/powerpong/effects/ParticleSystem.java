package de.noobisoft.powerpong.effects;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.media.opengl.GL2GL3;

import org.apache.log4j.Logger;

import de.bht.jvr.core.AttributeCloud;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.attributes.AttributeFloat;
import de.bht.jvr.core.attributes.AttributeVector4;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.math.Vector3;
import de.bht.jvr.math.Vector4;
import de.noobisoft.powerpong.domain.AbstractSceneObject;
import de.noobisoft.powerpong.domain.Game;

/**
 * a configurable particle system that uses sprites to display each particle
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class ParticleSystem extends AbstractSceneObject
{
	static Logger					logger				= Logger.getLogger(ParticleSystem.class);

	private GroupNode				node				= null;

	private static ShaderProgram	spriteShaderProgram	= null;

	private List<Float>				energy				= new LinkedList<Float>();
	private List<Vector4>			positions			= new LinkedList<Vector4>();

	private Particle				particles[]			= null;

	private Vector3					mainDirection		= null;
	private Vector3					directionDistortion	= null;
	private float					minSpeed			= 10.1f;
	private float					maxSpeed			= 30.3f;
	private float					startEnergy			= 2f;

	private double					energyDamping		= 0.9;
	private float					particleSize		= 0.07f;

	private double					timeToDelete		= 0;
	private double					emitRate			= 10;

	private boolean					continouslyEmitting	= false;

	private Vector4					gravity				= null;
	private AttributeCloud			attrCloud			= null;

	private Random					rand				= new Random();

	private int						numParticles		= 10;

	/**
	 * 
	 * @param position
	 *            start position of the particles
	 * @param mainDirection
	 *            main flow direction of the particles
	 * @param directionDistortion
	 *            the x,y,z values of this vector define how the particles have
	 *            scattered directions (around the main directoin vector)
	 * @param minSpeed
	 *            min speed of particles
	 * @param maxSpeed
	 *            max speed of particles
	 * @param numParticles
	 *            how many particles should be generated max
	 * @param emitRate
	 *            how many particles should be spawnt per second
	 * @param particleSize
	 *            the size of a particle
	 * @param durationTimeInSeconds
	 *            how long should the particle system exist? 0 means infinite
	 * @param continouslyEmitting
	 *            should particles that disapeared be respawned?
	 * @param energyDamping
	 *            how should be energy of a particle be damped (values between 0
	 *            and 1). energy is used to calculate the visibility of a
	 *            particle
	 * @param startEnergy
	 *            how much start energy do the particles have
	 * @param spriteTextureFile
	 *            the texture to use for the particles
	 * @param gravity
	 *            a gravity vector. this one does not need to direct to the
	 *            ground. a vector to the "left" would result in something like
	 *            wind
	 */
	public ParticleSystem(Vector3 position,
			Vector3 mainDirection,
			Vector3 directionDistortion,
			float minSpeed,
			float maxSpeed,
			int numParticles,
			double emitRate,
			float particleSize,
			double durationTimeInSeconds,
			boolean continouslyEmitting,
			double energyDamping,
			float startEnergy,
			String spriteTextureFile,
			Vector3 gravity)
	{
		this.numParticles = numParticles;
		this.continouslyEmitting = continouslyEmitting;
		this.mainDirection = mainDirection.normalize();
		this.energyDamping = energyDamping;
		this.directionDistortion = directionDistortion;
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
		this.particleSize = particleSize;
		this.emitRate = emitRate;
		this.startEnergy = startEnergy;
		this.gravity = new Vector4(gravity.x(), gravity.y(), gravity.z(), 0);

		// if 0 is given, this means infinity
		if (durationTimeInSeconds == 0)
		{
			this.timeToDelete = 0;
		}
		else
		{
			this.timeToDelete = System.currentTimeMillis()
					+ durationTimeInSeconds * 1000;
		}

		try
		{
			this.node = new GroupNode("ParticleSystem");

			// the shape node
			ShapeNode particleSystemShape = new ShapeNode();
			attrCloud = new AttributeCloud(this.numParticles, GL2GL3.GL_POINTS);
			particleSystemShape.setGeometry(attrCloud);

			particles = new Particle[numParticles];

			for (int i = 0; i < this.numParticles; i++)
			{
				positions.add(new Vector4(0, 0, 0, 1.0f));
				energy.add(0f);

				particles[i] = new Particle();
			}

			// the shader program
			if (spriteShaderProgram == null)
			{
				spriteShaderProgram = new ShaderProgram(new File(
						"shader/spritesParticle.vs"), new File(
						"shader/spritesParticle.gs"), new File(
						"shader/spritesParticle.fs"));

				// configure the shader pipeline
				spriteShaderProgram.setParameter(GL2GL3.GL_GEOMETRY_INPUT_TYPE,
						GL2GL3.GL_POINTS);
				spriteShaderProgram.setParameter(GL2GL3.GL_GEOMETRY_OUTPUT_TYPE,
						GL2GL3.GL_TRIANGLE_STRIP);
				spriteShaderProgram.setParameter(GL2GL3.GL_GEOMETRY_VERTICES_OUT,
						4);
			}

			// the material
			ShaderMaterial mat = new ShaderMaterial("PARTICLES",
					spriteShaderProgram);

			mat.setTexture("PARTICLES", "jvr_Texture0", new Texture2D(new File(
					spriteTextureFile)));
			mat.setUniform("PARTICLES", "jvr_particleSize", new UniformFloat(
					this.particleSize));

			particleSystemShape.setMaterial(mat);

			// add the shape to the groupnode
			this.node.setTransform(Transform.translate(position));
			this.node.addChildNode(particleSystemShape);
		}
		catch (Exception e)
		{
			logger.error(e);
		}
	}

	/**
	 * 
	 * @return a randomized velocity vector. its not totally random, but with
	 *         the settings of this object
	 */
	private Vector4 generateRandomVelocity()
	{
		Vector3 velocity = new Vector3(
				slightlyChangedVelocityComponent(mainDirection.x(),
						directionDistortion.x()),
				slightlyChangedVelocityComponent(mainDirection.y(),
						directionDistortion.y()),
				slightlyChangedVelocityComponent(mainDirection.z(),
						directionDistortion.z())).normalize();

		velocity = velocity.mul(this.minSpeed + rand.nextFloat()
				* (maxSpeed - minSpeed));

		return new Vector4(velocity.x(), velocity.y(), velocity.z(), 0);
	}

	/**
	 * returns a value that is slightly randomly changed
	 * 
	 * @param value
	 *            the value that should be slightly changed
	 * @return a new value slightly randomly changed
	 */
	private float slightlyChangedVelocityComponent(float value, float distortion)
	{
		return (value - distortion) + rand.nextFloat() * (2 * distortion);
	}

	@Override
	public SceneNode getNode()
	{
		return this.node;
	}

	@Override
	public void update(double elapsedTime, Game game, boolean inServerMode)
	{
		int toEmit = (int) (this.emitRate * elapsedTime);

		for (int i = 0; i < this.numParticles; i++)
		{
			Particle p = particles[i];
			// do we have not initialized particles?
			if (!p.initialized && toEmit >= 0)
			{
				p.initialized = true;
				positions.set(i, new Vector4(0, 0, 0, 1.0f));
				p.velocity = generateRandomVelocity();
				energy.set(i, startEnergy);
				p.ageSeconds = 0.0;
				toEmit -= 1;
			}

			// let them age
			p.ageSeconds += (elapsedTime * rand.nextFloat());

			// move the particle (but first lets have some gravity fun)
			p.velocity = p.velocity.add(gravity.mul((float) elapsedTime));
			positions.set(i,
					positions.get(i).add(p.velocity.mul((float) elapsedTime)));

			// change energy depending on age
			if (energy.get(i) >= 0.1f)
			{
				energy.set(i, startEnergy
						* (float) Math.pow(energyDamping, p.ageSeconds));
			}
			else
			{
				// reset this particle if the emitter should continously work
				if (this.continouslyEmitting && toEmit >= 0)
				{
					positions.set(i, new Vector4(0, 0, 0, 1.0f));
					p.velocity = generateRandomVelocity();
					energy.set(i, startEnergy);
					p.ageSeconds = 0.0;
					toEmit -= 1;
				}
				else
					energy.set(i, 0f);
			}
		}

		// update the new positions
		attrCloud.setAttribute("jvr_Vertex", new AttributeVector4(
				this.positions));

		// update the new energy values
		attrCloud.setAttribute("particleEnergy", new AttributeFloat(energy));
	}

	/**
	 * @return true if the particle system should be deleted
	 */
	public boolean isParticleSystemTooOld()
	{
		// 0 means no deleting at all
		if (this.timeToDelete == 0)
			return false;

		return (System.currentTimeMillis() >= this.timeToDelete);
	}
}
