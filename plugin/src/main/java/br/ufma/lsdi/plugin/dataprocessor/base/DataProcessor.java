package br.ufma.lsdi.plugin.dataprocessor.base;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import br.ufma.lsdi.plugin.Topics;
import br.ufma.lsdi.plugin.dataprocessor.digitalphenotypeevent.DigitalPhenotypeEvent;
import br.ufma.lsdi.plugin.pluginmanager.handlingexceptions.InvalidDataProcessorNameException;
import br.ufma.lsdi.plugin.pluginmanager.handlingexceptions.InvalidSensorNameException;

/*
public abstract class DataProcessor extends Service {
    private Context context;
    private String clientID;
    private List<String> listUsedSensors = new ArrayList();
    private List<String> listUsedSensorsAux = new ArrayList();
    private String dataProcessorName;
    private DPUtil dpUtilities;
    private DPUtil dpUtilitiesAux;
    private Publisher publisher = PublisherFactory.createPublisher();

    @Override
    public void onCreate() {
        context = this;

        this.clientID = CDDL.getInstance().getConnection().getClientId();

        publisher.addConnection(CDDL.getInstance().getConnection());

        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if(listUsedSensors.size()>0){
            dpUtilities.configSubscribers();
        }
        if(listUsedSensorsAux.size()>0) {
            dpUtilitiesAux.configSubscribers();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if(!listUsedSensors.isEmpty()) {
            stopSensor(listUsedSensors);
        }
        if(!listUsedSensorsAux.isEmpty()) {
            stopSensor(listUsedSensorsAux);
        }
        end();
    }

    public void init(){}

    public void onSensorDataArrived(Message message){}

    public void inferencePhenotypingEvent(Message message){ }

    public void sendProcessedData(Message message){
        message.setServiceName(Topics.INFERENCE_TOPIC.toString());
        message.setTopic(Topics.INFERENCE_TOPIC.toString());
        publishInference(message);
    }

    public void end(){}

    public String toJson(Object o){
        Gson gson = new Gson();
        String json = gson.toJson(o);
        return json;
    }

    public void setDataProcessorName(String dataProcessorName) {
//        if(dataProcessorName == null){
//            throw new InvalidDataProcessorNameException("#### Error: dataprocessorName cannot be null.");
//        }
//        if(dataProcessorName.isEmpty()){
//            throw new InvalidDataProcessorNameException("#### Error: dataprocessorName cannot be empty.");
//        }
//        else if(dataProcessorName.length() > 100){
//            throw new InvalidDataProcessorNameException("#### Error: dataprocessorName too long.");
//        }
        this.dataProcessorName = dataProcessorName;
    }


    public String getDataProcessorName(){ return this.dataProcessorName; }

    public void publishMessage(String service, String text) {
        Message message = new Message();
        message.setServiceName(service);
        message.setServiceValue(text);
        message.setAvailableAttributes(1);
        publisher.publish(message);
    }

    public void startSensor(List<String> sensorList){
        for(int i=0; i<sensorList.size(); i++) {
            publishMessage(Topics.ACTIVE_SENSOR_TOPIC.toString(), sensorList.get(i).toString());

            listUsedSensors.add(sensorList.get(i).toString());
        }
        dpUtilities = new DPUtil(listUsedSensors);
    }

    public void startSensor(List<String> sensorList, List<Integer> samplingRateList) {
        for(int i=0; i < sensorList.size(); i++) {
            publishMessage(Topics.ACTIVE_SENSOR_TOPIC.toString(), sensorList.get(i).toString(), samplingRateList.get(i));

            listUsedSensorsAux.add(sensorList.get(i).toString());
        }
        dpUtilitiesAux = new DPUtil(listUsedSensorsAux);

    }

    private void stopSensor(List<String> sensorList){
        for(int i=0; i<sensorList.size(); i++) {
            publishMessage(Topics.DEACTIVATE_SENSOR_TOPIC.toString(), sensorList.get(i).toString());

            if(listUsedSensors.contains(sensorList.get(i).toString())){
                listUsedSensors.remove(sensorList.get(i).toString());
            }
            else{
                listUsedSensorsAux.remove(sensorList.get(i).toString());
            }
        }
    }

    public List<String> getSensors(){
        List<String> listSensors = new ArrayList();
        listSensors = CDDL.getInstance().getSensorVirtualList();
        List<Sensor> sensorInternal = CDDL.getInstance().getInternalSensorList();

        if (sensorInternal.size() != 0) {
            for (int i = 0; i < sensorInternal.size(); i++) {
                listSensors.add(sensorInternal.get(i).getName());
            }
        }
        listSensors.add("Location");
        return listSensors;
    }

    private void publishMessage(String service, String text, int samplingRate) {
        int total = 2;
        Object[] value = {text, samplingRate};
        Message message = new Message();
        message.setServiceName(service);
        message.setServiceValue(value);
        message.setAvailableAttributes(Integer.valueOf(total));
        publisher.publish(message);
    }

    private void publishInference(Message message){
        publisher.publish(message);
    }

    */
