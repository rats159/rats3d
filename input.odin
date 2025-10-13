package rats3d

import "vendor:glfw"

Key :: enum {
	None         = 0,
	Space        = glfw.KEY_SPACE,
	A            = glfw.KEY_A,
	B            = glfw.KEY_B,
	C            = glfw.KEY_C,
	D            = glfw.KEY_D,
	E            = glfw.KEY_E,
	F            = glfw.KEY_F,
	G            = glfw.KEY_G,
	H            = glfw.KEY_H,
	I            = glfw.KEY_I,
	J            = glfw.KEY_J,
	K            = glfw.KEY_K,
	L            = glfw.KEY_L,
	M            = glfw.KEY_M,
	N            = glfw.KEY_N,
	O            = glfw.KEY_O,
	P            = glfw.KEY_P,
	Q            = glfw.KEY_Q,
	R            = glfw.KEY_R,
	S            = glfw.KEY_S,
	T            = glfw.KEY_T,
	U            = glfw.KEY_U,
	V            = glfw.KEY_V,
	W            = glfw.KEY_W,
	X            = glfw.KEY_X,
	Y            = glfw.KEY_Y,
	Z            = glfw.KEY_Z,
	Escape       = glfw.KEY_ESCAPE,
	Left_Arrow   = glfw.KEY_LEFT,
	Right_Arrow  = glfw.KEY_RIGHT,
	Up_Arrow     = glfw.KEY_UP,
	Down_Arrow   = glfw.KEY_DOWN,
	Left_Control = glfw.KEY_LEFT_CONTROL,
	Left_Alt     = glfw.KEY_LEFT_ALT,
	_Max         = glfw.KEY_LAST, // Ensure that the enum holds every glfw key
}

Mouse_Button :: enum {
	Left   = glfw.MOUSE_BUTTON_LEFT,
	Right  = glfw.MOUSE_BUTTON_RIGHT,
	Middle = glfw.MOUSE_BUTTON_MIDDLE,
	_Max   = glfw.MOUSE_BUTTON_LAST, // Ensure that the enum holds every glfw mouse button
}

Input_Action :: enum u8 {
	Press   = glfw.PRESS,
	Release = glfw.RELEASE,
	Repeat  = glfw.REPEAT,
}

mouse_button_callback :: proc "contextless" (button: Mouse_Button, action: Input_Action) {
	global_state.mouse_buttons[button] = action == .Press
}

key_callback :: proc "contextless" (key: Key, action: Input_Action) {
	global_state.keys[key] = action == .Press || action == .Repeat
}

is_key_down :: proc(key: Key) -> bool {
	return global_state.keys[key]
}

is_key_pressed :: proc(key: Key) -> bool {
	return global_state.keys[key] && !global_state.last_keys[key]
}

is_mouse_button_pressed :: proc(mouse_button: Mouse_Button) -> bool {
	return(
		global_state.mouse_buttons[mouse_button] &&
		!global_state.last_mouse_buttons[mouse_button] \
	)
}

get_mouse_motion :: proc() -> [2]f32 {
	return global_state.mouse_position - global_state.last_mouse_position
}

lock_cursor :: proc() {
	glfw.SetInputMode(global_state.window._handle, glfw.CURSOR, glfw.CURSOR_DISABLED)
}
unlock_cursor :: proc() {
	xpos, ypos := glfw.GetCursorPos(global_state.window._handle)
	glfw.SetInputMode(global_state.window._handle, glfw.CURSOR, glfw.CURSOR_NORMAL)
	glfw.SetCursorPos(global_state.window._handle, xpos, ypos)
}

is_cursor_locked :: proc() -> bool {
	return glfw.GetInputMode(global_state.window._handle, glfw.CURSOR) == glfw.CURSOR_DISABLED
}
