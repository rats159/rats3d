package dev.rats159.rats3d.postprocessing;

import dev.rats159.rats3d.shaders.Shader;

import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

public class ContrastShader extends Shader {

	private static final String VERTEX_FILE = "res/shaders/contrastVertex.glsl";
	private static final String FRAGMENT_FILE = "res/shaders/contrastFragment.glsl";
	
	public ContrastShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	public void enableAttributes() {
		glEnableVertexAttribArray(0);
	}

}