/**
     * For each started Sensor, a Subcriber is signed.
     *//*

    private class DPUtil {
        List<String> nameSensors = new ArrayList();
        Subscriber[] subscribers;
        int numSensors = 0;

        public DPUtil(List<String> listSensors){
            numSensors = listSensors.size();
            nameSensors = listSensors;
            subscribers = new Subscriber[numSensors];
        }

        public void configSubscribers(){
            for(int i=0; i<numSensors; i++) {
                subscribers[i] = SubscriberFactory.createSubscriber();
                subscribers[i].addConnection(CDDL.getInstance().getConnection());
                subscribeMessage(i, this.nameSensors.get(i).toString());
            }
        }

        public void subscribeMessage(int position, String serviceName) {
            subscribers[position].subscribeServiceByName(serviceName);
            testSubcriber();
            subscribers[position].setSubscriberListener(createSubcriber(position));
        }

        ISubscriberListener[] subscriberListener;
        public void testSubcriber(){
            subscriberListener = new ISubscriberListener[numSensors];
        }

        public ISubscriberListener createSubcriber(int position){
            return subscriberListener[position] = new ISubscriberListener() {
                @Override
                public void onMessageArrived(Message message) {
                    Object[] valor = message.getServiceValue();
                    String mensagemRecebida = StringUtils.join(valor, ", ");
                    Object[] finalValor = {getDataProcessorName(),mensagemRecebida};
                    Log.i("PLUGIN","#### VALOR: " + finalValor[0] + ", " + String.valueOf(finalValor[1]));
                    message.setServiceValue(finalValor);

                    onSensorDataArrived(message);
                }
            };
        }
    }
}
*/

public abstract class DataProcessor extends Service {
    private Context context;
    private String clientID;
    private String dataProcessorName = null;
    private List<String> sensorList = new ArrayList();
    private List<String> listUsedSensors = new ArrayList();
    private List<String> listUsedSensorsSamplingRate = new ArrayList();
    private Publisher publisher = PublisherFactory.createPublisher();
    private DPUtil dpUtil;
    private DPUtil dpUtilAux;

    @Override
    public void onCreate() {
        try {
            context = this;

            this.clientID = CDDL.getInstance().getConnection().getClientId();

            publisher.addConnection(CDDL.getInstance().getConnection());
        }catch (Exception e){
            this.clientID = "not set";
        }

        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(getDataProcessorName() == null) {
            try {
                throw new InvalidDataProcessorNameException("#### Error: invalid dataProcessorName, cannot be null.");
            } catch (InvalidDataProcessorNameException e) {
                e.printStackTrace();
            }
        }
        if(listUsedSensors.size()>0){
            dpUtil.configSubscribers();
        }
        if(listUsedSensorsSamplingRate.size()>0) {
            dpUtilAux.configSubscribers();
        }
        dO();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        end();
        try {
            if(!listUsedSensors.isEmpty()) {
                stopSensor(listUsedSensors);
            }
            if(!listUsedSensorsSamplingRate.isEmpty()) {
                stopSensor(listUsedSensorsSamplingRate);
            }
        } catch (InvalidSensorNameException e) {
            e.printStackTrace();
        }
        finally {
            /*if(databaseManager.getInstance().getDB().isOpen()) {
                databaseManager.getInstance().getDB().close();
            }*/
        }
        super.onDestroy();
    }


