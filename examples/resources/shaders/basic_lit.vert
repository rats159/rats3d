#version 330 core
layout (location = 0) in vec3 a_pos;
layout (location = 1) in vec3 a_normal;
layout (location = 2) in vec2 a_uv;

out vec3 f_normal;
out vec2 f_uv;
out vec3 f_pos;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    f_normal = mat3(transpose(inverse(model))) * a_normal;  
    f_uv = a_uv;
    f_pos = vec3(model * vec4(a_pos, 1.0));
    gl_Position = projection * view * model * vec4(a_pos, 1.0);
}