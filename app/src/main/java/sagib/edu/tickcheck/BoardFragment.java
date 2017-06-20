package sagib.edu.tickcheck;


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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_board, container, false);
        unbinder = ButterKnife.bind(this, v);
        database = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();
        setupRecycler();
        return v;
    }

    private void setupRecycler() {
        adapter = new BoardAdapter(database.getReference("Board"));
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
        BoardPost post = new BoardPost(title, content, email);
        database.getReference("Board").push().setValue(post);
        etMessage.setText(null);

    }

    public static class BoardAdapter extends FirebaseRecyclerAdapter<BoardPost, BoardAdapter.BoardViewHolder> {
        public BoardAdapter(Query ref) {
            super(BoardPost.class, R.layout.board_item, BoardViewHolder.class, ref);
        }


        @Override
        protected void populateViewHolder(final BoardViewHolder viewHolder, BoardPost post, final int position) {
            viewHolder.tvPostContent.setText(post.getContents());
            viewHolder.tvDisplayName.setText(post.getTitle());
        }

        public static class BoardViewHolder extends RecyclerView.ViewHolder {

            TextView tvDisplayName;
            TextView tvPostContent;
            ImageView ivDelete;

            public BoardViewHolder(View itemView) {
                super(itemView);
                tvDisplayName = (TextView) itemView.findViewById(R.id.tvDisplayName);
                tvPostContent = (TextView) itemView.findViewById(R.id.tvPostContent);
                ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
            }
        }
    }
}
