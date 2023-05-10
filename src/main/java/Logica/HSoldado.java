package Logica;

import java.util.concurrent.BrokenBarrierException;

public class HSoldado extends Thread {

    private String id, proximoEstado;
    private Colonia colonia;
    private int iter;

    public HSoldado(int id, Colonia colonia) {
        this.id = "HS" + String.format("%04d", id);
        this.iter = 6;
        this.colonia = colonia;
        this.proximoEstado = "instruccion";
    }

    private void protegerColonia() {
        colonia.logger.log("Hormiga soldado " + this.id + " acude a repeler al invasor");
        colonia.repelerInvasor(this.id);
    }

    private void instruccion() throws InterruptedException {
        colonia.logger.log("Hormiga soldado " + this.id + " entra a la zona de instrucción");
        colonia.zonaDeInstruccion(2000, 8000, this.id);
    }

    private void descansar() throws InterruptedException {
        colonia.logger.log("Hormiga soldado " + this.id + " entra a la zona de descanso");
        colonia.descansar(2000, 2000, this.id);
    }

    private void comer() throws InterruptedException {
        colonia.logger.log("Hormiga soldado " + this.id + " procede a comer");
        colonia.accederAlComedor(3000, 3000, -1, this.id);
    }

    private void limpiarZonas() {
        this.colonia.zonaDeInstruccion.remove(this.id);
        this.colonia.zonaDeDescanso.remove(this.id);
        this.colonia.zonaParaComer.remove(this.id);
    }

    private void interruptHandler() {
        if (this.colonia.hayInvasor()) {
            limpiarZonas();
            protegerColonia();
        } else if (this.colonia.estaPausado()) {
            this.colonia.realizarPausa();
            limpiarZonas();
        }
    }

    @Override
    public void run() {
        colonia.accederTunelEntrada();

        if (this.colonia.estaPausado()) this.colonia.realizarPausa();
        while (true) {
            try {
                switch (proximoEstado) {
                    case "instruccion" -> {
                        instruccion();
                        this.proximoEstado = "descansar";
                    }
                    case "descansar" -> {
                        descansar();
                        iter--;
                        if (iter > 0) this.proximoEstado = "instruccion";
                        else this.proximoEstado = "comer";
                    }

                    case "comer" -> {
                        comer();
                        this.iter = 6;
                        this.proximoEstado = "instruccion";
                    }
                }
            } catch (InterruptedException e) {
                interruptHandler();
            }
        }
    }

}