uniform sampler2D jvr_Texture0;
uniform sampler2D jvr_Texture1;
uniform sampler2D smokeDepthTexture;
uniform sampler2D smokeColorTexture;
varying vec2 texCoord;

const float EULER = 2.718281828459;

float linearizeDepth(sampler2D tex)
{
  float nearClip = 0.1;
  float farClip = 80.0;
  float z = texture2D(tex, texCoord).x;
  return (2.0 * nearClip) / (farClip + nearClip - z * (farClip - nearClip));
}

void main (void)
{
	vec4 base_color = texture2D(jvr_Texture1, texCoord);
	float zFog = 0.;
	float zSmoke = 0;
	
	zFog = linearizeDepth(jvr_Texture0);
	
	vec4 final_color = vec4(zFog,zFog,zFog,1);
	vec4 fog_color = vec4(0.4,0.4,0.4,1.0);	

	gl_FragColor = mix(fog_color, base_color , pow(EULER,-1.0*pow(zFog*1.5,2.0)));
		
	if (texture2D(jvr_Texture0,texCoord).x >= texture2D(smokeDepthTexture,texCoord).x)
	{ 
		gl_FragColor += texture2D(smokeColorTexture, texCoord);
	}		
	
	if (zFog > 0.99)
		gl_FragColor = (gl_FragColor + base_color)/2.0;
}
