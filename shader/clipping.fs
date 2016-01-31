varying vec4 color;
varying float clipDist;
void main (void)
{
	if(clipDist<0.0) discard;
	gl_FragColor = color;
}
