uniform sampler2D jvr_Texture0;
uniform bool withBloom;
varying vec2 texCoord;

void main()
{
   	if(withBloom)
   	{
	   	vec4 sum = vec4(0);
	   	int j;
	   	int i;
	
	   	for( i= -3 ;i < 3; i++)
	   	{
	    	for (j = -3; j < 3; j++)
	        {
	        	sum += texture2D(jvr_Texture0, texCoord + vec2(j, i)*0.004) * 0.25;
	        }
	   	}
		if (texture2D(jvr_Texture0, texCoord).r < 0.3)
	    {
	    	gl_FragColor = sum*sum*0.012 + texture2D(jvr_Texture0, texCoord);
	    }
	    else
	    {
	        if (texture2D(jvr_Texture0, texCoord).r < 0.5)
	        {
	            gl_FragColor = sum*sum*0.009 + texture2D(jvr_Texture0, texCoord);
	        }
	        else
	        {
	            gl_FragColor = sum*sum*0.0075 + texture2D(jvr_Texture0, texCoord);
	        }
	    }
    }
    else
    {
    	// without bloom
    	gl_FragColor = vec4(texture2D(jvr_Texture0, texCoord).rgb, 1.0);
    }
    
}