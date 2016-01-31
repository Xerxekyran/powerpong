uniform sampler2D jvr_Texture0;

uniform bool pixelIt;
uniform float rt_width;
uniform float rt_height;
uniform float pixel_w;
uniform float pixel_h;

varying vec2 texCoord;

void main (void)
{	
	vec3 color = vec3(1.0, 0.0, 0.0);
  	if (pixelIt)
  	{
    	float dx = pixel_w*(1./rt_width);
    	float dy = pixel_h*(1./rt_height);
    	vec2 coord = vec2(dx*floor(texCoord.x/dx), dy*floor(texCoord.y/dy));
    	color = texture2D(jvr_Texture0, coord).rgb;
	}
  	else
  	{
    	color = texture2D(jvr_Texture0, texCoord).rgb;
  	}
	gl_FragColor = vec4(color, 1.0);

}
