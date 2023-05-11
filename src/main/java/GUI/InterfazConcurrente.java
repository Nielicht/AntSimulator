package GUI;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import Logica.Colonia;
import Logica.Generador;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

public class InterfazConcurrente extends Application {

    @FXML
    public ToggleButton btnPausa;
    @FXML
    public Button btnAmenaza;
    @FXML
    private ListView exteriorStatus;
    @FXML
    private ListView almacenStatus;
    @FXML
    private ListView comidaAlComedor;
    @FXML
    private ListView instruccionStatus;
    @FXML
    private ListView descansoStatus;
    @FXML
    private ListView comedorStatus;
    @FXML
    private ListView refugioStatus;
    @FXML
    private ListView invasorStatus;
    @FXML
    private TextField comidaAlmacenStatus;
    @FXML
    private TextField comidaComedorStatus;
    private Colonia colonia;

    public static void main(String[] args) {
        launch();
    }

    public void GUIUpdater() {
        // Create a Timeline that runs every 0.5 seconds
        Duration intervalo = Duration.millis(500);
        Timeline timeline = new Timeline(new KeyFrame(intervalo, event -> {
            Object[] datos = this.colonia.recolectarDatos();

            Platform.runLater(() -> {
                CopyOnWriteArrayList buscandoComida = (CopyOnWriteArrayList) datos[0];
                CopyOnWriteArrayList zonaDeAlmacenaje = (CopyOnWriteArrayList) datos[1];
                CopyOnWriteArrayList transportandoAlComedor = (CopyOnWriteArrayList) datos[2];
                CopyOnWriteArrayList zonaDeInstruccion = (CopyOnWriteArrayList) datos[3];
                CopyOnWriteArrayList zonaDeDescanso = (CopyOnWriteArrayList) datos[4];
                CopyOnWriteArrayList zonaParaComer = (CopyOnWriteArrayList) datos[5];
                CopyOnWriteArrayList zonaParaRefugiarse = (CopyOnWriteArrayList) datos[6];
                CopyOnWriteArrayList repeliendoInvasor = (CopyOnWriteArrayList) datos[7];
                int unidadesComidaAlmacen = ((AtomicInteger) datos[8]).get();
                int unidadesComidaComedor = ((AtomicInteger) datos[9]).get();

                this.exteriorStatus.getItems().clear();
                this.exteriorStatus.getItems().addAll(buscandoComida);

                this.almacenStatus.getItems().clear();
                this.almacenStatus.getItems().addAll(zonaDeAlmacenaje);

                this.comidaAlComedor.getItems().clear();
                this.comidaAlComedor.getItems().addAll(transportandoAlComedor);

                this.instruccionStatus.getItems().clear();
                this.instruccionStatus.getItems().addAll(zonaDeInstruccion);

                this.descansoStatus.getItems().clear();
                this.descansoStatus.getItems().addAll(zonaDeDescanso);

                this.comedorStatus.getItems().clear();
                this.comedorStatus.getItems().addAll(zonaParaComer);

                this.refugioStatus.getItems().clear();
                this.refugioStatus.getItems().addAll(zonaParaRefugiarse);

                this.invasorStatus.getItems().clear();
                this.invasorStatus.getItems().addAll(repeliendoInvasor);

                this.comidaAlmacenStatus.setText(String.valueOf(unidadesComidaAlmacen));
                this.comidaComedorStatus.setText(String.valueOf(unidadesComidaComedor));
            });
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    public void btnAmenazaPress() {
        playBtnSonido();
        this.colonia.triggerInvasion();
    }

    @FXML
    public void togglePausa() {
        playBtnSonido();
        if (btnPausa.isSelected()) {
            colonia.pausa();
        } else {
            colonia.reanudar();
        }
    }

    private void playBtnSonido() {
        String path = getClass().getResource("/audio/btn.wav").toString();
        Media media = new Media(path);
        MediaPlayer mp = new MediaPlayer(media);
        mp.play();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InterfazConcurrente.fxml"));
        loader.setController(this);
        Parent raiz = loader.load();
        stage.setTitle("Simulador de hormigas | Imp. Concurrente");
        stage.setScene(new Scene(raiz));
        stage.setResizable(false);
        stage.show();

        Generador generador = new Generador();
        generador.setDaemon(true);
        generador.start();

        colonia = generador.getColonia();

        GUIUpdater();
    }

}