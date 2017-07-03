package sagib.edu.tickcheck;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class EditPostFragment extends DialogFragment {

    @BindView(R.id.etEditPost)
    EditText etEditPost;
    @BindView(R.id.btnEditPost)
    BootstrapButton btnEditPost;
    BoardPost model;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_post, container, false);
        unbinder = ButterKnife.bind(this, view);
        model = getArguments().getParcelable("model");
        etEditPost.setText(model.getContents());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnEditPost)
    public void onViewClicked() {
        model.setContents(etEditPost.getText().toString());
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
