package dev.rats159;

import dev.rats159.rats3d.assets.Texture;
import dev.rats159.rats3d.entities.*;
import dev.rats159.rats3d.input.MouseListener;
import dev.rats159.rats3d.models.TexturedModel;
import dev.rats159.rats3d.particle.ParticleMaster;
import dev.rats159.rats3d.particle.ParticleSystem;
import dev.rats159.rats3d.particle.TextureAtlas;
import dev.rats159.rats3d.renderer.Loader;
import dev.rats159.rats3d.renderer.RenderController;
import dev.rats159.rats3d.renderer.Window;
import dev.rats159.rats3d.terrain.Terrain;
import dev.rats159.rats3d.terrain.TerrainMultiTexture;
import dev.rats159.rats3d.textures.ModelTexture;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Main {
   public static void main(String[] args) {
      Window.create("Game Window Test");
      RenderController renderController = new RenderController();
      ParticleMaster.init(renderController.getProjectionMatrix());

      Loader.loadModel("player");
      Loader.loadTexture("player");

      Loader.loadTexture("terrain_1");
      Loader.loadTexture("terrain_2");
      Loader.loadTexture("terrain_3");
      Loader.loadTexture("terrain_4");
      Loader.loadTexture("terrain_blend");

      Loader.loadTexture("particleAtlas");

      TerrainMultiTexture multiTexture = new TerrainMultiTexture("terrain_1", "terrain_2", "terrain_3", "terrain_4", "terrain_blend");

      ModelTexture playerTexture = new ModelTexture(Loader.getTexture("player"));

      Terrain terrain = new Terrain(0, 0, multiTexture, "terrain_height");

      TexturedModel playerModel = new TexturedModel(Loader.getModel("player"), playerTexture);
      Player player = new Player(playerModel, new Vector3f(4, 0, 0));
      Entity reference1 = new Entity(playerModel, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1);
      Entity reference2 = new Entity(playerModel, new Vector3f(2, 0, 0), new Vector3f(0, 0, 0), 1);
      Camera camera = new Camera(player);

      List<Tower> towers = new ArrayList<>();

      Light[] lights = {new Light(new Vector3f(400,1000,400),new Vector3f(1))};

      Texture texture = Loader.getTexture("particleAtlas");
      TextureAtlas textureAtlas = new TextureAtlas(texture,4);
      ParticleSystem system = new ParticleSystem(textureAtlas);

      for (int i = 0; i < 5000; i++) {
         float x = (float) (Math.random() * 800);
         float z = (float) (Math.random() * 800);

         Tower tower = new Tower(playerModel, new Vector3f(x, terrain.getHeight(x, z), z));
//         towers.add(tower);
      }

      while (!Window.shouldClose()) {
         MouseListener.endFrame();
         Window.tick();
         ParticleMaster.update(camera);


         player.move(terrain);
         camera.tick();
         renderController.processTerrain(terrain);

         renderController.processEntity(player);
         renderController.processEntity(reference1);
         renderController.processEntity(reference2);

         system.generateParticles(player.getPosition(),camera);

         for (Tower t : towers) {
            t.tick(terrain);
            renderController.processEntity(t);
         }

         renderController.render(lights, camera);

         ParticleMaster.renderParticles(camera);
      }

      ParticleMaster.destroy();
      renderController.destroy();
      Loader.destroy();
      Window.destroy();
   }
}