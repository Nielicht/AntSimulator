package Logica;

import GUI.Interfaz;

public class Generador extends Thread {

    private Colonia colonia;
    private int numHormiga;

    public Generador(Interfaz controlador) {
        this.numHormiga = 1;
        this.colonia = new Colonia(controlador, this);
    }

    @Override
    public void run() {
        System.out.println("Simulación comenzada\n");

        while (true) {
            try {
                int ratioGeneracion = (int) (Math.random() * 3000) + 500;
                Thread.sleep(ratioGeneracion);
                colonia.generarHormiga();

                System.out.println("ID = " + numHormiga + ", TGen = " + (float) ratioGeneracion / 1000 + " Segundos\n");
                numHormiga++;
            } catch (InterruptedException e) {
                this.colonia.realizarPausa();
            }
        }
    }
}