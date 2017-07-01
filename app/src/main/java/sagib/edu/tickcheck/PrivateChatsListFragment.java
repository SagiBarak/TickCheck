package sagib.edu.tickcheck;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class PrivateChatsListFragment extends Fragment {

    @BindView(R.id.rvChatsList)
    RecyclerView rvChatsList;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

    public static class PrivateChatsListAdapter extends FirebaseRecyclerAdapter<PrivateChatListItem, PrivateChatsListAdapter.PrivateChatsListViewHolder> {

        Fragment fragment;

        public PrivateChatsListAdapter(Query query, Fragment fragment) {
            super(PrivateChatListItem.class, R.layout.private_chat_list_item, PrivateChatsListViewHolder.class, query);
            this.fragment = fragment;
        }

        @Override
        protected void populateViewHolder(PrivateChatsListViewHolder viewHolder, final PrivateChatListItem model, int position) {
            viewHolder.tvChatter.setText(model.getOtherUserDisplay());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrivateChatFragment privateChatFragment = new PrivateChatFragment();
                    Bundle args = new Bundle();
                    args.putString("recieverUID", model.getOtherUserUID());
                    args.putString("recieverDisplay", model.getOtherUserDisplay());
                    privateChatFragment.setArguments(args);
                    fragment.getFragmentManager().beginTransaction().replace(R.id.frame, privateChatFragment).commit();
                }
            });
        }

        public static class PrivateChatsListViewHolder extends RecyclerView.ViewHolder {
            TextView tvChatter;

            public PrivateChatsListViewHolder(View itemView) {
                super(itemView);
                tvChatter = (TextView) itemView.findViewById(R.id.tvChatter);
            }
        }
    }
}
