package GUI;

import Logica.Colonia;
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
//            if (this.colonia.estaPausado()) this.colonia.realizarPausa();

            Object[] datos = colonia.recolectarDatos();

            Platform.runLater(() -> {
                this.controlador.updateHormigas(datos);
            });

            Thread.sleep(500);
        }
    }
}
