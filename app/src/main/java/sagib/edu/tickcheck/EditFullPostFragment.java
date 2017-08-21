package sagib.edu.tickcheck;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import sagib.edu.tickcheck.models.BoardPost;

public class EditFullPostFragment extends DialogFragment {


    @BindView(R.id.etShowTitle)
    EditText etShowTitle;
    @BindView(R.id.tilShowTitle)
    TextInputLayout tilShowTitle;
    @BindView(R.id.etShowArena)
    EditText etShowArena;
    @BindView(R.id.tilShowArena)
    TextInputLayout tilShowArena;
    @BindView(R.id.etShowDate)
    EditText etShowDate;
    @BindView(R.id.tilShowDate)
    TextInputLayout tilShowDate;
    @BindView(R.id.etTicketsNumber)
    EditText etTicketsNumber;
    @BindView(R.id.tilTicketsNumber)
    TextInputLayout tilTicketsNumber;
    @BindView(R.id.etShowPrice)
    EditText etShowPrice;
    @BindView(R.id.tilShowPrice)
    TextInputLayout tilShowPrice;
    @BindView(R.id.etPostContent)
    EditText etPostContent;
    @BindView(R.id.tilPostContent)
    TextInputLayout tilPostContent;
    @BindView(R.id.btnEditThisPost)
    BootstrapButton btnEditThisPost;
    BoardPost model;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_full_post, container, false);
        unbinder = ButterKnife.bind(this, v);
        model = getArguments().getParcelable("model");
        etPostContent.setText(model.getContents());
        etShowArena.setText(model.getShowArena());
        etShowDate.setText(model.getShowDate());
        etShowPrice.setText(model.getShowPrice());
        etShowTitle.setText(model.getShowTitle());
        etTicketsNumber.setText(model.getTicketsNumber());
        this.getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnEditThisPost)
    public void onBtnEditThisPostClicked() {
        if (isFilled()) {
            model.setContents(etPostContent.getText().toString());
            model.setShowArena(etShowArena.getText().toString());
            model.setShowDate(etShowDate.getText().toString());
            model.setShowPrice(etShowPrice.getText().toString());
            model.setShowTitle(etShowTitle.getText().toString());
            model.setTicketsNumber(etTicketsNumber.getText().toString());
            FirebaseDatabase.getInstance().getReference("Board").child(model.getPostUID()).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dismiss();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                    Toast.makeText(getContext(), "ההודעה נערכה!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public boolean isFilled() {
        if (etShowArena.getText().toString().length() < 1) {
            etShowArena.setError("אין להשאיר שדה ריק!");
        }
        if (etShowDate.getText().toString().length() < 1) {
            etShowDate.setError("אין להשאיר שדה ריק!");
        }
        if (etShowPrice.getText().toString().length() < 1) {
            etShowPrice.setError("אין להשאיר שדה ריק!");
        }
        if (etShowTitle.getText().toString().length() < 1) {
            etShowTitle.setError("אין להשאיר שדה ריק!");
        }
        if (etTicketsNumber.getText().toString().length() < 1) {
            etTicketsNumber.setError("אין להשאיר שדה ריק!");
        }
        if (etShowArena.getText().toString().length() >= 1 && etShowDate.getText().toString().length() >= 1 && etShowPrice.getText().toString().length() >= 1 && etShowTitle.getText().toString().length() >= 1 && etTicketsNumber.getText().toString().length() >= 1) {
            return true;
        }
        return false;
    }
}
