package GUI;

import Distribuido.Servidor;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class InterfazDistribuido extends Application {

    @FXML
    private Button btnInvasion;
    @FXML
    private TextField oExterior;
    @FXML
    private TextField oInterior;
    @FXML
    private TextField sInstruccion;
    @FXML
    private TextField sRepeliendo;
    @FXML
    private TextField cComiendo;
    @FXML
    private TextField cRefugiadas;
    private Servidor servidor;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InterfazDistribuido.fxml"));
        loader.setController(this);
        Parent raiz = loader.load();
        stage.setTitle("Simulador de hormigas | Imp. Distribuida");
        stage.setScene(new Scene(raiz));
        stage.setResizable(false);
        stage.show();

        Registry registry = LocateRegistry.getRegistry();
        servidor = (Servidor) registry.lookup("Servidor");

        GUIUpdater();
    }

    public void GUIUpdater() {
        // Create a Timeline that runs every 0.5 seconds
        Duration intervalo = Duration.millis(500);
        Timeline timeline = new Timeline(new KeyFrame(intervalo, event -> {
            // Retrieve the worker count from the server
            int[] datos = recolectarDatosServidor();

            // Update the GUI on the JavaFX application thread
            Platform.runLater(() -> {
                // Update the worker count label
                this.oExterior.setText(String.valueOf(datos[0]));
                this.oInterior.setText(String.valueOf(datos[1]));
                this.sInstruccion.setText(String.valueOf(datos[2]));
                this.sRepeliendo.setText(String.valueOf(datos[3]));
                this.cComiendo.setText(String.valueOf(datos[4]));
                this.cRefugiadas.setText(String.valueOf(datos[5]));
            });
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private int[] recolectarDatosServidor() {
        try {
            return this.servidor.actualizarEstados();
        } catch (RemoteException e) {
            return new int[]{0, 0, 0, 0, 0};
        }
    }

    @FXML
    public void btnInvasionPress() throws RemoteException {
        playBtnSonido();
        this.servidor.generarInvasion();
    }

    private void playBtnSonido() {
        String path = getClass().getResource("/audio/btn.wav").toString();
        Media media = new Media(path);
        MediaPlayer mp = new MediaPlayer(media);
        mp.play();
    }
}
