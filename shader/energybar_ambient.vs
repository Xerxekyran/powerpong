attribute vec4 jvr_Vertex;
attribute vec2 jvr_TexCoord;

uniform mat4 jvr_ModelViewProjectionMatrix;
uniform mat4 jvr_ProjectionMatrix;
uniform mat4 jvr_ModelViewMatrix;
uniform float jvr_PolygonOffset;

uniform bool jvr_UseClipPlane0;
uniform vec4 jvr_ClipPlane0;

uniform float energyLevel;

varying float energy;
varying vec2 texCoord;


void main(void)
{
	energy = energyLevel;
	texCoord = jvr_TexCoord;
	
	if(jvr_PolygonOffset == 0.0)
	{
		gl_Position = jvr_ModelViewProjectionMatrix * jvr_Vertex;
	}
	else
	{
		gl_Position = jvr_ModelViewMatrix * jvr_Vertex;
		vec3 eyeVec = normalize(gl_Position.xyz);
		gl_Position.xyz += eyeVec*jvr_PolygonOffset;// the shadow map bias
		gl_Position = jvr_ProjectionMatrix * gl_Position;
	}
}