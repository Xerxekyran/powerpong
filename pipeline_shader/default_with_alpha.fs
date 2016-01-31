uniform sampler2D jvr_Texture0;
varying vec2 texCoord;

void main (void)
{
	vec4 tex = texture2D(jvr_Texture0, texCoord);
	gl_FragColor = tex;
	
	float alpha = tex.r+tex.g+tex.b;   	
   	if(alpha < 0.001) discard;	
}
