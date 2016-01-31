attribute vec3 partPosition;
attribute vec3 partVelocity;

attribute float partEnergy;
varying float partEnergyV;


uniform mat4 jvr_ModelViewMatrix;


void main(void)
{
	partEnergyV = partEnergy;
	
	// transform particle position into viewer space
	gl_Position = jvr_ModelViewMatrix * vec4(partPosition, 1.0);
}