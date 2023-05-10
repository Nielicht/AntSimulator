package Logica;

public class HCria extends Thread {

    private String id, proximoEstado;
    private Colonia colonia;

    public HCria(int id, Colonia colonia) {
        this.id = "HC" + String.format("%04d", id);
        this.colonia = colonia;
        this.proximoEstado = "";
    }

    private void descansar() throws InterruptedException {
        colonia.logger.log("Hormiga cria " + this.id + " procede a descansar");
        colonia.descansar(4000, 4000, this.id);
    }

    private void comer() throws InterruptedException {
        colonia.logger.log("Hormiga cria " + this.id + " procede a comer");
        colonia.accederAlComedor(3000, 5000, -1, this.id);
    }

    private void refugiarse() {
        colonia.logger.log("Hormiga cria " + this.id + " procede a refugiarse");
        colonia.accederAlRefugio(this.id);
    }

    private void determinarIntinerario() {
        if (this.colonia.hayInvasor()) this.proximoEstado = "refugiarse";
        else this.proximoEstado = "comer";
    }

    private void limpiarZonas() {
        colonia.zonaParaComer.remove(this.id);
        colonia.zonaDeDescanso.remove(this.id);
    }

    private void interruptHandler() {
        if (this.colonia.hayInvasor()) {
            limpiarZonas();
            refugiarse();
        } else if (this.colonia.estaPausado()) {
            this.colonia.realizarPausa();
            limpiarZonas();
        }
    }

    @Override
    public void run() {
        colonia.accederTunelEntrada();
        determinarIntinerario();

        if (this.colonia.estaPausado()) this.colonia.realizarPausa();
        while (true) {
            try {
                switch (proximoEstado) {
                    case "comer" -> {
                        comer();
                        this.proximoEstado = "descansar";
                    }
                    case "descansar" -> {
                        descansar();
                        this.proximoEstado = "comer";
                    }

                    case "refugiarse" -> {
                        refugiarse();
                        this.proximoEstado = "comer";
                    }
                }
            } catch (InterruptedException e) {
                interruptHandler();
            }
        }
    }
}
