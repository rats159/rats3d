package rats3d

import "core:math/linalg"
import "core:math/linalg/glsl"
import "core:slice"
import gl "vendor:OpenGL"

Vertex :: struct {
	position: glsl.vec3,
	normal:   glsl.vec3,
	uvs:      glsl.vec2,
}

Mesh :: struct {
	vertices: []Vertex,
}

Model :: struct {
	mesh:            Mesh,
	transformation:  glsl.mat4,
	shader:          Shader,
	texture:         Texture,
	_vao:            u32,
	_vbo:            u32,
	_instance_vbo:   u32, // Unallocated until you draw_instances
	_instance_count: int,
}

load_model_from_mesh :: proc(mesh: Mesh) -> Model {
	vao: u32
	gl.GenVertexArrays(1, &vao)
	gl.BindVertexArray(vao)

	vbos: [2]u32
	gl.GenBuffers(2, raw_data(&vbos))
	gl.BindBuffer(gl.ARRAY_BUFFER, vbos[0])
	gl.BufferData(
		gl.ARRAY_BUFFER,
		size_of(Vertex) * len(mesh.vertices),
		raw_data(mesh.vertices),
		gl.STATIC_DRAW,
	)

	gl.VertexAttribPointer(0, 3, gl.FLOAT, false, 8 * size_of(f32), 0)
	gl.VertexAttribPointer(1, 3, gl.FLOAT, false, 8 * size_of(f32), 3 * size_of(f32))
	gl.VertexAttribPointer(2, 2, gl.FLOAT, false, 8 * size_of(f32), 6 * size_of(f32))
	gl.EnableVertexAttribArray(0)
	gl.EnableVertexAttribArray(1)
	gl.EnableVertexAttribArray(2)

	gl.BindBuffer(gl.ARRAY_BUFFER, vbos[1])

	// no data yet
	gl.BufferData(gl.ARRAY_BUFFER, 0, nil, gl.DYNAMIC_DRAW)

	gl.EnableVertexAttribArray(3)
	gl.EnableVertexAttribArray(4)
	gl.EnableVertexAttribArray(5)
	gl.EnableVertexAttribArray(6)
	gl.VertexAttribPointer(3, 4, gl.FLOAT, false, 16 * size_of(f32), 0)
	gl.VertexAttribPointer(4, 4, gl.FLOAT, false, 16 * size_of(f32), 4 * size_of(f32))
	gl.VertexAttribPointer(5, 4, gl.FLOAT, false, 16 * size_of(f32), 8 * size_of(f32))
	gl.VertexAttribPointer(6, 4, gl.FLOAT, false, 16 * size_of(f32), 12 * size_of(f32))
	gl.VertexAttribDivisor(3, 1)
	gl.VertexAttribDivisor(4, 1)
	gl.VertexAttribDivisor(5, 1)
	gl.VertexAttribDivisor(6, 1)

	return Model {
		mesh = mesh,
		_vao = vao,
		_vbo = vbos[0],
		_instance_vbo = vbos[1],
		transformation = 1,
	}
}

draw_model :: proc(model: Model) {
	gl.BindVertexArray(model._vao)
	gl.BindTexture(gl.TEXTURE_2D, model.texture.id)
	use_shader(model.shader)
	upload(model.shader, model.transformation, DEFAULT_UNIFORM_NAME_MODEL_MATRIX)
	gl.DrawArrays(gl.TRIANGLES, 0, i32(len(model.mesh.vertices)))
}

draw_model_instanced :: proc(model: ^Model, transformations: []glsl.mat4) {
	if len(transformations) > model._instance_count {
		new_capacity := max(len(transformations), model._instance_count * 2)
		gl.BindBuffer(gl.ARRAY_BUFFER, model._instance_vbo)
		gl.BufferData(gl.ARRAY_BUFFER, new_capacity * size_of(glsl.mat4), nil, gl.STREAM_DRAW)
		model._instance_count = new_capacity
	}

	gl.BindBuffer(gl.ARRAY_BUFFER, model._instance_vbo)
	gl.BufferSubData(
		gl.ARRAY_BUFFER,
		0,
		len(transformations) * size_of(glsl.mat4),
		raw_data(transformations),
	)

	gl.BindTexture(gl.TEXTURE_2D, model.texture.id)
	gl.BindVertexArray(model._vao)
	use_shader(model.shader)
	upload(model.shader, model.transformation, DEFAULT_UNIFORM_NAME_MODEL_MATRIX)
	gl.DrawArraysInstanced(
		gl.TRIANGLES,
		0,
		i32(len(model.mesh.vertices)),
		i32(len(transformations)),
	)
}

generate_mesh_from_heightmap :: proc(base: Image, x_scale, y_scale, z_scale: f32) -> Mesh {
	emit_vertex :: proc(vertices: ^[dynamic]Vertex, pos: [3]f32, normal: [3]f32, uv: [2]f32) {
		append(vertices, Vertex{position = pos, uvs = uv, normal = normal})
	}

	vertices_raw := make([]Vertex, base.width * base.height * 6)
	vertices := slice.into_dynamic(vertices_raw)
	for x in 0 ..< f32(base.width) - 1 {
		for z in 0 ..< f32(base.height) - 1 {
			a_index := int(x + z * f32(base.width))
			b_index := int((x + 1) + z * f32(base.width))
			c_index := int(x + (z + 1) * f32(base.width))
			d_index := int((x + 1) + (z + 1) * f32(base.width))
			a := base.pixels[a_index].r
			b := base.pixels[b_index].r
			c := base.pixels[c_index].r
			d := base.pixels[d_index].r

			x1 := x * (x_scale / f32(base.width))
			x2 := (x + 1) * (x_scale / f32(base.width))
			z1 := z * (z_scale / f32(base.height))
			z2 := (z + 1) * (z_scale / f32(base.height))

			p1 := [3]f32{x2, b * y_scale, z1}
			p2 := [3]f32{x1, a * y_scale, z1}
			p3 := [3]f32{x1, c * y_scale, z2}
			p4 := [3]f32{x2, d * y_scale, z2}

			normal_1 := linalg.normalize(linalg.cross(p2 - p1, p3 - p1))
			normal_2 := normal_1 //linalg.normalize(linalg.cross(p1 - p4, p3 - p4))

			uv1 := [2]f32{(x + 1) / f32(base.width), z / f32(base.height)}
			uv2 := [2]f32{x / f32(base.width), z / f32(base.height)}
			uv3 := [2]f32{x / f32(base.width), (z + 1) / f32(base.height)}
			uv4 := [2]f32{(x + 1) / f32(base.width), (z + 1) / f32(base.height)}

			emit_vertex(&vertices, p1, normal_1, uv1)
			emit_vertex(&vertices, p2, normal_1, uv2)
			emit_vertex(&vertices, p3, normal_1, uv3)

			emit_vertex(&vertices, p4, normal_2, uv4)
			emit_vertex(&vertices, p1, normal_2, uv1)
			emit_vertex(&vertices, p3, normal_2, uv3)
		}
	}

	return {vertices[:]}
}
