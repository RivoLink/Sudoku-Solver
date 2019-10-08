package mg.rivolink.app.sudokusolver.core;

import static mg.rivolink.app.sudokusolver.core.utils.CvUtils.*;
import mg.rivolink.app.sudokusolver.core.sudoku.SudokuSolver;
import mg.rivolink.app.sudokusolver.core.tess.DigitRecognizer;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Core;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.CvType;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;

import org.opencv.android.Utils;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;

import java.util.List;
import java.util.ArrayList;

public class SolverCore{
	
	private Mat gray;
	
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
	
	private SudokuSolver solver;
	private DigitRecognizer recognizer;
	
	public SolverCore(SudokuSolver solver,DigitRecognizer recognizer){
		this.solver=solver;
		this.recognizer=recognizer;
	}
		
	public void init(){
		gray=new Mat();
		contours=new ArrayList<MatOfPoint>();
		
		recognizer.init();
	}
	
	public void free(){
		recognizer.clear();
		solver.free(mask);
	}
	
	public boolean execute(Mat original){
		
		Imgproc.cvtColor(original,gray,Imgproc.COLOR_RGBA2GRAY);
		Imgproc.GaussianBlur(gray,gray,new Size(11,11),0);
		Imgproc.adaptiveThreshold(gray,gray,255,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY_INV,5,2);

		Imgproc.findContours(gray.clone(),contours,new Mat(),Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);

		if(contours.size()==0)
			return false;

		Point[] src=maxAreaContour(contours).toArray();
		
		if(sort4(src)/* && area4(src,original.width()/2)*/){

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

						recognizer.setImage(bmp);
						sudoku[r][c]=recognizer.getUTF8Digit();
						mask[r][c]=0<sudoku[r][c];
						recognizer.clear();
					}
				}
			}

			Mat alpha=new Mat(length,length,CvType.CV_8UC3,new Scalar(0,0,0));

			solver.init(sudoku);
			if(solver.solve()){
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
				
				gray.release();
				alpha.release();
				
				return true;
			}
			alpha.release();
		}
		gray.release();
		
		return false;
	}
	
	public Bitmap execute(Bitmap image){
		Mat rgba=new Mat();
		Utils.bitmapToMat(image,rgba);
		
		if(!execute(rgba))
			return null;
		
		Bitmap bmp=Bitmap.createBitmap(rgba.width(),rgba.height(),Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(rgba,bmp);
		
		rgba.release();
		
		return bmp;
	}
	
}
