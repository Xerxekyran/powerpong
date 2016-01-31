package de.noobisoft.powerpong.util;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2GL3;

import org.apache.log4j.Logger;

import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.TextureCube;
import de.bht.jvr.core.uniforms.UniformBool;
import de.bht.jvr.core.uniforms.UniformColor;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.noobisoft.powerpong.core.Renderer;

/**
 * The MaterialGenerator. Static methods for retrieving several ShadingMaterials
 * 
 * This class is based on the MaterialGenerator by Felix Schulze, Martin Schmidt
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 */
public class MaterialGenerator
{
	static Logger							logger						= Logger.getLogger(MaterialGenerator.class);

	// create the shader program
	
	/** The ambient parallax prog. */
	private static ShaderProgram			extendedPortalAmbientProg	= null;
	
	private static ShaderProgram			extendedPortalLightingProg	= null;
	
	/** The ambient parallax prog. */
	private static ShaderProgram			ambientParallaxProg			= null;
	
	/** The ambient portal prog. */
	private static ShaderProgram			ambientPortalProg			= null;
	
	/** The ambient lighting prog. */
	private static ShaderProgram			lightingPortalProg			= null;

	/** The ambient prog for ghost objects */
	private static ShaderProgram			ghostAmbientProg			= null;

	/** The lighting parallax prog. */
	private static ShaderProgram			lightingParallaxProg		= null;

	/** The ambient prog. */
	private static ShaderProgram			defaultAmbientProg			= null;

	/** The phong prog. */
	private static ShaderProgram			defaultPhongLightingProg	= null;

	/** The environment mapping prog */
	private static ShaderProgram			environmentMappingProg		= null;

	/** The ball shader prog */
	private static ShaderProgram			ballShaderProg				= null;

	/** The lighting bump prog. */
	private static ShaderProgram			lightingBumpProg			= null;

	/** The pda shader prog */
	private static ShaderProgram			energyBarAmbientProg		= null;

	/** The tesselation shader prog */
	private static ShaderProgram			tesselationProg				= null;

	private static TextureCube				environmentCube				= null;

	private static Map<String, Texture2D>	loadedTextures				= new HashMap<String, Texture2D>();

	/**
	 * Sets the environment texture cube. This is needed for environment mapping
	 * effects
	 * 
	 * @param path
	 *            the path (without postfix) to the environment textures
	 */
	public static void setEnvironmentCubeTexture(String path)
	{
		try
		{
			logger.debug("Setting environment cube to: " + path);

			environmentCube = new TextureCube(new File(path + "_rt.jpg"),
					new File(path + "_lf.jpg"), new File(path + "_up.jpg"),
					new File(path + "_dn.jpg"), new File(path + "_bk.jpg"),
					new File(path + "_ft.jpg"));
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
	}

	/**
	 * creates a shadermaterial that uses triangle tesselation (one triangle
	 * transformed into 4)
	 * 
	 * @return the shadermaterial
	 */
	public static ShaderMaterial makeTesselationMaterial()
	{
		ShaderMaterial mat = null;
		try
		{
			if (tesselationProg == null)
			{
				tesselationProg = new ShaderProgram(new File(
						"shader/tesselation.vs"), new File(
						"shader/tesselation.gs"), new File(
						"shader/tesselation.fs"));

				// configure the shader pipeline
				tesselationProg.setParameter(GL2GL3.GL_GEOMETRY_INPUT_TYPE,
						GL2GL3.GL_TRIANGLES);
				tesselationProg.setParameter(GL2GL3.GL_GEOMETRY_OUTPUT_TYPE,
						GL2GL3.GL_TRIANGLE_STRIP);
				tesselationProg.setParameter(GL2GL3.GL_GEOMETRY_VERTICES_OUT,
						12);
			}

			if (defaultPhongLightingProg == null)
				defaultPhongLightingProg = new ShaderProgram(new File(
						"shader/phong_lighting.vs"), new File(
						"shader/phong_lighting.fs"));

			mat = new ShaderMaterial();
			mat.setShaderProgram("AMBIENT", tesselationProg);
			mat.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(
					Color.GRAY));

			mat.setShaderProgram("LIGHTING", defaultPhongLightingProg);
			mat.setUniform("LIGHTING",
					"jvr_Material_Shininess",
					new UniformFloat(10.0f));
			mat.setUniform("LIGHTING",
					"jvr_Material_Specular",
					new UniformColor(new Color(150, 150, 150, 150)));
			mat.setUniform("LIGHTING",
					"jvr_Material_Diffuse",
					new UniformColor(new Color(150, 150, 150, 150)));
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}

		return mat;
	}

