import java.util.concurrent.BrokenBarrierException;

public class HSoldado extends Thread {

    private String id;
    private Colonia colonia;
    private int iter;

    public HSoldado(int id, Colonia colonia) {
        this.id = "HS" + String.format("%04d", id);
        this.iter = 6;
        this.colonia = colonia;
    }

    private void protegerColonia() {
        try {
            colonia.logger.log("Hormiga soldado " + this.id + " acude a repeler al invasor");
            colonia.repelerInvasor();
        } catch (BrokenBarrierException e) {
            System.out.println("Algo fue mal con el cyclic barrier...");
            e.printStackTrace();
        } catch (InterruptedException ignored) {
        }
    }

    private void instruccion() throws InterruptedException {
        colonia.logger.log("Hormiga soldado " + this.id + " entra a la zona de instrucción");
        colonia.zonaDeInstruccion(2000, 8000);
    }

    private void descansar() throws InterruptedException {
        colonia.logger.log("Hormiga soldado " + this.id + " entra a la zona de descanso");
        colonia.descansar(2000, 2000);
    }

    private void comer() throws InterruptedException {
        colonia.logger.log("Hormiga soldado " + this.id + " procede a comer");
        colonia.accederAlComedor(3000, 3000, -1);
    }

    @Override
    public void run() {
        colonia.accederTunelEntrada();
        while (true) {
            try {
                if (this.iter > 0) {
                    instruccion();
                    descansar();
                    this.iter--;
                } else {
                    comer();
                    this.iter = 6;
                }
            } catch (InterruptedException e) {
                protegerColonia();
            }
        }
    }
}