    public void init(){ }


    public void dO(){ }


    public void onSensorDataArrived(Message message){}


    public void inferencePhenotypingEvent(Message message){ }


    public void sendProcessedData(Message message){
        message.setServiceName(Topics.INFERENCE_TOPIC.toString());
        message.setTopic(Topics.INFERENCE_TOPIC.toString());
        publishInference(message);
    }


    public void end(){ }


    public String toJson(Object o){
        Gson gson = new Gson();
        String json = gson.toJson(o);
        return json;
    }


    public void saveDigitalPhenotypeEvent(DigitalPhenotypeEvent digitalPhenotypeEvent){
        String json = toJson(digitalPhenotypeEvent);
        Message msg = new Message();
        msg.setServiceValue(json);
        msg.setServiceName(Topics.SAVE_PHENOTYPES_EVENT_TOPIC.toString());
        msg.setTopic(Topics.SAVE_PHENOTYPES_EVENT_TOPIC.toString());
        publishInference(msg);
    }


    public Context getContext(){ return this.context; }


    public void setDataProcessorName(String dataProcessorName) throws InvalidDataProcessorNameException {
        if(dataProcessorName == null){
            throw new InvalidDataProcessorNameException("#### Error: dataprocessorName cannot be null.");
        }
        if(dataProcessorName.isEmpty()){
            throw new InvalidDataProcessorNameException("#### Error: dataprocessorName cannot be empty.");
        }
        else if(dataProcessorName.length() > 100){
            throw new InvalidDataProcessorNameException("#### Error: dataprocessorName too long.");
        }
        this.dataProcessorName = dataProcessorName;
    }


    public String getDataProcessorName(){ return this.dataProcessorName; }


    public List<String> getSensors(){
        sensorList = CDDL.getInstance().getSensorVirtualList();
        List<Sensor> sensorInternal = CDDL.getInstance().getInternalSensorList();

        if (sensorInternal.size() != 0) {
            for (int i = 0; i < sensorInternal.size(); i++) {
                sensorList.add(sensorInternal.get(i).getName());
            }
        }
        sensorList.add("Location");
        return sensorList;
    }


    public void startSensor(List<String> sensorList) throws InvalidSensorNameException {
        if(sensorList.isEmpty()){
            throw new InvalidSensorNameException("#### Error: Sensor list cannot be empty.");
        }
        else if(sensorList == null){
            throw new InvalidSensorNameException("#### Error: Sensor list cannot be null.");
        }
        for(int i=0; i<sensorList.size(); i++) {
            publishMessage(Topics.ACTIVE_SENSOR_TOPIC.toString(), sensorList.get(i).toString());

            listUsedSensors.add(sensorList.get(i).toString());
        }
        dpUtil = new DPUtil(listUsedSensors);
    }


    public void startSensor(List<String> sensorList, List<Integer> samplingRateList) throws InvalidSensorNameException {
        if(sensorList.size() != samplingRateList.size()){
            throw new InvalidSensorNameException("#### Error: the sensor list must be the same size as the sample rate list.");
        }
        if(sensorList.isEmpty()){
            throw new InvalidSensorNameException("#### Error: Sensor list cannot be empty.");
        }
        else if(sensorList == null){
            throw new InvalidSensorNameException("#### Error: Sensor list cannot be null.");
        }
        if(samplingRateList.isEmpty()){
            throw new InvalidSensorNameException("#### Error: Sample rate list cannot be empty.");
        }
        else if(samplingRateList == null){
            throw new InvalidSensorNameException("#### Error: Sample rate list cannot be null.");
        }
        for(int i=0; i < sensorList.size(); i++) {
            publishMessage(Topics.ACTIVE_SENSOR_TOPIC.toString(), sensorList.get(i).toString(), samplingRateList.get(i));

            listUsedSensorsSamplingRate.add(sensorList.get(i).toString());
        }
        dpUtilAux = new DPUtil(listUsedSensorsSamplingRate);
    }


