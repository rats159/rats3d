package rats3d

import "core:fmt"
import "core:os/os2"
import "core:strings"
import gl "vendor:OpenGL"

DEFAULT_UNIFORM_NAME_PROJECTION_MATRIX :: "projection"
DEFAULT_UNIFORM_NAME_MODEL_MATRIX :: "model"
DEFAULT_UNIFORM_NAME_VIEW_MATRIX :: "view"

Shader :: struct {
	_id:       u32,
	locations: map[string]i32,
}

Shader_Load_Error :: enum {
	None,
	Failed_To_Read_File,
	Vertex_Compilation_Failed,
	Fragment_Compilation_Failed,
	Program_Linking_Failed,
}

load_shader :: proc(vertex_path, fragment_path: string, loc := #caller_location) -> Shader {
	shader, err := load_shader_err(vertex_path, fragment_path, loc = loc)

	#partial switch err {
	case .None:
	case:
		log_error("Shader loading failed: %s", err, loc = loc)
	}

	return shader
}

load_shader_err :: proc(
	vertex_path, fragment_path: string,
	loc := #caller_location,
) -> (
	Shader,
	Shader_Load_Error,
) {
	vertex_bytes, vertex_err := os2.read_entire_file(vertex_path, context.temp_allocator)
	fragment_bytes, fragment_err := os2.read_entire_file(fragment_path, context.temp_allocator)

	if vertex_err != nil || fragment_err != nil {
		return {}, .Failed_To_Read_File
	}

	vertex_len := i32(len(vertex_bytes))
	fragment_len := i32(len(fragment_bytes))

	vertex_source := cstring(raw_data(vertex_bytes))
	fragment_source := cstring(raw_data(fragment_bytes))

	vertexShader := gl.CreateShader(gl.VERTEX_SHADER)
	defer gl.DeleteShader(vertexShader)
	gl.ShaderSource(vertexShader, 1, &vertex_source, &vertex_len)
	gl.CompileShader(vertexShader)

	success: i32

	gl.GetShaderiv(vertexShader, gl.COMPILE_STATUS, &success)

	if success == 0 {
		length: i32
		gl.GetShaderiv(vertexShader, gl.INFO_LOG_LENGTH, &length)
		shader_log := make([]byte, length, context.temp_allocator)
		gl.GetShaderInfoLog(vertexShader, length, nil, raw_data(shader_log))
		log_warning("Vertex shader compilation failed:\n", string(shader_log), loc)
		return {}, .Vertex_Compilation_Failed
	}

	fragmentShader := gl.CreateShader(gl.FRAGMENT_SHADER)
	gl.ShaderSource(fragmentShader, 1, &fragment_source, &fragment_len)
	gl.CompileShader(fragmentShader)

	gl.GetShaderiv(vertexShader, gl.COMPILE_STATUS, &success)

	if success == 0 {
		length: i32
		gl.GetShaderiv(fragmentShader, gl.INFO_LOG_LENGTH, &length)
		shader_log := make([]byte, length, context.temp_allocator)
		gl.GetShaderInfoLog(fragmentShader, length, nil, raw_data(shader_log))
		log_warning("Fragment shader compilation failed:\n", string(shader_log), loc)
		return {}, .Fragment_Compilation_Failed
	}

	shaderProgram := gl.CreateProgram()

	gl.AttachShader(shaderProgram, vertexShader)
	gl.AttachShader(shaderProgram, fragmentShader)
	gl.LinkProgram(shaderProgram)

	gl.GetProgramiv(shaderProgram, gl.LINK_STATUS, &success)

	if success == 0 {
		length: i32
		gl.GetProgramiv(shaderProgram, gl.INFO_LOG_LENGTH, &length)
		program_log := make([]byte, length, context.temp_allocator)
		gl.GetProgramInfoLog(shaderProgram, length, nil, raw_data(program_log))
		log_warning("Program linking failed:\n", string(program_log), loc)
		return {}, .Program_Linking_Failed
	}

	shader := Shader {
		_id = shaderProgram,
	}

	update_default_locations(&shader)

	return shader, .None
}

upload :: proc {
	upload_mat4,
	upload_float,
}

upload_float :: proc(shader: Shader, float: f32, location: string, loc := #caller_location) {
	use_shader(shader)
	if location not_in shader.locations {
		log_error(
			"Tried to upload float to uniform \"%s\", whose location was not set.",
			location,
			loc = loc,
		)
	}
	gl.Uniform1f(shader.locations[location], float)
}

upload_mat4 :: proc(
	shader: Shader,
	mat: matrix[4, 4]f32,
	location: string,
	loc := #caller_location,
) {
	use_shader(shader)
	mat := mat
	if location not_in shader.locations {
		log_error(
			"Tried to upload matrix to uniform \"%s\", whose location was not set.",
			location,
			loc = loc,
		)
	}
	gl.UniformMatrix4fv(shader.locations[location], 1, false, raw_data(&mat))
}

update_location :: proc(shader: ^Shader, name: string) {
	cstr := strings.clone_to_cstring(name, context.temp_allocator)
	shader.locations[name] = gl.GetUniformLocation(shader._id, cstr)
}

update_default_locations :: proc(shader: ^Shader) {
	update_location(shader, DEFAULT_UNIFORM_NAME_MODEL_MATRIX)
	update_location(shader, DEFAULT_UNIFORM_NAME_PROJECTION_MATRIX)
	update_location(shader, DEFAULT_UNIFORM_NAME_VIEW_MATRIX)
}

use_shader :: proc(shader: Shader) {
	gl.UseProgram(shader._id)
}
