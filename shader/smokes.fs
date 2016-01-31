uniform sampler2D jvr_Texture0;

varying float partEnergyG;
varying vec2 texCoord;

void main (void)
{
	vec4 tex = texture2D(jvr_Texture0, texCoord);   	
   	
   	float alpha = (tex.r+tex.g+tex.b)/3.;   	
   	if(alpha<0.01) discard;   	
   	
   	vec3 additionalColor;
   	
   	if(partEnergyG < 1.0)
   		additionalColor  = vec3(0.7,0.7,0.7);
   	else if(partEnergyG < 0.6)
   		additionalColor  = vec3(0.5,0.5,0.5);	
   	else if(partEnergyG < 0.4)
   		additionalColor  = vec3(0.3,0.3,0.3);		
   		
   	gl_FragColor.rgb = tex.rgb/2 + additionalColor;
   	gl_FragColor.a = alpha * 0.7 * partEnergyG;
}