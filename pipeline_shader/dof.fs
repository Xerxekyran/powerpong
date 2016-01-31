uniform sampler2D jvr_Texture0;
uniform sampler2D jvr_Texture1;
uniform float intensity;
varying vec2 texCoord;

float linearizeDepth()
{
  float n = 0.1;
  float f = 100.0;
  float z = texture2D(jvr_Texture0, texCoord).x;
  return (2.0 * n) / (f + n - z * (f - n));
}

vec2 getTexC(int x, int y, float z)
{
	vec2 offset = intensity * z * vec2(float(x)/1024.0, float(y)/1024.0);
	vec2 texC = texCoord+offset;
	if(texC.x>1.0 || texC.x<0.0 || texC.y>1.0 || texC.y<0.0)
	{
		texC = texCoord;
	}
	
	return texC;
}

vec4 blur2(float z)
{   	   	
   	vec4 final_color = vec4(0,0,0,1);
   	 	
   	//final_color += texture2D(jvr_Texture1, getTexC(-2, -2, z));
	//final_color += texture2D(jvr_Texture1, getTexC(-2, -1, z));
   	//final_color += texture2D(jvr_Texture1, getTexC(-2,  0, z));
   	//final_color += texture2D(jvr_Texture1, getTexC(-2,  1, z));
   	//final_color += texture2D(jvr_Texture1, getTexC(-2,  2, z));
   	//final_color += texture2D(jvr_Texture1, getTexC(-1, -2, z));
   	final_color += texture2D(jvr_Texture1, getTexC(-1, -1, z));
   	final_color += texture2D(jvr_Texture1, getTexC(-1,  0, z));
   	final_color += texture2D(jvr_Texture1, getTexC(-1,  1, z));
   	//final_color += texture2D(jvr_Texture1, getTexC(-1,  2, z));
   	//final_color += texture2D(jvr_Texture1, getTexC( 0, -2, z));
   	final_color += texture2D(jvr_Texture1, getTexC( 0, -1, z));
   	final_color += texture2D(jvr_Texture1, getTexC( 0,  0, z));
   	final_color += texture2D(jvr_Texture1, getTexC( 0,  1, z));
   	//final_color += texture2D(jvr_Texture1, getTexC( 0,  2, z));
   	//final_color += texture2D(jvr_Texture1, getTexC( 1, -2, z));
   	final_color += texture2D(jvr_Texture1, getTexC( 1, -1, z));
   	final_color += texture2D(jvr_Texture1, getTexC( 1,  0, z));
   	final_color += texture2D(jvr_Texture1, getTexC( 1,  1, z));
   	//final_color += texture2D(jvr_Texture1, getTexC( 1,  2, z));
   	//final_color += texture2D(jvr_Texture1, getTexC( 2, -2, z));
   	//final_color += texture2D(jvr_Texture1, getTexC( 2, -1, z));
   	//final_color += texture2D(jvr_Texture1, getTexC( 2,  0, z));
   	//final_color += texture2D(jvr_Texture1, getTexC( 2,  1, z));
   	//final_color += texture2D(jvr_Texture1, getTexC( 2,  2, z));	

	final_color /= float(9); 
   	
   	final_color.w = 1.0;
	return final_color;
}

void main (void)
{
	float z = linearizeDepth();
	
	vec4 final_color = blur2(z); // without loops
	
	gl_FragColor = final_color;
}
