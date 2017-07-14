package sagib.edu.tickcheck;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShowsFragment extends Fragment implements ShowDataSource.OnShowArrivedListener {
    RecyclerView recycler;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    Unbinder unbinder;
    @BindView(R.id.tvTitleShows)
    TextView tvTitleShows;
    private ProgressDialog dialog;
    SharedPreferences prefs;
    String performer;
    private AdView mAdView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shows, container, false);
        mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        prefs = getContext().getSharedPreferences("DefaultPerformer", Context.MODE_PRIVATE);
        performer = prefs.getString("PerformerTitle", "שלמה ארצי");
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("נא להמתין,\nמרענן רשימת הופעות..." + "\n" + "מומלץ להתחבר לרשת אלחוטית.");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dialog.show();
                ShowDataSource.getShows(ShowsFragment.this, getContext());
                performer = prefs.getString("PerformerTitle", "שלמה ארצי");
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(performer);

            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        recycler = (RecyclerView) v.findViewById(R.id.recycler);
        ShowDataSource.getShows(this, getContext());
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                ShowDataSource.getShows(ShowsFragment.this, getContext());
                return false;
            }
        });
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(performer);
    }

    @Override
    public void onShowArrived(final ArrayList<Show> data, final Exception e) {
        final Fragment fragment = this;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e == null) {
                    ShowAdapter adapter = new ShowAdapter(data, getContext(), fragment);
                    recycler.setAdapter(adapter);
                    if (data.size() <= 0) {
                        tvTitleShows.setText("אין הופעות ברשימה");
                    } else if (data.size() > 0){
                        tvTitleShows.setText("רשימת הופעות");
                    }
                    dialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("Sagi", e.toString());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
