uniform float jvr_particleSize;
uniform mat4 jvr_ModelViewProjectionMatrix;

attribute vec4 jvr_Vertex;
attribute vec3 jvr_Color;
attribute float particleEnergy;

varying float particleEnergyV;
varying float particleSize;



void main(void)
{
	particleSize = jvr_particleSize;
	particleEnergyV  = particleEnergy;
	gl_Position = jvr_ModelViewProjectionMatrix * jvr_Vertex;
}