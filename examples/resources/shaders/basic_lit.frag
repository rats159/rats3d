#version 330

in vec2 f_uv;
in vec3 f_normal;
in vec3 f_pos;

uniform sampler2D u_tex;

out vec4 out_color;

const vec3 LIGHT_COLORS[] = vec3[](vec3(1,0,0), vec3(0,1,0), vec3(0,0,1));
const vec3 LIGHT_POSITIONS[] = vec3[](vec3(0,2,0), vec3(2,0,0), vec3(0,0,2));

void main()
{
    vec3 texel_color = texture(u_tex, f_uv).rgb;

    float ambientStrength = 0.1;
    vec3 norm = normalize(f_normal);
    
    vec3 result = vec3(0);

    for(int i = 0; i < 3; i++) {
        vec3 ambient = ambientStrength * LIGHT_COLORS[i];
        vec3 lightDir = -normalize(f_pos - LIGHT_POSITIONS[i]);
        float diff = max(dot(norm, lightDir), 0.0);
        vec3 diffuse = diff * LIGHT_COLORS[i];
        result += (ambient + diffuse) * texel_color;
    }



    out_color = vec4(result, 1.0);
}