package mg.rivolink.app.sudokusolver.core.utils;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Point;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;

import org.opencv.imgproc.Imgproc;

import java.util.List;
import java.util.ArrayList;

import static java.lang.Math.*;

public class CvUtils{
	
	public static boolean containDigit(Mat gray){
		Mat dest=new Mat();
		Imgproc.threshold(gray,dest,200,255,Imgproc.THRESH_BINARY);

		int count=0;
		int three=dest.rows()/3;
		for(int r=three;r<2*three;r++){
			for(int c=three;c<2*three;c++){
				if(128<dest.get(r,c)[0])
					if(5<++count)
						return true;
			}
		}
		return 5<count;
	}
	
	public static boolean containDigit_optim(Mat gray){
		Mat dest=new Mat();
		Imgproc.threshold(gray,dest,200,255,Imgproc.THRESH_BINARY);
		
		int row=dest.rows()/2;
		int limit=dest.rows()/3;
		for(int c=0;c<limit;c++){
			if(dest.get(row,c)[0]==255){
				return true;
			}
		}
		return false;
	}

	public static MatOfPoint maxAreaContour(List<MatOfPoint> contours){
		int index=0;
		double maxarea=0;
		for(int i=0;i<contours.size();i++){
			double area=Imgproc.contourArea(contours.get(i),false);

			if(area>maxarea){
				maxarea=area;
				index=i;
			}
		}

		MatOfPoint2f pt2f=new MatOfPoint2f(contours.get(index).toArray());
		double perimeter=Imgproc.arcLength(pt2f,true);
		Imgproc.approxPolyDP(pt2f,pt2f,0.01*perimeter, true);

		return new MatOfPoint(pt2f.toArray());

	}

	public static Rect maxAreaRect(List<MatOfPoint> contours,double limite){
		Rect rect=null;

		double maxarea=0;
		for(int i=0;i<contours.size();i++){
			Rect r=Imgproc.boundingRect(contours.get(i));
			double area=r.area();

			if(maxarea<area && area<limite){
				maxarea=area;
				rect=r;
			}
		}

		return rect;
	}

	public static boolean sort4(Point[] pts){
		if(pts==null || pts.length!=4)
			return false;

		Point[] temp=new Point[4];
		for(int i=0;i<4;i++){
			temp[i]=pts[i];
		}

		double mx=max(pts[0].x+pts[1].x,pts[0].x+pts[2].x)/2;
		double my=max(pts[0].y+pts[1].y,pts[0].y+pts[2].y)/2;

		for(Point pt:temp){
			if(pt.x<mx){
				if(pt.y<my)
					pts[0]=pt;
				else
					pts[3]=pt;
			}
			else{
				if(pt.y<my)
					pts[1]=pt;
				else
					pts[2]=pt;
			}
		}

		//crop4(pts);

		return true;
	}

	public static boolean area4(Point[] points,int side){
		return (side*side)<new Rect(points[0],points[2]).area();
	}

	public static void crop4(Point[] pts){
		if(pts[0].y<pts[1].y)
			pts[0].y=pts[1].y;
		else
			pts[1].y=pts[0].y;
	}
	
}
