package mg.rivolink.app.sudokusolver.views.sudoku;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SudokuGridView extends LinearLayout{
	
	private int[][] sudoku;
	private boolean[][] mask;

	public SudokuGridView(Context context){
		super(context);
		init(context);
	}

	public SudokuGridView(Context context,AttributeSet attr){
		super(context,attr);
		init(context);
	}

	private void init(Context context){
		//setOrientation(LinearLayout.VERTICAL);
		//setBackgroundResource(R.drawable.sudoku_grid_empty);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec,widthMeasureSpec);
	}

	public void set(int[][] sudoku,boolean[][] mask){
		this.sudoku=sudoku;
		this.mask=mask;
	}

}
