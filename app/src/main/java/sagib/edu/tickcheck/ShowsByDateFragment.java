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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.gson.Gson;

import org.joda.time.LocalDate;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import sagib.edu.tickcheck.models.MyDate;
import sagib.edu.tickcheck.models.Show;

public class ShowsByDateFragment extends Fragment implements ShowByDateDataSource.OnShowArrivedListener {

    Unbinder unbinder;
    SharedPreferences prefs;
    @BindView(R.id.btnDateDialog)
    BootstrapButton btnDateDialog;
    @BindView(R.id.rvShows)
    RecyclerView rvShows;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shows_by_date, container, false);
        unbinder = ButterKnife.bind(this, v);
        prefs = getContext().getSharedPreferences("ShowsDate", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("נא להמתין,\nמרענן רשימת הופעות..." + "\n");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Gson gson = new Gson();
        LocalDate now = LocalDate.now();
        MyDate nowDate = new MyDate(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());
        String myDateParsing = prefs.getString("Date", "");
        MyDate date = nowDate;
        if (!myDateParsing.equals("")) {
            date = gson.fromJson(myDateParsing, MyDate.class);
        }
        LocalDate before = new LocalDate(date.getYear(), date.getMonth(), date.getDay());
        if (date.getYear() == 0 || before.isBefore(now)) {
            btnDateDialog.setText("הופעות בתאריך: " + nowDate.toString());
        } else {
            btnDateDialog.setText("הופעות בתאריך: " + date.toString());
        }
        ShowByDateDataSource.getShows(this, getContext());
        rvShows = (RecyclerView) v.findViewById(R.id.rvShows);
        rvShows.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setDistanceToTriggerSync(600);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dialog.show();
                ShowByDateDataSource.getShows(ShowsByDateFragment.this, getContext());
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("הופעות לפי תאריך");
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnDateDialog)
    public void onBtnDateDialogClicked() {
        ChooseDateDialogFragment chooseDateDialogFragment = new ChooseDateDialogFragment();
        chooseDateDialogFragment.show(getChildFragmentManager(), "DateChooserDialog");
    }

    @Override
    public void onShowArrived(final ArrayList<Show> data, final Exception e) {
        final Fragment fragment = this;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e == null) {
                    ShowAdapter adapter = new ShowAdapter(data, getContext(), fragment);
                    rvShows.setAdapter(adapter);
                    dialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("הופעות לפי תאריך");
    }
}
