package mg.rivolink.app.sudokusolver.core.tess;

import android.app.Activity;

import android.content.res.AssetManager;

import java.io.File;

import java.io.InputStream;
import java.io.FileInputStream;

import java.io.OutputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.FileNotFoundException;

public class TessBaseAPI extends com.googlecode.tesseract.android.TessBaseAPI implements DigitRecognizer{

	private final Activity activity;
	
	public TessBaseAPI(Activity acitvity){
		super();
		this.activity=acitvity;
	}

	@Override
	public boolean init(){
		final String datapath=activity.getFilesDir()+"/tesseract/";
		this.checkFile(new File(datapath+"tessdata/"),datapath);
		return super.init(datapath,"eng");
	}

	@Override
	public void free(){
		this.end();
	}
	
	@Override
	public int getUTF8Digit(){
		String sval=getUTF8Text().replaceAll("[^0-9]","");
		int digit=0;
		try{
			digit=Integer.parseInt(sval);
		}
		catch(NumberFormatException nfe){
		}
		return digit;
	}

	private void checkFile(File dir,String datapath){
        if(!dir.exists()&&dir.mkdirs()){
			copyFiles(datapath);
        }
        if(dir.exists()){
            String datafilepath=datapath+"/tessdata/eng.traineddata";
            File datafile=new File(datafilepath);

            if(!datafile.exists()){
                copyFiles(datapath);
            }
        }
    }

    private void copyFiles(String datapath){
        try{
            String filepath=datapath+"/tessdata/eng.traineddata";
            AssetManager assetManager=activity.getAssets();

            InputStream instream=assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream=new FileOutputStream(filepath);

            byte[] buffer=new byte[1024];
            int read;
            while((read=instream.read(buffer))!=-1){
                outstream.write(buffer,0,read);
            }

            outstream.flush();
            outstream.close();
            instream.close();

            File file=new File(filepath);
            if(!file.exists()){
                throw new FileNotFoundException();
            }
        }
		catch(FileNotFoundException e){
            e.printStackTrace();
        }
		catch(IOException e){
            e.printStackTrace();
        }
    }

}

