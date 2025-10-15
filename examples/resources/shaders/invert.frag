#version 330 core
out vec4 out_color;

in vec2 f_uv;

uniform sampler2D screenTexture;

void main()
{
    vec3 col = texture(screenTexture, f_uv).rgb;
    out_color = vec4(1 - col, 1.0);

} 