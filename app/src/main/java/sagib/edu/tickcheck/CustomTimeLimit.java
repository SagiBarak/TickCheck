package sagib.edu.tickcheck;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CustomTimeLimit extends BottomSheetDialogFragment {

    @BindView(R.id.tvDays)
    TextView tvDays;
    @BindView(R.id.tvHours)
    TextView tvHours;
    @BindView(R.id.tvMinutes)
    TextView tvMinutes;
    @BindView(R.id.etMinutes)
    EditText etMinutes;
    @BindView(R.id.etHours)
    EditText etHours;
    @BindView(R.id.etDays)
    EditText etDays;
    @BindView(R.id.btnChooseTime)
    BootstrapButton btnChooseTime;
    SharedPreferences prefs;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_custom_time_limit, container, false);
        unbinder = ButterKnife.bind(this, v);
        prefs = getContext().getSharedPreferences("BandSwitchBoolean", Context.MODE_PRIVATE);
        int limitMinutes = prefs.getInt("Minutes", 5);
        if (limitMinutes < 60) {
            etDays.setText("0");
            etHours.setText("0");
            etMinutes.setText(String.valueOf(limitMinutes));
        } else {
            int hours = limitMinutes / 60;
            int minutes = limitMinutes % 60;
            int days = hours / 24;
            if (days != 0) {
                hours = hours % 24;
            }
            etDays.setText(String.valueOf(days));
            etHours.setText(String.valueOf(hours));
            etMinutes.setText(String.valueOf(minutes));
        }
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnChooseTime)
    public void onViewClicked() {
        if (etMinutes.getText().toString().equals("") && etHours.getText().toString().equals("") & etDays.getText().toString().equals("")) {
            etMinutes.setError("חובה לבחור דקה ומעלה");
        } else {
            if (etMinutes.getText().toString().equals("")) {
                etMinutes.setText("0");
            }
            if (etHours.getText().toString().equals("")) {
                etHours.setText("0");
            }
            if (etDays.getText().toString().equals("")) {
                etDays.setText("0");
            }
            int minutes = Integer.valueOf(etMinutes.getText().toString());
            int hours = Integer.valueOf(etHours.getText().toString());
            int days = Integer.valueOf(etDays.getText().toString());
            int total = 0;
            if (days != 0)
                total += days * 24 * 60;
            if (hours != 0)
                total += hours * 60;
            if (minutes != 0)
                total += minutes;
            prefs.edit().putInt("Minutes", total).commit();
            Intent intent = new Intent("CustomTime");
            intent.putExtra("minutes", total);
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            dismiss();
        }
    }
}
