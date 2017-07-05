package sagib.edu.tickcheck;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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
public class DefaultPerformerFragment extends DialogFragment {

    SharedPreferences prefs;
    @BindView(R.id.btnChoose)
    BootstrapButton btnChoose;
    @BindView(R.id.etPerformer)
    EditText etPerformer;
    Unbinder unbinder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_default_performer, container, false);
        unbinder = ButterKnife.bind(this, v);
        prefs = getContext().getSharedPreferences("DefaultPerformer", Context.MODE_PRIVATE);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnChoose)
    public void onViewClicked() {
        String performer = etPerformer.getText().toString();
        performer = performer.replace(" ", "-");
        performer = Uri.encode(performer);
        prefs.edit().putString("PerformerName", performer).commit();
        dismiss();
    }
}
