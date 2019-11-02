package mg.rivolink.app.sudokusolver;

import java.util.List;
import java.util.LinkedList;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.support.v7.app.AppCompatActivity;

import mg.rivolink.app.sudokusolver.core.sudoku.SudokuGenerator;
import mg.rivolink.app.sudokusolver.views.sudoku.SudokuView;
import mg.rivolink.app.sudokusolver.views.sudoku.PlayPopup;
import mg.rivolink.app.sudokusolver.views.sudoku.PlayPopup.OnMenuSelectListener;

public class PlayActivity extends AppCompatActivity 
	implements Runnable,SudokuView.OnSelectionChangeListener,
	PlayPopup.OnMenuSelectListener{

	private int id=-1;
	private int filled=0;
	private int[][] soluce;

	private boolean paused=false;

	private View layout;
	private SudokuView grid;
	private SudokuGenerator generator;

	private int sp=-1;
	private int maxSp=sp;
	private List<Integer> undo;
	private List<Integer> redo;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);

		grid=(SudokuView)findViewById(R.id.sudoku_view);
		grid.setListener(this);

		generator=new SudokuGenerator();

		undo=new LinkedList<>();
		redo=new LinkedList<>();

		layout=findViewById(R.id.layout_main);
		layout.post(new Runnable(){
			@Override
			public void run(){
				onMenu(null);
			}
		});
	}

	public void onMenu(View view){
		View inflate=getLayoutInflater().inflate(R.layout.popup_play,null);
		new PlayPopup(inflate,layout,this).show();
	}

	@Override
	public void onMenuSelect(PlayPopup.Menu menu,int filled){
		switch(menu){
			case Reset:{
				Toast.makeText(this,"Reset",Toast.LENGTH_SHORT).show();

				maxSp=sp=-1;
				undo.clear();
				redo.clear();

				grid.unselect();
				grid.fill(generator.getSudoku());
				this.filled=generator.getK();

				break;
			}
			case New:{
				Toast.makeText(this,"New game",Toast.LENGTH_SHORT).show();

				maxSp=sp=-1;
				undo.clear();
				redo.clear();

				grid.unselect();
				grid.fill(generator.generate(filled));
				soluce=generator.getSoluce();
				this.filled=filled;

				break;
			} 
			case Export:{
				Toast.makeText(this,"Export",Toast.LENGTH_SHORT).show();

				break;
			}
		}
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
	public void onChange(int selected){
		id=selected;
	}

	private int parse(CharSequence str){
		if(str.length()!=1)
			return 0;
		else
			return Integer.parseInt(str.toString());
	}

	public void onInput(View view){
		if(-1<id){
			int input=parse(((Button)view).getText());

			if(grid.getValue(id)==0)
				filled++;

			saveActions(input);

			grid.setValue(id,input);
			maxSp=sp;

			if(filled==81){
				new Thread(this).start();
			}
		}
	}

	private boolean verifyUserSoluce(){
		boolean solved=true;

		for(int row=0;row<9;row++){
			for(int col=0;col<9;col++){
				solved=(grid.getValue(row,col)==soluce[row][col]);
				if(!solved){
					break;
				}
			}
			if(!solved)
				break;
		}

		if(!solved){
			for(int i=0;i<9;i++){
				int sum=0;
				for(int j=0;j<9;j++){
					sum+=grid.getValue(i,j);
					sum+=grid.getValue(j,i);
				}
				if(sum!=90)
					return false;
			}
		}

		return solved;
	}

	@Override
	public void run(){
		boolean solved=verifyUserSoluce();
		final String text=(solved)?"Solved":"Wrong";

		this.runOnUiThread(new Runnable(){
			@Override
			public void run(){
				Toast.makeText(PlayActivity.this,text,Toast.LENGTH_SHORT).show();
			}
		});

	}

	private void countFilled(int id,int val){
		int lastVal=grid.getValue(id);

		if(lastVal==0 && val!=0)
			filled++;
		else if(lastVal!=0 && val==0)
			filled--;
	}

	private void saveActions(int input){
		int action=id*10+grid.getValue(id);
		if(++sp<undo.size())
			undo.set(sp,action);
		else
			undo.add(action);

		action=id*10+input;
		if(sp<redo.size())
			redo.set(sp,action);
		else
			redo.add(action);

		maxSp=sp;
	}

	public void onUndo(View view){
		if(-1<sp){
			int action=undo.get(sp);
			int id=action/10;
			int val=action-id*10;

			countFilled(id,val);
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

			countFilled(id,val);
			grid.setValue(id,val);
		}
	}

	public void onClear(View view){
		if(-1<id){
			if(0<grid.getValue(id)){
				filled--;
				saveActions(0);
				grid.setValue(id,0);
			}
		}
	}

	public void onVerify(View view){
		Toast.makeText(this,"Verify",Toast.LENGTH_SHORT).show();

		int userVal=0;
		for(int row=0;row<9;row++){
			for(int col=0;col<9;col++){
				userVal=grid.getValue(row,col);
				if(0<userVal && userVal!=soluce[row][col])
					grid.badValue(row,col);
			}
		}
	}

	public void onRefresh(View view){

	}

}

