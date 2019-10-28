package mg.rivolink.app.sudokusolver;

import java.util.List;
import java.util.LinkedList;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.support.v7.app.AppCompatActivity;

import mg.rivolink.app.sudokusolver.views.sudoku.SudokuView;
import mg.rivolink.app.sudokusolver.core.sudoku.SudokuGenerator;

public class PlayActivity extends AppCompatActivity 
implements SudokuView.OnSelectionChangeListener{

	private TextView selectedCell;

	private boolean paused=false;

	private SudokuView grid;
	private SudokuGenerator generator;

	private int sp=-1;
	private int maxSp=sp;
	private List<Integer> undo;
	private List<Integer> redo;

	private int[][] soluce;
	private boolean bad=false;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);

		undo=new LinkedList<>();
		redo=new LinkedList<>();

		generator=new SudokuGenerator(30);

		grid=(SudokuView)findViewById(R.id.sudoku_view);
		grid.fill(generator.generate());
		grid.setListener(this);

		soluce=generator.getSoluce();

	}

	public void onPause(View view){
		paused=!paused;

		String text;
		ImageView imgView=(ImageView)view;
		if(paused){
			text="Game paused";
			imgView.setImageResource(R.drawable.ic_pause_white_48dp);
		}
		else{
			text="Game started";
			imgView.setImageResource(R.drawable.ic_play_arrow_white_48dp);
		}

		Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onChange(TextView before,TextView selected){
		selectedCell=selected;

		/*
		 if(before!=null){

		 int row=9-before.getId()/9;
		 int col=before.getId()%9;

		 }
		 */

	}

	private int parse(CharSequence str){
		if(str.length()!=1)
			return 0;
		else
			return Integer.parseInt(str.toString());
	}

	public void onInput(View view){
		if(selectedCell!=null){

			int action=selectedCell.getId()*10+parse(selectedCell.getText());
			if(++sp<undo.size())
				undo.set(sp,action);
			else
				undo.add(action);

			String input=((Button)view).getText().toString();
			selectedCell.setText(input);
			maxSp=sp;

			action=selectedCell.getId()*10+parse(input);
			if(sp<redo.size())
				redo.set(sp,action);
			else
				redo.add(action);

		}
	}

	public void onUndo(View view){
		if(-1<sp){
			int action=undo.get(sp);
			int id=action/10;
			int val=action-id*10;
			grid.setValue(id,val);
			sp--;
		}
	}

	public void onRedo(View view){
		if(sp<maxSp){
			sp++;
			int action=redo.get(sp);
			int id=action/10;
			int val=action-id*10;
			grid.setValue(id,val);
		}
	}

	public void onVerify(View view){
		Toast.makeText(this,"Verify",Toast.LENGTH_SHORT).show();
	}

	public void onClear(View view){
		if(selectedCell!=null)
			selectedCell.setText("");
	}

	public void onRefresh(View view){
		Toast.makeText(this,"Refresh",Toast.LENGTH_SHORT).show();

		maxSp=sp=-1;
		undo.clear();
		redo.clear();

		grid.free();
		grid.fill(generator.generate());
		soluce=generator.getSoluce();

	}

}

