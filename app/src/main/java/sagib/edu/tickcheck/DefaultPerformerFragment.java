package sagib.edu.tickcheck;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DefaultPerformerFragment extends DialogFragment {

    SharedPreferences prefs;
    @BindView(R.id.btnChoose)
    BootstrapButton btnChoose;
    @BindView(R.id.etPerformer)
    AutoCompleteTextView etPerformer;
    Unbinder unbinder;
    @BindView(R.id.tvDeleteHistory)
    TextView tvDeleteHistory;
    boolean dropdownShowing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_default_performer, container, false);
        unbinder = ButterKnife.bind(this, v);
        prefs = getContext().getSharedPreferences("DefaultPerformer", Context.MODE_PRIVATE);
        String performers = prefs.getString("RecentPerformers", "");
        String[] split = performers.split("\\r?\\n");
        List<String> performersList = new ArrayList<>();
        for (String s : split) {
            if (!s.isEmpty())
                performersList.add(s);
        }
        Set<String> hs = new HashSet<>();
        hs.addAll(performersList);
        performersList.clear();
        performersList.addAll(hs);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdownitem, performersList);
        etPerformer.setAdapter(adapter);
        etPerformer.setDropDownAnchor(R.id.etPerformer);
        etPerformer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                etPerformer.setDropDownHeight(ActionBar.LayoutParams.WRAP_CONTENT);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etPerformer.setDropDownHeight(ActionBar.LayoutParams.WRAP_CONTENT);
                etPerformer.showDropDown();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etPerformer.getAdapter().getCount() > 2)
                    etPerformer.setDropDownHeight(300);
                Log.d("SagiB", String.valueOf(etPerformer.getAdapter().getCount()));
            }
        });
        etPerformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPerformer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dropdownShowing) {
                            if (etPerformer.getAdapter().getCount() > 2)
                                etPerformer.setDropDownHeight(300);
                            else
                                etPerformer.setDropDownHeight(ActionBar.LayoutParams.WRAP_CONTENT);
                            etPerformer.dismissDropDown();
                            dropdownShowing = false;
                        } else {
                            if (etPerformer.getAdapter().getCount() > 2)
                                etPerformer.setDropDownHeight(300);
                            else
                                etPerformer.setDropDownHeight(ActionBar.LayoutParams.WRAP_CONTENT);
                            etPerformer.showDropDown();
                            dropdownShowing = true;
                        }
                    }
                });
            }
        });

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnChoose)
    public void onBtnChooseClicked() {
        if (etPerformer.getText().toString().length() < 1) {
            etPerformer.setError("יש לרשום את שם האמן...");
        } else {
            String performer = etPerformer.getText().toString();
            String title = etPerformer.getText().toString();
            String values = prefs.getString("RecentPerformers", "");
            prefs.edit().putString("RecentPerformers", values + "\n" + performer).commit();
            prefs.edit().putString("PerformerTitle", performer).commit();
            performer = performer.replace(" ", "-").replace("׳","");
            performer = Uri.encode(performer);
            prefs.edit().putString("PerformerName", performer).commit();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            dismiss();
            Toast.makeText(getContext(), "רשימת ההופעות עודכנה לרשימה של: " + title, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.tvDeleteHistory)
    public void onTvDeleteHistoryClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("האם למחוק היסטוריית אמנים?").setPositiveButton("כן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prefs.edit().putString("RecentPerformers", "").commit();
                String performers = prefs.getString("RecentPerformers", "");
                String[] split = performers.split("\\r?\\n");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, split);
                etPerformer.setAdapter(adapter);
                Toast.makeText(getContext(), "היסטוריית אמנים נמחקה!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).setNegativeButton("לא", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
