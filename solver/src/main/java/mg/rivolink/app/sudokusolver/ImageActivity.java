package mg.rivolink.app.sudokusolver;

import mg.rivolink.app.sudokusolver.core.SolverCore;
import mg.rivolink.app.sudokusolver.core.sudoku.bfb.SolverBFB;
import mg.rivolink.app.sudokusolver.core.tess.TessBaseAPI;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

import android.os.Bundle;
import android.net.Uri;
import android.app.ProgressDialog;
import android.view.View;
import android.widget.Toast;
import android.widget.ImageView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageActivity extends AppCompatActivity{

	private static final int FILE_KEY=89;

	private Bitmap bmp;
	private ImageView imageView;

	private SolverCore solverCore;

	private FloatingActionButton fabAction;

	private boolean canRetry=false;

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
		setContentView(R.layout.activity_image);

		solverCore=new SolverCore(new SolverBFB(),new TessBaseAPI(this));

		imageView=(ImageView)findViewById(R.id.image_image_view);

		fabAction=(FloatingActionButton)findViewById(R.id.image_fab_action);
		fabAction.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				actions();
			}
		});
		
		
		if(getIntent().getStringExtra("image")!=null) try{
			String filename=getIntent().getStringExtra("image");
			FileInputStream is=this.openFileInput(filename);
			imageView.setImageBitmap(bmp=BitmapFactory.decodeStream(is));
			is.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}

		else if(getIntent().getData()!=null) try{
        	Uri uri=getIntent().getData();
            InputStream is=getContentResolver().openInputStream(uri);
            imageView.setImageBitmap(bmp=BitmapFactory.decodeStream(is));
		}
		catch(FileNotFoundException e){
        	e.printStackTrace();
		}

	}

	@Override
	protected void onResume(){
		super.onResume();
		if(OpenCVLoader.initDebug())
			loader.onManagerConnected(LoaderCallbackInterface.SUCCESS);
		else
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0,this,loader);
	}

	@Override
	protected void onStop(){
		
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode,resultCode,data);

		if(requestCode==FILE_KEY&&resultCode==RESULT_OK){
			try{
				Uri uri=data.getData();
				InputStream is=getContentResolver().openInputStream(uri);
				imageView.setImageBitmap(bmp=BitmapFactory.decodeStream(is));
				
				canRetry=false;
				fabAction.setImageResource(R.drawable.ic_send_white_48dp);
			}
			catch(FileNotFoundException e){
			}
		}
	}

	public void onLoadingEnd(){
		Toast.makeText(this,"OpenCV loaded successfully",Toast.LENGTH_SHORT).show();
		solverCore.init();
	}

	private void actions(){
		if(canRetry)
			loading();
		else
			solving();
	}

	private void loading(){
		Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent,FILE_KEY);
	}

	public void solving(){
		final ProgressDialog dialog=ProgressDialog.show(ImageActivity.this,"","Solving. Please wait...",true);

		new Thread(){
			String text="Solved";
			@Override
			public void run(){
				bmp=solverCore.execute(bmp);
				solverCore.free();

				ImageActivity.this.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						if(bmp!=null)
							imageView.setImageBitmap(bmp);
						else
							text="Not solved";

						canRetry=true;
						fabAction.setImageResource(R.drawable.ic_refresh_white_48dp);
						
						dialog.cancel();
						Toast.makeText(ImageActivity.this,text,Toast.LENGTH_SHORT).show();
					}
				});
			}
		}.start();
	}

}
