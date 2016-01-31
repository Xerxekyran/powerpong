attribute vec4 jvr_Vertex;

uniform mat4 jvr_ProjectionMatrix;
uniform mat4 jvr_ModelViewMatrix;

void main(void)
{
	gl_Position = jvr_ProjectionMatrix * jvr_ModelViewMatrix * jvr_Vertex;
}