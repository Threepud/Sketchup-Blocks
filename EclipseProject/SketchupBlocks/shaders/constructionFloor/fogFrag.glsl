#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;

uniform int trans;

varying vec4 pos;
varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() 
{
	vec4 color = texture2D(texture, vertTexCoord.xy);
	
	if(trans == 0)
	{
		color.w = 1.0f;
		
		float dist = sqrt(pos.x * pos.x + pos.y * pos.y + pos.z * pos.z);
		
		if(dist > 5000.0 && dist < 8000.0)
		{
			color.w = 1 - ((dist - 5000.0) / 15000.0);
		}
		else if(dist >= 8000.0)
		{
			color.w = 1 - ((8000 - 5000.0) / 15000.0) - pow((dist - 8000.0) / 1000.0, 2);
		}
		
		if(color.w < 0.0)
			color.w = 0.0;
		else if(color.w > 1.0)
			color.w = 1.0;
	}
	else
	{
		color.w = 0.5;
	}
	
	gl_FragColor = color;
}