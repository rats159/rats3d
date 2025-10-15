package render_target

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
	r3d.open_window(1280, 720, "Render Target Example!", {.Panic_On_Error})

	r3d.maximize()

	shader := r3d.load_shader(
		RESOURCES + "shaders/basic_lit.vert",
		RESOURCES + "shaders/basic_lit.frag",
	)

	screen_shader := r3d.load_shader(
		RESOURCES + "shaders/invert.vert",
		RESOURCES + "shaders/invert.frag",
	)

	mesh := r3d.load_obj(RESOURCES + "models/suzanne.obj")

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

	render_target := r3d.create_render_target(r3d.screen_width(), r3d.screen_height(), {.Color})

	fullscreen_quad.texture = render_target.color_tex.?

	use_render_target := true

	for !r3d.window_should_close() {
		freecam.move_camera(&camera)
		r3d.use_camera(camera, shader)

		r3d.poll_events()

		if r3d.is_key_pressed(.Q) {
			use_render_target = !use_render_target
		}

		if use_render_target {
			if r3d.use_render_target(render_target) {
				r3d.enable_depth_test()
				r3d.clear({1, 1, 1, 1})

				r3d.draw_model(model)
			}

			r3d.disable_depth_test()

			r3d.draw_model(fullscreen_quad)
		} else {
			r3d.enable_depth_test()
			r3d.clear({1, 1, 1, 1})

			r3d.draw_model(model)
		}

		r3d.swap_buffers()
	}
}
