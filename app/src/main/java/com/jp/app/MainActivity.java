package com.jp.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.plugin.pluginmanager.DPManagerService;
import br.ufma.lsdi.plugin.pluginmanager.PluginManager;
import br.ufma.lsdi.plugin.SaveActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private boolean servicesStarted = false;
    private PluginManager pluginManager;
    private DPManagerService myService;
    private List<String> processorList = new ArrayList();
    private SaveActivity saveActivity;;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        activity = (Activity) this;

        initPermissions();

        saveActivity = new SaveActivity(activity);
        processorList.add("PhysicalActivity");
        startPlugin();
    }


    public void startPlugin(){
        pluginManager = new PluginManager.Builder(this)
                .setProcessorList(processorList)
                .setClientID("jeancomp")
                .build();
        pluginManager.start();
    }


    @Override
    public void onStart() {
        super.onStart();
        try{
            Intent intent = new Intent(this, DPManagerService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            } else {
                startService(intent);
            }
        }catch (Exception e){
            Log.e(TAG, "#### Error: " + e.getMessage());
        }
    }


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG,"#### Connection service MainService");
            DPManagerService.LocalBinder binder = (DPManagerService.LocalBinder) iBinder;
            myService = binder.getService();

            pluginManager.getInstance().setMainService(myService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG,"#### Disconnection service MainService");
        }
    };


    @Override
    public void onDestroy() {
        //pluginManager.getInstance().stop();
        super.onDestroy();
        pluginManager.stop();
    }


    private void initPermissions() {
        // Checa as permissões
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.FOREGROUND_SERVICE, android.Manifest.permission.ACTIVITY_RECOGNITION};

            if (!hasPermissions(activity, PERMISSIONS)) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
            }
    }


    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_close) {
            List<String> activePluginList = new ArrayList();
            activePluginList = pluginManager.getInstance().getMyService().getProcessorActive();

            List<String> finalActivePluginList = activePluginList;
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Closing Plugin")
                    .setMessage("Are you sure you want to close this app? It will stop the data collection.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                if(finalActivePluginList.size() > 0){
                                    Toast.makeText(getBaseContext(), "Need to close Physical_Activity in OpenDPMH.",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    pluginManager.getInstance().stop();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            finally {
                            }
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
            //finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}