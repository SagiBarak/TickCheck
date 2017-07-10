package sagib.edu.tickcheck;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.daasuu.bl.BubbleLayout;
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

import org.joda.time.LocalDateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;


public class PrivateChatFragment extends Fragment {


    @BindView(R.id.etPrvMessage)
    EditText etPrvMessage;
    @BindView(R.id.btnPrvSend)
    BootstrapButton btnPrvSend;
    @BindView(R.id.rvPrvChat)
    RecyclerView rvPrvChat;
    FirebaseUser sender;
    String recieverUID;
    String recieverDisplay;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_private_chat, container, false);
        unbinder = ButterKnife.bind(this, v);
        recieverUID = getArguments().getString("recieverUID");
        recieverDisplay = getArguments().getString("recieverDisplay");
        sender = FirebaseAuth.getInstance().getCurrentUser();
        btnPrvSend.setOnClickListener(null);
        btnPrvSend.setBackgroundColor(Color.GRAY);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PrivateChats");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(sender.getUid() + recieverUID)) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PrivateChats").child(sender.getUid() + recieverUID);
                    PrivateChatAdapter adapter = new PrivateChatAdapter(ref);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                            linearLayoutManager.setStackFromEnd(true);
                            rvPrvChat.setLayoutManager(linearLayoutManager);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    rvPrvChat.setAdapter(adapter);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setStackFromEnd(true);
                    rvPrvChat.setLayoutManager(layoutManager);
                } else if (dataSnapshot.hasChild(recieverUID + sender.getUid())) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PrivateChats").child(recieverUID + sender.getUid());
                    PrivateChatAdapter adapter = new PrivateChatAdapter(ref);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                            linearLayoutManager.setStackFromEnd(true);
                            rvPrvChat.setLayoutManager(linearLayoutManager);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    rvPrvChat.setAdapter(adapter);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setStackFromEnd(true);
                    rvPrvChat.setLayoutManager(layoutManager);
                } else if (!dataSnapshot.hasChild(recieverUID + sender.getUid()) && !dataSnapshot.hasChild(sender.getUid() + recieverUID)) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PrivateChats").child(sender.getUid() + recieverUID);
                    PrivateChatAdapter adapter = new PrivateChatAdapter(ref);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                            linearLayoutManager.setStackFromEnd(true);
                            rvPrvChat.setLayoutManager(linearLayoutManager);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    rvPrvChat.setAdapter(adapter);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setStackFromEnd(true);
                    rvPrvChat.setLayoutManager(layoutManager);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

    @OnTextChanged(R.id.etPrvMessage)
    public void changeButton() {
        if (etPrvMessage.getText().toString().length() < 1) {
            btnPrvSend.setOnClickListener(null);
            btnPrvSend.setBackgroundColor(Color.GRAY);
        } else {
            btnPrvSend.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
            btnPrvSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PrivateChats");
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(sender.getUid() + recieverUID)) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PrivateChats").child(sender.getUid() + recieverUID).push();
                                PrivateChatListItem sending = new PrivateChatListItem(recieverDisplay, sender.getUid() + recieverUID, recieverUID);
                                PrivateChatListItem recieving = new PrivateChatListItem(sender.getDisplayName(), sender.getUid() + recieverUID, sender.getUid());
                                if (!FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(recieverUID).child(sender.getUid()).toString().contains("otherUserUID: " + sender.getUid())) {
                                    FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(recieverUID).child(sender.getUid()).setValue(recieving);
                                }
                                if (!FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(sender.getUid()).child(recieverUID).toString().contains("otherUserUID: " + recieverUID)) {
                                    FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(sender.getUid()).child(recieverUID).setValue(sending);
                                }
                                String prvMessageUID = ref.getKey();
                                PrivateMessage privateMessage = new PrivateMessage(sender.getUid(), recieverUID, sender.getDisplayName(), recieverDisplay, LocalDateTime.now().toString("dd/MM/yy"), LocalDateTime.now().toString("HH:mm"), etPrvMessage.getText().toString(), prvMessageUID);
                                ref.setValue(privateMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        int position = rvPrvChat.getAdapter().getItemCount();
                                        rvPrvChat.getLayoutManager().scrollToPosition(position - 1);
                                    }
                                });
                                etPrvMessage.setText(null);
                                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                            } else if (dataSnapshot.hasChild(recieverUID + sender.getUid())) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PrivateChats").child(recieverUID + sender.getUid()).push();
                                PrivateChatListItem sending = new PrivateChatListItem(recieverDisplay, sender.getUid() + recieverUID, recieverUID);
                                PrivateChatListItem recieving = new PrivateChatListItem(sender.getDisplayName(), sender.getUid() + recieverUID, sender.getUid());
                                if (!FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(recieverUID).child(sender.getUid()).toString().contains("otherUserUID: " + sender.getUid())) {
                                    FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(recieverUID).child(sender.getUid()).setValue(recieving);
                                }
                                if (!FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(sender.getUid()).child(recieverUID).toString().contains("otherUserUID: " + recieverUID)) {
                                    FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(sender.getUid()).child(recieverUID).setValue(sending);
                                }
                                String prvMessageUID = ref.getKey();
                                PrivateMessage privateMessage = new PrivateMessage(sender.getUid(), recieverUID, sender.getDisplayName(), recieverDisplay, LocalDateTime.now().toString("dd/MM/yy"), LocalDateTime.now().toString("HH:mm"), etPrvMessage.getText().toString(), prvMessageUID);
                                ref.setValue(privateMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        int position = rvPrvChat.getAdapter().getItemCount();
                                        rvPrvChat.getLayoutManager().scrollToPosition(position - 1);
                                    }
                                });
                                etPrvMessage.setText(null);
                                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                            } else if (!dataSnapshot.hasChild(recieverUID + sender.getUid()) && !dataSnapshot.hasChild(sender.getUid() + recieverUID)) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PrivateChats").child(sender.getUid() + recieverUID).push();
                                PrivateChatListItem sending = new PrivateChatListItem(recieverDisplay, sender.getUid() + recieverUID, recieverUID);
                                PrivateChatListItem recieving = new PrivateChatListItem(sender.getDisplayName(), sender.getUid() + recieverUID, sender.getUid());
                                if (!FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(recieverUID).child(sender.getUid()).toString().contains("otherUserUID: " + sender.getUid())) {
                                    FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(recieverUID).child(sender.getUid()).setValue(recieving);
                                }
                                if (!FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(sender.getUid()).child(recieverUID).toString().contains("otherUserUID: " + recieverUID)) {
                                    FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(sender.getUid()).child(recieverUID).setValue(sending);
                                }
                                String prvMessageUID = ref.getKey();
                                PrivateMessage privateMessage = new PrivateMessage(sender.getUid(), recieverUID, sender.getDisplayName(), recieverDisplay, LocalDateTime.now().toString("dd/MM/yy"), LocalDateTime.now().toString("HH:mm"), etPrvMessage.getText().toString(), prvMessageUID);
                                ref.setValue(privateMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        int position = rvPrvChat.getAdapter().getItemCount();
                                        rvPrvChat.getLayoutManager().scrollToPosition(position - 1);
                                    }
                                });
                                etPrvMessage.setText(null);
                                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("שיחה פרטית עם " + recieverDisplay);
    }

    public static class PrivateChatAdapter extends FirebaseRecyclerAdapter<PrivateMessage, PrivateChatAdapter.PrivateChatViewHolder> {
        public PrivateChatAdapter(Query query) {
            super(PrivateMessage.class, R.layout.private_message_item, PrivateChatViewHolder.class, query);
        }

        @Override
        protected void populateViewHolder(PrivateChatViewHolder viewHolder, PrivateMessage model, int position) {
            if (model.getSenderUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                viewHolder.tvDate.setText(model.getDate());
                viewHolder.tvMessage.setText(model.getMessage());
                viewHolder.tvSender.setText(model.getSenderDisplayName());
                viewHolder.tvTime.setText(model.getTime());
                viewHolder.tvMyDate.setVisibility(View.GONE);
                viewHolder.tvMyMessage.setVisibility(View.GONE);
                viewHolder.tvMyName.setVisibility(View.GONE);
                viewHolder.tvMyTime.setVisibility(View.GONE);
                viewHolder.blMe.setVisibility(View.GONE);
            } else {
                viewHolder.tvMyDate.setText(model.getDate());
                viewHolder.tvMyMessage.setText(model.getMessage());
                viewHolder.tvMyName.setText(model.getSenderDisplayName());
                viewHolder.tvMyTime.setText(model.getTime());
                viewHolder.tvDate.setVisibility(View.GONE);
                viewHolder.tvMessage.setVisibility(View.GONE);
                viewHolder.tvSender.setVisibility(View.GONE);
                viewHolder.tvTime.setVisibility(View.GONE);
                viewHolder.blOther.setVisibility(View.GONE);
            }
        }

        public static class PrivateChatViewHolder extends RecyclerView.ViewHolder {

            TextView tvTime;
            TextView tvSender;
            TextView tvDate;
            TextView tvMessage;
            TextView tvMyName;
            TextView tvMyMessage;
            TextView tvMyTime;
            TextView tvMyDate;
            BubbleLayout blOther;
            BubbleLayout blMe;

            public PrivateChatViewHolder(View v) {
                super(v);
                tvTime = (TextView) v.findViewById(R.id.tvTime);
                tvSender = (TextView) v.findViewById(R.id.tvSender);
                tvDate = (TextView) v.findViewById(R.id.tvDate);
                tvMessage = (TextView) v.findViewById(R.id.tvMessage);
                tvMyMessage = (TextView) v.findViewById(R.id.tvMyMessage);
                tvMyName = (TextView) v.findViewById(R.id.tvMyName);
                tvMyTime = (TextView) v.findViewById(R.id.tvMyTime);
                tvMyDate = (TextView) v.findViewById(R.id.tvMyDate);
                blOther = (BubbleLayout) v.findViewById(R.id.blOther);
                blMe = (BubbleLayout) v.findViewById(R.id.blMe);
            }
        }
    }
}