package sagib.edu.tickcheck;


import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import sagib.edu.tickcheck.models.PrivateMessage;
import sagib.edu.tickcheck.models.User;

public class PrivateChatsListFragment extends Fragment {

    @BindView(R.id.rvChatsList)
    RecyclerView rvChatsList;
    Unbinder unbinder;
    @BindView(R.id.fabNewChat)
    FloatingActionButton fabNewChat;
    @BindView(R.id.tvNoChats)
    TextView tvNoChats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_private_chats_list, container, false);
        unbinder = ButterKnife.bind(this, v);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        PrivateChatsListAdapter adapter = new PrivateChatsListAdapter(ref.orderByChild("date"), this);
        rvChatsList.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        rvChatsList.setLayoutManager(linearLayoutManager);
        FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (tvNoChats != null) {
                    if (dataSnapshot.getChildrenCount() == 0) {
                        tvNoChats.setVisibility(View.VISIBLE);
                    }
                    if (!dataSnapshot.exists()) {
                        tvNoChats.setVisibility(View.VISIBLE);
                    } else {
                        tvNoChats.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(R.layout.abs_layout);
        ((TextView) ((AppCompatActivity) getActivity()).getSupportActionBar().getCustomView().findViewById(R.id.mytext)).setText("שיחות פרטיות");
    }

    @OnClick(R.id.fabNewChat)
    public void onFabNewChatClicked() {
        FragmentManager fm = getFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
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
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference messages = database.getReference("PrivateChats").child(model.getPrivateChatUID());
            messages.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        DataSnapshot messageSnapShot = dataSnapshot.getChildren().iterator().next();
                        PrivateMessage value = messageSnapShot.getValue(PrivateMessage.class);
                        viewHolder.tvLastMessage.setText(value.getMessage());
                        viewHolder.tvLastHour.setText(value.getTime());
                        viewHolder.tvLastDate.setText(value.getDate());
                        if (value.getSenderUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            viewHolder.tvLastMessage.setTextColor(Color.rgb(92, 184, 92));
                            viewHolder.tvLastDate.setTextColor(Color.rgb(92, 184, 92));
                            viewHolder.tvLastHour.setTextColor(Color.rgb(92, 184, 92));
                        } else {
                            viewHolder.tvLastMessage.setTextColor(Color.rgb(91, 192, 222));
                            viewHolder.tvLastDate.setTextColor(Color.rgb(91, 192, 222));
                            viewHolder.tvLastHour.setTextColor(Color.rgb(91, 192, 222));
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            FirebaseDatabase.getInstance().getReference("Users").child(model.getOtherUserUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        viewHolder.civProfileImageList.setImageURI(user.getProfileImage());
                    } else {
                        Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/tickcheck-2bdf2.appspot.com/o/ProfilePictures%2Fdefault_profile.jpg?alt=media&token=72b274a4-8a84-446f-ade4-dfafb3c8c06c");
                        viewHolder.civProfileImageList.setImageURI(uri);
                    }
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
                    FragmentManager fm = fragment.getFragmentManager();
                    for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                        fm.popBackStack();
                    }
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
            TextView tvLastMessage;
            TextView tvLastHour;
            TextView tvLastDate;
            SimpleDraweeView civProfileImageList;

            public PrivateChatsListViewHolder(View itemView) {
                super(itemView);
                tvChatter = (TextView) itemView.findViewById(R.id.tvChatter);
                tvLastMessage = (TextView) itemView.findViewById(R.id.tvLastMessage);
                tvLastDate = (TextView) itemView.findViewById(R.id.tvLastDate);
                tvLastHour = (TextView) itemView.findViewById(R.id.tvLastHour);
                civProfileImageList = (SimpleDraweeView) itemView.findViewById(R.id.civProfileImageList);
            }
        }
    }
}
