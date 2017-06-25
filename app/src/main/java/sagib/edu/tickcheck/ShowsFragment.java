package sagib.edu.tickcheck;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowsFragment extends Fragment implements ShowDataSource.OnShowArrivedListener {
    RecyclerView recycler;
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shows, container, false);
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("נא להמתין,\nמרענן רשימת הופעות..." + "\n" + "מומלץ להתחבר לרשת אלחוטית.");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        recycler = (RecyclerView) v.findViewById(R.id.recycler);
        ShowDataSource.getShows(this);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }

    @Override
    public void onShowArrived(final ArrayList<Show> data, final Exception e) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e == null) {
                    ShowAdapter adapter = new ShowAdapter(data, getContext());
                    recycler.setAdapter(adapter);
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("Sagi", e.toString());
                }
            }
        });
    }
}
