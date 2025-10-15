package rats3d

import "base:runtime"
import "core:os/os2"
import "core:strconv"
import "core:strings"

Obj_Error :: enum {
	None,
	Failed_To_Read_File,
	Malformed_Data,
}

Obj_Vertex :: distinct [3]f32
Obj_Normal :: distinct [3]f32
Obj_Uv :: distinct [2]f32
Obj_Face :: struct {
	position_indices: [3]int,
	uv_indices:       [3]int,
	normal_indices:   [3]int,
}

load_obj :: proc(path: string, allocator := context.allocator, loc := #caller_location) -> Mesh {
	mesh, err := load_obj_err(path, allocator)

	#partial switch err {
	case .None:
	case:
		log_error("OBJ loading failed: %s", err, loc = loc)
	}

	return mesh
}

load_obj_err :: proc(path: string, allocator := context.allocator) -> (Mesh, Obj_Error) {
	bytes, err := os2.read_entire_file(path, context.temp_allocator)

	if err != nil {
		return {}, .Failed_To_Read_File
	}

	text := string(bytes)

	vertices: [dynamic]Obj_Vertex
	normals: [dynamic]Obj_Normal
	tex_coords: [dynamic]Obj_Uv
	faces: [dynamic]Obj_Face

	for line in strings.split_lines_iterator(&text) {
		if err := parse_line(line, &vertices, &normals, &tex_coords, &faces); err != .None {
			return {}, err
		}
	}

	return resolve_model(vertices, normals, tex_coords, faces, allocator), .None
}

resolve_model :: proc(
	positions: [dynamic]Obj_Vertex,
	normals: [dynamic]Obj_Normal,
	tex_coords: [dynamic]Obj_Uv,
	faces: [dynamic]Obj_Face,
	allocator: runtime.Allocator,
) -> Mesh(Pos_Uv_Normal_Vertex) {
	vertices := make([]Pos_Uv_Normal_Vertex, len(faces) * 3, allocator)

	vertex_index := 0
	for face in faces {
		vertices[vertex_index] = {
			position = ([3]f32)(positions[face.position_indices[0]]),
			normal   = ([3]f32)(normals[face.normal_indices[0]]),
			uvs      = ([2]f32)(tex_coords[face.uv_indices[0]]),
		}
		vertices[vertex_index + 1] = {
			position = ([3]f32)(positions[face.position_indices[1]]),
			normal   = ([3]f32)(normals[face.normal_indices[1]]),
			uvs      = ([2]f32)(tex_coords[face.uv_indices[1]]),
		}
		vertices[vertex_index + 2] = {
			position = ([3]f32)(positions[face.position_indices[2]]),
			normal   = ([3]f32)(normals[face.normal_indices[2]]),
			uvs      = ([2]f32)(tex_coords[face.uv_indices[2]]),
		}

		vertex_index += 3
	}

	return {vertices}
}

@(private)
parse_line :: proc(
	line: string,
	vertices: ^[dynamic]Obj_Vertex,
	normals: ^[dynamic]Obj_Normal,
	tex_coords: ^[dynamic]Obj_Uv,
	faces: ^[dynamic]Obj_Face,
) -> Obj_Error {
	line := line

	if line[0] == '#' {
		return .None
	}

	for word in strings.split_multi_iterate(&line, {" ", "#"}) {
		if len(word) == 0 {
			continue
		}

		if word[0] == '#' {
			return .None
		}
		switch word {
		case "v":
			vertex, ok := parse_3_floats(&line)
			if !ok {
				return .Malformed_Data
			}
			append(vertices, Obj_Vertex(vertex))
		case "vn":
			normal, ok := parse_3_floats(&line)
			if !ok {
				return .Malformed_Data
			}
			append(normals, Obj_Normal(normal))
		case "vt":
			uv, ok := parse_2_floats(&line)
			if !ok {
				return .Malformed_Data
			}
			append(tex_coords, Obj_Uv(uv))
		case "f":
			face, ok := parse_face(&line)
			if !ok {
				return .Malformed_Data
			}
			append(faces, face)
		case "s", "o":
			return .None
		case:
			panic(word)
		}

	}

	return .None
}

parse_3_floats :: proc(line: ^string) -> (_v: [3]f32, _ok: bool) {
	x_str := strings.split_multi_iterate(line, {" ", "#"}) or_return
	y_str := strings.split_multi_iterate(line, {" ", "#"}) or_return
	z_str := strings.split_multi_iterate(line, {" ", "#"}) or_return

	x := strconv.parse_f32(x_str) or_return
	y := strconv.parse_f32(y_str) or_return
	z := strconv.parse_f32(z_str) or_return

	return {x, y, z}, true
}

parse_2_floats :: proc(line: ^string) -> (_v: [2]f32, _ok: bool) {
	x_str := strings.split_multi_iterate(line, {" ", "#"}) or_return
	y_str := strings.split_multi_iterate(line, {" ", "#"}) or_return

	x := strconv.parse_f32(x_str) or_return
	y := strconv.parse_f32(y_str) or_return

	return {x, y}, true
}

parse_face :: proc(line: ^string) -> (_face: Obj_Face, _ok: bool) {
	v1 := strings.split_multi_iterate(line, {" ", "#"}) or_return
	v2 := strings.split_multi_iterate(line, {" ", "#"}) or_return
	v3 := strings.split_multi_iterate(line, {" ", "#"}) or_return

	pos1, uv1, normal1 := parse_face_vertex(&v1) or_return
	pos2, uv2, normal2 := parse_face_vertex(&v2) or_return
	pos3, uv3, normal3 := parse_face_vertex(&v3) or_return

	return {
			position_indices = {pos1, pos2, pos3},
			uv_indices = {uv1, uv2, uv3},
			normal_indices = {normal1, normal2, normal3},
		},
		true
}

parse_face_vertex :: proc(str: ^string) -> (_pos, _uv, _normal: int, _ok: bool) {
	pos_str := strings.split_iterator(str, "/") or_return
	uv_str := strings.split_iterator(str, "/") or_return
	normal_str := strings.split_iterator(str, "/") or_return

	pos := strconv.parse_int(pos_str, base = 10) or_return
	uv := strconv.parse_int(uv_str, base = 10) or_return
	normal := strconv.parse_int(normal_str, base = 10) or_return

	return pos - 1, uv - 1, normal - 1, true
}
