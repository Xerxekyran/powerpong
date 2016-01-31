uniform sampler2D jvr_Texture0;
uniform bool      jvr_UseTexture0;

varying float energy;
varying vec2 texCoord;

void main (void)
{
	vec4 tex = texture2D(jvr_Texture0, texCoord);
	gl_FragColor = tex;
	
	// first look at the color value of the texture to determine the alpha
	float alpha = tex.r+tex.g+tex.b;   	
   	if(alpha * energy < 0.3) discard;
	
	gl_FragColor.a = alpha * energy;
}
