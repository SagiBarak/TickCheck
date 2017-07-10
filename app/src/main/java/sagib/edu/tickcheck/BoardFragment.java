package sagib.edu.tickcheck;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BoardFragment extends Fragment {

    FirebaseUser user;
    FirebaseDatabase database;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    BoardAdapter adapter;
    @BindView(R.id.btnSend)
    BootstrapButton btnSend;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    Unbinder unbinder;
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_board, container, false);
        unbinder = ButterKnife.bind(this, v);
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("טוען רשימת מודעות...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewPostFragment newPostFragment = new NewPostFragment();
                newPostFragment.show(getChildFragmentManager(), "NewPost");
            }
        });
        database = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();
        database.getReference("Board").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        database.getReference("Board").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setStackFromEnd(true);
                recycler.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        setupRecycler();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("פורום מכירת כרטיסים");
    }

    private void setupRecycler() {
        Fragment f = null;
        if (getParentFragment() != null) {
            f = getParentFragment();
        } else
            f = this;
        adapter = new BoardAdapter(database.getReference("Board"), dialog, getContext(), f);
        recycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recycler.setLayoutManager(layoutManager);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static class BoardAdapter extends FirebaseRecyclerAdapter<BoardPost, BoardAdapter.BoardViewHolder> {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Context context;
        ProgressDialog dialog;
        Fragment fragment;

        public BoardAdapter(Query ref, ProgressDialog dialog, Context context, Fragment fragment) {
            super(BoardPost.class, R.layout.board_item, BoardViewHolder.class, ref);
            this.context = context;
            this.dialog = dialog;
            this.fragment = fragment;
            dialog = new ProgressDialog(context);
        }

        @Override
        public BoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            return new BoardViewHolder(view, fragment);
        }

        @Override
        protected void populateViewHolder(final BoardViewHolder viewHolder, final BoardPost post, final int position) {
            viewHolder.model = post;
            dialog.dismiss();
            viewHolder.ivDelete.setVisibility(View.GONE);
            viewHolder.ivEdit.setVisibility(View.GONE);
            viewHolder.tvPostContent.setText("הערות: " + post.getContents());
            viewHolder.tvDisplayName.setText(post.getUserDisplay());
            viewHolder.tvShowTitle.setText("מופע: " + post.getShowTitle());
            viewHolder.tvShowArena.setText("מיקום: " + post.getShowArena());
            viewHolder.tvShowDate.setText("תאריך: " + post.getShowDate());
            viewHolder.tvTicketsNumber.setText("כמות כרטיסים: " + post.getTicketsNumber());
            viewHolder.tvShowPrice.setText("עלות לכרטיס: " + post.getShowPrice());
            if (user.getDisplayName().equals(post.getUserDisplay()))
                viewHolder.tvDisplayName.setTextColor(context.getResources().getColor(R.color.bootstrap_brand_danger));
            viewHolder.tvHour.setText(post.getHour());
            viewHolder.tvDate.setText(post.getDate());
            String email = post.getEmail();
            if (user.getEmail().equals(email)) {
                viewHolder.ivDelete.setVisibility(View.VISIBLE);
                viewHolder.ivEdit.setVisibility(View.VISIBLE);
            }
            if (!post.getUserUID().equals(user.getUid())) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("הודעה פרטית").setMessage("האם ברצונך לשלוח הודעה פרטית ל-" + post.getUserDisplay() + "?").setPositiveButton("כן", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PrivateChatFragment privateChatFragment = new PrivateChatFragment();
                                Bundle args = new Bundle();
                                args.putString("recieverUID", post.getUserUID());
                                args.putString("recieverDisplay", post.getUserDisplay());
                                privateChatFragment.setArguments(args);
                                FragmentManager fm = fragment.getFragmentManager();
                                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                                    fm.popBackStack();
                                }
                                fragment.getFragmentManager().beginTransaction().replace(R.id.frame, privateChatFragment).addToBackStack("List").commit();
                            }
                        }).setNegativeButton("לא", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                });
            }
        }

        public static class BoardViewHolder extends RecyclerView.ViewHolder {

            TextView tvDisplayName;
            TextView tvShowTitle;
            TextView tvShowArena;
            TextView tvShowDate;
            TextView tvTicketsNumber;
            TextView tvShowPrice;
            TextView tvPostContent;
            ImageView ivDelete;
            TextView tvDate;
            TextView tvHour;
            ImageView ivEdit;
            Fragment fragment;
            BoardPost model;

            public BoardViewHolder(View itemView, final Fragment fragment) {
                super(itemView);
                this.fragment = fragment;
                tvDisplayName = (TextView) itemView.findViewById(R.id.tvDisplayName);
                tvPostContent = (TextView) itemView.findViewById(R.id.tvPostContent);
                tvShowTitle = (TextView) itemView.findViewById(R.id.tvShowTitle);
                tvShowArena = (TextView) itemView.findViewById(R.id.tvShowArena);
                tvShowDate = (TextView) itemView.findViewById(R.id.tvShowDate);
                tvTicketsNumber = (TextView) itemView.findViewById(R.id.tvTicketsNumber);
                tvShowPrice = (TextView) itemView.findViewById(R.id.tvShowPrice);
                tvDate = (TextView) itemView.findViewById(R.id.tvDate);
                tvHour = (TextView) itemView.findViewById(R.id.tvHour);
                ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
                ivEdit = (ImageView) itemView.findViewById(R.id.ivEdit);
                ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
                        builder.setTitle("הסר הודעה").setMessage("האם ברצונך למחוק את ההודעה?").setPositiveButton("כן", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference("Board").child(model.getPostUID()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(fragment.getContext(), "ההודעה נמחקה!", Toast.LENGTH_SHORT).show();
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
                    }
                });
                ivEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle args = new Bundle();
                        args.putParcelable("model", model);
                        EditFullPostFragment editFullPostFragment = new EditFullPostFragment();
                        editFullPostFragment.setArguments(args);
                        editFullPostFragment.show(fragment.getChildFragmentManager(), "EditFullPost");
                    }
                });
            }
        }
    }
}
