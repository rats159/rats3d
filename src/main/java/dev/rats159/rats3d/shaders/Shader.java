package dev.rats159.rats3d.shaders;

import org.joml.Matrix4f;
import dev.rats159.rats3d.util.math.Vector2f;
import dev.rats159.rats3d.util.math.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;

public abstract class Shader {
   private final int programID;
   private final int vertexID;
   private final int fragmentID;

   // Optimization: Store a buffer as a field to save on recreating it every time we load a matrix into a shader
   private static final FloatBuffer matrix4fBuffer = BufferUtils.createFloatBuffer(16);

   public Shader(String vertexPath, String fragmentPath){
      this.vertexID = loadShader(vertexPath, GL_VERTEX_SHADER);
      this.fragmentID = loadShader(fragmentPath, GL_FRAGMENT_SHADER);

      this.programID = glCreateProgram();
      glAttachShader(this.programID,this.vertexID);
      glAttachShader(this.programID,this.fragmentID);
      this.bindAttributes();
      glLinkProgram(this.programID);
      glValidateProgram(this.programID);
   }

   public void start(){
      glUseProgram(this.programID);
   }

   public void stop(){
      glUseProgram(0);
   }

   public void destroy(){
      this.stop();
      glDetachShader(this.programID,this.vertexID);
      glDetachShader(this.programID,this.fragmentID);

      glDeleteShader(this.vertexID);
      glDeleteShader(this.fragmentID);

      glDeleteProgram(this.programID);
   }

   protected abstract void bindAttributes();

   protected void bindAttribute(int attribute, String name){
      glBindAttribLocation(this.programID,attribute,name);
   }

   protected void loadFloat(String name, float value){
      glUniform1f(getUniformLocation(name),value);
   }

   protected void loadInt(String name, int value){
      glUniform1i(getUniformLocation(name),value);
   }

   protected void loadMatrix4f(String name, Matrix4f matrix4f){
      matrix4f.get(matrix4fBuffer);
      glUniformMatrix4fv(getUniformLocation(name),false,matrix4fBuffer);
   }

   protected void loadBoolean(String name, boolean value){
      glUniform1f(getUniformLocation(name), value? 1 : 0);
   }

   protected void loadVector3f(String name, Vector3f value){
      glUniform3f(getUniformLocation(name),value.x(),value.y(),value.z());
   }

   protected void loadVector2f(String name, Vector2f value){
      glUniform2f(getUniformLocation(name),value.x(),value.y());
   }

   protected int getUniformLocation(String name){
      return glGetUniformLocation(this.programID, name);
   }

   private static int loadShader(String path, int type){
      String source = "";
      try{
         source = new String(Files.readAllBytes(Paths.get(path)));
      }catch(IOException e){
         System.err.printf("Could not open file for shader %s%n", path);
         System.exit(-1);
      }

      int shaderID = glCreateShader(type);
      glShaderSource(shaderID,source);
      glCompileShader(shaderID);
      if(glGetShaderi(shaderID,GL_COMPILE_STATUS) == GL_FALSE){
         int len = glGetShaderi(shaderID,GL_INFO_LOG_LENGTH);
         System.err.printf("Shader `%s` compilation failed\n", path);
         System.err.println(glGetShaderInfoLog(shaderID,len));
         System.exit(-1);
      }

      return shaderID;
   }

   public abstract void enableAttributes();
}
