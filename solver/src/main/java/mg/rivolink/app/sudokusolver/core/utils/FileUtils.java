package mg.rivolink.app.sudokusolver.core.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class FileUtils{
	
	public static final String APP_PATH=Environment.getExternalStorageDirectory().toString()+"/Solver/";
	
	public static boolean checkAppDir(){
		File appDir=new File(APP_PATH);
		return appDir.exists() || appDir.mkdir();
	}
	
	private static String getTime(){
		return new SimpleDateFormat("HH-mm-ss").format(new Date());
	}
	
	public static boolean saveToPng(Mat mat){
		return saveToPng(mat,getTime());
	}
	
	public static boolean saveToPng(Mat rgba,String name){
		if(!checkAppDir())
			return false;
		
		Bitmap bmp=Bitmap.createBitmap(rgba.width(),rgba.height(),Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(rgba,bmp);
			
		File file=new File(APP_PATH,"SudokuSolver-"+name+".png");
		
		try{
			FileOutputStream outputStream=new FileOutputStream(file);
			int quality=100;
			bmp.compress(Bitmap.CompressFormat.PNG,quality,outputStream);
			outputStream.flush();
			outputStream.close();
			return true;
		}
		catch(Throwable e){
			e.printStackTrace();
		}

		return false;
	}
	
}
