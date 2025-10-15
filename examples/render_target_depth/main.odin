package render_target_depth

import r3d "../../"
import freecam "../free_camera"
import "core:math"
import "core:math/linalg"
import "core:slice"

// relative to the cwd
RESOURCES :: "./examples/resources/"

XY_UV_Vertex :: struct {
	position: [2]f32,
	uvs:      [2]f32,
}

main :: proc() {
	hue_offset: f32 = 0
	hue_scale: f32 = 1
	r3d.open_window(1280, 720, "Render Target Example!", {.Panic_On_Error})

	r3d.maximize()

	shader := r3d.load_shader(
		RESOURCES + "shaders/depth_only.vert",
		RESOURCES + "shaders/depth_only.frag",
	)

	screen_shader := r3d.load_shader(
		RESOURCES + "shaders/render_target_depth.vert",
		RESOURCES + "shaders/render_target_depth.frag",
	)

	mesh := r3d.load_obj(RESOURCES + "models/just_a_girl.obj")

	model := r3d.load_model_from_mesh(mesh)
	model.shader = shader
	model.texture = r3d.gen_texture_color(1, 1, 1)

	camera := r3d.Camera {
		fov = linalg.to_radians(f32(80)),
	}
	freecam.speed = 4

	fullscreen_mesh := r3d.Mesh(XY_UV_Vertex) {
		vertices = {
			{position = {-1, 1}, uvs = {0, 1}},
			{position = {-1, -1}, uvs = {0, 0}},
			{position = {1, -1}, uvs = {1, 0}},
			{position = {-1, 1}, uvs = {0, 1}},
			{position = {1, -1}, uvs = {1, 0}},
			{position = {1, 1}, uvs = {1, 1}},
		},
	}

	vertex_format := r3d.Vertex_Format{{{[2]f32, 0}, {[2]f32, 0}}}

	fullscreen_quad := r3d.load_model_from_mesh(fullscreen_mesh, vertex_format)
	fullscreen_quad.shader = screen_shader

	SCALING_FACTOR :: 8
	render_target := r3d.create_render_target(
		r3d.screen_width() / SCALING_FACTOR,
		r3d.screen_height() / SCALING_FACTOR,
		{.Depth},
	)

	fullscreen_quad.texture = render_target.depth_tex.?

	r3d.update_location(&screen_shader, "min_depth")
	r3d.update_location(&screen_shader, "hue_offset")
	r3d.update_location(&screen_shader, "hue_scale")

	buf := make(
		[]f32,
		(r3d.screen_width() / SCALING_FACTOR) * (r3d.screen_height() / SCALING_FACTOR),
	)

	for !r3d.window_should_close() {
		freecam.move_camera(&camera)
		r3d.use_camera(camera, shader)

		r3d.poll_events()

		if r3d.is_key_down(.Q) {
			hue_offset += 2.0 / 360.0
		}

		if r3d.is_key_down(.E) {
			hue_offset -= 2.0 / 360.0
		}

		if r3d.is_key_down(.Left_Arrow) {
			hue_scale += 0.1
		}

		if r3d.is_key_down(.Right_Arrow) {
			hue_scale -= 0.1
		}

		if r3d.is_key_down(.Up_Arrow) {
			camera.fov += math.to_radians(f32(1))
		}

		if r3d.is_key_down(.Down_Arrow) {
			camera.fov -= math.to_radians(f32(1))
		}

		if r3d.use_render_target(render_target) {
			r3d.enable_depth_test()
			r3d.clear({1, 1, 1, 1})

			r3d.draw_model(model)
		}
		r3d.disable_depth_test()

		r3d.read_texture_data(fullscreen_quad.texture, buf)
		min_depth := slice.min(buf)
		r3d.upload(screen_shader, min_depth, "min_depth")
		r3d.upload(screen_shader, hue_offset, "hue_offset")
		r3d.upload(screen_shader, hue_scale, "hue_scale")
		r3d.draw_model(fullscreen_quad)

		r3d.swap_buffers()
	}
}
