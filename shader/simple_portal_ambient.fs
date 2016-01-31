uniform sampler2D jvr_Texture0;
uniform sampler2D jvr_HeightMap;
uniform float jvr_HeightScale;
uniform vec4 jvr_Material_Ambient;
uniform float jvr_ParallaxBias;

uniform bool  jvr_UseClipPlane0;
varying float clipDist0;

varying vec2 texCoord;
varying vec3 eyeVecTSpace;

uniform mat4 jvr_ProjectionMatrix;
uniform sampler2D jvr_MirrorTexture;
uniform float waveTime;
uniform float waveScale;

uniform float yflow;
varying vec3 eyeDir;
varying vec4 vertex;

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
	
	vec3 ETSpace = normalize(eyeVecTSpace);
	vec2 texCoord3 = texCoord;
	texCoord3.y = texCoord.y - yflow;
    float height = texture2D(jvr_HeightMap, texCoord3).r;
    
    height = height * jvr_HeightScale + jvr_ParallaxBias;
    
    vec2 texCoord2 = texCoord + (height * ETSpace.xy); // calculate new texture coordinates
    
    
    gl_FragColor = jvr_Material_Ambient * texture2D(jvr_MirrorTexture, texCoord2);
}
