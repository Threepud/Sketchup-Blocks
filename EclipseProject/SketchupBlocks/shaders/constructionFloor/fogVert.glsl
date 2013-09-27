uniform mat4 transform;
uniform mat3 normalMatrix;
uniform vec3 lightNormal;

attribute vec4 vertex;
attribute vec4 color;
attribute vec3 normal;

varying vec4 pos;
varying vec4 vertColor;

void main() {
  gl_Position = transform * vertex;  
  pos = vertex;
  vertColor = color;
}