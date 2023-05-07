package Logica;

import GUI.Interfaz;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class GUIUpdater extends Task<Void> {

    Interfaz controlador;
    Colonia colonia;

    public GUIUpdater(Interfaz controlador, Colonia colonia) {
        this.controlador = controlador;
        this.colonia = colonia;
    }

    @Override
    protected Void call() throws Exception {
        while (true) {
            Object[] datos = colonia.recolectarDatos();

            Platform.runLater(() -> {
                this.controlador.updateHormigas(datos);
            });

            Thread.sleep(500);
        }
    }
}
