package sagib.edu.tickcheck;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.gson.Gson;

import org.joda.time.LocalDate;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseDateDialogFragment extends DialogFragment {

    @BindView(R.id.datePicker)
    DatePicker datePicker;
    @BindView(R.id.btnChooseDate)
    BootstrapButton btnChooseDate;
    Unbinder unbinder;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_choose_date_dialog, container, false);
        unbinder = ButterKnife.bind(this, v);
        prefs = getContext().getSharedPreferences("ShowsDate", Context.MODE_PRIVATE);
        LocalDate date = LocalDate.now();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnChooseDate)
    public void onBtnChooseDate() {
        MyDate date = new MyDate(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
        Gson gson = new Gson();
        String dateJson = gson.toJson(date);
        LocalDate now = LocalDate.now();
        MyDate nowDate = new MyDate(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());
        MyDate chosenDate = gson.fromJson(prefs.getString("Date", nowDate.toString()), MyDate.class);
        final MyDate myDate;
        if (chosenDate.getYear() != 0) {
            prefs.edit().putString("LastDate", gson.toJson(chosenDate)).commit();
        }
        prefs.edit().putString("Date", dateJson).commit();
        dismiss();
        FragmentManager fm = getParentFragment().getFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        getParentFragment().getFragmentManager().beginTransaction().replace(R.id.frame, new ShowsByDateFragment(), "ShowsByDate").commit();
    }
}
