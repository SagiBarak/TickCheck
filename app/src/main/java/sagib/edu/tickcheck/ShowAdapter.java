package sagib.edu.tickcheck;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import sagib.edu.tickcheck.models.Show;

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ShowViewHolder> {

    private ArrayList<Show> data;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;

    public ShowAdapter(ArrayList<Show> data, Context context, Fragment fragment) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.fragment = fragment;
    }

    @Override
    public ShowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.show_item, parent, false);
        return new ShowViewHolder(v, fragment);
    }

    @Override
    public void onBindViewHolder(ShowViewHolder holder, int position) {
        final Show show = data.get(position);
        Picasso.with(context).load(show.getImage()).into(holder.ivImage);
        holder.tvPerformer.setText("\n" + show.getPerformer());
        holder.tvArena.setText(show.getArena());
        if (show.isTicketsAvailable()) {
            holder.ivSoldOut.setVisibility(View.INVISIBLE);
            holder.tvTicketsAvailable.setText("כרטיסים" + "\n" + "זמינים");
            holder.tvTicketsAvailable.setTextColor(Color.rgb(0, 190, 0));
            holder.container.setBackgroundColor(Color.argb(35, 0, 190, 0));
            holder.tvTicketsAvailable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToWebView(show, fragment);
                }
            });
        } else {
            holder.tvTicketsAvailable.setVisibility(View.INVISIBLE);
            holder.ivSoldOut.setVisibility(View.VISIBLE);
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
        ImageView ivSoldOut;
        TextView tvPerformer;
        TextView tvArena;
        TextView tvTicketsAvailable;
        TextView tvDayDateTime;
        ConstraintLayout container;
        Fragment fragment;

        public ShowViewHolder(View v, final Fragment fragment) {
            super(v);
            this.fragment = fragment;
            ivSoldOut = (ImageView) v.findViewById(R.id.ivSoldOut);
            ivImage = (ImageView) v.findViewById(R.id.ivImage);
            tvPerformer = (TextView) v.findViewById(R.id.tvPerformer);
            tvArena = (TextView) v.findViewById(R.id.tvArena);
            tvTicketsAvailable = (TextView) v.findViewById(R.id.tvTicketsAvailable);
            tvDayDateTime = (TextView) v.findViewById(R.id.tvDayDateTime);
            container = (ConstraintLayout) v.findViewById(R.id.container);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Show show = data.get(getAdapterPosition());
                    ShowOptionsFragment showOptionsFragment = new ShowOptionsFragment();
                    Bundle args = new Bundle();
                    args.putParcelable("show", show);
                    showOptionsFragment.setArguments(args);
                    showOptionsFragment.show(fragment.getChildFragmentManager(), "ShowOptions");
                }
            });
        }
    }

    public void goToWebView(Show show, Fragment fragment) {
        Bundle args = new Bundle();
        args.putParcelable("show", show);
        WebViewFragment webViewFragment = new WebViewFragment();
        webViewFragment.setArguments(args);
        FragmentManager fm = fragment.getFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        fragment.getFragmentManager().beginTransaction().replace(R.id.frame, webViewFragment, "Buy").addToBackStack("Buy").commit();
    }
}
