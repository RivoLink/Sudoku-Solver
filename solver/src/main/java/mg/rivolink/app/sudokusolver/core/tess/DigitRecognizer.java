package mg.rivolink.app.sudokusolver.core.tess;

import android.graphics.Bitmap;

public interface DigitRecognizer{
	
	public boolean init();

	public void setImage(Bitmap image);
	
	public void clear();
	
	public int getUTF8Digit();
	
	public void free();
	
}
