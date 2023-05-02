import java.util.concurrent.BrokenBarrierException;

public class HSoldado extends Thread {

    String id;
    Colonia colonia;
    int iter;

    public HSoldado(int id, Colonia colonia) {
        this.id = "HS" + String.format("%04d", id);
        this.iter = 6;
        this.colonia = colonia;
    }

    void protegerColonia() {
        try {
            System.out.println("¡Hormiga soldado " + id + " acude a repeler el invasor!");
            colonia.repelerInvasor();
            System.out.println("¡Hormiga soldado " + id + " ha repelido al invasor!");
        } catch (BrokenBarrierException e) {
            System.out.println("Algo fue mal con el cyclic barrier...");
            e.printStackTrace();
        } catch (InterruptedException ignored) {
        }
    }

    void instruccion() throws InterruptedException {
        colonia.zonaDeInstruccion(2000, 8000);
    }

    void descansar() throws InterruptedException {
        colonia.descansar(2000, 2000);
    }

    void comer() throws InterruptedException {
        colonia.accederAlComedor(3000, 3000, -1);
        System.out.println("Hormiga " + this.id + " procedió a la comisión\n");
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
            } catch (InterruptedException ignored) {
                protegerColonia();
            }
        }

//        while (true) {
//            if (Thread.interrupted()) {
//                try {
//                    protegerColonia();
//                } catch (Exception ignored) {
//                }
//            } else if (this.iter > 0) {
//                try {
//                    instruccion();
//                    descansar();
//                    this.iter--;
//                } catch (InterruptedException ignored) {
//                    Thread.currentThread().interrupt();
//                }
//            } else {
//                try {
//                    comer();
//                    this.iter = 6;
//                } catch (InterruptedException ignored) {
//                    Thread.currentThread().interrupt();
//                }
//            }
//        }
    }
}
