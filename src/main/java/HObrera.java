public class HObrera extends Thread {

    String id;
    Colonia colonia;
    int iter, comidaTransportada;

    public HObrera(int id, Colonia colonia) {
        this.id = "HO" + String.format("%04d", id);
        this.iter = 10;
        this.colonia = colonia;
        this.comidaTransportada = 0;
    }

    public void recogerComida() throws InterruptedException {
        colonia.accederTunerSalida();
        Thread.sleep(4000);
        comidaTransportada += 5;
    }

    public void recogerComidaAlmacen() throws InterruptedException {
        colonia.recogerDelAlmacen(1000, 2000, 5);
        this.comidaTransportada += 5;
    }

    public void almacenarComida() throws InterruptedException {
        colonia.accederTunelEntrada();
        colonia.accederAlAlmacen(2000, 4000, this.comidaTransportada);
        this.comidaTransportada = 0;
    }

    public void irZonaComedor() throws InterruptedException {
        int tiempoCaminando = (int) (Math.random() * 2000) + 1000;
        Thread.sleep(tiempoCaminando);
    }

    public void depositarComida() throws InterruptedException {
        colonia.accederAlComedor(1000, 2000, comidaTransportada);
        this.comidaTransportada = 0;
    }

    private void comer() throws InterruptedException {
        colonia.accederAlComedor(3000, 3000, -1);
        System.out.println("Hormiga " + this.id + " procedió a la comisión\n");
    }

    private void descansar() throws InterruptedException {
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


//        while (true) {
//            if (iter > 0 && esPar) { // Hormigas pares
//                try {
//                    recogerComidaAlmacen();
//                    irZonaComedor();
//                    depositarComida();
//                    iter--;
//                } catch (InterruptedException ignored) {
//                }
//            } else if (iter > 0 && !esPar) { // Hormigas impares
//                try {
//                    recogerComida();
//                    almacenarComida();
//                    iter--;
//                } catch (InterruptedException ignored) {
//                }
//            } else { // Descanso time
//                try {
//                    comer();
//                    descansar();
//                    this.iter = 10;
//                } catch (InterruptedException ignored) {
//                }
//            }
//        }
    }
}