	/**
	 * Creates an environment mapping material with extras
	 * 
	 * @return the material for the glue paste
	 */
	public static ShaderMaterial makeGluePasteMaterial()
	{
		ShaderMaterial mat = null;

		try
		{
			if (environmentMappingProg == null)
				environmentMappingProg = new ShaderProgram(new File(
						"shader/gluepaste_envmapping.vs"), new File(
						"shader/gluepaste_envmapping.fs"));

			mat = new ShaderMaterial();

			mat.setShaderProgram("AMBIENT", environmentMappingProg);
			mat.setUniform("AMBIENT", "waveTime", new UniformFloat(0f));
			mat.setUniform("AMBIENT", "waveWidth", new UniformFloat(2.0f));
			mat.setUniform("AMBIENT", "waveHeight", new UniformFloat(0.2f));

			mat.setTexture("AMBIENT", "MyEnvMap", environmentCube);

		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		return mat;
	}

	/**
	 * Make parallax material.
	 * 
	 * @param path
	 *            the path
	 * @param heightScale
	 *            the height scale
	 * @param heightBias
	 *            the height bias
	 * @param shininess
	 *            the shininess
	 * @return the shader material
	 */
	public static ShaderMaterial makeParallaxMaterial(	String path,
														float heightScale,
														float heightBias,
														float shininess)
	{
		ShaderMaterial mat = null;
		try
		{
			if (ambientParallaxProg == null)
				ambientParallaxProg = new ShaderProgram(new File(
						"shader/bumpmapping_ambient.vs"), new File(
						"shader/parallaxmapping_ambient.fs"));

			if (lightingParallaxProg == null)
				lightingParallaxProg = new ShaderProgram(new File(
						"shader/bumpmapping_lighting.vs"), new File(
						"shader/parallaxmapping_lighting.fs"));

			Texture2D colorMap = loadTexture2D(path + "_COLOR.jpg");
			Texture2D heightMap = loadTexture2D(path + "_DISP.jpg");
			Texture2D normalMap = loadTexture2D(path + "_NORMAL.jpg");

			// create the shader material
			mat = new ShaderMaterial();

			mat.setShaderProgram("AMBIENT", ambientParallaxProg);

			// set the ambient shaderprogram
			mat.setTexture("AMBIENT", "jvr_Texture0", colorMap);
			mat.setTexture("AMBIENT", "jvr_HeightMap", heightMap);
			mat.setUniform("AMBIENT", "jvr_HeightScale", new UniformFloat(
					heightScale));
			mat.setUniform("AMBIENT", "jvr_ParallaxBias", new UniformFloat(
					heightBias));
			mat.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(
					new Color(1f, 1f, 1f, 1f)));

			mat.setShaderProgram("LIGHTING", lightingParallaxProg);

			// set the lighting shader program
			mat.setTexture("LIGHTING", "jvr_NormalMap", normalMap);
			mat.setTexture("LIGHTING", "jvr_Texture0", colorMap);
			mat.setTexture("LIGHTING", "jvr_HeightMap", heightMap);
			mat.setUniform("LIGHTING", "jvr_HeightScale", new UniformFloat(
					heightScale));
			mat.setUniform("LIGHTING", "jvr_ParallaxBias", new UniformFloat(
					heightBias));
			mat.setUniform("LIGHTING",
					"jvr_Material_Diffuse",
					new UniformColor(new Color(1.0f, 1.0f, 1.0f, 1.0f)));
			mat.setUniform("LIGHTING",
					"jvr_Material_Specular",
					new UniformColor(new Color(0.6f, 0.6f, 0.6f, 1.0f)));
			mat.setUniform("LIGHTING",
					"jvr_Material_Shininess",
					new UniformFloat(shininess));
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}

		return mat;
	}
	
