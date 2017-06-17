package sagib.edu.tickcheck;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements ShowDataSource.OnShowArrivedListener {
    RecyclerView recycler;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        ShowDataSource.getShows(this);
        recycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        dialog = new ProgressDialog(this);
        dialog.setMessage("נא להמתין, מרענן רשימת הופעות..." + "\n" + "על מנת לחסוך בשימוש חבילת נתונים," + "\n" + "מומלץ להתחבר לרשת אלחוטית.");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            dialog.show();
            ShowDataSource.getShows(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onShowArrived(final ArrayList<Show> data, final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e == null) {
                    ShowAdapter adapter = new ShowAdapter(data, MainActivity.this);
                    recycler.setAdapter(adapter);
                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("Sagi", e.toString());
                }
            }
        });
    }
}