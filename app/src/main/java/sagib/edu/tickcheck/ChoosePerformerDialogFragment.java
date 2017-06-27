package sagib.edu.tickcheck;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.beardedhen.androidbootstrap.BootstrapButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChoosePerformerDialogFragment extends DialogFragment {


    @BindView(R.id.etChoosePerformer)
    EditText etChoosePerformer;
    @BindView(R.id.btnChoosePerformer)
    BootstrapButton btnChoosePerformer;
    SharedPreferences prefs;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_choose_performer_dialog, container, false);
        unbinder = ButterKnife.bind(this, v);
        prefs = getActivity().getSharedPreferences("DefaultPerformer", Context.MODE_PRIVATE);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnChoosePerformer)
    public void onBtnChoosePerformerClicked() {
        String perfomer = etChoosePerformer.getText().toString();
        String replace = perfomer.replace(" ", getResources().getString(R.string.precentage));
//        if (replace.endsWith("-"))
//            perfomer = replace.substring(0, replace.length() - 1);
//        else
//            perfomer = replace;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Performer", replace);
        editor.apply();
        dismiss();

    }
}
