package sagib.edu.tickcheck;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
            holder.tvTicketsAvailable.setText("כרטיסים" + "\n" + "זמינים");
            holder.tvTicketsAvailable.setTextColor(Color.rgb(0, 190, 0));
            holder.container.setBackgroundColor(Color.argb(35, 0, 190, 0));
            holder.tvTicketsAvailable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToWebView(show);
                }
            });
        } else {
            holder.tvTicketsAvailable.setText("כרטיסים" + "\n" + "תפוסים");
            holder.tvTicketsAvailable.setTextColor(Color.RED);
            holder.container.setBackgroundColor(Color.argb(35, 255, 0, 0));
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
        ConstraintLayout container;

        public ShowViewHolder(View v) {
            super(v);
            ivImage = (ImageView) v.findViewById(R.id.ivImage);
            tvPerformer = (TextView) v.findViewById(R.id.tvPerformer);
            tvArena = (TextView) v.findViewById(R.id.tvArena);
            tvTicketsAvailable = (TextView) v.findViewById(R.id.tvTicketsAvailable);
            tvDayDateTime = (TextView) v.findViewById(R.id.tvDayDateTime);
            container = (ConstraintLayout) v.findViewById(R.id.container);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final Show show = data.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("איזורים ב" + show.getArena()).
                            setMessage(" " + show.getZones().
                                    toString().replace("[", "").replace("]", "").replace(",", "")).
                            setCancelable(true).
                            setNegativeButton("חזרה", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    if (show.getFreeFromShow() > 0) {
                        builder.setPositiveButton("לרכישה", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                goToWebView(show);
                            }
                        });
                    }
                    builder.show();
                }
            });
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    final Show show = data.get(position);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("הוסף לרשימת ההופעות שלי?").setMessage(String.format("האם להוסיף את ההופעה:\n%s ב%s\nב%s\nלרשימת המופעים שלי?", show.getPerformer(), show.getArena(), show.getDayDateTime()));
                    builder.setPositiveButton("הוסף", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("MyShows").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push();
                            MyShow myShow = new MyShow(show.getPerformer(), show.getDayDateTime(), show.getArena(), show.getImage(), ref.getKey());
                            ref.setValue(myShow).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    Toast.makeText(context, "ההופעה נוספה לרשימת ההופעות שלי", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(context, "הפעולה נכשלה, נסה שנית", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }).setNegativeButton("בטל", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                    return false;
                }
            });
        }
    }

    public void goToWebView(Show show) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("show", show);
        context.startActivity(intent);
    }
}
