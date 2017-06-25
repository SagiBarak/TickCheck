package sagib.edu.tickcheck;

// TODO: check facebook
// TODO: refresh isn't showing up in the menu.

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.LocalDateTime;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class BoardFragment extends Fragment {

    FirebaseUser user;
    FirebaseDatabase database;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    BoardAdapter adapter;
    @BindView(R.id.etMessage)
    EditText etMessage;
    @BindView(R.id.tilMessage)
    TextInputLayout tilMessage;
    @BindView(R.id.btnSend)
    BootstrapButton btnSend;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    Unbinder unbinder;
    private ProgressDialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_board, container, false);
        unbinder = ButterKnife.bind(this, v);
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("טוען רשימת הודעות...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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
        setupRecycler();
        return v;
    }

    private void setupRecycler() {
        adapter = new BoardAdapter(database.getReference("Board"), dialog, getContext());
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick(R.id.btnSend)
    public void onBtnSendClicked() {
        String content = etMessage.getText().toString() + "\n";
        if (TextUtils.isEmpty(content)) return;
        String title = user.getDisplayName();
        String email = user.getEmail();
        String hour = LocalDateTime.now().toString("HH:mm");
        String date = LocalDateTime.now().toString("dd/MM/yy");
        BoardPost post = new BoardPost(title, content, email, hour, date);
        database.getReference("Board").push().setValue(post);
        etMessage.setText(null);

    }

    public static class BoardAdapter extends FirebaseRecyclerAdapter<BoardPost, BoardAdapter.BoardViewHolder> {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Context context;
        ProgressDialog dialog;

        public BoardAdapter(Query ref, ProgressDialog dialog, Context context) {
            super(BoardPost.class, R.layout.board_item, BoardViewHolder.class, ref);
            this.context = context;
            this.dialog = dialog;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void populateViewHolder(final BoardViewHolder viewHolder, BoardPost post, final int position) {
            dialog.dismiss();
            viewHolder.ivDelete.setVisibility(View.GONE);
            viewHolder.ivEdit.setVisibility(View.GONE);
            viewHolder.tvPostContent.setText(post.getContents());
            viewHolder.tvDisplayName.setText(post.getTitle());
            if (user.getDisplayName().equals(post.getTitle()))
                viewHolder.tvDisplayName.setTextColor(Color.RED);
            viewHolder.tvHour.setText(post.getHour());
            viewHolder.tvDate.setText(post.getDate());
            String email = post.getEmail();
            if (user.getEmail().equals(email)) {
                viewHolder.ivDelete.setVisibility(View.VISIBLE);
                viewHolder.ivEdit.setVisibility(View.VISIBLE);
            }
        }

        public static class BoardViewHolder extends RecyclerView.ViewHolder {

            TextView tvDisplayName;
            TextView tvPostContent;
            ImageView ivDelete;
            TextView tvDate;
            TextView tvHour;
            ImageView ivEdit;

            public BoardViewHolder(View itemView) {
                super(itemView);
                tvDisplayName = (TextView) itemView.findViewById(R.id.tvDisplayName);
                tvPostContent = (TextView) itemView.findViewById(R.id.tvPostContent);
                tvDate = (TextView) itemView.findViewById(R.id.tvDate);
                tvHour = (TextView) itemView.findViewById(R.id.tvHour);
                ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
                ivEdit = (ImageView) itemView.findViewById(R.id.ivEdit);
            }
        }
    }
}