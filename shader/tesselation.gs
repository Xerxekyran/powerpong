// #version 150
 
#extension GL_EXT_geometry_shader4 : enable
 
// layout(triangle) in;
// layout(triangle_strip, max_vertices = 12) out;

uniform vec3 jvr_Normal;

varying out vec2 texCoord;


void triangle( vec4 v0, vec4 v1, vec4 v2 )
{
    gl_Position = v0;
    EmitVertex();
    gl_Position = v1;
    EmitVertex();
    gl_Position = v2;
    EmitVertex();
 
    EndPrimitive();
}
 
void subdivide( vec4 v0, vec4 v1, vec4 v2 )
{
    vec4 h[3];
 
    h[0] = ( v1 + v2 ) * 0.5;
    h[1] = ( v0 + v2 ) * 0.5;
    h[2] = ( v0 + v1 ) * 0.5;
 
    triangle( v0, h[2], h[1] );
    triangle( h[2], v1, h[0] );
    triangle( h[2], h[0], h[1] );
    triangle( h[1], h[0], v2 );
}
 
void main()
{
    vec4 v[3];
 
    v[0] = gl_PositionIn[0];
    v[1] = gl_PositionIn[1];
    v[2] = gl_PositionIn[2];
 
    subdivide( v[0], v[1], v[2] );
}
