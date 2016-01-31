
uniform sampler2D energyBarTex;

varying vec2 texCoord;
varying float energy;

void main (void)
{    
	// use the texture (the surrounding)
    vec4 final_color = texture2D(energyBarTex, texCoord);
    
    // if its bright, than its time to look at the energy level
    if(final_color.r > 0.2)
    {
	    // look at energy level to define color at this position
	    if(1.0 - texCoord.y > energy)
	    {
	    	final_color.r = 0.3;
	    	final_color.g = 0.0;
	    	final_color.b = 0.0;
	    }
	    else
	    {
	    	final_color.r = 0.0;
	    	final_color.g = 0.5;
	    	final_color.b = 0.0;
	    }
    }    
 
	gl_FragColor = final_color;
}
