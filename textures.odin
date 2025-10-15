package rats3d

import "core:image"
import "core:image/png"
import "core:slice"
import gl "vendor:OpenGL"

_ :: png

Texture :: struct {
	width, height: int,
	id:            u32,
}
Texture_Load_Error :: enum {
	None,
	Failed_To_Read_File,
	PNG_Error,
}

load_texture :: proc(path: string, loc := #caller_location) -> Texture {
	texture, err := load_texture_err(path)

	#partial switch err {
	case .None:
	case:
		log_error("Texture loading failed: %s", err, loc = loc)
	}

	return texture
}

gen_texture_color :: proc(color: Color, width, height: int) -> Texture {
	return load_texture_from_image(gen_image_color(color, width, height))
}

gen_texture_depth :: proc(width, height: int) -> Texture {
	texture: u32

	gl.GenTextures(1, &texture)
	gl.BindTexture(gl.TEXTURE_2D, texture)

	gl.TexParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST)
	gl.TexParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST)
	gl.TexParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP)
	gl.TexParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP)

	gl.TexImage2D(
		gl.TEXTURE_2D,
		0,
		gl.DEPTH_COMPONENT32,
		i32(width),
		i32(height),
		0,
		gl.DEPTH_COMPONENT,
		gl.FLOAT,
		nil,
	)

	return {width = width, height = height, id = texture}
}

gen_texture_color_target :: proc(width, height: int) -> Texture {
	texture: u32

	gl.GenTextures(1, &texture)
	gl.BindTexture(gl.TEXTURE_2D, texture)

	gl.TexParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST)
	gl.TexParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST)
	gl.TexParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP)
	gl.TexParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP)

	gl.TexImage2D(gl.TEXTURE_2D, 0, gl.RGB, i32(width), i32(height), 0, gl.RGB, gl.FLOAT, nil)

	return {width = width, height = height, id = texture}
}

load_texture_from_image :: proc(img: Image) -> Texture {
	texture: u32

	gl.GenTextures(1, &texture)
	gl.BindTexture(gl.TEXTURE_2D, texture)

	gl.TexParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST)
	gl.TexParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST)
	gl.TexParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.REPEAT)
	gl.TexParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.REPEAT)

	gl.TexImage2D(
		gl.TEXTURE_2D,
		0,
		gl.RGBA,
		i32(img.width),
		i32(img.height),
		0,
		gl.RGBA,
		gl.FLOAT,
		raw_data(img.pixels),
	)
	gl.GenerateMipmap(gl.TEXTURE_2D)

	return {width = img.width, height = img.height, id = texture}
}

read_texture_data :: proc(tex: Texture, out_data: []f32) {
	gl.BindTexture(gl.TEXTURE_2D, tex.id)
	gl.GetTexImage(gl.TEXTURE_2D, 0, gl.DEPTH_COMPONENT, gl.FLOAT, raw_data(out_data))
}

load_texture_err :: proc(filepath: string) -> (Texture, Texture_Load_Error) {
	img, err := image.load_from_file(filepath, {.alpha_add_if_missing})
	if err != nil {
		#partial switch type in err {
		case image.PNG_Error:
			return {}, .PNG_Error
		case:
			return {}, .Failed_To_Read_File
		}
	}

	rgba_pixels := slice.reinterpret([][4]u8, img.pixels.buf[:])

	flip_buffer := make([][4]u8, len(rgba_pixels), context.temp_allocator)

	for pixel, i in rgba_pixels {
		x := i % img.width
		y := i / img.width

		flip_buffer[x + (img.height - 1 - y) * img.width] = pixel
	}

	copy(rgba_pixels, flip_buffer)

	texture: u32

	gl.GenTextures(1, &texture)
	gl.BindTexture(gl.TEXTURE_2D, texture)

	gl.TexParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST)
	gl.TexParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST)
	gl.TexParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.REPEAT)
	gl.TexParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.REPEAT)

	gl.TexImage2D(
		gl.TEXTURE_2D,
		0,
		gl.RGBA,
		i32(img.width),
		i32(img.height),
		0,
		gl.RGBA,
		gl.UNSIGNED_BYTE,
		raw_data(img.pixels.buf),
	)
	gl.GenerateMipmap(gl.TEXTURE_2D)

	image.destroy(img)

	return {width = img.width, height = img.height, id = texture}, nil
}
