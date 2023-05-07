package Logica;

public class HCria extends Thread {

    private String id;
    private Colonia colonia;

    public HCria(int id, Colonia colonia) {
        this.id = "HC" + String.format("%04d", id);
        this.colonia = colonia;
    }

    private void descansar() throws InterruptedException {
        colonia.logger.log("Hormiga cria " + this.id + " procede a descansar");
        colonia.descansar(4000, 4000, this.id);
    }

    private void comer() throws InterruptedException {
        colonia.logger.log("Hormiga cria " + this.id + " procede a comer");
        colonia.accederAlComedor(3000, 5000, -1, this.id);
    }

    private void refugiarse() throws InterruptedException {
        colonia.logger.log("Hormiga cria " + this.id + " procede a refugiarse");
        colonia.accederAlRefugio(this.id);
    }

    @Override
    public void run() {
        colonia.accederTunelEntrada();

        while (true) {
            try {
                if (colonia.hayInvasor()) {
                    refugiarse();
                } else {
                    comer();
                    descansar();
                }
            } catch (InterruptedException ignored) {
            }
        }
    }
}
