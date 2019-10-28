package mg.rivolink.app.sudokusolver.views.sudoku;

import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.View;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;

import mg.rivolink.app.sudokusolver.R;

public class SudokuView extends LinearLayout implements View.OnTouchListener{

	public interface OnSelectionChangeListener{
		public void onChange(TextView before,TextView selected);
	}

	public SudokuRow rows[]=new SudokuRow[9];

	private int[][] value;
	private boolean[][] mask;
	private boolean rowsResized=false;

	private Paint paint;
	private int primary=Color.parseColor("#ff455a64");
	private int primary_blue=Color.parseColor("#ff007EC0");
	private int primary_light=Color.parseColor("#66cfd8dc");

	private TextView selectedCell;
	private OnSelectionChangeListener listener;

	public SudokuView(Context context){
		super(context);
		init(context);
	}

	public SudokuView(Context context,AttributeSet attr){
		super(context,attr);
		init(context);
	}

	private void init(Context context){
		setOrientation(LinearLayout.VERTICAL);

		paint=new Paint();
		paint.setColor(primary);
        paint.setStyle(Paint.Style.STROKE);

		for(int i=0;i<rows.length;i++){
			rows[i]=new SudokuRow(context,this,9*i);
			addView(rows[i]);
		}
	}

	public void setListener(OnSelectionChangeListener listener){
		this.listener=listener;
	}

	public void setValue(int id,int value){
		rows[id/9].cells[id%9].setText((0<value)?""+value:"");
	}

	public void free(){
		if(selectedCell!=null)
			selectedCell.setBackgroundColor(Color.WHITE);

		if(listener!=null)
			listener.onChange(selectedCell,null);

		selectedCell=null;
	}

	public void fill(int[][] mat){

		int val=0;
		for(int row=0;row<mat.length;row++){
			for(int cell=0;cell<mat[0].length;cell++){
				val=mat[row][cell];

				TextView view=rows[row].cells[cell];
				if(0<val){
					view.setText(""+val);
					view.setEnabled(false);
					view.setTextColor(primary_blue);
				}
				else{
					view.setText("");
					view.setEnabled(true);
					view.setTextColor(primary);
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec,heightMeasureSpec);

		int width=9*(MeasureSpec.getSize(widthMeasureSpec)/9);
		setMeasuredDimension(width,width);

		if(!rowsResized){
			rowsResized=true;

			for(SudokuRow row:rows){
				for(TextView cell:row.cells){
					ViewGroup.LayoutParams params=cell.getLayoutParams();
					params.width=width/9;
					params.height=params.width;
				}
			}
		}
	}

	@Override
	public boolean onTouch(View view,MotionEvent event){
		if(selectedCell!=null)
			selectedCell.setBackgroundColor(Color.WHITE);

		if(listener!=null)
			listener.onChange(selectedCell,(TextView)view);

		selectedCell=(TextView)view;
		selectedCell.setBackgroundColor(primary_light);

		return true;
	}

	@Override
	public void draw(Canvas canvas){
		super.draw(canvas);
		for(int i=1,step,width=getWidth();i<9;i++){
			step=i*(width/9);
			paint.setStrokeWidth((i%3==0)?5:1);
			canvas.drawLine(step,0,step,width,paint);
			canvas.drawLine(0,step,width,step,paint);
		}
	}

	private class SudokuRow extends LinearLayout{

		public final int index;
		public TextView[] cells=new TextView[9];

		public SudokuRow(Context context,View.OnTouchListener listener,int index){
			super(context);
			this.index=index;
			this.setOrientation(LinearLayout.HORIZONTAL);

			for(int i=0;i<cells.length;i++){
				final int id=index+i;
				cells[i]=new TextView(context){
					@Override
					public int getId(){
						return id;
					}
				};
				cells[i].setOnTouchListener(listener);
				cells[i].setGravity(Gravity.CENTER);
				cells[i].setTextColor(primary);
				cells[i].setTextSize(20);
				addView(cells[i]);
			}
		}
	}

}

