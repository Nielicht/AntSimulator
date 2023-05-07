package Logica;

import GUI.Interfaz;

public class Generador extends Thread {

    private Colonia colonia;
    private int numHormiga;

    public Generador(Interfaz controlador) {
        this.colonia = new Colonia(controlador);
        this.numHormiga = 1;
    }

    @Override
    public void run() {
        System.out.println("Simulación comenzada\n");

        while (true) {
            if (numHormiga == 20) colonia.triggerInvasion();
            int ratioGeneracion = (int) (Math.random() * 3000) + 500;
            try {
                Thread.sleep(ratioGeneracion);
            } catch (InterruptedException ignored) {
            }
            colonia.generarHormiga();
            System.out.println("ID = " + numHormiga + ", TGen = " + (float) ratioGeneracion / 1000 + " Segundos\n");

            numHormiga++;
        }
    }
}