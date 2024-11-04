package dev.rats159.rats3d.postprocessing;

import dev.rats159.rats3d.assets.Texture;
import dev.rats159.rats3d.models.Model;
import dev.rats159.rats3d.models.QuadModelData;
import dev.rats159.rats3d.renderer.Loader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class PostProcessing {
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static Model quad;

	private static ContrastChanger contrastChanger;

	public static void init(){
		quad = Loader.loadModel(new QuadModelData(POSITIONS));
		contrastChanger = new ContrastChanger();
	}
	
	public static void doPostProcessing(Texture texture){
		start();
		contrastChanger.render(texture);
		end();
	}
	
	public static void destroy(){
		contrastChanger.destroy();
	}
	
	private static void start(){
		glBindVertexArray(quad.vaoID());
		glEnableVertexAttribArray(0);
		glDisable(GL_DEPTH_TEST);
	}
	
	private static void end(){
		glEnable(GL_DEPTH_TEST);
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
	}


}
