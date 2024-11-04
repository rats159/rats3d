package dev.rats159;

import dev.rats159.rats3d.Rats3d;
import dev.rats159.rats3d.entities.Camera;
import dev.rats159.rats3d.entities.Entity;
import dev.rats159.rats3d.entities.Light;
import dev.rats159.rats3d.entities.Player;
import dev.rats159.rats3d.models.TexturedModel;
import dev.rats159.rats3d.particle.ParticleMaster;
import dev.rats159.rats3d.renderer.Loader;
import dev.rats159.rats3d.renderer.RenderController;
import dev.rats159.rats3d.renderer.Window;
import dev.rats159.rats3d.terrain.Chunk;
import dev.rats159.rats3d.textures.ModelTexture;
import dev.rats159.rats3d.util.math.Vector3f;


public class Main {
   public static void main(String[] args) {
      Window.create("Game Window Test");
      RenderController renderController = new RenderController();
      ParticleMaster.init(renderController.getProjectionMatrix());

      loadAssets();

      ModelTexture playerTexture = new ModelTexture(Loader.getTexture("player"));

      Chunk chunk = new Chunk(0, 0, Loader.getTexture("terrain_1"));

      TexturedModel playerModel = new TexturedModel(Loader.getModel("player"), playerTexture);
      Player player = new Player(playerModel, new Vector3f(400,0,400));
      Entity lightModel = new Entity(playerModel,new Vector3f(0,0,0), new Vector3f(0,0,0), 8);
      Camera camera = new Camera(player);

      Light[] lights = {
        new Light(new Vector3f(400,20,400),new Vector3f(1))
      };


      while (!Window.shouldClose()) {
         Rats3d.step();
         ParticleMaster.update(camera);

         player.move(chunk);
         camera.tick();

         renderController.processTerrain(chunk);
         renderController.processEntity(player);
         renderController.processEntity(lightModel);
         renderController.render(lights, camera);

         ParticleMaster.renderParticles(camera);
      }

      ParticleMaster.destroy();
      renderController.destroy();
      Loader.destroy();
      Window.destroy();
   }

   private static void loadAssets(){
      Loader.loadModel("player");
      Loader.loadTexture("player");

      Loader.loadTexture("terrain_1");

      Loader.loadTextureAtlas("particleAtlas",4);
      Loader.loadTextureAtlas("light",1);
   }
}