#version 400 core

#define FOG_DENSITY 0//0.0035
#define FOG_GRADIENT 5

#define MAX_LIGHTS_PER_VERT 29


in vec3 aPos;
in vec2 aUV;
in vec3 aNormal;

out vec2 fUV;
out vec3 fNormal;
out vec3 fLightDir[MAX_LIGHTS_PER_VERT];
out vec3 fCameraDir;
out float fVisibility;

uniform mat4 uTransformation;
uniform mat4 uProjection;
uniform mat4 uView;
uniform vec3 uLightPos[MAX_LIGHTS_PER_VERT];

void main(void){
    vec4 worldPos = uTransformation * vec4(aPos,1);
    gl_Position = uProjection * uView * worldPos;
    fUV = aUV;
    fNormal = (uTransformation * vec4(aNormal,0)).xyz;

    for(int i = 0; i < MAX_LIGHTS_PER_VERT; i++){
        fLightDir[i] = uLightPos[i] - worldPos.xyz;
    }
    fCameraDir = (inverse(uView) * vec4(0,0,0,1)).xyz - worldPos.xyz;

    vec4 positionToCam = uView * worldPos;

    float distance = length(positionToCam.xyz);

    fVisibility = exp(-pow((distance*FOG_DENSITY),FOG_GRADIENT));
    fVisibility =clamp(fVisibility,0,1);
}