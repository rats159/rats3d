package rats3d

import "base:runtime"
import "core:log"
import "core:time"
import gl "vendor:OpenGL"
import "vendor:glfw"

Window :: struct {
	_handle:       glfw.WindowHandle,
	width, height: int,
}

Window_Error :: enum {
	None = 0,
	Initialization_Failed,
	Window_Creation_Failed,
}

setup_defaults :: proc() {
	glfw.SwapInterval(1)
	enable_depth_test()
	gl.Enable(gl.CULL_FACE)
	gl.CullFace(gl.BACK)
	lock_cursor()
}

open_window :: proc(
	width, height: int,
	title: cstring,
	config_flags: Config_Flags = {},
	logger: Maybe(log.Logger) = nil,
) -> Window_Error {
	if !glfw.Init() {
		return .Initialization_Failed
	}

	logger := logger
	if logger == nil {
		logger = create_default_logger(.Panic_On_Error in config_flags)
	}

	global_state.logger = logger.?


	glfw.SetErrorCallback(proc "c" (errorCode: i32, description: cstring) {
		context = runtime.default_context()

		log_error("[GLFW ERROR %d]: %s", errorCode, description)
	})


	glfw.WindowHint(glfw.CONTEXT_VERSION_MAJOR, 3)
	glfw.WindowHint(glfw.CONTEXT_VERSION_MINOR, 3)
	glfw.WindowHint(glfw.OPENGL_PROFILE, glfw.OPENGL_CORE_PROFILE)

	window := glfw.CreateWindow(i32(width), i32(height), title, nil, nil)

	if window == nil {
		return .Window_Creation_Failed
	}

	glfw.SetFramebufferSizeCallback(
		window,
		proc "c" (window: glfw.WindowHandle, width, height: i32) {
			gl.Viewport(0, 0, width, height)
			global_state.window.width = int(width)
			global_state.window.height = int(height)
		},
	)

	glfw.SetKeyCallback(
		window,
		proc "c" (window: glfw.WindowHandle, key, scancode, action, mods: i32) {
			key_callback(Key(key), Input_Action(action))
		},
	)

	glfw.SetMouseButtonCallback(
		window,
		proc "c" (window: glfw.WindowHandle, button, action, mods: i32) {
			mouse_button_callback(Mouse_Button(button), Input_Action(action))
		},
	)

	global_state.window._handle = window

	glfw.MakeContextCurrent(window)
	gl.load_up_to(3, 3, glfw.gl_set_proc_address)

	width, height := glfw.GetFramebufferSize(window)
	global_state.window.width = int(width)
	global_state.window.height = int(height)

	gl.Viewport(0, 0, width, height)

	setup_defaults()

	return .None
}

window_should_close :: proc() -> bool {
	return bool(glfw.WindowShouldClose(global_state.window._handle))
}

close_window :: proc() {
	glfw.DestroyWindow(global_state.window._handle)
	glfw.Terminate()
}

poll_events :: proc() {
	global_state.last_frame_start = global_state.frame_start
	global_state.frame_start = time.tick_now()

	global_state.last_mouse_position = global_state.mouse_position
	mouse_x, mouse_y := glfw.GetCursorPos(global_state.window._handle)
	global_state.mouse_position = {f32(mouse_x), f32(mouse_y)}

	global_state.last_keys = global_state.keys
	global_state.last_mouse_buttons = global_state.mouse_buttons
	glfw.PollEvents()
}

maximize :: proc() {
	glfw.MaximizeWindow(global_state.window._handle)
}

swap_buffers :: proc() {
	glfw.SwapBuffers(global_state.window._handle)
}

clear :: proc(color: [4]f32) {
	gl.ClearColor(color.r, color.g, color.b, color.a)

	gl.Clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT)
}

screen_width :: proc() -> int {
	return global_state.window.width
}

screen_height :: proc() -> int {
	return global_state.window.height
}

enable_depth_test :: proc() {
	gl.Enable(gl.DEPTH_TEST)
}

disable_depth_test :: proc() {
	gl.Disable(gl.DEPTH_TEST)
}