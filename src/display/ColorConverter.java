package display;

import java.awt.Color;
import com.jme3.math.ColorRGBA;

public class ColorConverter {
	public static ColorRGBA convertToColorRGBA(Color color) {
		float red = color.getRed()/255f;
		float green = color.getGreen()/255f;
		float blue = color.getBlue()/255f;
		float alpha = color.getAlpha()/255f;
		return new ColorRGBA(red,green,blue,alpha);
	}
	
	public static Color convertToColorAWT(ColorRGBA color) {
		int red = (int)(color.getRed() * 255);
		int green = (int)(color.getGreen() * 255);
		int blue = (int)(color.getBlue() * 255);
		int alpha = (int)(color.getAlpha() * 255);
		return new Color(red,green,blue,alpha);
	}
}
