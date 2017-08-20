package sagib.edu.tickcheck;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class IntroFragment extends Fragment {


    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivScreen)
    ImageView ivScreen;
    Unbinder unbinder;

    public static IntroFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        IntroFragment fragment = new IntroFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_intro, container, false);
        unbinder = ButterKnife.bind(this, v);
        int position = getArguments().getInt("position");
        switch (position) {
            case 1:
                ivScreen.setImageDrawable(getResources().getDrawable(R.drawable.intro01));
                tvTitle.setText("חפש את ההופעות\nשל האמן האהוב עליך");
                break;
            case 2:
                ivScreen.setImageDrawable(getResources().getDrawable(R.drawable.intro2));
                tvTitle.setText("פנוי בתאריך ספציפי?\nחפש הופעה לפי תאריך");
                break;
            case 3:
                ivScreen.setImageDrawable(getResources().getDrawable(R.drawable.intro3));
                tvTitle.setText("הוסף הופעות ונהל אותן\nברשימת ההופעות האישית");
                break;
            case 4:
                ivScreen.setImageDrawable(getResources().getDrawable(R.drawable.intro4));
                tvTitle.setText("נגמרו הכרטיסים? לא נורא...\nחפש כרטיס בלוח המכירות");
                break;
        }
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
