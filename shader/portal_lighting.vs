attribute vec4 jvr_Vertex;
attribute vec2 jvr_TexCoord;
attribute vec3 jvr_Normal;
attribute vec3 jvr_Binormal;
attribute vec3 jvr_Tangent;

uniform mat4  jvr_ModelViewMatrix;
uniform mat4  jvr_ModelViewProjectionMatrix;
uniform mat3  jvr_NormalMatrix;

uniform vec4  jvr_LightSource_Position;
uniform float jvr_LightSource_ConstantAttenuation;
uniform float jvr_LightSource_LinearAttenuation;
uniform float jvr_LightSource_QuadraticAttenuation;
uniform mat4  jvr_LightSource_ModelViewProjectionMatrix;
uniform bool  jvr_LightSource_CastShadow;

uniform bool  jvr_UseClipPlane0;
uniform vec4  jvr_ClipPlane0;
varying float clipDist0;

varying vec3  normal;
varying vec3  eyeVec;
varying vec3  lightDir;
varying vec3  eyeVecTSpace;
varying vec3  lightDirTSpace;
varying vec2  texCoord;
varying float attenuation;
varying vec4  shadowCoord;

uniform float yflow;

float calcClipDist(vec4 point, vec4 plane)
{
	vec3 p = point.xyz / point.w;
	float len = length(plane.xyz);
	return (plane.x * p.x + plane.y * p.y + plane.z * p.z - plane.w) / len;
}

void main(void)
{
	normal = jvr_NormalMatrix * jvr_Normal;

	vec4 vVertex = jvr_ModelViewMatrix * jvr_Vertex;
	eyeVec = -vVertex.xyz;
	
    if(jvr_LightSource_Position.w > 0.0)
    {
    	// point light or spot light

    	lightDir = vec3( jvr_LightSource_Position.xyz - vVertex.xyz );
    	
    	float distance = length(lightDir);
    	attenuation = 1.0 / (jvr_LightSource_QuadraticAttenuation * distance * distance
                           + jvr_LightSource_LinearAttenuation * distance
                           + jvr_LightSource_ConstantAttenuation);
    }
    else
    {
    	// directional light

    	lightDir = -vec3( normalize( jvr_LightSource_Position.xyz ) );
    	attenuation = 1.0;
    }

	texCoord = jvr_TexCoord;
	gl_Position = jvr_ModelViewProjectionMatrix * jvr_Vertex;
	
	if(jvr_LightSource_CastShadow)
	{
		// Moving from [-1,1] to [0,1]
		mat4 DCtoTC = mat4(	0.5, 0.0, 0.0, 0.0,
							0.0, 0.5, 0.0, 0.0,
							0.0, 0.0, 0.5, 0.0,
							0.5, 0.5, 0.5, 1.0);
		shadowCoord = DCtoTC * jvr_LightSource_ModelViewProjectionMatrix * jvr_Vertex;
	}
	
	// user defined clipping
	if(jvr_UseClipPlane0)
	{
		clipDist0 = calcClipDist(jvr_ModelViewMatrix * jvr_Vertex, jvr_ClipPlane0);
	}
	
	// transform to tangent space ////////////////////////////////////////////////////////	
	vec3 n = normalize(jvr_NormalMatrix * jvr_Normal);
	vec3 t = normalize(jvr_NormalMatrix * jvr_Tangent);
	vec3 b = normalize(jvr_NormalMatrix * jvr_Binormal);
	
	mat3 tbn = mat3(t.x, b.x, n.x,
					t.y, b.y, n.y,
					t.z, b.z, n.z);
	
	lightDirTSpace =  tbn * lightDir;
	eyeVecTSpace = tbn * eyeVec;
}