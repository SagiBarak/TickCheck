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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersListFragment extends Fragment {

    @BindView(R.id.rvUsersList)
    RecyclerView rvUsersList;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_users_list, container, false);
        UsersListAdapter adapter = new UsersListAdapter(FirebaseDatabase.getInstance().getReference("Users"), this);
        rvUsersList = (RecyclerView) v.findViewById(R.id.rvUsersList);
        rvUsersList.setAdapter(adapter);
        rvUsersList.setLayoutManager(new LinearLayoutManager(getContext()));
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static class UsersListAdapter extends FirebaseRecyclerAdapter<User, UsersListAdapter.UsersListViewHolder> {
        Fragment fragment;

        public UsersListAdapter(Query query, Fragment fragment) {
            super(User.class, R.layout.user_list_item, UsersListViewHolder.class, query);
            this.fragment = fragment;
        }

        @Override
        protected void populateViewHolder(final UsersListViewHolder viewHolder, final User model, int position) {
            viewHolder.tvUserName.setText(model.getDisplayName());
            if (model.getProfileImage().isEmpty()) {
                viewHolder.civProfileImage.setImageDrawable(viewHolder.itemView.getContext().getResources().getDrawable(R.drawable.ic_profile));
            } else
                Picasso.with(viewHolder.itemView.getContext()).load(model.getProfileImage()).into(viewHolder.civProfileImage);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!model.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        PrivateChatFragment privateChatFragment = new PrivateChatFragment();
                        Bundle args = new Bundle();
                        args.putString("recieverUID", model.getUid());
                        args.putString("recieverDisplay", model.getDisplayName());
                        privateChatFragment.setArguments(args);
                        fragment.getFragmentManager().beginTransaction().replace(R.id.frame, privateChatFragment).addToBackStack("List").commit();
                    }
                }
            });
        }

        public static class UsersListViewHolder extends RecyclerView.ViewHolder {

            TextView tvUserName;
            CircularImageView civProfileImage;

            public UsersListViewHolder(View itemView) {
                super(itemView);
                tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
                civProfileImage = (CircularImageView) itemView.findViewById(R.id.civProfileImageList);
            }
        }
    }
}
