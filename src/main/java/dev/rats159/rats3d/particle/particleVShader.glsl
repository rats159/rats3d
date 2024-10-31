#version 400

in vec2 aPosition;

out vec2 fUV1;
out vec2 fUV2;
out float fBlendFactor;

in mat4 iModelView;
in vec4 iTexOffsets;
in float iBlendFactor;

uniform mat4 uProj;
uniform float uRowCount;

void main(void){
    vec2 UV = aPosition + vec2(0.5);
    UV.y = 1- UV.y;
    UV /= uRowCount;

    fUV1 = UV + iTexOffsets.xy;
    fUV2 = UV + iTexOffsets.zw;

    fBlendFactor = iBlendFactor;

    gl_Position = uProj * iModelView * vec4(aPosition, 0.0, 1.0);
}
