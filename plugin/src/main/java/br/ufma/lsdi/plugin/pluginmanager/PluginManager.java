package br.ufma.lsdi.plugin.pluginmanager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.plugin.SaveActivity;

public class PluginManager implements OpenDPMHInterface {
    private static final String TAG = PluginManager.class.getName();
    private static PluginManager instance = null;
    private boolean servicesStarted = false;
    private Context context;
    private DPManagerService myService;
    private Builder builderCopy;
    private SaveActivity saveActivity = SaveActivity.getInstance();


    public PluginManager(){}


    public PluginManager(final Builder builder){
        this.context = builder.context;
        this.builderCopy = builder;
    }


    public static PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }
        return instance;
    }


    @Override
    public void start() {
        try{
            if(!servicesStarted) {
                Intent intent = new Intent(this.context, DPManagerService.class);
                if(!builderCopy.nameProcessor.isEmpty()){
                    intent.putStringArrayListExtra("processorlist", (ArrayList<String>) builderCopy.nameProcessor);
                    intent.putExtra("clientID", builderCopy.clientID);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getContext().startForegroundService(intent);
                } else {
                    getContext().startService(intent);
                }
                servicesStarted = true;
            }
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.toString());
        }
    }


    @Override
    public void stop() {
        Log.i(TAG,"#### Stopped framework MainService.");
        // Stop the foreground service
        myService.stopForeground(true);
        try {
            if(servicesStarted) {
                Intent intent = new Intent(getContext(), DPManagerService.class);
                getContext().stopService(intent);

                servicesStarted = false;
            }
        }catch (Exception e){
            Log.e(TAG,e.toString());
        }
    }


    public boolean getServicesStarted(){
        return servicesStarted;
    }


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG,"#### Connection service MainService");
            DPManagerService.LocalBinder binder = (DPManagerService.LocalBinder) iBinder;
            myService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG,"#### Disconnection service MainService");
        }
    };


    public DPManagerService getMyService(){
        Intent intent = new Intent(saveActivity.getInstance().getActivity(), DPManagerService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            saveActivity.getInstance().getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            saveActivity.getInstance().getActivity().startService(intent);
        }
        return myService;
    }


    /**
     *
     * @param DPManagerService
     */
    public void setMainService(DPManagerService DPManagerService) {
//        if(mainService == null){
//            throw new InvalidMainServiceException("#### Error: mainService cannot be null.");
//        }
        this.myService = DPManagerService;
    }


    public Context getContext(){
        return this.context;
    }


    public static class Builder{
        private static final String TAG = Builder.class.getName();
        private Context context = null;
        private String clientID = null;
        List<String> nameProcessor = new ArrayList();

        public Builder(Context context){
            this.context = context;
        }

        public Builder setProcessorList(List<String> nameProcessor){
            this.nameProcessor = nameProcessor;
            return this;
        }

        public Builder setClientID(String clientID){
            this.clientID = clientID;
            return this;
        }

        public PluginManager build(){
            try{
                if(this.context == null){
                    Log.e(TAG,"#### Error: The context is mandatory.");
                }
                else if(this.clientID == null){
                    Log.e(TAG,"#### Error: The clientID is mandatory.");
                }
            }catch (Exception e){
                Log.e(TAG,"#### Error: " + e.toString());
            }
            return new PluginManager(this);
        }
    }
}
