uniform sampler2D MyNormalMap;
uniform sampler2D MyTextureMap;
uniform vec4  jvr_LightSource_Diffuse;
uniform vec4  jvr_LightSource_Specular;
uniform float jvr_LightSource_Intensity;
uniform vec4      jvr_Material_Specular;
uniform vec4      jvr_Material_Diffuse;
uniform float     jvr_Material_Shininess;

varying vec3  eyeVec;
varying vec3  lightDir;
varying vec2  texCoord;

vec4 phong(vec3 N, vec3 L, vec3 E, vec4 diffuseColor, vec4 specularColor, float shininess)
{
	vec4 final_color = vec4(0,0,0,0);
	
	float lambertTerm = dot(N,L);
	if(lambertTerm > 0.0) 
	{
		final_color += jvr_LightSource_Intensity * jvr_LightSource_Diffuse * diffuseColor * lambertTerm;		
		vec3 R = reflect(-L, N);
		final_color += jvr_LightSource_Intensity * jvr_LightSource_Specular * specularColor * pow( max(dot(R, E), 0.0), shininess);
	}
	
	return final_color;	
}

void main (void)
{
	vec4 color = texture2D(MyTextureMap, texCoord);

	vec3 N = normalize(texture2D(MyNormalMap, texCoord).xyz * 2.0 - 1.0);
	N.y = -N.y; // flip y (depends on your normal map)
	
	vec3 L = normalize(lightDir);	
	vec3 E = normalize(eyeVec);
	gl_FragColor = phong(N, L, E, jvr_Material_Diffuse * color, jvr_Material_Specular * color, jvr_Material_Shininess);
}
