package sagib.edu.tickcheck;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by sagib on 14/06/2017.
 */

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ShowViewHolder>{

    private ArrayList<Show> data;
    private Context context;
    private LayoutInflater inflater;

    public ShowAdapter(ArrayList<Show> data, Context context) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ShowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.show_item, parent, false);
        return new ShowViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ShowViewHolder holder, int position) {
        Show show = data.get(position);
        Picasso.with(context).load(show.getImage()).into(holder.ivImage);
        holder.tvPerformer.setText(show.getPerformer());
        holder.tvArena.setText(show.getArena());
        if (show.isTicketsAvailable()){
            holder.tvTicketsAvailable.setText("יש כרטיסים זמינים");
            holder.tvTicketsAvailable.setTextColor(Color.GREEN);
        } else{
            holder.tvTicketsAvailable.setText("אין כרטיסים זמינים");
            holder.tvTicketsAvailable.setTextColor(Color.RED);
        }
        holder.tvDayDateTime.setText(show.getDayDateTime());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ShowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivImage;
        TextView tvPerformer;
        TextView tvArena;
        TextView tvTicketsAvailable;
        TextView tvDayDateTime;

        public ShowViewHolder(View v) {
            super(v);
            ivImage = (ImageView) v.findViewById(R.id.ivImage);
            tvPerformer = (TextView) v.findViewById(R.id.tvPerformer);
            tvArena = (TextView) v.findViewById(R.id.tvArena);
            tvTicketsAvailable = (TextView) v.findViewById(R.id.tvTicketsAvailable);
            tvDayDateTime = (TextView) v.findViewById(R.id.tvDayDateTime);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
