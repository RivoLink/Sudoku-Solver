package mg.rivolink.app.sudokusolver.views.sudoku;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import mg.rivolink.app.sudokusolver.R;

public class SudokuView extends LinearLayout{
	
	public SudokuRow rows[]=new SudokuRow[9];

	private int[][] value;
	private boolean[][] mask;
	private boolean rowsResized=false;
	
	private Paint paint;
	
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
		setBackgroundResource(R.drawable.sudoku_grid_empty);

		paint=new Paint();
		paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);

		for(int i=0;i<rows.length;i++){
			rows[i]=new SudokuRow(context);
			addView(rows[i]);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec,heightMeasureSpec);

		if(!rowsResized){
			rowsResized=true;

			int width=MeasureSpec.getSize(widthMeasureSpec);
			for(SudokuRow row:rows){
				for(TextView cell:row.cells){
					ViewGroup.LayoutParams params=cell.getLayoutParams();
					params.width=width/9;
					params.height=params.width;
				}
			}
		}
	}
	
	
	private class SudokuRow extends LinearLayout{

		public TextView[] cells=new TextView[9];

		public SudokuRow(Context context){
			super(context);
			setOrientation(LinearLayout.HORIZONTAL);

			for(int i=0;i<cells.length;i++){
				cells[i]=new TextView(context);
				cells[i].setGravity(Gravity.CENTER);
				cells[i].setTextColor(Color.WHITE);
				cells[i].setText("0");
				addView(cells[i]);
			}
		}
	}
	
}
