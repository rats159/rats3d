package rats3d

import "core:slice"

Color :: distinct [4]f32 // 0-1 RGBA

Image :: struct {
	pixels:        []Color,
	width, height: int,
}

gen_image_color :: proc(color: Color, width, height: int) -> (img: Image) {
	pixels := make([]Color, width * height)
	slice.fill(pixels, color)

	return {width = width, height = height, pixels = pixels}
}
