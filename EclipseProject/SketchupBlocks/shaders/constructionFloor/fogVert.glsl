#define PROCESSING_TEXLIGHT_SHADER

uniform mat4 world;
uniform mat4 transform;
uniform mat4 texMatrix;

attribute vec4 vertex;
attribute vec4 colour;
attribute vec2 texCoord;

varying vec4 pos;
varying vec4 vertColor;
varying vec4 vertTexCoord;

void main()
{
	gl_Position = transform * vertex;
    
	pos = vertex;
	vertColor = colour;
	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
}