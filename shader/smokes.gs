uniform mat4 jvr_ProjectionMatrix;
attribute vec2 jvr_TexCoord;
float hs = 0.07;

varying in float partEnergyV[];
varying out float partEnergyG;
varying vec2 texCoord;

void quadVertex(float dx, float dy) {
	gl_Position = jvr_ProjectionMatrix * (gl_PositionIn[0] + vec4(dx, dy, 0, 0));	
	partEnergyG = partEnergyV[0];  
}

void main(void)
{
	float formfaktor = (1/partEnergyV[0])/20;
	
	if (formfaktor > 0.2)
		hs = 0.2;
	else
		hs = hs + formfaktor; 
	
	if (partEnergyV[0] > 0.01) {
		
		quadVertex(-hs,  hs);
		texCoord = vec2(0.0, 1.0);
		EmitVertex();  
		
		quadVertex(-hs, -hs);
		texCoord = vec2(0.0, 0.0);	
		EmitVertex();  
		
		quadVertex( hs,  hs);
		texCoord = vec2(1.0, 1.0);
		EmitVertex(); 
		 
		quadVertex( hs, -hs);
		texCoord = vec2(1.0, 0.0);	
		EmitVertex();  	
			
  		EndPrimitive();
	}	
}