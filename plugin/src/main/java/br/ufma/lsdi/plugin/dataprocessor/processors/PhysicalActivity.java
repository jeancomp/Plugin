package br.ufma.lsdi.plugin.dataprocessor.processors;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionClient;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.plugin.dataprocessor.base.DataProcessor;
import br.ufma.lsdi.plugin.dataprocessor.digitalphenotypeevent.DigitalPhenotypeEvent;
import br.ufma.lsdi.plugin.dataprocessor.digitalphenotypeevent.Situation;
import br.ufma.lsdi.plugin.pluginmanager.handlingexceptions.InvalidDataProcessorNameException;

/**
 * PhysicalActivity does activity recognition: walking, running, driving, standing
 * GPS connected required
 */
public class PhysicalActivity extends DataProcessor {
    private static final String TAG = PhysicalActivity.class.getName();
    List<String> sensorList = new ArrayList();
    List<Integer> samplingRateList = new ArrayList();
    ActivityRecognitionClient mActivityRecognitionClient;

    @Override
    public void init(){
        try {
            setDataProcessorName("PhysicalActivity");

            Intent i = new Intent(this, ActivityDetectionService.class);
            startService(i);
        } catch (InvalidDataProcessorNameException e) {
            e.printStackTrace();
        }
        /*sensorList.add("Call");
        startSensor(sensorList);*/
        /*sensorList.add("MC34XX ACCELEROMETER");
        samplingRateList.add(8000);
        startSensor(sensorList,samplingRateList);*/
    }


    @Override
    public void onSensorDataArrived(Message message){
        inferencePhenotypingEvent(message);
    }


    @Override
    public void inferencePhenotypingEvent(Message message){
        Log.i("PhysicalActivity","#### MSG ORIGINAL PhysicalActivity: " + message);
        DigitalPhenotypeEvent digitalPhenotypeEvent = new DigitalPhenotypeEvent();
        digitalPhenotypeEvent.setDataProcessorName(getDataProcessorName());
        digitalPhenotypeEvent.setUid(CDDL.getInstance().getConnection().getClientId());

        Object[] valor1 = message.getServiceValue();
        String mensagemRecebida1 = StringUtils.join(valor1, ",");
        String[] listValues = mensagemRecebida1.split(",");

        String[] valor2 = message.getAvailableAttributesList();
        String mensagemRecebida2 = StringUtils.join(valor2, ",");
        String[] listAttributes = mensagemRecebida2.split(",");

        Situation situation = new Situation();
        situation.setLabel(listValues[0]);
        situation.setDescription("Type of activity");
        digitalPhenotypeEvent.setSituation(situation);

        if(!listAttributes[1].isEmpty() && !listValues[1].isEmpty()) {
            digitalPhenotypeEvent.setAttributes(listAttributes[1], listValues[1], "Integer", false);
        }
        if(!listAttributes[2].isEmpty() && !listValues[2].isEmpty()) {
            digitalPhenotypeEvent.setAttributes(listAttributes[2], listValues[2], "Date", false);
        }

        Log.i("PhysicalActivity","#### DigitalPhenotypeEvent: " + digitalPhenotypeEvent.toString());

        String json = toJson(digitalPhenotypeEvent);
        Message msg = new Message();
        msg.setServiceValue(json);
        sendProcessedData(msg);
        saveDigitalPhenotypeEvent(digitalPhenotypeEvent);
    }


    public void end(){ }


    public final IBinder mBinder = new PhysicalActivity.LocalBinder();


    public class LocalBinder extends Binder {
        public PhysicalActivity getService() {
            return PhysicalActivity.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) { return mBinder; }
}
