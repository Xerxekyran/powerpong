uniform samplerCube EnvMap;

varying vec3 ReflectDir;

void main(void)
{
	vec4 color = textureCube(EnvMap, ReflectDir);
	color.r = color.r / 2;
	color.b = color.b / 2; 
	
	gl_FragColor = color; 
}