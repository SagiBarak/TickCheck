package sagib.edu.tickcheck;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PrivateChatsListFragment extends Fragment {

    @BindView(R.id.rvChatsList)
    RecyclerView rvChatsList;
    Unbinder unbinder;
    @BindView(R.id.fabNewChat)
    FloatingActionButton fabNewChat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_private_chats_list, container, false);
        unbinder = ButterKnife.bind(this, v);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        PrivateChatsListAdapter adapter = new PrivateChatsListAdapter(ref, this);
        rvChatsList.setAdapter(adapter);
        rvChatsList.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("שיחות פרטיות");
    }

    @OnClick(R.id.fabNewChat)
    public void onFabNewChatClicked() {
        getFragmentManager().beginTransaction().replace(R.id.frame, new UsersListFragment()).addToBackStack("UsersList").commit();
    }

    public static class PrivateChatsListAdapter extends FirebaseRecyclerAdapter<PrivateChatListItem, PrivateChatsListAdapter.PrivateChatsListViewHolder> {

        Fragment fragment;

        public PrivateChatsListAdapter(Query query, Fragment fragment) {
            super(PrivateChatListItem.class, R.layout.private_chat_list_item, PrivateChatsListViewHolder.class, query);
            this.fragment = fragment;
        }

        @Override
        protected void populateViewHolder(final PrivateChatsListViewHolder viewHolder, final PrivateChatListItem model, int position) {
            viewHolder.tvChatter.setText(model.getOtherUserDisplay());
            FirebaseDatabase.getInstance().getReference("Users").child(model.getOtherUserUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Picasso.with(viewHolder.itemView.getContext()).load(user.getProfileImage()).into(viewHolder.civProfileImageList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrivateChatFragment privateChatFragment = new PrivateChatFragment();
                    Bundle args = new Bundle();
                    args.putString("recieverUID", model.getOtherUserUID());
                    args.putString("recieverDisplay", model.getOtherUserDisplay());
                    privateChatFragment.setArguments(args);
                    fragment.getFragmentManager().beginTransaction().replace(R.id.frame, privateChatFragment).addToBackStack("List").commit();
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.itemView.getContext());
                    builder.setTitle("מחיקת שיחה").setMessage("האם ברצונך למחוק את השיחה עם " + model.getOtherUserDisplay() + " מהרשימה?\nהערה: השיחה עדיין תופיע ברשימה של " + model.getOtherUserDisplay() + ".").setNegativeButton("לא", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getOtherUserUID()).removeValue();
                            dialog.dismiss();
                        }
                    }).show();
                    return false;
                }
            });
        }

        public static class PrivateChatsListViewHolder extends RecyclerView.ViewHolder {
            TextView tvChatter;
            CircularImageView civProfileImageList;

            public PrivateChatsListViewHolder(View itemView) {
                super(itemView);
                tvChatter = (TextView) itemView.findViewById(R.id.tvChatter);
                civProfileImageList = (CircularImageView) itemView.findViewById(R.id.civProfileImageList);
            }
        }
    }
}
