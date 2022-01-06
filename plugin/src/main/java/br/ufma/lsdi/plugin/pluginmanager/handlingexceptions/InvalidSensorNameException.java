package br.ufma.lsdi.plugin.pluginmanager.handlingexceptions;

public class InvalidSensorNameException extends Exception{
    public InvalidSensorNameException(String message){
        super(message);
    }
}
