package mg.rivolink.app.sudokusolver.views.sudoku;

import android.text.Html;
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
		public void onChange(int selected);
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

	public int getValue(int row,int coll){
		String text=rows[row].cells[coll].getText().toString();
		return (text.length()!=1)?0:Integer.parseInt(text);
	}

	public int getValue(int id){
		return getValue(id/9,id%9);
	}

	public void setValue(int id,int value){
		rows[id/9].cells[id%9].setText((0<value)?""+value:"");
	}

	public void badValue(int row,int col){
		String text="<font color='red'>"+getValue(row,col)+"</font>";
		rows[row].cells[col].setText(Html.fromHtml(text));
	}

	public void unselect(){
		if(selectedCell!=null)
			selectedCell.setBackgroundColor(Color.WHITE);

		if(listener!=null)
			listener.onChange(-1);

		selectedCell=null;
	}

	public void fill(int[][] mat){
		int val=0;
		for(int row=0;row<mat.length;row++){
			for(int col=0;col<mat[0].length;col++){
				val=mat[row][col];

				TextView view=rows[row].cells[col];
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

		selectedCell=(TextView)view;
		selectedCell.setBackgroundColor(primary_light);

		if(listener!=null)
			listener.onChange(selectedCell.getId());

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

