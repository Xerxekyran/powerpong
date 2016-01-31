/*
uniform vec3 toonColor;
uniform vec3 lightColor ;
uniform vec3 lightIntensity;
uniform vec3 circleColor;
uniform float depth;
uniform float density;
uniform float radius;

uniform vec3 ka, kd, ks;
uniform float ke;

varying vec3 lightDirT;
varying vec3 eyeDirT;
varying vec2 texCoord;
*/

uniform vec4  jvr_LightSource_Diffuse;
uniform vec4  jvr_LightSource_Specular;
uniform float jvr_LightSource_Intensity;
uniform vec4  jvr_LightSource_Position;
uniform vec3  jvr_LightSource_SpotDirection;
uniform float jvr_LightSource_SpotExponent;
uniform float jvr_LightSource_SpotCutOff;
uniform float jvr_LightSource_SpotCosCutOff;
uniform bool  jvr_LightSource_CastShadow;

uniform float depth;
uniform float density;
uniform float radius;

uniform sampler2D jvr_Texture0;
uniform bool      jvr_UseTexture0;
uniform vec4      jvr_Material_Specular;
uniform vec4      jvr_Material_Diffuse;
uniform vec4      jvr_Material_Ambient;
uniform float     jvr_Material_Shininess;


varying vec3  normal;
varying vec3  eyeVec;
varying vec3  lightDir;
varying vec2  texCoord;
varying float attenuation;
varying vec3 lightDirT;
varying vec3 eyeDirT;

uniform sampler2D jvr_ShadowMap;
varying vec4 shadowCoord;

uniform bool  jvr_UseClipPlane0;
varying float clipDist0;

float pi = 2.0 * asin(1.0);

vec4 phong2(vec3 L, vec4 kst, vec4 kdt, float attenuation)
{
	vec4 final_color = vec4(0,0,0,0);
	
	vec3 N = normalize(normal);	
	float lambertTerm = dot(N,L);
	if(lambertTerm > 0.0) 
	{
		final_color += attenuation * jvr_LightSource_Intensity * jvr_LightSource_Diffuse * kdt * lambertTerm;
		vec3 E = normalize(eyeVec);
		vec3 R = reflect(-L, N);
		final_color += attenuation * jvr_LightSource_Intensity * jvr_LightSource_Specular * kst * pow( max(dot(R, E), 0.0), jvr_Material_Shininess);
	}
	
	return final_color;	
}

vec3 phong(vec3 N,vec3 L,vec3 E,vec3 color)
{
	//phong
	// diffuse intensity 
	float intensity = dot(N, L);
	
	vec3 textureColor = color;//vec3(texture2D(image, texCoord / textureScale));

    vec3 currentColor = jvr_Material_Ambient.xyz * jvr_LightSource_Intensity;	
	
	if (intensity > 0.0) {
		// diffuse reflectance
		currentColor += jvr_LightSource_Diffuse.xyz * jvr_LightSource_Intensity * intensity ;		
	}

	currentColor = currentColor * textureColor;

	if (intensity > 0.0) {	
		// specular highlight 	
		vec3 R = reflect(-L, N);
		currentColor += jvr_LightSource_Specular.xyz * jvr_LightSource_Intensity * pow(max(dot(R, E), 0.0), jvr_Material_Shininess );
	}
	
	return currentColor;	
}

/*
vec3 getBumpedNormal(vec3 color)
{
	vec2 texDens = (texCoord * density); 

	texDens.x = mod(texDens.x, 1.0);
	texDens.y = mod(texDens.y, 1.0);
	texDens = texDens - vec2(0.5, 0.5);

	float len = 1.0;

    if ( length(texDens) < radius) 
	{
		color = circleColor;

		len =1.0/ sqrt(texDens.x * texDens.x + texDens.y * texDens.y);
	}
	else
	{
		texDens = vec2(0.0);
	}
	
	return vec3(-texDens.x, -texDens.y, 1.0/depth) * len;
}
*/

vec3 getBumpedNormalByChris(vec3 color)
{
	vec2 texDens = (texCoord * density); 
	vec3 normalM = vec3(0.0, 0.0, 1.0);
	
	texDens.x = mod(texDens.x, 1.0);
	texDens.y = mod(texDens.y, 1.0);
	texDens = texDens - vec2(0.5, 0.5);
	
	//Eigener Ansatz druch Vector rotation

    if ( length(texDens) < radius) 
	{
		color = jvr_Material_Diffuse.xyz;
				
		//Eigener Ansatz druch Vector rotation
		//vector from textcoord to middlepoint of the circle
		vec2 pointToMid = texDens ;
		vec3 pointToMidV3 = vec3(pointToMid.xy,0.0);
		
		//is the point in the middle value=0 is it at the circle line value=1
		float nearToMiddle = length(pointToMid)/radius;
		
		//angle between this vector an the final normal
		//float angle = cos(nearToMiddle*(pi/2));//cos(nearToMiddle);
		float angle = nearToMiddle*(pi/2);
		
		vec3 rotationvector = cross(normalM, pointToMidV3);
		
		//float satangle = angle * 6.282;
		float satangle = angle;
		float csat = cos(satangle);
		float ssat = sin(satangle);
		float usat = 1.0 - csat;

		mat3 rotmat;
		
		rotmat[0][0] = rotationvector.x*rotationvector.x*usat + csat; 
		// Mat[0] = (x * x *u) + c;		
		rotmat[1][0] = rotationvector.y*rotationvector.x*usat - (rotationvector.z*ssat);
		// Mat[4] = (y* x * u) - (z * s);
		rotmat[2][0] = rotationvector.z*rotationvector.x*usat + (rotationvector.y*ssat);
		// Mat[8] = (z* x * u) + (y * s);
		rotmat[0][1] = rotationvector.x*rotationvector.y*usat + (rotationvector.z*ssat);
		// Mat[1] = (x* y * u) + (z * s);	
		rotmat[1][1] = rotationvector.y*rotationvector.y*usat + csat; 
		// Mat[5] = (y * y *u) + c;	
		rotmat[2][1] = rotationvector.z*rotationvector.y*usat - (rotationvector.x*ssat);
		// Mat[9] = (z* y * u) - (x * s);
		rotmat[0][2] = rotationvector.x*rotationvector.z*usat - (rotationvector.y*ssat);
		// Mat[2] = (x* z * u) - (y * s);
		rotmat[1][2] = rotationvector.x*rotationvector.z*usat - (rotationvector.y*ssat);
		// Mat[6] = (y* z * u) + (x * s);
		rotmat[2][2] = rotationvector.z*rotationvector.z*usat + csat; 
		// Mat[10] = (z * z *u) + c;
	
		pointToMidV3.xyz *= rotmat;				
				
		normalM = pointToMidV3.xyz;
		normalM.x *= -1;
		normalM.y *= -1;
		normalM.z = 1.0/depth;

		
	}
	
	return normalM;
}

void main (void)
{
	vec3 color = jvr_Material_Ambient.xyz;		   
	vec3 normalT = vec3(0.0, 0.0, 1.0);
	
	//normalT = getBumpedNormal(circleColor);
	normalT = getBumpedNormalByChris(color);
	
	vec3 N = normalize(normalT);
	vec3 L = normalize(lightDirT);
	vec3 E = normalize(eyeDirT);			

	vec3 currentColor = phong(N,L,E,color);
	gl_FragColor = vec4 (currentColor , 1);
	
	//vec4 kst = jvr_Material_Specular;
   	//vec4 kdt = jvr_Material_Diffuse;
	//gl_FragColor = phong2(L, kst, kdt, 105f);	
}
