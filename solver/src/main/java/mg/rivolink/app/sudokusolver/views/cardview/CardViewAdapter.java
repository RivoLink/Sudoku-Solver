package mg.rivolink.app.sudokusolver.views.cardview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mg.rivolink.app.sudokusolver.R;
import mg.rivolink.app.sudokusolver.views.cardview.CardView;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder>{
	
	private Context context;
	private List<CardView> cards;
	private ItemListener listener;
	
	public CardViewAdapter(Context context,List<CardView> cards,ItemListener itemListener) {
		this.context=context;
		this.cards=cards;
		this.listener=itemListener;
    }

	@Override
	public int getItemCount(){
		return cards.size();
	}
	
	@Override
	public CardViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int position){
		View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(CardViewAdapter.ViewHolder vHolder,int position){
		vHolder.setCard(cards.get(position),position);
	}
	
	public interface ItemListener {
        void onItemClick(CardView item,int position);
    }

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		
		public int position=0;
		
		public CardView item;
        public TextView textView;
        public ImageView imageView;

        public ViewHolder(View view){
            super(view);
            view.setOnClickListener(this);
			textView=view.findViewById(R.id.textView);
            imageView=view.findViewById(R.id.imageView);
		}

        public void setCard(CardView item,int position){
            this.item=item;
			this.position=position;
            textView.setText("");//(item.text);
            imageView.setImageResource(item.drawable);
        }
		
        @Override
        public void onClick(View view){
            if(listener!=null){
                listener.onItemClick(item,position);
            }
        }
    }
}
