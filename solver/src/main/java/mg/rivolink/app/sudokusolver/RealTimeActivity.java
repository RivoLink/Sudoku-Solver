package mg.rivolink.app.sudokusolver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.ArrayList;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import mg.rivolink.app.sudokusolver.core.sudoku.SudokuSolver;
import mg.rivolink.app.sudokusolver.core.tess.TessBaseAPI;
import mg.rivolink.app.sudokusolver.views.sudoku.SudokuGridView;
import mg.rivolink.app.sudokusolver.core.sudoku.bfb.SolverBFB;
import mg.rivolink.app.sudokusolver.views.camera.LandscapeCameraLayout;

import static mg.rivolink.app.sudokusolver.core.utils.CvUtils.*;

public class RealTimeActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
	
	private Mat gray;
	private Mat original;
	
	//private Rect rect;
	//private Point[] pts;
	
	private TessBaseAPI tessAPI;
	private SudokuSolver solverBFB;
	//private SudokuSolver solverDLX;
	
	private List<MatOfPoint> contours;

	private static final int length=450;
	private static final Point dst[]={
		new Point(0,0),
		new Point(length,0),
		new Point(length,length),
		new Point(0,length)
	};
	
	private int sudoku[][]=new int[9][9];
	private boolean mask[][]=new boolean[9][9];

	private SudokuGridView grid;
	private CameraBridgeViewBase camera;
	
	private BaseLoaderCallback loader=new BaseLoaderCallback(this){
		@Override
		public void onManagerConnected(final int status){
			switch(status){
				case LoaderCallbackInterface.SUCCESS:{
					Toast.makeText(RealTimeActivity.this,"OpenCV loaded successfully",Toast.LENGTH_SHORT).show();
					camera.enableView();
					listener.start();
					break;
				}
				default:{
					super.onManagerConnected(status);
					break;
				}
			}     
		}
	};
	
	private Thread listener=new Thread(){
		private Bitmap sudokuBMP;
		private boolean changed=true;
		
		@Override
		public void run(){
			sudokuBMP=BitmapFactory.decodeResource(getResources(),R.drawable.sudoku_grid_empty);
			while(true){
				try{
					sleep(500);
					runOnParallel();
					if(changed){
						runOnUiThread(new Runnable(){
							@Override
							public void run(){
								runOnMain();
							}
						});
					}
				}
				catch(InterruptedException e){
				}
			}
		}
		
		void runOnParallel(){
			/*
			if(changed&&false){
				
				
				
				Mat rgba=new Mat();
				Utils.bitmapToMat(sudokuBMP,rgba);
				
				//Mat cell=rgba.submat(new Rect(0,0,50,50));
				
				
				String text="8";//+sudoku[row][col];
				
				int col=0,row=0;
				int bl[]=new int[1];
				int font=Core.FONT_HERSHEY_DUPLEX;
				Size size=Imgproc.getTextSize(text,font,1,3,bl);
				Point point=new Point(50*col+(50-size.width)/2,50*row+(50+size.height)/2);
				
				
				Imgproc.putText(rgba,text,point,font,1,new Scalar(255,127,0),3);
				
				sudokuBMP=Bitmap.createBitmap(rgba.width(),rgba.height(),Bitmap.Config.ARGB_8888);
				Utils.matToBitmap(rgba,sudokuBMP);
			}
			
			
			
			*/
			//if(mask[0][0]){
				
			//}
		}
		
		void runOnMain(){
			changed=false;
			grid.setBackgroundDrawable(new BitmapDrawable(sudokuBMP));
			Toast.makeText(RealTimeActivity.this,""+sudokuBMP.getWidth(),Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_real_time);
		
		int uiOptions=getWindow().getDecorView().getSystemUiVisibility();
		uiOptions|=View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		uiOptions|=View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		getWindow().getDecorView().setSystemUiVisibility(uiOptions);
		
		tessAPI=new TessBaseAPI(this);
		tessAPI.init();

		solverBFB=new SolverBFB();
		
		grid=(SudokuGridView)findViewById(R.id.real_time_sudoku_grid_view);
		grid.set(sudoku,mask);
		
		camera=((LandscapeCameraLayout)findViewById(R.id.real_time_camera_layout)).getCamera();
		camera.setCvCameraViewListener(this);
		
	}

	@Override
	protected void onResume(){
		super.onResume();
		
		if(OpenCVLoader.initDebug())
			loader.onManagerConnected(LoaderCallbackInterface.SUCCESS);
		else
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0,this,loader);
	}

	@Override
	protected void onPause(){
		camera.disableView();
        super.onPause();
    }

	@Override
	protected void onStop(){
		camera.disableView();
		tessAPI.end();
		super.onStop();
	}
	
	@Override
	public void onCameraViewStarted(int width,int height){
		
		gray=new Mat();
		contours=new ArrayList<>();
		
		Toast.makeText(this,"w: "+width+" h: "+height,Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onCameraViewStopped(){
		original.release();
		gray.release();
	}

	@Override
	public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
		original=inputFrame.rgba();
		
		Imgproc.cvtColor(original,gray,Imgproc.COLOR_RGBA2GRAY);
		Imgproc.GaussianBlur(gray,gray,new Size(11,11),0);
		Imgproc.adaptiveThreshold(gray,gray,255,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY_INV,5,2);

		contours.clear();
		Imgproc.findContours(gray.clone(),contours,new Mat(),Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
		
		if(contours.size()==0)
			return original;
		
		Point[] src=maxAreaContour(contours).toArray();
		
		if(sort4(src) && area4(src,original.width()/2)){

			Imgproc.warpPerspective(gray,gray,Imgproc.getPerspectiveTransform(new MatOfPoint2f(src),new MatOfPoint2f(dst)),new Size(length,length)); 

			int w=gray.width()/9;
			int h=gray.height()/9;

			for(int r=0;r<9;r++){
				for(int c=0;c<9;c++){

					Rect rect=new Rect(c*w,r*h,w,h);
					Mat cell=new Mat(gray,rect);

					if(containDigit(cell)){

						contours.clear();
						Imgproc.findContours(cell.clone(),contours,new Mat(),Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);

						rect=maxAreaRect(contours,2*rect.area()/3);

						Core.copyMakeBorder(new Mat(cell,rect),cell,10,10,10,10,Core.BORDER_ISOLATED);

						Bitmap bmp=Bitmap.createBitmap(cell.width(),cell.height(),Bitmap.Config.ARGB_4444);
						Utils.matToBitmap(cell,bmp);

						tessAPI.setImage(bmp);
						sudoku[r][c]=tessAPI.getUTF8Digit();
						mask[r][c]=0<sudoku[r][c];
						tessAPI.clear();
					}
				}
			}

			Mat alpha=new Mat(length,length,CvType.CV_8UC3,new Scalar(0,0,0));

			solverBFB.init(sudoku);
			if(solverBFB.solve()){
				
				for(int row=0;row<sudoku.length;row++){
					for(int col=0;col<sudoku[row].length;col++){
						if(mask[row][col]){
							mask[row][col]=false;
							continue;
						}

						String text=""+sudoku[row][col];

						int bl[]=new int[1];
						int font=Core.FONT_HERSHEY_DUPLEX;
						Size size=Imgproc.getTextSize(text,font,1,3,bl);
						Point point=new Point(50*col+(50-size.width)/2,50*row+(50+size.height)/2);
						Imgproc.putText(alpha,text,point,font,1,new Scalar(255,127,0),3);
					}
				}

				Imgproc.warpPerspective(alpha,alpha,Imgproc.getPerspectiveTransform(new MatOfPoint2f(dst),new MatOfPoint2f(src)),new Size(original.width(),original.height())); 

				Imgproc.cvtColor(original,original,Imgproc.COLOR_RGBA2RGB);
				//Core.bitwise_or(original,alpha,original);
				Core.bitwise_xor(original,alpha,original);
				
			}

		}
		
		return original;
	}

}
