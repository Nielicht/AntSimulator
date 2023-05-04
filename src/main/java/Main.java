public class Main {
    public static void main(String[] args) throws InterruptedException {
        Colonia colonia = new Colonia();
        int numHormiga = 1;

        System.out.println("Simulación comenzada\n");

        while (true) {
            if (numHormiga == 150) colonia.triggerInvasion();
            int ratioGeneracion = (int) (Math.random() * 3000) + 500;
            Thread.sleep(ratioGeneracion);
            colonia.generarHormiga();
            System.out.println("ID = " + numHormiga + ", TGen = " + (float) ratioGeneracion / 1000 + " Segundos\n");

            numHormiga++;
        }
    }
}
