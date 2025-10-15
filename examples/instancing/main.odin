package instancing

import r3d "../../"
import freecam "../free_camera"
import "core:fmt"
import "core:math"
import "core:math/linalg"
import "core:math/linalg/glsl"
import "core:time"

RESOURCES :: "./examples/resources/"

main :: proc() {
	r3d.open_window(1280, 820, "Freecam example!", {.Panic_On_Error})

	shader := r3d.load_shader(
		RESOURCES + "shaders/lit_instancing.vert",
		RESOURCES + "shaders/basic_lit.frag",
	)

	white := r3d.gen_texture_color({1, 1, 1, 1}, 1, 1)

	mesh := r3d.load_obj(RESOURCES + "models/suzanne.obj")
	model := r3d.load_model_from_mesh(mesh)
	model.shader = shader
	model.texture = white

	camera := r3d.Camera {
		fov      = math.to_radians(f32(70)),
		pitch    = 0,
		yaw      = 0,
		roll     = 0,
		position = {0, 0, 2},
	}

	transforms: [dynamic]glsl.mat4

	for x in 0 ..< 20 {
		for y in 0 ..< 20 {
			for z in 0 ..< 20 {
				append(&transforms, linalg.matrix4_translate([3]f32{f32(x), f32(y), f32(z)} * 3))
			}
		}
	}

	for !r3d.window_should_close() {
		model.transformation *= linalg.matrix4_rotate(
			f32(time.duration_seconds(r3d.delta_time())),
			[3]f32{0, 1, 0},
		)

		if r3d.is_key_pressed(.Escape) {
			if r3d.is_cursor_locked() {
				r3d.unlock_cursor()
			} else {
				r3d.lock_cursor()
			}
		}

		freecam.move_camera(&camera)

		r3d.use_camera(camera, shader)
		r3d.poll_events()
		r3d.clear({.6, .8, 1, 1})

		// first vbo registered, see the default vertex format
		vbo_id := model._vbos[1]
		r3d.draw_model_instanced(&model, transforms[:], vbo_id)

		r3d.swap_buffers()
	}
}
