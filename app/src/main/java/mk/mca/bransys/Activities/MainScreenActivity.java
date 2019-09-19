package mk.mca.bransys.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mk.mca.bransys.Adapter.MainAdapter;
import mk.mca.bransys.ModelData.Feed;
import mk.mca.bransys.ModelData.Network;
import mk.mca.bransys.ModelData.RetrofitInstance;
import mk.mca.bransys.Interfaces.Service;
import mk.mca.bransys.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainScreenActivity extends AppCompatActivity {

    private MainAdapter adapter;
    private ProgressDialog progressDoalog;
    private RecyclerView recyclerView;
    private List<Network> mCompanies = new ArrayList<>();
    private ArrayList<Network> modelArrayList = new ArrayList();
    private static final String TAG = MainScreenActivity.class.getSimpleName();
    private Integer time = 0;
    private String key = "Key";
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);
        final SharedPreferences sheredpref = getSharedPreferences("sharedpref", Context.MODE_PRIVATE);
        time = 0;
        time = (sheredpref.getInt("time", 0)) * 60000;
        final Integer firstTime = sheredpref.getInt("frst", 0);

        progressDoalog = new ProgressDialog(MainScreenActivity.this);
        progressDoalog.setMessage("Please wait....");
        progressDoalog.setCancelable(false);
        progressDoalog.show();
        if (firstTime == 0) {
            if (haveNetworkConnection() == true) {
                loadFromNet(sheredpref);
                editor = sheredpref.edit();
                editor.putInt("frst", 1);
                editor.commit();

            } else {
                progressDoalog.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Required internet connection for the first time").setMessage("Please enable your internet connection so we can gather data for the first time")
                        .setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                        .setPositiveButton("Internet settings", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        } else {
            loadFromPhone(sheredpref);
        }


        if (time > 0) {
            setTimerFromNet(sheredpref);
        }

    }

    private void loadFromPhone(SharedPreferences sheredpref) {
        Gson gson = new Gson();
        String response = sheredpref.getString("Key", null);
        Type type = new TypeToken<ArrayList<Network>>() {
        }.getType();
        modelArrayList = gson.fromJson(response, type);
        if (modelArrayList == null) {
            modelArrayList = new ArrayList<>();
        }
        progressDoalog.dismiss();

        generateDataList(modelArrayList);
    }

    private void setTimerFromNet(final SharedPreferences sheredpref) {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            loadFromNet(sheredpref);

                        } catch (Exception e) {

                        }

                    }
                });
            }
        };

        timer.schedule(doAsynchronousTask, time, time);


    }

    private void loadFromNet(final SharedPreferences sheredpref) {

        Service serviceAPI = RetrofitInstance.getClient();
        Call<Feed> call = serviceAPI.getAllCompanies();

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                progressDoalog.dismiss();

                ArrayList<Network> listdata = response.body().getNetworks();

                generateDataList(listdata);
                Gson gson = new Gson();
                String json = gson.toJson(listdata);

                editor = sheredpref.edit();
                editor.remove(key).commit();
                editor.putString(key, json);
                editor.commit();

                Toast.makeText(MainScreenActivity.this, "loaded from net", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                progressDoalog.dismiss();
                Toast.makeText(MainScreenActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void generateDataList(ArrayList<Network> list) {
        recyclerView = findViewById(R.id.recycleview);
        adapter = new MainAdapter(list, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainScreenActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchComapny = menu.findItem(R.id.search_menu);
        MenuItem settings = menu.findItem(R.id.settings_menu);


        settings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getApplicationContext(), SettingsMenuActivity.class);
                startActivity(intent);

//


                return false;
            }
        });

        SearchView searchView = (SearchView) searchComapny.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