	/**
	 * Make parallax material.
	 * 
	 * @param path
	 *            the path
	 * @param heightScale
	 *            the height scale
	 * @param heightBias
	 *            the height bias
	 * @param shininess
	 *            the shininess
	 * @return the shader material
	 */
	public static ShaderMaterial makePortalMaterial(	String path,
														float heightScale,
														float heightBias,
														float shininess)
	{
		ShaderMaterial mat = null;
		try
		{
			if (ambientPortalProg == null)
				ambientPortalProg = new ShaderProgram(new File(
						"shader/portal_ambient.vs"), new File(
						"shader/portal_ambient.fs"));

			if (lightingPortalProg == null)
				lightingPortalProg = new ShaderProgram(new File(
						"shader/portal_lighting.vs"), new File(
						"shader/portal_lighting.fs"));

			Texture2D colorMap = loadTexture2D(path + "_COLOR.jpg");
			Texture2D heightMap = loadTexture2D(path + "_DISP.jpg");
			Texture2D normalMap = loadTexture2D(path + "_NORMAL.jpg");

			// create the shader material
			mat = new ShaderMaterial();

			mat.setShaderProgram("AMBIENT", ambientPortalProg);

			// set the ambient shaderprogram
			mat.setTexture("AMBIENT", "jvr_Texture0", colorMap);
			mat.setTexture("AMBIENT", "jvr_HeightMap", heightMap);
			mat.setUniform("AMBIENT", "jvr_HeightScale", new UniformFloat(
					heightScale));
			mat.setUniform("AMBIENT", "jvr_ParallaxBias", new UniformFloat(
					heightBias));
			mat.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(
					new Color(1f, 1f, 1f, 1f)));

			mat.setShaderProgram("LIGHTING", lightingPortalProg);

			// set the lighting shader program
			mat.setTexture("LIGHTING", "jvr_NormalMap", normalMap);
			mat.setTexture("LIGHTING", "jvr_Texture0", colorMap);
			mat.setTexture("LIGHTING", "jvr_HeightMap", heightMap);
			mat.setUniform("LIGHTING", "jvr_HeightScale", new UniformFloat(
					heightScale));
			mat.setUniform("LIGHTING", "jvr_ParallaxBias", new UniformFloat(
					heightBias));
			mat.setUniform("LIGHTING",
					"jvr_Material_Diffuse",
					new UniformColor(new Color(1.0f, 1.0f, 1.0f, 1.0f)));
			mat.setUniform("LIGHTING",
					"jvr_Material_Specular",
					new UniformColor(new Color(0.6f, 0.6f, 0.6f, 1.0f)));
			mat.setUniform("LIGHTING",
					"jvr_Material_Shininess",
					new UniformFloat(shininess));
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}

