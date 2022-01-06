package br.ufma.lsdi.plugin.pluginmanager.handlingexceptions;

public class InvalidClientIDException extends Exception{
    public InvalidClientIDException(String message){
        super(message);
    }
}
