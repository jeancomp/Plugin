package br.ufma.lsdi.plugin.pluginmanager;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.ConnectionFactory;
import br.ufma.lsdi.cddl.listeners.IConnectionListener;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.network.ConnectionImpl;
import br.ufma.lsdi.cddl.network.SecurityService;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import br.ufma.lsdi.plugin.R;
import br.ufma.lsdi.plugin.Topics;
import br.ufma.lsdi.plugin.dataprocessor.processors.PhysicalActivity;

public class DPManagerService extends Service {
    private static final String TAG = DPManagerService.class.getName();
    private static CDDL cddl;
    private String clientID = "";
    private ConnectionImpl con;
    private Context context;
    private TextView messageTextView;
    private String statusConnection = "";
    private int communicationTechnology = 4;
    private Subscriber subPluginActive;
    private Subscriber subPluginStop;
    private String nameCaCertificate = "rootCA.crt";
    private String nameClientCertificate = "client.crt";
    private static final int ID_SERVICE = 102;
    private Publisher publisher = PublisherFactory.createPublisher();
    private List<String> processorList = new ArrayList();
    private List<String> processorActive = new ArrayList();

    long automaticReconnectionTime = 1000L;
    boolean cleanSession = true;
    int connectionTimeout = 30;
    int keepAliveInterval = 60;
    boolean automaticReconnection = true;
    boolean publishConnectionChangedStatus = false;
    int maxInflightMessages = 10;
    MqttConnectOptions options = null;
    int mqttVersion = 3;


    @RequiresApi(api = Build.VERSION_CODES.P)
    @RequiresPermission(allOf = {
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED    // Na dúvida ainda se vai ter esse recurso no framework
    })
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        messageTextView = new TextView(context);

        // Create the Foreground Service
        Log.i(TAG, "#### Criando notificação");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Plugin")
                .setContentText("Behavior monitoring application")
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(ID_SERVICE, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "my_service_channelid_plugin";
        String channelName = "Service Plugin";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        //channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        //channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        Log.i(TAG,"#### channelId: " + channelId);
        return channelId;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        processorList = intent.getStringArrayListExtra("processorlist");
        clientID = intent.getStringExtra("clientID");
        Log.i(TAG,"#### client: " + clientID);
        startCDDL();

        subPluginActive = SubscriberFactory.createSubscriber();
        subPluginActive.addConnection(CDDL.getInstance().getConnection());

        subPluginStop = SubscriberFactory.createSubscriber();
        subPluginStop.addConnection(CDDL.getInstance().getConnection());

        subscribeMessagePluginActive(Topics.SELECT_PLUGIN_TOPIC.toString());
        subscribeMessagePluginStop(Topics.DELETE_PLUGIN_TOPIC.toString());

        if(!processorList.isEmpty()){
            for(int i=0; i<processorList.size(); i++){
                publishMessage(Topics.PLUGIN_LIST_TOPIC.toString(), processorList.get(i).toString());
            }
        }

        //startProcessor("PhysicalActivity");

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }


