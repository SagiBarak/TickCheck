package sagib.edu.tickcheck;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.joda.time.LocalDateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
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
    Toolbar toolbar;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_private_chat, container, false);
        unbinder = ButterKnife.bind(this, v);
        recieverUID = getArguments().getString("recieverUID");
        recieverDisplay = getArguments().getString("recieverDisplay");
        sender = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = null;
        if (FirebaseDatabase.getInstance().getReference("PrivateChats").child(sender.getUid() + recieverUID).toString().isEmpty() && FirebaseDatabase.getInstance().getReference("PrivateChats").child(recieverUID + sender.getUid()).toString().isEmpty()) {
            ref = FirebaseDatabase.getInstance().getReference("PrivateChats").child(sender.getUid() + recieverUID).push();
            Log.d("Sagi", "first");
        } else if (FirebaseDatabase.getInstance().getReference("PrivateChats").child(sender.getUid() + recieverUID).toString().isEmpty()) {
            ref = FirebaseDatabase.getInstance().getReference("PrivateChats").child(recieverUID + sender.getUid());
            Log.d("Sagi", "second");
        } else if (FirebaseDatabase.getInstance().getReference("PrivateChats").child(recieverUID + sender.getUid()).toString().isEmpty()) {
            ref = FirebaseDatabase.getInstance().getReference("PrivateChats").child(sender.getUid() + recieverUID);
            Log.d("Sagi", "third");
        } else {
            ref = FirebaseDatabase.getInstance().getReference("PrivateChats").child(sender.getUid() + recieverUID);
        }

        PrivateChatAdapter adapter = new PrivateChatAdapter(ref);
        rvPrvChat.setAdapter(adapter);
        rvPrvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("שיחה פרטית עם " + recieverDisplay);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnPrvSend)
    public void onBtnPrvSend() {
        DatabaseReference ref = null;
        if (FirebaseDatabase.getInstance().getReference("PrivateChats").child(sender.getUid() + recieverUID).toString().isEmpty() && FirebaseDatabase.getInstance().getReference("PrivateChats").child(recieverUID + sender.getUid()).toString().isEmpty()) {
            ref = FirebaseDatabase.getInstance().getReference("PrivateChats").child(sender.getUid() + recieverUID).push();
        } else if (FirebaseDatabase.getInstance().getReference("PrivateChats").child(sender.getUid() + recieverUID).toString().isEmpty()) {
            ref = FirebaseDatabase.getInstance().getReference("PrivateChats").child(recieverUID + sender.getUid()).push();
        } else if (FirebaseDatabase.getInstance().getReference("PrivateChats").child(recieverUID + sender.getUid()).toString().isEmpty()) {
            ref = FirebaseDatabase.getInstance().getReference("PrivateChats").child(sender.getUid() + recieverUID).push();
        } else {
            ref = FirebaseDatabase.getInstance().getReference("PrivateChats").child(sender.getUid() + recieverUID).push();
            PrivateChatListItem sending = new PrivateChatListItem(recieverDisplay, sender.getUid() + recieverUID, recieverUID);
            PrivateChatListItem recieving = new PrivateChatListItem(sender.getDisplayName(), sender.getUid() + recieverUID, sender.getUid());
            FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(recieverUID).push().setValue(recieving);
            FirebaseDatabase.getInstance().getReference("PrivateChatsLists").child(sender.getUid()).push().setValue(sending);
        }
        String prvMessageUID = ref.getKey();
        PrivateMessage privateMessage = new PrivateMessage(sender.getUid(), recieverUID, sender.getDisplayName(), recieverDisplay, LocalDateTime.now().toString("dd/MM/yy"), LocalDateTime.now().toString("HH:mm"), etPrvMessage.getText().toString(), prvMessageUID);
        ref.setValue(privateMessage);
        etPrvMessage.setText(null);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    public static class PrivateChatAdapter extends FirebaseRecyclerAdapter<PrivateMessage, PrivateChatAdapter.PrivateChatViewHolder> {
        public PrivateChatAdapter(Query query) {
            super(PrivateMessage.class, R.layout.private_message_item, PrivateChatViewHolder.class, query);
        }

        @Override
        protected void populateViewHolder(PrivateChatViewHolder viewHolder, PrivateMessage model, int position) {
            viewHolder.tvDate.setText(model.getDate());
            viewHolder.tvMessage.setText(model.getMessage());
            viewHolder.tvSender.setText(model.getSenderDisplayName());
            viewHolder.tvTime.setText(model.getTime());
            if (model.getSenderUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                viewHolder.tvSender.setTextColor(Color.RED);
        }

        public static class PrivateChatViewHolder extends RecyclerView.ViewHolder {

            TextView tvTime;
            TextView tvSender;
            TextView tvDate;
            TextView tvMessage;

            public PrivateChatViewHolder(View v) {
                super(v);
                tvTime = (TextView) v.findViewById(R.id.tvTime);
                tvSender = (TextView) v.findViewById(R.id.tvSender);
                tvDate = (TextView) v.findViewById(R.id.tvDate);
                tvMessage = (TextView) v.findViewById(R.id.tvMessage);
            }
        }
    }

}
