uniform sampler2D 	jvr_Texture0;
uniform bool      	jvr_UseTexture0;
uniform vec4 		jvr_Material_Ambient;

void main()
{
	vec4 final_color = jvr_Material_Ambient;
   	//if (jvr_UseTexture0)
   	//{
      //  final_color *= texture2D(jvr_Texture0, texCoord);
        //if(final_color.w<0.2) discard;
    //}
	gl_FragColor = final_color;
}