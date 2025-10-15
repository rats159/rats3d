package rats3d

import "base:runtime"
import "core:fmt"
import "core:math/linalg"
import "core:math/linalg/glsl"
import "core:reflect"
import "core:slice"
import gl "vendor:OpenGL"

Pos_Uv_Normal_Vertex :: struct {
	position: glsl.vec3,
	normal:   glsl.vec3,
	uvs:      glsl.vec2,
}

Mesh :: struct($Vertex_Type: typeid = typeid_of(Pos_Uv_Normal_Vertex)) {
	vertices: []Vertex_Type,
}

Model :: struct($Vertex_Type: typeid = typeid_of(Pos_Uv_Normal_Vertex)) {
	mesh:            Mesh(Vertex_Type),
	transformation:  glsl.mat4,
	shader:          Shader,
	texture:         Texture,
	_vao:            u32,
	_vbos:           []u32,
	_instance_count: int,
}

Vertex_Format :: [][]struct {
	type:    typeid,
	divisor: u32,
}


default_vertex_format := Vertex_Format {
	{{[3]f32, 0}, {[3]f32, 0}, {[2]f32, 0}},
	{{[4]f32, 1}, {[4]f32, 1}, {[4]f32, 1}, {[4]f32, 1}},
}

component_type_count :: proc(t: typeid) -> (typeid, int) {
	info := type_info_of(t)
	#partial switch variant in info.variant {
	case runtime.Type_Info_Named:
		return component_type_count(variant.base.id)
	case runtime.Type_Info_Array:
		return variant.elem.id, variant.count
	case runtime.Type_Info_Float, runtime.Type_Info_Integer:
		return t, 1
	}
	fmt.panicf("Unsupported VBO component type %v", t)
}

type_to_gl_type :: proc(t: typeid) -> u32 {
	switch t {
	case f32:
		return gl.FLOAT
	}

	fmt.panicf("Unsupported GL type %v", t)
}

load_vertex_attributes :: proc(vertex_format: Vertex_Format) -> []u32 {
	vbos := make([]u32, len(vertex_format))
	gl.GenBuffers(i32(len(vbos)), raw_data(vbos))

	total_index: u32 = 0

	for format, i in vertex_format {
		gl.BindBuffer(gl.ARRAY_BUFFER, vbos[i])

		total_stride: i32 = 0

		for attribute, j in format {
			gl.EnableVertexAttribArray(u32(j))
			comp_type, comp_count := component_type_count(attribute.type)
			total_stride += i32(reflect.size_of_typeid(comp_type) * comp_count)
		}

		current_offset: uintptr = 0

		for attribute, j in format {
			comp_type, comp_count := component_type_count(attribute.type)
			gl.VertexAttribPointer(
				total_index,
				i32(comp_count),
				type_to_gl_type(comp_type),
				false,
				total_stride,
				current_offset,
			)

			gl.VertexAttribDivisor(total_index, attribute.divisor)

			total_index += 1
			current_offset += uintptr(reflect.size_of_typeid(comp_type) * comp_count)
		}
	}

	return vbos
}

load_model_from_mesh :: proc(mesh: Mesh($V), vertex_format := default_vertex_format) -> Model(V) {
	vao: u32
	gl.GenVertexArrays(1, &vao)
	gl.BindVertexArray(vao)

	vbos := load_vertex_attributes(vertex_format)
	gl.BindBuffer(gl.ARRAY_BUFFER, vbos[0])
	gl.BufferData(
		gl.ARRAY_BUFFER,
		size_of(V) * len(mesh.vertices),
		raw_data(mesh.vertices),
		gl.STATIC_DRAW,
	)

	return Model(V){mesh = mesh, _vao = vao, _vbos = vbos, transformation = 1}
}

draw_model :: proc(model: Model($_Format)) {
	gl.BindVertexArray(model._vao)
	gl.BindTexture(gl.TEXTURE_2D, model.texture.id)
	use_shader(model.shader)
	upload(model.shader, model.transformation, DEFAULT_UNIFORM_NAME_MODEL_MATRIX)
	gl.DrawArrays(gl.TRIANGLES, 0, i32(len(model.mesh.vertices)))
}

draw_model_instanced :: proc(model: ^Model, transformations: []glsl.mat4, instance_vbo_id: u32) {
	if len(transformations) > model._instance_count {
		new_capacity := max(len(transformations), model._instance_count * 2)
		gl.BindBuffer(gl.ARRAY_BUFFER, instance_vbo_id)
		gl.BufferData(gl.ARRAY_BUFFER, new_capacity * size_of(glsl.mat4), nil, gl.STREAM_DRAW)
		model._instance_count = new_capacity
	}

	gl.BindBuffer(gl.ARRAY_BUFFER, instance_vbo_id)
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

generate_mesh_from_heightmap :: proc(base: Image, x_scale, y_scale, z_scale: f32) -> Mesh(Pos_Uv_Normal_Vertex) {
	emit_vertex :: proc(vertices: ^[dynamic]Pos_Uv_Normal_Vertex, pos: [3]f32, normal: [3]f32, uv: [2]f32) {
		append(vertices, Pos_Uv_Normal_Vertex{position = pos, uvs = uv, normal = normal})
	}

	vertices_raw := make([]Pos_Uv_Normal_Vertex, base.width * base.height * 6)
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
