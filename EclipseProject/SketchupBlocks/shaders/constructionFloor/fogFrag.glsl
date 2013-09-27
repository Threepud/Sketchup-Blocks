#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_LIGHT_SHADER

varying vec4 pos;
varying vec4 vertColor;

void main() {  
	vec4 top = vec4(0.2156862745098039, 0.4901960784313725, 0.7647058823529412, 1.0);
	vec4 bot = vec4(0.3882352941176471, 0.6666666666666667, 0.9176470588235294, 1.0);
	
	float max = 4000.0;
	float min = 400.0;
	
	vec4 color;
	if(-pos.y > max)
		color = top;
	else if(-pos.y < min)
		color = bot;
	else if(-pos.y >= min && -pos.y <= max)
	{
		float ratio = -pos.y / (max - min);
	
		color = ((bot * ratio) + (top * (1 - ratio))) / 2.0;
		color.w = 1.0;
	}
	
  gl_FragColor = color;  
}