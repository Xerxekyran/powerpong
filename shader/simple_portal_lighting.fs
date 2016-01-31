uniform vec4  jvr_LightSource_Diffuse;
uniform vec4  jvr_LightSource_Specular;
uniform float jvr_LightSource_Intensity;
uniform vec4  jvr_LightSource_Position;
uniform vec3  jvr_LightSource_SpotDirection;
uniform float jvr_LightSource_SpotExponent;
uniform float jvr_LightSource_SpotCutOff;
uniform float jvr_LightSource_SpotCosCutOff;
uniform bool  jvr_LightSource_CastShadow;

uniform sampler2D jvr_NormalMap;
uniform sampler2D jvr_Texture0;
uniform sampler2D jvr_HeightMap;
uniform float jvr_HeightScale;
uniform vec4  jvr_Material_Specular;
uniform vec4  jvr_Material_Diffuse;
uniform float jvr_Material_Shininess;
uniform float jvr_ParallaxBias;

varying vec3  normal;
varying vec3  eyeVec;
varying vec3  lightDir;
varying vec3  eyeVecTSpace;
varying vec3  lightDirTSpace;
varying vec2  texCoord;
varying float attenuation;

uniform sampler2D jvr_ShadowMap;
varying vec4 shadowCoord;

uniform bool  jvr_UseClipPlane0;
varying float clipDist0;

uniform mat4 jvr_ProjectionMatrix;
uniform sampler2D jvr_MirrorTexture;
uniform float waveTime;
uniform float waveScale;

uniform float yflow;
varying vec3 eyeDir;
varying vec4 vertex;

vec4 phong(vec3 N, vec3 L, vec3 E, vec4 diffuseColor, vec4 specularColor, float shininess, float attenuation)
{
	vec4 final_color = vec4(0,0,0,0);
	
	float lambertTerm = dot(N,L);
	if(lambertTerm > 0.0) 
	{
		final_color += attenuation * jvr_LightSource_Intensity * jvr_LightSource_Diffuse * diffuseColor * lambertTerm;		
		vec3 R = reflect(-L, N);
		final_color += attenuation * jvr_LightSource_Intensity * specularColor * jvr_Material_Specular * pow( max(dot(R, E), 0.0), shininess);
	}
	
	return final_color;	
}

float lookup(float x, float y)
{
	vec4 sc = shadowCoord / shadowCoord.w;
	sc.x+=x;
	sc.y+=y;
	float distanceFromLight = texture2D(jvr_ShadowMap, sc.xy).z;
	
	return distanceFromLight < sc.z ? 0.0: 1.0; 	
}

void main (void)
{
///////////////////////////////////////
	// Moving from [-1,1] to [0,1]
	mat4 DCtoTC = mat4(	0.5, 0.0, 0.0, 0.0,
						0.0, 0.5, 0.0, 0.0,
						0.0, 0.0, 0.5, 0.0,
						0.5, 0.5, 0.5, 1.0);

  
    // Project an homogenize vertex to yield texture coordinates.
	vec4 vertexNDC = DCtoTC * jvr_ProjectionMatrix * vertex;	
	vec2 texCoord = vertexNDC.xy / vertexNDC.w;

	texCoord.x = 1.0-texCoord.x;
	
	// Make a cyanish tint.
	//gl_FragColor = texture2D(jvr_MirrorTexture, texCoord);

///////////////////////////////////////

    if(jvr_UseClipPlane0)
	{
		if(clipDist0<0.0) discard;
	}
	
	vec3 E = normalize(eyeVec);
	vec3 ETSpace = normalize(eyeVecTSpace);
	
	texCoord.y = texCoord.y - yflow;
    float height = texture2D(jvr_HeightMap, texCoord).r;
    texCoord.y = texCoord.y + yflow;
    
    height = height * jvr_HeightScale + jvr_ParallaxBias;
    vec2 texCoord2 = texCoord + (height * ETSpace.xy); // calculate new texture coordinates
    
    vec4 color = texture2D(jvr_MirrorTexture, texCoord2);

	vec3 N = normalize(texture2D(jvr_NormalMap, texCoord).xyz * 2.0 - 1.0);
	//N.y = -N.y; // flip y (depends on your normal map)
	
	vec3 L = normalize(lightDir);
	vec3 LTSpace = normalize(lightDirTSpace);	
	
	if (jvr_LightSource_SpotCutOff == 0.0)
	{	
		// point light or directional light
		gl_FragColor = phong(N, LTSpace, ETSpace, jvr_Material_Diffuse * color, jvr_Material_Specular * color, jvr_Material_Shininess, attenuation);
	}
	else
	{
		vec3 D = normalize(jvr_LightSource_SpotDirection);
		float LdotD = dot(-L, D);
		if(LdotD > jvr_LightSource_SpotCosCutOff)
		{
			// spot light			
			if(jvr_LightSource_CastShadow)
			{
				// with shadow mapping
				int samples = 2;
				float sum = 0.0;
				int x;
				int y;
				for(x = -samples; x<samples; x++)
				{
					for(y = -samples; y<samples; y++)
					{
						sum += lookup(float(x)*0.0002, float(y)*0.0002);
					}
				}
				
				sum /= float((2*samples)*(2*samples));
				
				if(sum > 0.0)
				{
					gl_FragColor = phong(N, LTSpace, ETSpace, jvr_Material_Diffuse * color, jvr_Material_Specular * color, jvr_Material_Shininess, attenuation * pow(LdotD, jvr_LightSource_SpotExponent));
					gl_FragColor.w = sum;
				}
				else
				{
					discard;
				}
			}
			else
			{
				// without shadow mapping
				gl_FragColor = phong(N, LTSpace, ETSpace, jvr_Material_Diffuse * color, jvr_Material_Specular * color, jvr_Material_Shininess, attenuation * pow(LdotD, jvr_LightSource_SpotExponent));	
			}
		}
		else
		{
			discard;
		}
	}
}