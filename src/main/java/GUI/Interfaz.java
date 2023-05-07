package GUI;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import Logica.Generador;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Interfaz extends Application {

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

    public static void main(String[] args) {
        launch();
    }

    public void updateHormigas(Object[] datos) {
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
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Interfaz.fxml"));
        Parent raiz = loader.load();
        Interfaz controlador = loader.getController();
        stage.setTitle("Simulador de hormigas");
        stage.setScene(new Scene(raiz));
        stage.setResizable(false);
        stage.show();

        Generador generador = new Generador(controlador);
        generador.setDaemon(true);
        generador.start();
    }
}