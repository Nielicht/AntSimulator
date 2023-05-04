public class HCria extends Thread {

    private String id;
    private Colonia colonia;

    public HCria(int id, Colonia colonia) {
        this.id = "HC" + String.format("%04d", id);
        this.colonia = colonia;
    }

    private void descansar() throws InterruptedException {
        colonia.descansar(4000, 4000);
    }

    private void comer() throws InterruptedException {
        colonia.accederAlComedor(3000, 5000, -1);
        System.out.println("Hormiga " + this.id + " procedió a la comisión\n");
    }

    private void refugiarse() throws InterruptedException {
        System.out.println("¡Hormiga " + this.id + " procedió a refugiarse!\n");
        colonia.accederAlRefugio();
        System.out.println("¡Hormiga " + this.id + " ha vuelto a la engordasion!\n");
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
