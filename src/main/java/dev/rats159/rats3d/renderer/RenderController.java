package dev.rats159.rats3d.renderer;

import dev.rats159.rats3d.entities.Camera;
import dev.rats159.rats3d.entities.Entity;
import dev.rats159.rats3d.entities.Light;
import dev.rats159.rats3d.models.TexturedModel;
import dev.rats159.rats3d.shaders.StaticShader;
import dev.rats159.rats3d.shaders.TerrainShader;
import dev.rats159.rats3d.terrain.Terrain;
import org.joml.Matrix4f;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class RenderController {
   private static final float FOV = 80;
   private static final float NEAR_PLANE = 0.1f;
   private static final float FAR_PLANE = 2000f;

   public static final float SKY_RED = .572f;
   public static final float SKY_GREEN = .78f;
   public static final float SKY_BLUE = .988f;

   private Matrix4f projection;

   private final StaticShader staticShader = new StaticShader();
   private final EntityRenderer entityRenderer;
   private final TerrainShader terrainShader = new TerrainShader();
   private final TerrainRenderer terrainRenderer;

   private final Map<TexturedModel, List<Entity>> entities = new HashMap<>();
   private final List<Terrain> terrains = new ArrayList<>();

   public RenderController(){
      createProjectionMatrix();
      this.entityRenderer = new EntityRenderer(staticShader,projection);
      glEnable(GL_CULL_FACE);
      glCullFace(GL_BACK);

      this.terrainRenderer = new TerrainRenderer(terrainShader,projection);
   }

   public void render(Light[] lights, Camera camera){
      List<Light> lightsList = Arrays.asList(lights);
      Collections.shuffle(lightsList);
      lights = lightsList.toArray(Light[]::new);
      prepare();
      staticShader.start();
      staticShader.loadLights(lights);
      staticShader.loadViewMatrix(camera);
      staticShader.loadSkyColor(SKY_RED,SKY_GREEN,SKY_BLUE);
      entityRenderer.render(entities);
      staticShader.stop();

      terrainShader.start();
      terrainShader.loadLights(lights);
      terrainShader.loadViewMatrix(camera);
      terrainShader.loadSkyColor(SKY_RED,SKY_GREEN,SKY_BLUE);
      terrainRenderer.render(terrains);
      terrainShader.stop();

      terrains.clear();
      entities.clear();
   }

   public void processTerrain(Terrain terrain){
      this.terrains.add(terrain);
   }

   public void prepare(){
      glEnable(GL_DEPTH_TEST);
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
      glClearColor(SKY_RED,SKY_GREEN,SKY_BLUE,1);
   }

   private void createProjectionMatrix(){
      float aspectRatio = Window.getWidth() / (float) Window.getHeight();
      float yScale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
      float xScale = yScale / aspectRatio;
      float frustumLength = FAR_PLANE - NEAR_PLANE;

      projection = new Matrix4f()
        .m00(xScale)
        .m11(yScale)
        .m22(-((FAR_PLANE + NEAR_PLANE) / frustumLength))
        .m23(-1)
        .m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustumLength))
        .m33(0);
   }

   public void processEntity(Entity entity){
      TexturedModel model = entity.getModel();
      List<Entity> batch = entities.get(model);

      if(batch != null){
         batch.add(entity);
      }else {
         List<Entity> newBatch = new ArrayList<>();
         newBatch.add(entity);
         entities.put(model,newBatch);
      }
   }

   public void destroy(){
      staticShader.destroy();
      terrainShader.destroy();
   }

   public Matrix4f getProjectionMatrix() {
      return projection;
   }
}
