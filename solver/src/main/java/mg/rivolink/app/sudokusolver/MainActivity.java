package mg.rivolink.app.sudokusolver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mg.rivolink.app.sudokusolver.views.cardview.CardView;
import mg.rivolink.app.sudokusolver.views.cardview.CardViewAdapter;

public class MainActivity extends AppCompatActivity implements CardViewAdapter.ItemListener{
	
	private static final int FILE_KEY=89;
	
	private List<CardView> cards;
	private RecyclerView recyclerView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
		cards=new ArrayList<>();
		cards.add(new CardView("Capture",R.drawable.ic_camera));
		cards.add(new CardView("Real Time",R.drawable.ic_time));
		cards.add(new CardView("Image",R.drawable.ic_sudoku_grid));
		cards.add(new CardView("Create",R.drawable.ic_pen));
		cards.add(new CardView("Source Code",R.drawable.ic_source_code));
		cards.add(new CardView("About",R.drawable.ic_setting));
		
        CardViewAdapter adapter=new CardViewAdapter(this,cards,this);
        recyclerView.setAdapter(adapter);
		
		GridLayoutManager manager=new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
		recyclerView.setLayoutManager(manager);
		
    }

	@Override
	public void onItemClick(CardView item,int position){
		switch(position){
			case 0:{
				startActivity(new Intent(this,CaptureActivity.class));
				break;
			}
			case 1:{
				startActivity(new Intent(this,RealTimeActivity.class));
				break;
			}
			case 2:{
				Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent,FILE_KEY);
				break;
			}
			default:{
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode,resultCode,data);
		if(requestCode==FILE_KEY){
			if(resultCode==RESULT_OK){
				Intent intent=new Intent(this,ImageActivity.class);
				intent.setData(data.getData());
				startActivity(intent);
			}
			else{
				Toast.makeText(this,"Please select file.",Toast.LENGTH_SHORT).show();
			}
		}
	}
	
}
