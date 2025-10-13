package rats3d

Color :: distinct [4]f32 // 0-1 RGBA

Image :: struct {
	pixels:        []Color,
	width, height: int,
}
