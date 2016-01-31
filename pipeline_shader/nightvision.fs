uniform sampler2D jvr_Texture0;

uniform bool withNightVision;
uniform float elapsedTime;
uniform sampler2D noiseTex;
uniform sampler2D maskTex;
uniform float luminanceThreshold; 
uniform float colorAmplification;

varying vec2 texCoord;


void main (void)
{	
	vec3 color = vec3(1.0, 0.0, 0.0);
  	if (withNightVision)
  	{
    	vec2 uv;
    	uv.x = 0.4*sin(elapsedTime*50.0);
    	uv.y = 0.4*cos(elapsedTime*50.0);
    	float m = texture2D(maskTex, texCoord.st).r;
    	vec3 n  = texture2D(noiseTex, (texCoord.st*3.5) + uv).rgb;
    	vec3 c  = texture2D(jvr_Texture0, texCoord.st + (n.xy*0.005)).rgb;

    	float lum = dot(vec3(0.30, 0.59, 0.11), c);
    	if (lum < luminanceThreshold)
      		c *= colorAmplification; 

    	vec3 visionColor = vec3(0.1, 0.95, 0.2);
    	color.rgb = (c + (n*0.2)) * visionColor * m;
	}
  	else
  	{
    	color = texture2D(jvr_Texture0, texCoord).rgb;
  	}
	gl_FragColor = vec4(color, 1.0);

}
