attribute vec4 jvr_Vertex;
uniform mat4 jvr_ProjectionMatrix;
uniform mat4 jvr_ModelViewMatrix;
uniform vec4 jvr_ClipPlane0;
varying vec4 color;
varying float clipDist;

float calcClipDist(vec4 point, vec4 plane)
{
	vec3 p = point.xyz / point.w;
	float len = length(plane.xyz);
	return (plane.x * p.x + plane.y * p.y + plane.z * p.z - plane.w) / len;
}

void main(void)
{
	gl_Position = jvr_ProjectionMatrix * jvr_ModelViewMatrix * jvr_Vertex;
	
	color = normalize(jvr_Vertex);
	color.w = 1.0;
	
	clipDist = calcClipDist(jvr_ModelViewMatrix * jvr_Vertex, jvr_ClipPlane0);
}