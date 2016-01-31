attribute vec4 jvr_Vertex;
attribute vec3 jvr_Normal;

uniform mat4 jvr_ModelViewMatrix;
uniform mat4 jvr_ModelViewProjectionMatrix;
uniform mat3 jvr_NormalMatrix;

uniform float waveTime;
uniform float waveWidth;
uniform float waveHeight;

varying vec3 ReflectDir;

void main(void)
{
	vec4 v = vec4(jvr_Vertex);
	v.y = v.y +	sin(waveWidth * v.x + waveTime) * sin(waveWidth * v.y + waveTime) * cos(waveWidth * v.z + waveTime) * waveHeight;
	
	gl_Position = jvr_ModelViewProjectionMatrix * v;
	
	vec3 eyeVec = (jvr_ModelViewMatrix * jvr_Vertex).xyz;
	vec3 normal = normalize(jvr_NormalMatrix * jvr_Normal);
	ReflectDir  = reflect(eyeVec, normal);	
}
