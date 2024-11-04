package dev.rats159.rats3d.postprocessing;

import static org.lwjgl.opengl.GL11.*;

public class ImageRenderer {

	private FBO fbo;

	protected ImageRenderer(int width, int height) {
		this.fbo = new FBO(width, height, FBO.NONE);
	}

	protected ImageRenderer() {}

	protected void renderQuad() {
		if (fbo != null) {
			fbo.bindFrameBuffer();
		}
		glClear(GL_COLOR_BUFFER_BIT);
		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
		if (fbo != null) {
			fbo.unbindFrameBuffer();
		}
	}

	protected int getOutputTexture() {
		return fbo.getColorTexture();
	}

	protected void destroy() {
		if (fbo != null) {
			fbo.destroy();
		}
	}

}
