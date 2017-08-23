package sagib.edu.tickcheck;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import sagib.edu.tickcheck.models.BoardPost;

public class NewBoardFragment extends Fragment {

    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.btnSearch)
    BootstrapButton btnSearch;
    @BindView(R.id.btnSend)
    BootstrapButton btnSend;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    LinearLayoutManager llManager;
    BoardAdapter adapter;
    boolean isFiltered = false;
    ArrayList<BoardPost> originalBoard = new ArrayList<>();
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_board, container, false);
        unbinder = ButterKnife.bind(this, v);
        llManager = new LinearLayoutManager(getContext());
        llManager.setStackFromEnd(true);
        llManager.setReverseLayout(true);
        recycler.setLayoutManager(llManager);
        btnSearch.setClickable(false);
        btnSearch.setText("חיפוש");
        btnSearch.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
        FirebaseDatabase.getInstance().getReference("Board").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                BoardPost value = dataSnapshot.getValue(BoardPost.class);
                addToArrayList(value);
                setAdapter();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                BoardPost value = dataSnapshot.getValue(BoardPost.class);
                editBoardPostOnList(value);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                BoardPost value = dataSnapshot.getValue(BoardPost.class);
                removeFromList(value);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etSearch.getText().length() > 0) {
                    btnSearch.setClickable(true);
                    btnSearch.setText("חיפוש");
                    btnSearch.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                }
                if (etSearch.getText().length() == 0 && !isFiltered) {
                    btnSearch.setClickable(false);
                    btnSearch.setText("חיפוש");
                    btnSearch.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                }
                if (etSearch.getText().length() == 0 && isFiltered) {
                    btnSearch.setText("ניקוי");
                    btnSearch.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return v;
    }

    private void editBoardPostOnList(BoardPost value) {
        for (int i = 0; i < originalBoard.size(); i++) {
            if (originalBoard.get(i).getPostUID().equals(value.getPostUID())) {
                originalBoard.set(i, value);
            }
        }
        setAdapter();
    }

    private void removeFromList(BoardPost value) {
        for (int i = 0; i < originalBoard.size(); i++) {
            if (originalBoard.get(i).getPostUID().equals(value.getPostUID())) {
                originalBoard.remove(i);
            }
        }
        setAdapter();
    }

    private void setAdapter() {
        adapter = new BoardAdapter(originalBoard, this, getContext());
        recycler.setAdapter(adapter);
    }

    private void addToArrayList(BoardPost post) {
        originalBoard.add(post);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnSearch)
    public void onBtnSearchClicked() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        if (etSearch.getText().toString().length() > 0) {
            String tags = etSearch.getText().toString();
            ArrayList<BoardPost> filteredBoard = new ArrayList<>();
            for (BoardPost boardPost : originalBoard) {
                if (boardPost.getContents().contains(tags) || boardPost.getShowTitle().contains(tags) || boardPost.getShowArena().contains(tags)) {
                    filteredBoard.add(boardPost);
                }
            }
            BoardAdapter filteredAdapter = new BoardAdapter(filteredBoard, this, getContext());
            recycler.setAdapter(filteredAdapter);
            isFiltered = true;
        }
        if (etSearch.getText().toString().length() == 0 && isFiltered) {
            recycler.setAdapter(adapter);
            isFiltered = false;
            btnSearch.setClickable(false);
            btnSearch.setText("חיפוש");
            btnSearch.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
        }
        etSearch.setText("");
    }

    @OnClick(R.id.btnSend)
    public void onBtnSendClicked() {
        NewPostFragment newPostFragment = new NewPostFragment();
        newPostFragment.show(getChildFragmentManager(), "NewPost");
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("לוח מכירת כרטיסים");

    }


    public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {

        private ArrayList<BoardPost> data;
        private Context context;
        private LayoutInflater inflater;
        private Fragment fragment;
        private SharedPreferences prefs;
        private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        public BoardAdapter(ArrayList<BoardPost> data, Fragment fragment, Context context) {
            this.data = data;
            this.fragment = fragment;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public BoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.board_item, parent, false);
            return new BoardViewHolder(v, fragment);
        }

        @Override
        public void onBindViewHolder(BoardViewHolder viewHolder, int position) {
            prefs = context.getSharedPreferences("SearchForPost", Context.MODE_PRIVATE);
            final BoardPost post = data.get(position);
            viewHolder.model = post;
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
                        builder.setMessage("האם ברצונך לשלוח הודעה פרטית ל-" + post.getUserDisplay() + "?").setPositiveButton("כן", new DialogInterface.OnClickListener() {
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
                                fragment.getFragmentManager().beginTransaction().replace(R.id.frame, privateChatFragment).commit();
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
            String tags = prefs.getString("Tags", null);
            if (tags != null) {
                Log.d("SagiB", post.toString());
                if (post.getContents() != null) {
                    if (!post.getContents().contains(tags) && !post.getShowArena().contains(tags) && !post.getShowTitle().contains(tags)) {
                        viewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    }
                } else {
                    if (!post.getShowArena().contains(tags) && !post.getShowTitle().contains(tags)) {
                        viewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class BoardViewHolder extends RecyclerView.ViewHolder {
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
                tvDisplayName = (TextView) itemView.findViewById(R.id.tvDisplayNameTitle);
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
                        builder.setMessage("האם ברצונך למחוק את המודעה?").setPositiveButton("כן", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference("Board").child(model.getPostUID()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(fragment.getContext(), "המודעה נמחקה!", Toast.LENGTH_SHORT).show();
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
