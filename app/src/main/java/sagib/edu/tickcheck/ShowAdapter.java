package sagib.edu.tickcheck;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
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

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ShowViewHolder> {

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
        final Show show = data.get(position);
        Picasso.with(context).load(show.getImage()).into(holder.ivImage);
        holder.tvPerformer.setText("\n" + show.getPerformer());
        holder.tvArena.setText(show.getArena());
        if (show.isTicketsAvailable()) {
            holder.tvTicketsAvailable.setText("כרטיסים: זמינים");
            holder.tvTicketsAvailable.setTextColor(Color.rgb(0, 204, 0));
            holder.tvTicketsAvailable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(show.getLink()));
                    context.startActivity(intent);
                }
            });
        } else {
            holder.tvTicketsAvailable.setText("כרטיסים: תפוסים");
            holder.tvTicketsAvailable.setTextColor(Color.RED);
        }
        holder.tvDayDateTime.setText(show.getDayDateTime() + "\n");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ShowViewHolder extends RecyclerView.ViewHolder {

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
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final Show show = data.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("איזורים ב" + show.getArena()).
                            setMessage(" " + show.getZones().toString().replace("[", "").replace("]", "").replace(",", "")).
                            setCancelable(true).
                            setNegativeButton("חזרה", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("לרכישה", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(show.getLink()));
                            context.startActivity(intent);
                        }
                    }).show();

                }
            });
        }
    }
}
