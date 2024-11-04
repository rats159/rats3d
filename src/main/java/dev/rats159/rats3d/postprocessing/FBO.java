
package dev.rats159.rats3d.postprocessing;

import dev.rats159.rats3d.renderer.Window;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.*;

public class FBO {

	public static final int NONE = 0;
	public static final int DEPTH_TEXTURE = 1;
	public static final int DEPTH_RENDER_BUFFER = 2;

	private final int width;
	private final int height;

	private int frameBuffer;

	private int colorTexture;
	private int depthTexture;

	private int depthBuffer;
	private int colorBuffer;

	public FBO(int width, int height, int depthBufferType) {
		this.width = width;
		this.height = height;
		initializeFrameBuffer(depthBufferType);
	}

	public void destroy() {
		glDeleteFramebuffers(frameBuffer);
		glDeleteTextures(colorTexture);
		glDeleteTextures(depthTexture);
		glDeleteRenderbuffers(depthBuffer);
		glDeleteRenderbuffers(colorBuffer);
	}

	public void bindFrameBuffer() {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBuffer);
		glViewport(0, 0, width, height);
	}

	public void unbindFrameBuffer() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, Window.getWidth(), Window.getHeight());
	}

	public void bindToRead() {
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBuffer);
		glReadBuffer(GL_COLOR_ATTACHMENT0);
	}

	public int getColorTexture() {
		return colorTexture;
	}

	public int getDepthTexture() {
		return depthTexture;
	}

	private void initializeFrameBuffer(int type) {
		createFrameBuffer();
		createTextureAttachment();
		if (type == DEPTH_RENDER_BUFFER) {
			createDepthBufferAttachment();
		} else if (type == DEPTH_TEXTURE) {
			createDepthTextureAttachment();
		}
		unbindFrameBuffer();
	}

	private void createFrameBuffer() {
		frameBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
		glDrawBuffer(GL_COLOR_ATTACHMENT0);
	}

	private void createTextureAttachment() {
		colorTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, colorTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE,
				(ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture2D(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTexture, 0);
	}

	private void createDepthTextureAttachment() {
		depthTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, depthTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, width, height, 0, GL_DEPTH_COMPONENT,
				GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glFramebufferTexture2D(GL_FRAMEBUFFER,GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);
	}

	private void createDepthBufferAttachment() {
		depthBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER,GL_DEPTH_ATTACHMENT,GL_RENDERBUFFER, depthBuffer);
	}

}
