import java.util.concurrent.BrokenBarrierException;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Colonia colonia = new Colonia();
        int invasionRate = 50;
        int iter = 1;

        System.out.println("Simulación comenzada, invasion rate al " + invasionRate);

        while (true) {
            if (colonia.numHormiwis() >= 200) break;

            int rate = (int) (Math.random() * 3000) + 500;
            Thread.sleep(rate);
            colonia.generarHormiga();
            System.out.println("ID = " + iter + ", TGen = " + (float) rate / 1000 + " Segundos\n");

            iter++;
            invasionRate--;
            if (invasionRate == 0) {
                colonia.invasion();
            }
        }
    }
}