		return mat;
	}

	/**
	 * loads the given texture
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static Texture2D loadTexture2D(String path) throws Exception
	{
		Texture2D tex = loadedTextures.get(path);
		if (tex == null)
		{
			SplashScreen.setLoadingText(path);
			logger.debug("Loading Texture 2D [" + path + "]");
			tex = new Texture2D(new File(path));
			loadedTextures.put(path, tex);
		}

		return tex;
	}

	/**
	 * creates a material for ghost objects
	 * 
	 * @param c
	 *            the ambient color of the object
	 * @return the shader material
	 */
	public static ShaderMaterial makeGhostobjectMaterial(Color c)
	{
		ShaderMaterial mat = null;
		try
		{
			if (ghostAmbientProg == null)
				ghostAmbientProg = new ShaderProgram(new File(
						"shader/default_ambient.vs"), new File(
						"shader/ghostobject_ambient.fs"));

			mat = new ShaderMaterial();
			mat.setShaderProgram("AMBIENT", ghostAmbientProg);
			mat.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(
					c));

			mat.setUniform("AMBIENT", "isVisible", new UniformBool(Renderer.isNightVision()));

		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}

		return mat;
	}

	/**
	 * Generates a material, that uses phong and colorizes the object with the
	 * given color
	 * 
	 * @param c
	 *            the color for the object
	 * @return the shader material
	 */
	public static ShaderMaterial makeColloredPhongMaterial(Color c)
	{
		ShaderMaterial mat = null;
		try
		{
			if (defaultAmbientProg == null)
				defaultAmbientProg = new ShaderProgram(new File(
						"shader/default_ambient.vs"), new File(
						"shader/default_ambient.fs"));

			if (defaultPhongLightingProg == null)
				defaultPhongLightingProg = new ShaderProgram(new File(
						"shader/phong_lighting.vs"), new File(
						"shader/phong_lighting.fs"));

			mat = new ShaderMaterial();
			mat.setShaderProgram("AMBIENT", defaultAmbientProg);
			mat.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(
					c));

			mat.setShaderProgram("LIGHTING", defaultPhongLightingProg);
			mat.setUniform("LIGHTING",
					"jvr_Material_Shininess",
					new UniformFloat(10.0f));
			mat.setUniform("LIGHTING",
					"jvr_Material_Specular",
					new UniformColor(new Color(255, 255, 255, 255)));
			mat.setUniform("LIGHTING",
					"jvr_Material_Diffuse",
					new UniformColor(new Color(255, 255, 255, 255)));
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}

		return mat;
	}

	/**
	 * creates the material for the pong ball
	 * 
	 * @return the shadermaterial
	 */
	public static ShaderMaterial makeBallMaterial()
	{
		ShaderMaterial mat = null;
		try
		{
			if (defaultAmbientProg == null)
				defaultAmbientProg = new ShaderProgram(new File(
						"shader/default_ambient.vs"), new File(
						"shader/default_ambient.fs"));

			if (ballShaderProg == null)
				ballShaderProg = new ShaderProgram(new File(
						"shader/golfball.vs"), new File("shader/golfball.fs"));

			mat = new ShaderMaterial();
			mat.setShaderProgram("AMBIENT", defaultAmbientProg);
			mat.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(
					new Color(0.3f, 0.3f, 0.3f, 1.0f)));

			mat.setShaderProgram("LIGHTING", ballShaderProg);
			mat.setUniform("LIGHTING",
					"jvr_Material_Shininess",
					new UniformFloat(5f));
			mat.setUniform("LIGHTING",
					"jvr_Material_Specular",
					new UniformColor(new Color(0.7f, 0.7f, 0.7f, 0.7f)));
			mat.setUniform("LIGHTING",
					"jvr_Material_Diffuse",
					new UniformColor(new Color(0.9f, 0.9f, 0.9f, 0.9f)));

			mat.setUniform("LIGHTING", "depth", new UniformFloat(4f));
			mat.setUniform("LIGHTING", "density", new UniformFloat(25.0f));
			mat.setUniform("LIGHTING", "radius", new UniformFloat(0.45f));

		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}

		return mat;
	}

	/**
	 * creates the shadermaterial for the pda of the player
	 * 
	 * @return the shadermaterial
	 */
	public static ShaderMaterial makeEnergyBarMaterial()
	{
		ShaderMaterial mat = null;

		try
		{
			if (energyBarAmbientProg == null)
				energyBarAmbientProg = new ShaderProgram(new File(
						"shader/energybar_ambient.vs"), new File(
						"shader/energybar_ambient.fs"));

			// create the shader material
			mat = new ShaderMaterial();

			// set the surrounding texuture (not the "energy" itself)
			Texture2D tex = loadTexture2D("textures/energyBar.png");
			mat.setTexture("AMBIENT", "energyBarTex", tex);

			// set the ambient shader program
			mat.setShaderProgram("AMBIENT", energyBarAmbientProg);
			mat.setUniform("AMBIENT", "energyLevel", new UniformFloat(1.0f));

		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}

		return mat;
	}

	/**
	 * Make normal material.
	 * 
	 * @param path
	 *            the path to the texture files
	 * @param shininess
	 *            the shininess
	 * @return the shader material
	 */
	public static ShaderMaterial makeNormalMaterial(String path, float shininess)
	{
		ShaderMaterial mat = null;
		try
		{
			if (defaultAmbientProg == null)
				defaultAmbientProg = new ShaderProgram(new File(
						"shader/default_ambient.vs"), new File(
						"shader/default_ambient.fs"));

			if (lightingBumpProg == null)
				lightingBumpProg = new ShaderProgram(new File(
						"shader/bumpmapping_lighting.vs"), new File(
						"shader/bumpmapping_lighting.fs"));

			Texture2D colorMap = loadTexture2D(path + "_color.jpg");
			Texture2D normalMap = loadTexture2D(path + "_NORMAL.jpg");

			// create the shader material
			mat = new ShaderMaterial();

			// set the ambient shader program
			mat.setShaderProgram("AMBIENT", defaultAmbientProg);

			mat.setTexture("AMBIENT", "jvr_Texture0", colorMap);
			mat.setUniform("AMBIENT", "jvr_UseTexture0", new UniformBool(true));
			mat.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(
					new Color(0.1f, 0.1f, 0.1f, 1f)));

			mat.setShaderProgram("LIGHTING", lightingBumpProg); // set the
			// lighting
			// shader
			// program
			mat.setTexture("LIGHTING", "jvr_NormalMap", normalMap);
			mat.setTexture("LIGHTING", "jvr_Texture0", colorMap);
			mat.setUniform("LIGHTING",
					"jvr_Material_Diffuse",
					new UniformColor(new Color(1.0f, 1.0f, 1.0f, 1.0f)));
			mat.setUniform("LIGHTING",
					"jvr_Material_Specular",
					new UniformColor(new Color(0.6f, 0.60f, 0.6f, 1.0f)));
			mat.setUniform("LIGHTING",
					"jvr_Material_Shininess",
					new UniformFloat(shininess));

		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}

		return mat;
	}
	
	/**
	 * Make normal material.
	 * 
	 * @param path
	 *            the path to the texture files
	 * @param shininess
	 *            the shininess
	 * @return the shader material
	 */
	public static ShaderMaterial makeViewablePortalMaterial(	String path,
	    														float heightScale,
	    														float heightBias,
	    														float shininess)
	{
		ShaderMaterial mat = null;
		try
		{
			if (extendedPortalAmbientProg == null)
				extendedPortalAmbientProg = new ShaderProgram(new File(
				"shader/simple_portal_ambient.vs"), new File(
				"shader/simple_portal_ambient.fs"));
			
			mat = new ShaderMaterial("AMBIENT", extendedPortalAmbientProg);
			
			Texture2D colorMap = loadTexture2D(path + "_COLOR.jpg");
			Texture2D heightMap = loadTexture2D(path + "_DISP.jpg");
			Texture2D normalMap = loadTexture2D(path + "_NORMAL.jpg");

			// set the ambient shaderprogram
			mat.setTexture("AMBIENT", "jvr_Texture0", colorMap);
			mat.setTexture("AMBIENT", "jvr_HeightMap", heightMap);
			mat.setUniform("AMBIENT", "jvr_HeightScale", new UniformFloat(
					heightScale));
			mat.setUniform("AMBIENT", "jvr_ParallaxBias", new UniformFloat(
					heightBias));
			mat.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(
					new Color(1f, 1f, 1f, 1f)));
			
			
			if (extendedPortalLightingProg == null)
				extendedPortalLightingProg = new ShaderProgram(new File(
						"shader/simple_portal_lighting.vs"), new File(
						"shader/simple_portal_lighting.fs"));
			
			mat.setShaderProgram("LIGHTING", extendedPortalLightingProg);

			// set the lighting shader program
			mat.setTexture("LIGHTING", "jvr_NormalMap", normalMap);
			mat.setTexture("LIGHTING", "jvr_Texture0", colorMap);
			mat.setTexture("LIGHTING", "jvr_HeightMap", heightMap);
			mat.setUniform("LIGHTING", "jvr_HeightScale", new UniformFloat(
					heightScale));
			mat.setUniform("LIGHTING", "jvr_ParallaxBias", new UniformFloat(
					heightBias));
			mat.setUniform("LIGHTING",
					"jvr_Material_Diffuse",
					new UniformColor(new Color(1.0f, 1.0f, 1.0f, 0.1f)));
			mat.setUniform("LIGHTING",
					"jvr_Material_Specular",
					new UniformColor(new Color(0.6f, 0.6f, 0.6f, 0.1f)));
			mat.setUniform("LIGHTING",
					"jvr_Material_Shininess",
					new UniformFloat(shininess));
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}

		return mat;
	}
}
