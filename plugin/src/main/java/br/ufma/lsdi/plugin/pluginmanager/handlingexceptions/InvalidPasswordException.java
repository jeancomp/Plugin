package br.ufma.lsdi.plugin.pluginmanager.handlingexceptions;

public class InvalidPasswordException extends Exception{
    public InvalidPasswordException(String message){
        super(message);
    }
}
