package rats3d

import gl "vendor:OpenGL"

Render_Target :: struct {
	_id:           u32,
	width, height: int,
	depth_tex:     Maybe(Texture),
	color_tex:     Maybe(Texture),
}

Render_Target_Textures :: enum {
	Color,
	Depth,
}
Render_Target_Texture_Options :: bit_set[Render_Target_Textures;u8]

create_render_target :: proc(
	width, height: int,
	texture_options: Render_Target_Texture_Options,
) -> Render_Target {
	fbo: u32
	gl.GenFramebuffers(1, &fbo)
	gl.BindFramebuffer(gl.FRAMEBUFFER, fbo)

	color_tex: Maybe(Texture)
	depth_tex: Maybe(Texture)

	if .Depth in texture_options {
		texture_depth_buffer := gen_texture_depth(width, height)
		gl.FramebufferTexture2D(
			gl.FRAMEBUFFER,
			gl.DEPTH_ATTACHMENT,
			gl.TEXTURE_2D,
			texture_depth_buffer.id,
			0,
		)
		depth_tex = texture_depth_buffer
	} else {
		rbo: u32
		gl.GenRenderbuffers(1, &rbo)

		gl.BindRenderbuffer(gl.RENDERBUFFER, rbo)

		gl.RenderbufferStorage(gl.RENDERBUFFER, gl.DEPTH24_STENCIL8, i32(width), i32(height))
		gl.FramebufferRenderbuffer(
			gl.FRAMEBUFFER,
			gl.DEPTH_STENCIL_ATTACHMENT,
			gl.RENDERBUFFER,
			rbo,
		)
	}

	if .Color in texture_options {
		texture_color_buffer := gen_texture_color_target(width, height)
		gl.FramebufferTexture2D(
			gl.FRAMEBUFFER,
			gl.COLOR_ATTACHMENT0,
			gl.TEXTURE_2D,
			texture_color_buffer.id,
			0,
		)
		color_tex = texture_color_buffer
	} else {
		gl.DrawBuffer(gl.NONE)
		gl.ReadBuffer(gl.NONE)
	}


	assert(gl.CheckFramebufferStatus(gl.FRAMEBUFFER) == gl.FRAMEBUFFER_COMPLETE)

	gl.BindFramebuffer(gl.FRAMEBUFFER, 0)

	return {
		_id = fbo,
		depth_tex = depth_tex,
		color_tex = color_tex,
		width = width,
		height = height,
	}

}

push_render_target :: proc(target: Render_Target) {
	append(&global_state.framebuffer_stack, target)

	gl.BindFramebuffer(gl.FRAMEBUFFER, target._id)
	gl.Viewport(0, 0, i32(target.width), i32(target.height))
}

pop_render_target :: proc() {
	pop(&global_state.framebuffer_stack)

	length := len(global_state.framebuffer_stack)

	if length == 0 {
		gl.BindFramebuffer(gl.FRAMEBUFFER, 0)
		gl.Viewport(0, 0, i32(screen_width()), i32(screen_height()))
	} else {
		target := global_state.framebuffer_stack[length - 1]
		gl.BindFramebuffer(gl.FRAMEBUFFER, target._id)
		gl.Viewport(0, 0, i32(target.width), i32(target.height))
	}

}

@(deferred_none = pop_render_target)
use_render_target :: proc(target: Render_Target) -> bool {
	push_render_target(target)
	return true
}
