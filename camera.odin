package rats3d

import "core:math/linalg"

Camera :: struct {
	fov:              f32,
	position:         [3]f32,
	pitch, yaw, roll: f32,
}

use_camera :: proc(cam: Camera, shader: Shader) {
	yaw_mat := linalg.matrix4_from_yaw_pitch_roll(cam.yaw, 0, cam.roll)
	pitch_mat := linalg.matrix4_from_yaw_pitch_roll(0, cam.pitch, 0)

	rot_mat := pitch_mat * yaw_mat

	view := rot_mat * linalg.matrix4_translate(-cam.position)

	proj := linalg.matrix4_perspective(cam.fov, 16.0 / 9.0, 0.1, 1000)

	upload(shader, proj, DEFAULT_UNIFORM_NAME_PROJECTION_MATRIX)
	upload(shader, view, DEFAULT_UNIFORM_NAME_VIEW_MATRIX)
}
