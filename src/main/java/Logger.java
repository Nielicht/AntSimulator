import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private FileWriter writer;

    public Logger() {
        try {
            this.writer = new FileWriter("log.txt", true);
            writer.write("{Inicio log}\n\n");
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error en la generación del FileWriter");
        }
    }

    public synchronized void log(String msg) {
        LocalDateTime fechaActual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fecha = fechaActual.format(formato);
        fecha += ", ";

        try {
            writer.write(fecha + msg + "\n");
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error en la escritura en log");
        }
    }
}