    private void stopSensor(List<String> sensorList) throws InvalidSensorNameException {
        if(sensorList.isEmpty()){
            throw new InvalidSensorNameException("#### Error: Sensor list cannot be empty.");
        }
        else if(sensorList == null){
            throw new InvalidSensorNameException("#### Error: Sensor list cannot be null.");
        }
        for(int i=0; i<sensorList.size(); i++) {
            publishMessage(Topics.DEACTIVATE_SENSOR_TOPIC.toString(), sensorList.get(i).toString());

            if(listUsedSensors.contains(sensorList.get(i).toString())){
                listUsedSensors.remove(sensorList.get(i).toString());
            }
            else{
                listUsedSensorsSamplingRate.remove(sensorList.get(i).toString());
            }
        }
    }


    private void publishMessage(String service, String text) {
        Message message = new Message();
        message.setServiceName(service);
        message.setServiceValue(text);
        message.setAvailableAttributes(1);
        publisher.publish(message);
    }


    private void publishMessage(String service, String text, int samplingRate) {
        int total = 2;
        Object[] value = {text, samplingRate};
        Message message = new Message();
        message.setServiceName(service);
        message.setServiceValue(value);
        message.setAvailableAttributes(Integer.valueOf(total));
        publisher.publish(message);
    }


    private void publishInference(Message message){
        publisher.publish(message);
    }


    /**
     * For each started Sensor, a Subcriber is signed.
     */
    public class DPUtil {
        List<String> nameSensors = new ArrayList();
        Subscriber[] subscribers;
        int numSensors = 0;

        public DPUtil(List<String> listSensors){
            numSensors = listSensors.size();
            nameSensors = listSensors;
            subscribers = new Subscriber[numSensors];
        }

        public void configSubscribers(){
            for(int i=0; i<numSensors; i++) {
                subscribers[i] = SubscriberFactory.createSubscriber();
                subscribers[i].addConnection(CDDL.getInstance().getConnection());
                subscribeMessage(i, this.nameSensors.get(i).toString());
            }
        }

        public void subscribeMessage(int position, String serviceName) {
            subscribers[position].subscribeServiceByName(serviceName);
            testSubcriber();
            subscribers[position].setSubscriberListener(createSubcriber(position));
        }

        ISubscriberListener[] subscriberListener;
        public void testSubcriber(){
            subscriberListener = new ISubscriberListener[numSensors];
        }

        public ISubscriberListener createSubcriber(int position){
            return subscriberListener[position] = new ISubscriberListener() {
                @Override
                public void onMessageArrived(Message message) {
                    //Add processor name
                    Object[] valor1 = message.getServiceValue();
                    Object o = getDataProcessorName();
                    Object[] finalValor = new Object[valor1.length + 1];
                    finalValor[0] = o;
                    for(int i=0; i < valor1.length; i++){
                        finalValor[i+1] = valor1[i];
                    }
                    String mensagemRecebida1 = StringUtils.join(finalValor, ",");
                    Object[] finalValor1 = {mensagemRecebida1};

                    message.setAvailableAttributes(message.getAvailableAttributes() + 1);
                    String[] valor2 = message.getAvailableAttributesList();
                    String mensagemRecebida2 = StringUtils.join(valor2, ",");
                    String[] finalValor2 = {"Data Processor Name",mensagemRecebida2};

                    message.setAvailableAttributesList(finalValor2);
                    message.setServiceValue(finalValor1);

                    onSensorDataArrived(message);
                }
            };
        }
    }
    //public class ProcessedInformation extends Message{ }
}
