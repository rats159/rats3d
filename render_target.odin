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

	texture_depth_buffer := gen_texture_depth(width, height)
	gl.FramebufferTexture2D(
		gl.FRAMEBUFFER,
		gl.DEPTH_ATTACHMENT,
		gl.TEXTURE_2D,
		texture_depth_buffer.id,
		0,
	)

	gl.DrawBuffer(gl.NONE)
	gl.ReadBuffer(gl.NONE)

	assert(gl.CheckFramebufferStatus(gl.FRAMEBUFFER) == gl.FRAMEBUFFER_COMPLETE)

	gl.BindFramebuffer(gl.FRAMEBUFFER, 0)

	return {
		_id = fbo,
		depth_tex = texture_depth_buffer,
		color_tex = nil,
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
