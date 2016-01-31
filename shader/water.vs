varying vec4 waterTex0;	//unit 0 = water_reflection
varying vec4 waterTex1;	//unit 1 = water_refraction
varying vec4 waterTex2; //unit 2 = water_normalmap
varying vec4 waterTex3;	//unit 3 = water_dudvmap
varying vec4 waterTex4;	//unit 4 = water_depthmap
uniform vec4 viewpos, lightpos;
uniform float time, time2;

void main(void)
{
	vec4 mpos, temp;
	vec4 tangent = vec4(1.0, 0.0, 0.0, 0.0);
	vec4 norm = vec4(0.0, 1.0, 0.0, 0.0);
	vec4 binormal = vec4(0.0, 0.0, 1.0, 0.0);
	
	mat4 mvp = gl_ModelViewProjectionMatrix;
	mat4 mtx = gl_TextureMatrix[0];
	
	
	temp = viewpos - gl_Vertex;
	waterTex4.x = dot(temp, tangent);
	waterTex4.y = dot(temp, binormal);
	waterTex4.z = dot(temp, norm);
	waterTex4.w = 0.0;
	
	temp = lightpos - gl_Vertex;
	waterTex0.x = dot(temp, tangent);
	waterTex0.y = dot(temp, binormal);
	waterTex0.z = dot(temp, norm);
	waterTex0.w = 0.0;
	
	mpos = mvp * gl_Vertex;
	
	vec4 t1 = vec4(0.0, -time, 0.0,0.0);
	vec4 t2 = vec4(0.0, -time2, 0.0,0.0);
	
	waterTex1 = gl_MultiTexCoord0 + t1;
	waterTex2 = gl_MultiTexCoord0 + t2;
	
	waterTex3 = mpos;
	
	gl_Position = ftransform();
}