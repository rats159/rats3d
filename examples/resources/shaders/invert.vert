#version 330 core
layout (location = 0) in vec2 a_pos;
layout (location = 1) in vec2 a_uv;

out vec2 f_uv;

void main()
{
    f_uv = a_uv;
    gl_Position = vec4(a_pos.x, a_pos.y, 0.0, 1.0); 
}  