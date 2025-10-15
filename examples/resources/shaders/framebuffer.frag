#version 330 core
out vec4 out_color;

in vec2 f_uv;

uniform sampler2D screenTexture;
uniform float min_depth;
uniform float hue_offset;
uniform float hue_scale;

float near = 0.1;
float far = 1000;

float hue2rgb(float p, float q, float t) {
    if (t < 0.0) t += 1.0;
    if (t > 1.0) t -= 1.0;
    if (t < 1.0/6.0) return p + (q - p) * 6.0 * t;
    if (t < 1.0/2.0) return q;
    if (t < 2.0/3.0) return p + (q - p) * (2.0/3.0 - t) * 6.0;
    return p;
}

vec3 hslToRgb(vec3 hsl) {
    float h = hsl.x, s = hsl.y, l = hsl.z;

    if (s == 0.0) {
        return vec3(l);
    }

    float q = l < 0.5 ? l * (1.0 + s) : l + s - l * s;
    float p = 2.0 * l - q;

    float r = hue2rgb(p, q, h + 1.0/3.0);
    float g = hue2rgb(p, q, h);
    float b = hue2rgb(p, q, h - 1.0/3.0);
    return vec3(r, g, b);
}

float remap(float value, float old_min, float old_max, float new_min, float new_max) {
    float old_range = old_max - old_min;
	float new_range = new_max - new_min;
	if (old_range == 0.0) {
		return new_range / 2.0;
	}
	return ((value - old_min) / old_range) * new_range + new_min;
}

void main()
{
    float col = texture(screenTexture, f_uv).r;
    if (col == 1.0) {
        out_color = vec4(0,0,0,1);
        return;
    }
    float depth = remap(col,min_depth,1.0,0.0, 1.0);

    out_color = vec4(hslToRgb(vec3(1 - mod(((depth) + hue_offset) * hue_scale, 1), .6, 0.6)), 1.0);

} 