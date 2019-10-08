package mg.rivolink.app.sudokusolver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.Toast;

import mg.rivolink.app.sudokusolver.core.SolverCore;
import mg.rivolink.app.sudokusolver.core.sudoku.bfb.SolverBFB;
import mg.rivolink.app.sudokusolver.core.tess.TessBaseAPI;
import mg.rivolink.app.sudokusolver.core.utils.FileUtils;
import mg.rivolink.app.sudokusolver.views.camera.PortraitCameraLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class CaptureActivity extends AppCompatActivity implements View.OnClickListener,CameraBridgeViewBase.CvCameraViewListener2{
	
	private Mat rgba;
	
	private View background;
	private LinearLayout fabRecLayout;
	private LinearLayout fabRetryLayout;
	
	private SolverCore solverCore;

	private MenuItem menuSave;
	private MenuItem menuShare;
	
	private FloatingActionButton fabCamera;
	private FloatingActionButton fabRetry;
	private FloatingActionButton fabRecognize;
	
	private boolean fabsOpened=false;
	
	private CameraBridgeViewBase camera;

	private Action action=Action.CAPTURE;
	private enum Action{
		NONE,
		CAPTURE,
		RECOGNIZE,
	}
	
	private BaseLoaderCallback loader=new BaseLoaderCallback(this){
		@Override
		public void onManagerConnected(final int status){
			switch(status){
				case LoaderCallbackInterface.SUCCESS:{
					onLoadingEnd();
					break;
				}
				default:{
					super.onManagerConnected(status);
					break;
				}
			}     
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		
		solverCore=new SolverCore(new SolverBFB(),new TessBaseAPI(this));
		
		camera=((PortraitCameraLayout)findViewById(R.id.capture_camera_layout)).getCamera();
		camera.setCvCameraViewListener(this);
		
		fabCamera=(FloatingActionButton)findViewById(R.id.capture_fab_camera);
		fabCamera.setOnClickListener(this);

		fabRecognize=(FloatingActionButton)findViewById(R.id.capture_fab_recognize);
		fabRecLayout=(LinearLayout)findViewById(R.id.capture_fab_recognize_layout);
		fabRecognize.setOnClickListener(this);

		fabRetry=(FloatingActionButton)findViewById(R.id.capture_fab_retry);
		fabRetryLayout=(LinearLayout)findViewById(R.id.capture_fab_retry_layout);
		fabRetry.setOnClickListener(this);
		
		background=findViewById(R.id.capture_bg);
		background.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				if(fabsOpened)
					closeFabs();
			}
		});
		
	}

	@Override
	protected void onResume(){
		super.onResume();
		
		if(OpenCVLoader.initDebug())
			loader.onManagerConnected(LoaderCallbackInterface.SUCCESS);
		else
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0,this,loader);
	}
	
	public void onLoadingEnd(){
		Toast.makeText(this,"OpenCV loaded successfully",Toast.LENGTH_SHORT).show();
		solverCore.init();
		camera.enableView();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.activity_capture,menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		menuSave=menu.findItem(R.id.capture_menu_save);
		menuShare=menu.findItem(R.id.capture_menu_share);
		setMenuVisibility(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.capture_menu_save:{
				final ProgressDialog dialog=ProgressDialog.show(CaptureActivity.this,"","Saving. Please wait...",true);
				
				new Thread(){
					String text="";
					@Override
					public void run(){
						if(rgba!=null && FileUtils.saveToPng(rgba))
							text="Image saved at: "+FileUtils.APP_PATH;
						else
							text="Can't save image.";
						CaptureActivity.this.runOnUiThread(new Runnable(){
							@Override
							public void run(){
								dialog.cancel();
								Toast.makeText(CaptureActivity.this,text,Toast.LENGTH_SHORT).show();
							}
						});
					}
				}.start();
				break;
			}
			case R.id.capture_menu_share:{
				if(rgba!=null && FileUtils.saveToPng(rgba,"Shared")){
					Intent share=new Intent(Intent.ACTION_SEND);
					share.setType("image/png");
					share.putExtra(Intent.EXTRA_STREAM,FileUtils.APP_PATH+"SudokuSolver-Shared.png");
					startActivity(Intent.createChooser(share,"Share Image"));
				}
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed(){
		if(fabsOpened)
			closeFabs();
		else
			super.onBackPressed();
	}
	
	@Override
	public void onClick(View view){
		if(fabsOpened)
			closeFabs();
		else
			openFabs();
		
		if(view==fabCamera){
			if(action!=Action.NONE){
				action=Action.NONE;
				setMenuVisibility(true);
			}
		}
		else if(view==fabRetry){
			if(action!=Action.CAPTURE){
				action=Action.CAPTURE;
				setMenuVisibility(false);
			}
		}
		else if(view==fabRecognize){
			action=Action.RECOGNIZE;
		}
	}
	
	private void setMenuVisibility(boolean visible){
		menuSave.setVisible(visible);
		menuShare.setVisible(visible);
	}
	
	private void openFabs(){
		fabsOpened=true;

		background.setVisibility(View.VISIBLE);

		fabRecLayout.setVisibility(View.VISIBLE);
		fabRetryLayout.setVisibility(View.VISIBLE);

		float t=getResources().getDimension(R.dimen.standard_70);
		fabRecLayout.animate().translationX(-t);
		fabRetryLayout.animate().translationY(-t);
		
	}

	private void closeFabs(){
		fabsOpened=false;
		
		background.setVisibility(View.GONE);
		
		fabRetryLayout.animate().translationY(0);
		fabRecLayout.animate().translationX(0);
		
		fabRetryLayout.setVisibility(View.VISIBLE);
		fabRecLayout.setVisibility(View.VISIBLE);
		
	}
	

	@Override
	protected void onStop(){
		super.onStop();
		
		solverCore.free();
		camera.disableView();
	}

	@Override
	public void onCameraViewStarted(int width,int height){
		// TODO: Implement this method
		Toast.makeText(this,"w: "+width+" h: "+height,Toast.LENGTH_SHORT).show();
	}
		

	@Override
	public void onCameraViewStopped(){
		rgba.release();
	}

	@Override
	public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
		if(action==Action.CAPTURE)
			rgba=inputFrame.rgba();
			
		if(action==Action.RECOGNIZE){
			solverCore.execute(rgba);
			action=Action.NONE;
		}
		
		return rgba;
	}

}