    public void startProcessor(String processorName) {
        try {
            if(processorName.equalsIgnoreCase("PhysicalActivity")) {
                Intent s = new Intent(context, PhysicalActivity.class);
                context.startService(s);
                //Toast.makeText(getContext(), "Start situation of interest: PhysicalActivity",Toast.LENGTH_SHORT).show();
                Log.i(TAG, "#### Starting processor: PhysicalActivity");
                processorActive.add(processorName);
            }
            publishMessage(Topics.ADD_PLUGIN_TOPIC.toString(),processorName);
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.toString());
        }
    }


    public void stopProcessor(String processorName) {
        try {
            if(processorName.equalsIgnoreCase("PhysicalActivity")) {
                Intent s = new Intent(context, PhysicalActivity.class);
                context.stopService(s);
                //Toast.makeText(getContext(), "Finish situation of interest: PhysicalActivity",Toast.LENGTH_SHORT).show();
                Log.i(TAG, "#### Stopping processor: PhysicalActivity");
                processorActive.remove(processorName);
            }
            publishMessage(Topics.REMOVE_PLUGIN_TOPIC.toString(),processorName);
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.toString());
        }
    }


    @Override
    public void onDestroy() {
        for(int i=0; i<processorList.size(); i++) {
            stopProcessor(processorList.get(i).toString());
        }
        cddl.stopAllCommunicationTechnologies();
        cddl.stopService();
        con.disconnect();
        CDDL.stopMicroBroker();
    }

    public final IBinder mBinder = new LocalBinder();


    public class LocalBinder extends Binder {
        public DPManagerService getService() {
            //return mBinder;
            return DPManagerService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) { return mBinder; }


    public static CDDL CDDLGetInstance(){
        if(cddl == null){
            cddl = CDDL.getInstance();
        }
        return cddl;
    }


    public void startCDDL(){
        try {
            //String host = CDDL.startMicroBroker();
            //String host = "10.0.2.5";
            String host = "127.0.0.1";
            Log.i(TAG,"#### ENDEREÇO DO BROKER: " + host);
            //val host = "broker.hivemq.com";
            con = ConnectionFactory.createConnection();
            con.setClientId(clientID);
            con.setPort("1883");
            con.addConnectionListener(connectionListener);
            con.connect("tcp",host,"1883", this.automaticReconnection,this.automaticReconnectionTime,false,this.connectionTimeout,
                    this.keepAliveInterval,this.publishConnectionChangedStatus,this.maxInflightMessages,"","",this.mqttVersion);
            cddl = CDDL.getInstance();

            //Log.i(TAG,"#### CONECTADO: " + con.isConnected());
            int delay = 0;
            boolean reconnect = false;
            while(!reconnect){
                Log.i(TAG,"#### RECONNECT BROKER...");
                try{
                    reconnect = con.isConnected();
                    Thread.sleep(1000 + delay);
                    delay = delay + 1000;
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                con.reconnect();
            }

            cddl.setConnection(con);
            cddl.setContext(getContext());
            cddl.startService();

            // Para todas as tecnologias, para entao iniciar apenas a que temos interresse
            cddl.stopAllCommunicationTechnologies();

            // Para todas os sensores, para entao iniciar apenas a que temos interresse
            cddl.stopAllSensors();

            //cddl.startCommunicationTechnology(CDDL.INTERNAL_TECHNOLOGY_VIRTUAL_ID);
            cddl.startCommunicationTechnology(this.communicationTechnology);
        }catch (Exception e){
            Log.i(TAG,"#### Error: " + e.getMessage());
        }
    }


    public void initSecureCDDL(){
        try {
            //String host = CDDL.startMicroBroker();
            String host = CDDL.startSecureMicroBroker(getContext(), true);
            //val host = "broker.hivemq.com";
            con = ConnectionFactory.createConnection();
            con.setClientId(getClientID());
            con.setHost(host);
            con.addConnectionListener(connectionListener);
            //con.connect();
            con.secureConnect(getContext());
            cddl = CDDL.getInstance();
            cddl.setConnection(con);
            cddl.setContext(getContext());
            cddl.startService();

            cddl.startCommunicationTechnology(CDDL.INTERNAL_TECHNOLOGY_VIRTUAL_ID);
        }catch (Exception e){
            Log.i(TAG,"#### Error: " + e.getMessage());
        }
    }


    public void initSecure(){
        // Android 9-10
        // Versão 26
        //Parte de segurança - Certificados Digitais
        // Senha da Chave privada: 123456
        SecurityService securityService = new SecurityService(getContext());
        //securityService.generateCSR("jean","LSDi","ufma","slz","ma","br");
        try {
            securityService.setCaCertificate(nameCaCertificate);
            securityService.setCertificate(nameClientCertificate);

            securityService.grantReadPermissionByCDDLTopic("lcmuniz@gmail.com", SecurityService.ALL_TOPICS);
            securityService.grantWritePermissionByCDDLTopic("lcmuniz@gmail.com", SecurityService.ALL_TOPICS);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private IConnectionListener connectionListener = new IConnectionListener() {
        @Override
        public void onConnectionEstablished() {
            statusConnection = "Established connection.";
            messageTextView.setText("Conexão estabelecida.");
            Log.i(TAG,"#### Status CDDL: " + statusConnection);
        }

        @Override
        public void onConnectionEstablishmentFailed() {
            statusConnection = "Failed connection.";
            messageTextView.setText("Falha na conexão.");
            Log.i(TAG,"#### Status MQTT: " + statusConnection);
        }

        @Override
        public void onConnectionLost() {
            statusConnection = "Lost connection.";
            messageTextView.setText("Conexão perdida.");
            Log.i(TAG,"#### Status MQTT: " + statusConnection);
        }

        @Override
        public void onDisconnectedNormally() {
            statusConnection = "A normal disconnect has occurred.";
            messageTextView.setText("Uma disconexão normal ocorreu.");
            Log.i(TAG,"#### Status MQTT: " + statusConnection);
        }
    };


    public CDDL getInstanceCDDL(){
        return cddl.getInstance();
    }


    public Context getContext() {
        return context;
    }


    public void setContext(Context context) {
        this.context= context;
    }


    public String getStatusCon(){ return statusConnection; }


    public void setNameCaCertificate(String name){
        this.nameCaCertificate = name;
    }


    public String getNameCaCertificate(){ return nameCaCertificate; }


    public void setnameClientCertificate(String name){
        this.nameClientCertificate = name;
    }


    public String getNameClientCertificate(){ return nameClientCertificate;}


    public void setClientID(String clientID){
        this.clientID = clientID;
    }


    public String getClientID(){
        return clientID;
    }


    public List<String> getProcessorActive(){
        return processorActive;
    }


    public void publishMessage(String nameService, String text) {
        publisher.addConnection(CDDL.getInstance().getConnection());
        Message message = new Message();
        message.setServiceName(nameService);
        message.setServiceValue(text);
        publisher.publish(message);
    }


    public void subscribeMessagePluginActive(String serviceName) {
        subPluginActive.subscribeServiceByName(serviceName);
        subPluginActive.setSubscriberListener(subscriberPluginActive);
    }


    public void subscribeMessagePluginStop(String serviceName) {
        subPluginStop.subscribeServiceByName(serviceName);
        subPluginStop.setSubscriberListener(subscriberPluginStop);
    }


    public ISubscriberListener subscriberPluginActive = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
            Log.i(TAG, "#### Read messages (started Processor):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            String[] separated = mensagemRecebida.split(",");
            String dataProcessorName = String.valueOf(separated[0]);

            startProcessor(dataProcessorName);
        }
    };


    public ISubscriberListener subscriberPluginStop = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
            Log.i(TAG, "#### Read messages (stop Processor):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            String[] separated = mensagemRecebida.split(",");
            String dataProcessorName = String.valueOf(separated[0]);

            stopProcessor(dataProcessorName);
        }
    };
}
