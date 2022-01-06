package br.ufma.lsdi.plugin.pluginmanager.handlingexceptions;

public class InvalidUsernameException extends Exception{
    public InvalidUsernameException(String message){
        super(message);
    }
}
