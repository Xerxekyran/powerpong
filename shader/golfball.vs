/*
attribute vec4 jvr_Vertex;
attribute vec3 jvr_Normal;
attribute vec3 jvr_Tangent;
attribute vec3 jvr_Binormal;
attribute vec2 jvr_TexCoord;

uniform vec3 lightPosV;
uniform mat3 jvr_NormalMatrix;
uniform mat4 jvr_ModelViewMatrix;
uniform mat4 jvr_ModelViewProjectionMatrix;

varying vec3 normalV;
varying vec3 lightDirV;
varying vec3 eyeDirV;

varying vec3 lightDirT;
varying vec3 eyeDirT;
varying vec2 texCoord;
*/



attribute vec4 jvr_Vertex;
attribute vec3 jvr_Normal;
attribute vec3 jvr_Tangent;
attribute vec3 jvr_Binormal;
attribute vec2 jvr_TexCoord;

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
varying vec2  texCoord;
varying float attenuation;
varying vec4  shadowCoord;
varying vec3 lightDirT;
varying vec3 eyeDirT;


vec3 xformTo(vec3 b0,vec3 b1,vec3 b2,vec3 v);
void transformIntoEyeCoordinates();

vec3 xformTo(vec3 b0,vec3 b1,vec3 b2,vec3 v)
{
	return vec3(dot(v, b0), dot(v, b1),dot(v, b2));
}

void main(void)
{
	transformIntoEyeCoordinates();
	
	gl_Position = jvr_ModelViewProjectionMatrix * jvr_Vertex;
	texCoord = jvr_TexCoord;	
}

void transformIntoEyeCoordinates()
{
	vec3 vertexV = (jvr_ModelViewMatrix * jvr_Vertex).xyz;	
	
	vec3 normalV = normalize(jvr_NormalMatrix * jvr_Normal);
	vec3 tangentV = normalize(jvr_NormalMatrix * jvr_Tangent);
	vec3 binormalV = normalize(jvr_NormalMatrix * jvr_Binormal);
	
	vec3 eyeDirV = -vertexV;
	vec3 lightDirV = jvr_LightSource_Position.xyz - vertexV;
	
	lightDirT = xformTo(tangentV, binormalV, normalV, lightDirV);
	
	eyeDirT = xformTo(tangentV, binormalV, normalV, eyeDirV);
}