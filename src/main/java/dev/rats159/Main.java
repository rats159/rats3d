package dev.rats159;

import dev.rats159.rats3d.assets.OBJLoader;
import dev.rats159.rats3d.entities.*;
import dev.rats159.rats3d.input.MouseListener;
import dev.rats159.rats3d.models.Model;
import dev.rats159.rats3d.models.TexturedModel;
import dev.rats159.rats3d.particle.ParticleMaster;
import dev.rats159.rats3d.particle.ParticleSystem;
import dev.rats159.rats3d.particle.ParticleTexture;
import dev.rats159.rats3d.renderer.Loader;
import dev.rats159.rats3d.renderer.RenderController;
import dev.rats159.rats3d.renderer.Window;
import dev.rats159.rats3d.terrain.Terrain;
import dev.rats159.rats3d.terrain.TerrainMultiTexture;
import dev.rats159.rats3d.terrain.TerrainTexture;
import dev.rats159.rats3d.textures.ModelTexture;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Main {
   public static void main(String[] args) {
      Window.create("Game Window Test");
      Loader loader = new Loader();

      RenderController renderController = new RenderController();
      ParticleMaster.init(loader,renderController.getProjectionMatrix());

      Model model = loader.loadModel(OBJLoader.loadOBJ("player"));
      ModelTexture texture = new ModelTexture(loader.loadTexture("player"));

      TerrainTexture base = new TerrainTexture(loader.loadTexture("terrain_1"));
      TerrainTexture r = new TerrainTexture(loader.loadTexture("terrain_2"));
      TerrainTexture g = new TerrainTexture(loader.loadTexture("terrain_3"));
      TerrainTexture b = new TerrainTexture(loader.loadTexture("terrain_4"));

      TerrainMultiTexture multiTexture = new TerrainMultiTexture(base,r,g,b);

      TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("terrain_blend"));


      Terrain terrain = new Terrain(0,0,loader,multiTexture,blendMap,"terrain_height");

      TexturedModel playerModel = new TexturedModel(model, texture);
      Player player = new Player(playerModel,new Vector3f(4,0,0));
      Entity reference1 = new Entity(playerModel, new Vector3f(0,0,0), new Vector3f(0,0,0), 1);
      Entity reference2 = new Entity(playerModel, new Vector3f(2,0,0), new Vector3f(0,0,0), 1);
      Camera camera = new Camera(player);

      ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"),4);

      ParticleSystem particleSystem = new ParticleSystem(particleTexture);

      List<Tower> towers = new ArrayList<>();

      Light[] lights = new Light[5000+1];
      // Sun
      lights[0] = new Light(new Vector3f(400, 1000, 400), new Vector3f(1));

      for(int i = 0; i < 5000; i++){
         float x = (float) (Math.random() * 800);
         float z = (float) (Math.random() * 800);

         Tower tower = new Tower(playerModel,new Vector3f(x,terrain.getHeight(x,z),z));
         towers.add(tower);
         lights[i+1] = tower.getLight();
      }

      while(!Window.shouldClose()){
         MouseListener.endFrame();
         Window.tick();
         ParticleMaster.update(camera);



         player.move(terrain);
         camera.tick();
         renderController.processTerrain(terrain);

         renderController.processEntity(player);
         renderController.processEntity(reference1);
         renderController.processEntity(reference2);

         for(Tower t : towers){
            t.tick(terrain);
            renderController.processEntity(t);
            particleSystem.generateParticles(new Vector3f(t.getPosition()),camera);
         }

         renderController.render(lights,camera);

         ParticleMaster.renderParticles(camera);
      }

      ParticleMaster.destroy();
      renderController.destroy();
      loader.destroy();
      Window.destroy();
   }
}