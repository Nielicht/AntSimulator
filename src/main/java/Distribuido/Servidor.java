package Distribuido;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Servidor extends Remote {
    int[] actualizarEstados() throws RemoteException;

    void generarInvasion() throws RemoteException;
}
