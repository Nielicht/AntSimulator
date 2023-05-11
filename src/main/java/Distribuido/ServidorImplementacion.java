package Distribuido;

import Logica.Colonia;
import Logica.Generador;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServidorImplementacion extends UnicastRemoteObject implements Servidor {
    private Generador generador;
    private Colonia colonia;

    public ServidorImplementacion() throws RemoteException {
        super();
        this.generador = new Generador();
        generador.setDaemon(true);
        generador.start();
        this.colonia = generador.getColonia();
    }

    @Override
    public void generarInvasion() throws RemoteException {
        this.colonia.triggerInvasion();
    }

    @Override
    public int[] actualizarEstados() throws RemoteException {
        Object datosBruto[] = this.colonia.recolectarDatos();

        CopyOnWriteArrayList<String> obrerasExterior = (CopyOnWriteArrayList<String>) datosBruto[0];
        CopyOnWriteArrayList<String> soldadosInstruccion = (CopyOnWriteArrayList<String>) datosBruto[3];
        CopyOnWriteArrayList<String> soldadosRepeliendo = (CopyOnWriteArrayList<String>) datosBruto[7];
        CopyOnWriteArrayList<String> criasRefugiadas = (CopyOnWriteArrayList<String>) datosBruto[6];

        int obrerasComiendoODescansando = 0, criasComiendo = 0;
        CopyOnWriteArrayList<String> zonaParaComer = (CopyOnWriteArrayList<String>) datosBruto[5];
        CopyOnWriteArrayList<String> zonaDeAlmacenaje = (CopyOnWriteArrayList<String>) datosBruto[1];
        CopyOnWriteArrayList<String> transportandoAlComedor = (CopyOnWriteArrayList<String>) datosBruto[2];
        CopyOnWriteArrayList<String> zonaDeDescanso = (CopyOnWriteArrayList<String>) datosBruto[4];

        for (String hormigaID : zonaParaComer) {
            if (hormigaID.contains("HO")) obrerasComiendoODescansando++;
            if (hormigaID.contains("HC")) criasComiendo++;
        }

        for (String hormigaID : zonaDeDescanso) {
            if (hormigaID.contains("HO")) obrerasComiendoODescansando++;
        }

        int datos[] = new int[6];
        // 0 - obreras exterior
        // 1 - obreras interior ⚠ Almacen + Filtrar Comiendo + Transportando + Filtrar descansando
        // 2 - Soldados haciendo instruccion
        // 3 - Soldados repeliendo invasor
        // 4 - Crias comiendo ⚠ Filtrar comiendo
        // 5 - Crias refugiadas

        datos[0] = obrerasExterior.size();
        datos[1] = obrerasComiendoODescansando + zonaDeAlmacenaje.size() + transportandoAlComedor.size();
        datos[2] = soldadosInstruccion.size();
        datos[3] = soldadosRepeliendo.size();
        datos[4] = criasComiendo;
        datos[5] = criasRefugiadas.size();

        return datos;
    }

}
