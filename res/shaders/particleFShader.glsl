#version 400

in vec2 fUV1;
in vec2 fUV2;
in float fBlendFactor;

uniform sampler2D uTex;

out vec4 outColor;


void main(void){
    vec4 color1 = texture(uTex, fUV1);
    vec4 color2 = texture(uTex, fUV2);

    outColor = mix(color1,color2,fBlendFactor);
    outColor = vec4(1,1,0,1);
}
