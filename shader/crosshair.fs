uniform sampler2D crossHairTex;
varying vec2 texCoord;

void main(void)
{
   vec4 crossHair = texture2D(crossHairTex, texCoord.st);
   
   //if (crossHair.x < 0.9 && crossHair.y <0.9 && crossHair.z < 0.9)
   //   discard;
   //else
      gl_FragColor = vec4(1.,1.,0.,1.);
}