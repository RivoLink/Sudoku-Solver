package mg.rivolink.app.sudokusolver.views.sudoku;

import mg.rivolink.app.sudokusolver.R;

import android.app.Activity;

import android.view.View;
import android.view.Gravity;
import android.view.MotionEvent;

import android.widget.Toast;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.PopupWindow;
import android.widget.LinearLayout.LayoutParams;

public class PlayPopup extends PopupWindow implements View.OnTouchListener{

	public interface OnMenuSelectListener{
		public void onMenuSelect(Menu menu,int filled);
	}
	
	public enum Menu{
		Close,Reset,Export,New
	}
	
	private final int MIN=10;
	
	private View root;
	private View parent;
	
	private SeekBar barCount;
	private TextView textCount;
	
	private OnMenuSelectListener listener;
	
	public PlayPopup(View root,View parent,OnMenuSelectListener listener){
		super(
			root,
			LayoutParams.MATCH_PARENT,
			LayoutParams.MATCH_PARENT
		);
		
		this.root=root;
		this.parent=parent;
		this.listener=listener;

		root.findViewById(R.id.img_reset).setOnTouchListener(this);
		root.findViewById(R.id.img_export).setOnTouchListener(this);
		root.findViewById(R.id.img_new).setOnTouchListener(this);
		root.findViewById(R.id.img_close).setOnTouchListener(this);
		
		textCount=root.findViewById(R.id.text_count);
		textCount.setText("Filled cells: 30");
		
		barCount=root.findViewById(R.id.seekbar_count);
		barCount.setMax(60);
		barCount.setProgress(20);
		barCount.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar bar,int progress,boolean p3){
				textCount.setText("Filled cells: "+(MIN+progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar bar){
				// TODO: Implement this method
			}

			@Override
			public void onStopTrackingTouch(SeekBar bar){
				//20+bar.getProgress();
			}

		});
	}
	
	public void show(){
		showAtLocation(parent,Gravity.CENTER,0,0);
	}

	@Override
	public boolean onTouch(View view,MotionEvent event){
		if(view.getId()==R.id.img_reset)
			listener.onMenuSelect(Menu.Reset,MIN+barCount.getProgress());
		else if(view.getId()==R.id.img_export)
			listener.onMenuSelect(Menu.Export,MIN+barCount.getProgress());
		else if(view.getId()==R.id.img_new)
			listener.onMenuSelect(Menu.New,MIN+barCount.getProgress());
			
		dismiss();
		return true;
	}
	
}
