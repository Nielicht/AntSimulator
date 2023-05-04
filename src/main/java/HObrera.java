public class HObrera extends Thread {

    private String id;
    private Colonia colonia;
    private int iter, comidaTransportada;

    public HObrera(int id, Colonia colonia) {
        this.id = "HO" + String.format("%04d", id);
        this.iter = 10;
        this.colonia = colonia;
        this.comidaTransportada = 0;
    }

    private void recogerComida() throws InterruptedException {
        colonia.logger.log("Hormiga obrera " + this.id + " sale a recoger comida");
        colonia.accederTunerSalida();
        Thread.sleep(4000);
        comidaTransportada += 5;
    }

    private void recogerComidaAlmacen() throws InterruptedException {
        colonia.logger.log("Hormiga obrera " + this.id + " entra al almacen a recoger comida");
        colonia.recogerDelAlmacen(1000, 2000, 5);
        this.comidaTransportada += 5;
    }

    private void almacenarComida() throws InterruptedException {
        colonia.logger.log("Hormiga obrera " + this.id + " entra al almacen a almacenar comida");
        colonia.accederTunelEntrada();
        colonia.accederAlAlmacen(2000, 4000, this.comidaTransportada);
        this.comidaTransportada = 0;
    }

    private void irZonaComedor() throws InterruptedException {
        colonia.logger.log("Hormiga obrera " + this.id + " se dispone a ir hacia el comedor");
        int tiempoCaminando = (int) (Math.random() * 2000) + 1000;
        Thread.sleep(tiempoCaminando);
    }

    private void depositarComida() throws InterruptedException {
        colonia.logger.log("Hormiga obrera " + this.id + " entra en el comedor y se dispone a servir comida");
        colonia.accederAlComedor(1000, 2000, comidaTransportada);
        this.comidaTransportada = 0;
    }

    private void comer() throws InterruptedException {
        colonia.logger.log("Hormiga obrera " + this.id + " procede a comer");
        colonia.accederAlComedor(3000, 3000, -1);
        System.out.println("Hormiga " + this.id + " procedió a la comisión\n");
    }

    private void descansar() throws InterruptedException {
        colonia.logger.log("Hormiga obrera " + this.id + " procede a descansar");
        colonia.descansar(1000, 1000);
    }

    @Override
    public void run() {
        colonia.accederTunelEntrada();
        int id = Integer.parseInt(this.id.replaceAll("\\D+", ""));
        boolean esPar = id % 2 == 0;

        while (true) {
            try {
                if (iter > 0 && esPar) { // Hormigas pares
                    recogerComidaAlmacen();
                    irZonaComedor();
                    depositarComida();
                    iter--;
                } else if (iter > 0 && !esPar) { // Hormigas impares
                    recogerComida();
                    almacenarComida();
                    iter--;
                } else { // Descanso time
                    comer();
                    descansar();
                    this.iter = 10;
                }
            } catch (InterruptedException ignored) {
            }
        }
    }
}
