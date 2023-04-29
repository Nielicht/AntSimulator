public class HObrera extends Thread {

    String id;
    Colonia colonia;
    int iter, comidaTransportada;

    public HObrera(int id, Colonia colonia) {
        this.id = "HO" + String.format("%04d", id);
        this.iter = 2;
        this.colonia = colonia;
        this.comidaTransportada = 0;
    }

    public void recogerComida() {
        colonia.accederTunerSalida();
        try {
            Thread.sleep(4000);
            comidaTransportada += 5;
        } catch (InterruptedException ignored) {
        }
    }

    public void recogerComidaAlmacen() {
        colonia.accederAlAlmacen(1000, 2000, -5);
        this.comidaTransportada += 5;
    }

    public void almacenarComida() {
        colonia.accederTunelEntrada();
        colonia.accederAlAlmacen(2000, 4000, this.comidaTransportada);
        this.comidaTransportada = 0;
    }

    public void irZonaComedor() {
        int tiempoCaminando = (int) (Math.random() * 2000) + 1000;

        try {
            Thread.sleep(tiempoCaminando);
        } catch (InterruptedException ignored) {
        }
    }

    public void depositarComida() {
        colonia.accederAlComedor(1000, 2000, comidaTransportada);
        this.comidaTransportada = 0;
    }

    private void comer() {
        colonia.accederAlComedor(3000, 3000, -1);
        System.out.println("Hormiga " + this.id + " procedió a la comisión\n");
    }

    private void descansar() {
        colonia.descansar(1000, 1000);
    }

    @Override
    public void run() {
        colonia.accederTunelEntrada();
        int id = Integer.parseInt(this.id.replaceAll("\\D+", ""));

        while (true) {
            if (iter > 0 && id % 2 == 0) { // Hormigas pares
                recogerComidaAlmacen();
                irZonaComedor();
                depositarComida();
                iter--;
            } else if (iter > 0 && id % 2 != 0) { // Hormigas impares
                recogerComida();
                almacenarComida();
                iter--;
            } else { // Descanso time
                comer();
                descansar();
                this.iter = 10;
            }
        }
    }
}
