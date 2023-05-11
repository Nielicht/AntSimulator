package Distribuido;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class IncializadorRegistro {
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        Servidor servidor = new ServidorImplementacion();
        Registry registro = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        registro.rebind("Servidor", servidor);
    }
}
