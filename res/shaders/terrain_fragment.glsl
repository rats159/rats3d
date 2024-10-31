#version 400 core

#define MAX_LIGHTS_PER_VERT 29
#define MIN_AMBIENT_LIGHT .2

in vec2 fUV;
in vec3 fNormal;
in vec3 fLightDir[MAX_LIGHTS_PER_VERT];
in vec3 fCameraDir;
in float fVisibility;

out vec4 outColor;

uniform sampler2D backgroundTex;
uniform sampler2D rTex;
uniform sampler2D gTex;
uniform sampler2D bTex;
uniform sampler2D blendMap;

uniform vec3 uLightColor[MAX_LIGHTS_PER_VERT];
uniform vec3 uAttenuation[MAX_LIGHTS_PER_VERT];
uniform float uShineDamper;
uniform float uReflectivity;
uniform vec3 uSkyColor;

void main(void){
    vec4 blendMapColor = texture(blendMap, fUV);

    float backgroundTexAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
    vec2 tiledCoords = fUV * 40;
    vec4 bgTexColor = texture(backgroundTex, tiledCoords) * backgroundTexAmount;
    vec4 rTexColor = texture(rTex, tiledCoords) * blendMapColor.r;
    vec4 gTexColor = texture(gTex, tiledCoords) * blendMapColor.g;
    vec4 bTexColor = texture(bTex, tiledCoords) * blendMapColor.b;

    vec4 totalColor = bgTexColor + rTexColor + gTexColor + bTexColor;

    vec3 unitNormal = normalize(fNormal);
    vec3 unitCameraDir = normalize(fCameraDir);

    vec3 totalDiffuse = vec3(0);
    vec3 totalSpecular = vec3(0);

    for(int i = 0; i < MAX_LIGHTS_PER_VERT; i++){
        float distance = length(fLightDir[i]);

        vec3 currentAttenuation = uAttenuation[i];
        float attenuationFactor = currentAttenuation.x + currentAttenuation.y * distance + currentAttenuation.z * distance * distance;

        vec3 unitLightDir = normalize(fLightDir[i]);
        float normalDotLight = dot(unitNormal, unitLightDir);
        float brightness = max(normalDotLight, 0);


        vec3 lightDir = -unitLightDir;
        vec3 reflectedLightDir = reflect(lightDir, unitNormal);

        float specularFactor = max(dot(reflectedLightDir, unitCameraDir), 0f);
        float dampenedSpecularFactor = pow(specularFactor, uShineDamper);

        vec3 diffuseLightColor = brightness * uLightColor[i] / attenuationFactor;
        vec3 specularLightColor = dampenedSpecularFactor * uReflectivity * uLightColor[i] / attenuationFactor;

        totalDiffuse += diffuseLightColor;
        totalSpecular += specularLightColor;
    }

    totalDiffuse = max(totalDiffuse,MIN_AMBIENT_LIGHT);


    outColor = vec4(totalDiffuse, 1) * totalColor + vec4(totalSpecular,1);
    outColor = mix(vec4(uSkyColor,1),outColor,fVisibility);
}