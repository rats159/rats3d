#version 400 core

#define MAX_LIGHTS_PER_VERT 29
#define MIN_AMBIENT_LIGHT .2

in vec2 fUV;
in vec3 fNormal;
in vec3 fLightDir[MAX_LIGHTS_PER_VERT];
in vec3 fCameraDir;
in float fVisibility;

out vec4 outColor;

uniform sampler2D tex;
uniform vec3 uLightColor[MAX_LIGHTS_PER_VERT];
uniform vec3 uAttenuation[MAX_LIGHTS_PER_VERT];
uniform float uShineDamper;
uniform float uReflectivity;
uniform vec3 uSkyColor;

void main(void){
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

        totalDiffuse += brightness * uLightColor[i] / attenuationFactor;
        totalSpecular += dampenedSpecularFactor * uReflectivity * uLightColor[i] / attenuationFactor;
    }

    totalDiffuse = max(totalDiffuse,MIN_AMBIENT_LIGHT);

    outColor = vec4(totalDiffuse, 1) * texture(tex,fUV) + vec4(totalSpecular,1);
    outColor = mix(vec4(uSkyColor,1),outColor,fVisibility);
}