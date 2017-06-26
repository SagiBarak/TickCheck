package sagib.edu.tickcheck;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyShowsListFragment extends Fragment {

    RecyclerView rvMyShows;
    FirebaseUser user;
    TextView tvTitleMyShows;
    ProgressBar pbLoadingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_shows_list, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        rvMyShows = (RecyclerView) v.findViewById(R.id.rvMyShows);
        tvTitleMyShows = (TextView) v.findViewById(R.id.tvTitleMyShows);
        pbLoadingList = (ProgressBar) v.findViewById(R.id.pbLoadingList);
        tvTitleMyShows.setText("טוען את ההופעות שלי...");
        MyShowsListAdapter adapter = new MyShowsListAdapter(FirebaseDatabase.getInstance().getReference("MyShows").child(user.getUid()), getContext());
        rvMyShows.setAdapter(adapter);
        rvMyShows.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseDatabase.getInstance().getReference("MyShows").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    tvTitleMyShows.setText("אין הופעות ברשימה...");
                    pbLoadingList.setVisibility(View.GONE);
                } else {
                    tvTitleMyShows.setText("ההופעות שלי:");
                    pbLoadingList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return v;
    }

    public static class MyShowsListAdapter extends FirebaseRecyclerAdapter<Show, MyShowsListAdapter.MyShowsListViewHolder> {

        Context context;

        public MyShowsListAdapter(Query query, Context context) {
            super(Show.class, R.layout.myshow_item, MyShowsListViewHolder.class, query);
            this.context = context;
        }

        @Override
        protected void populateViewHolder(MyShowsListViewHolder viewHolder, Show show, int position) {
            Picasso.with(context).load(show.getImage()).into(viewHolder.ivImage);
            viewHolder.tvArena.setText(show.getArena());
            viewHolder.tvPerformer.setText(show.getPerformer());
            viewHolder.tvDayDateTime.setText(show.getDayDateTime());
        }

        public static class MyShowsListViewHolder extends RecyclerView.ViewHolder {

            ImageView ivImage;
            TextView tvPerformer;
            TextView tvArena;
            TextView tvDayDateTime;

            public MyShowsListViewHolder(View itemView) {
                super(itemView);
                ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
                tvPerformer = (TextView) itemView.findViewById(R.id.tvPerformer);
                tvArena = (TextView) itemView.findViewById(R.id.tvArena);
                tvDayDateTime = (TextView) itemView.findViewById(R.id.tvDayDateTime);
            }
        }
    }

}
