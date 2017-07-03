package sagib.edu.tickcheck;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MyShowsListFragment extends Fragment {

    RecyclerView rvMyShows;
    FirebaseUser user;
    TextView tvTitleMyShows;
    ProgressBar pbLoadingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_shows_list, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        rvMyShows = (RecyclerView) v.findViewById(R.id.rvMyShows);
        tvTitleMyShows = (TextView) v.findViewById(R.id.tvTitleMyShows);
        pbLoadingList = (ProgressBar) v.findViewById(R.id.pbLoadingList);
        tvTitleMyShows.setText("טוען את ההופעות שלי...");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MyShows").child(user.getUid());
        MyShowsListAdapter adapter = new MyShowsListAdapter(reference.orderByChild("date"), getContext(), this);
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

    public static class MyShowsListAdapter extends FirebaseRecyclerAdapter<MyShow, MyShowsListAdapter.MyShowsListViewHolder> {

        Context context;
        Fragment fragment;

        public MyShowsListAdapter(Query query, Context context, Fragment fragment) {
            super(MyShow.class, R.layout.myshow_item, MyShowsListViewHolder.class, query);
            this.context = context;
            this.fragment = fragment;
        }

        @Override
        public MyShowsListAdapter.MyShowsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            return new MyShowsListAdapter.MyShowsListViewHolder(view, fragment);
        }

        @Override
        protected void populateViewHolder(MyShowsListViewHolder viewHolder, MyShow show, int position) {
            viewHolder.model = show;
            Picasso.with(context).load(show.getImage()).into(viewHolder.ivImage);
            viewHolder.tvArena.setText(show.getArena());
            viewHolder.tvPerformer.setText(show.getPerformer());
            viewHolder.tvDayDateTime.setText(show.getDateTime());
        }

        public static class MyShowsListViewHolder extends RecyclerView.ViewHolder {

            ImageView ivImage;
            TextView tvPerformer;
            TextView tvArena;
            TextView tvDayDateTime;
            ImageView ivNav;
            MyShow model;
            Fragment fragment;
            TextView tvNav;

            public MyShowsListViewHolder(final View itemView, final Fragment fragment) {
                super(itemView);
                this.fragment = fragment;
                ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
                tvPerformer = (TextView) itemView.findViewById(R.id.tvPerformer);
                tvArena = (TextView) itemView.findViewById(R.id.tvArena);
                tvDayDateTime = (TextView) itemView.findViewById(R.id.tvDayDateTime);
                ivNav = (ImageView) itemView.findViewById(R.id.ivNav);
                tvNav = (TextView) itemView.findViewById(R.id.tvNav);
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = "";
                        if (tvArena.getText().toString().contains("זאפה אמפי שוני")) {
                            uri = "geo: 32.534777, 34.948529&navigate=yes";
                        } else if (tvArena.getText().toString().contains("פארק ענבה מודיעין")) {
                            uri = "geo: 31.898358, 35.004058&navigate=yes";
                        } else if (tvArena.getText().toString().contains("אמפי קיסריה (הגן הלאומי)")) {
                            uri = "geo: 32.495838, 34.891096&navigate=yes";
                        } else if (tvArena.getText().toString().contains("לייב פארק")) {
                            uri = "geo: 31.976074, 34.741294&navigate=yes";
                        }
                        v.getContext().startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse(uri)));
                    }
                };
                ivNav.setOnClickListener(onClickListener);
                tvNav.setOnClickListener(onClickListener);
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
                        builder.setTitle("הסר הופעה מהרשימה").setMessage("האם ברצונך למחוק את ההופעה מרשימת ״ההופעות שלי״?").setPositiveButton("כן", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference("MyShows").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getMyShowUID()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FirebaseDatabase.getInstance().getReference("MyShowsList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getEventID()).removeValue();
                                        Toast.makeText(fragment.getContext(), "ההופעה נמחקה מהרשימה!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialog.dismiss();
                            }
                        }).setNegativeButton("לא", new DialogInterface.OnClickListener() {
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
    }

}
