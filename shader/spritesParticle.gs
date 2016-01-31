// #version 150

// layout(points) in;
// layout(triangle_strip, max_vertices = 4) out;

varying in float particleSize[];
varying in float particleEnergyV[];
varying out float energy;

varying out vec2 texCoord;


 
void main() 
{
	energy = particleEnergyV[0];
	
	float spriteSize = particleSize[0];
	
    gl_Position = gl_PositionIn[0];
    gl_Position.x -= spriteSize;
    gl_Position.y += spriteSize;
	texCoord = vec2(0.0, 1.0);	
    EmitVertex();
    
    gl_Position = gl_PositionIn[0];
    gl_Position.x -= spriteSize;
    gl_Position.y -= spriteSize;
    texCoord = vec2(0.0, 0.0);
    EmitVertex();
    
    gl_Position = gl_PositionIn[0];
    gl_Position.x += spriteSize;
    gl_Position.y += spriteSize;
    texCoord = vec2(1.0, 1.0);
    EmitVertex();
    
    gl_Position = gl_PositionIn[0];
    gl_Position.x += spriteSize;
    gl_Position.y -= spriteSize;
    texCoord = vec2(1.0, 0.0);
    EmitVertex();
    
  	EndPrimitive();
}