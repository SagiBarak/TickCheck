package sagib.edu.tickcheck;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.LocalDateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewPostFragment extends DialogFragment {


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
    @BindView(R.id.btnAddNewPost)
    BootstrapButton btnAddNewPost;
    FirebaseUser user;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_post, container, false);
        unbinder = ButterKnife.bind(this, v);
        user = FirebaseAuth.getInstance().getCurrentUser();
        this.getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnAddNewPost)
    public void onBtnAddNewPostClicked() {
        if (isFilled()) {
            String postContent = etPostContent.getText().toString();
            String showArena = etShowArena.getText().toString();
            String showDate = etShowDate.getText().toString();
            String showPrice = etShowPrice.getText().toString();
            String showTitle = etShowTitle.getText().toString();
            String ticketsNumber = etTicketsNumber.getText().toString();
            String hour = LocalDateTime.now().toString("HH:mm");
            String date = LocalDateTime.now().toString("dd/MM/yy");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Board");
            DatabaseReference row = ref.push();
            String postUID = row.getKey();
            BoardPost post = new BoardPost(postContent, user.getEmail(), hour, date, postUID, user.getUid(), user.getDisplayName(), ticketsNumber, showTitle, showDate, showArena, showPrice);
            row.setValue(post);
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            dismiss();
            Toast.makeText(getContext(), "המודעה פורסמה!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isFilled() {
        if (etPostContent.getText().toString().length() < 1) {
            etPostContent.setError("אין להשאיר שדה ריק!");
        }
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
        if (etPostContent.getText().toString().length() >= 1 && etShowArena.getText().toString().length() >= 1 && etShowDate.getText().toString().length() >= 1 && etShowPrice.getText().toString().length() >= 1 && etShowTitle.getText().toString().length() >= 1 && etTicketsNumber.getText().toString().length() >= 1) {
            return true;
        }
        return false;
    }
}
