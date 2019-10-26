package mg.rivolink.app.sudokusolver;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import mg.rivolink.app.sudokusolver.views.sudoku.SudokuView;

public class PlayActivity extends AppCompatActivity 
	implements SudokuView.OnSelectionChangeListener{

	private TextView selectedCell;
		
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
	}

	@Override
	public void onChange(TextView before,TextView selected){
		selectedCell=selected;
	}
	
	public void onInput(View view){
		if(selectedCell!=null)
			selectedCell.setText(((Button)view).getText());
	}
	
	public void onClear(View view){
		if(selectedCell!=null)
			selectedCell.setText("");
	}
	
	public void onRedo(View view){
		Toast.makeText(this,"Redo",Toast.LENGTH_SHORT).show();
	}
	
	public void onUndo(View view){
		Toast.makeText(this,"Undo",Toast.LENGTH_SHORT).show();
	}
	
	public void onVerify(View view){
		Toast.makeText(this,"Verify",Toast.LENGTH_SHORT).show();
	}

	public void onRefresh(View view){
		Toast.makeText(this,"Refresh",Toast.LENGTH_SHORT).show();
	}
	
}
