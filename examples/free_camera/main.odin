package free_camera

import "core:fmt"
import r3d "../../"
import "core:math"
import "core:math/linalg"
import "core:time"

RESOURCES :: "./examples/resources/"

speed: f32 = 20

move_camera :: proc(cam: ^r3d.Camera) {
	if !r3d.is_cursor_locked() do return
	mouse_motion := r3d.get_mouse_motion()

	cam.pitch += mouse_motion.y / 100
	cam.yaw += mouse_motion.x / 100

	cam.pitch = clamp(cam.pitch, math.to_radians(f32(-80)), math.to_radians(f32(80)))

	yaw_mat := linalg.matrix3_from_yaw_pitch_roll(cam.yaw, 0, cam.roll)
	pitch_mat := linalg.matrix3_from_yaw_pitch_roll(0, cam.pitch, 0)

	rot_mat := pitch_mat * yaw_mat

	forward := [3]f32{0, 0, -1} * rot_mat
	side := [3]f32{1, 0, 0} * rot_mat

	dt := f32(time.duration_seconds(r3d.delta_time()))

	if r3d.is_key_down(.W) {
		cam.position += forward * speed * dt
	}

	if r3d.is_key_down(.S) {
		cam.position -= forward * speed * dt
	}

	if r3d.is_key_down(.D) {
		cam.position += side * speed * dt
	}

	if r3d.is_key_down(.A) {
		cam.position -= side * speed * dt
	}

	if r3d.is_key_down(.Space) {
		cam.position.y += speed * dt
	}

	if r3d.is_key_down(.Left_Control) {
		cam.position.y -= speed * dt
	}
}

main :: proc() {
    r3d.open_window(1280,820,"Freecam example!", {.Panic_On_Error})

    shader := r3d.load_shader(RESOURCES+"shaders/basic_lit.vert",RESOURCES+"shaders/basic_lit.frag")

    white := r3d.gen_texture_color({1,1,1,1},1,1)

    mesh := r3d.load_obj(RESOURCES + "models/suzanne.obj")
    model := r3d.load_model_from_mesh(mesh)
    model.shader = shader
    model.texture = white

    camera := r3d.Camera {
        fov = math.to_radians(f32(70)),
        pitch = 0,
        yaw = 0,
        roll = 0,
        position = {0,0,2}
    }

    for !r3d.window_should_close() {
        model.transformation *= linalg.matrix4_rotate(f32(time.duration_seconds(r3d.delta_time())), [3]f32{.2,1,.7})
        
        move_camera(&camera)

        r3d.use_camera(camera, shader)
        r3d.poll_events()
        r3d.clear({.6,.8,1,1})

        r3d.draw_model(model)

        r3d.swap_buffers()
    }
}