package Logica;

public class HObrera extends Thread {

    private String id, proximoEstado;
    private Colonia colonia;
    private int iter, comidaTransportada;

    public HObrera(int id, Colonia colonia) {
        this.id = "HO" + String.format("%04d", id);
        this.iter = 10;
        this.colonia = colonia;
        this.comidaTransportada = 0;
        this.proximoEstado = "";
    }

    private void recogerComidaExterior() throws InterruptedException {
        colonia.logger.log("Hormiga obrera " + this.id + " sale a recoger comida");
        colonia.accederTunerSalida();
        colonia.buscandoComida.add(this.id);
        Thread.sleep(4000);
        comidaTransportada += 5;
        colonia.buscandoComida.remove(this.id);
    }

    private void recogerComidaAlmacen() throws InterruptedException {
        colonia.logger.log("Hormiga obrera " + this.id + " entra al almacen a recoger comida");

        colonia.accederAlAlmacen(1000, 2000, -5, this.id);
        this.comidaTransportada += 5;
    }

    private void almacenarComidaAlmacen() throws InterruptedException {
        colonia.logger.log("Hormiga obrera " + this.id + " entra al almacen a almacenar comida");

        colonia.accederTunelEntrada();
        colonia.accederAlAlmacen(2000, 4000, this.comidaTransportada, this.id);
        this.comidaTransportada = 0;
    }

    private void irZonaComedor() throws InterruptedException {
        colonia.logger.log("Hormiga obrera " + this.id + " se dispone a ir hacia el comedor");
        int tiempoCaminando = (int) (Math.random() * 2000) + 1000;
        colonia.transportandoAlComedor.add(this.id);
        Thread.sleep(tiempoCaminando);
        colonia.transportandoAlComedor.remove(this.id);
    }

    private void depositarComida() throws InterruptedException {
        colonia.logger.log("Hormiga obrera " + this.id + " entra en el comedor y se dispone a servir comida");
        colonia.accederAlComedor(1000, 2000, comidaTransportada, this.id);
        this.comidaTransportada = 0;
    }

    private void comer() throws InterruptedException {
        colonia.logger.log("Hormiga obrera " + this.id + " procede a comer");
        colonia.accederAlComedor(3000, 3000, -1, this.id);
    }

    private void descansar() throws InterruptedException {
        colonia.logger.log("Hormiga obrera " + this.id + " procede a descansar");
        colonia.descansar(1000, 1000, this.id);
    }

    private void determinarIntinerario() {
        if (iter <= 0) {
            this.proximoEstado = "descansar";
            return;
        }

        int id = Integer.parseInt(this.id.replaceAll("\\D+", ""));
        boolean esPar = id % 2 == 0;

        if (esPar) {
            this.proximoEstado = "recogerDelAlmacen";
        } else {
            this.proximoEstado = "recogerDelExterior";
        }
    }

    private void limpiarZonas() {
        this.colonia.buscandoComida.remove(this.id);
        this.colonia.zonaDeAlmacenaje.remove(this.id);
        this.colonia.transportandoAlComedor.remove(this.id);
        this.colonia.zonaParaComer.remove(this.id);
        this.colonia.zonaDeDescanso.remove(this.id);
    }

    private void interruptHandler() {
        this.colonia.realizarPausa();
        limpiarZonas();
    }

    @Override
    public void run() {
        colonia.accederTunelEntrada();
        determinarIntinerario();

        if (this.colonia.estaPausado()) this.colonia.realizarPausa();
        while (true) {
            try {
                switch (proximoEstado) {
                    case "recogerDelAlmacen" -> {
                        recogerComidaAlmacen();
                        this.proximoEstado = "transportar";
                    }
                    case "transportar" -> {
                        irZonaComedor();
                        this.proximoEstado = "depositar";
                    }
                    case "depositar" -> {
                        depositarComida();
                        iter--;
                        determinarIntinerario();
                    }

                    case "recogerDelExterior" -> {
                        recogerComidaExterior();
                        this.proximoEstado = "almacenar";
                    }
                    case "almacenar" -> {
                        almacenarComidaAlmacen();
                        iter--;
                        determinarIntinerario();
                    }

                    case "comer" -> {
                        comer();
                        this.proximoEstado = "descansar";
                    }
                    case "descansar" -> {
                        descansar();
                        this.iter = 10;
                        determinarIntinerario();
                    }
                }
            } catch (InterruptedException e) {
                interruptHandler();
            }
        }
    }
}
