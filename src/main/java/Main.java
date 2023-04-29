public class Main {
    public static void main(String[] args) throws InterruptedException {
        Colonia colonia = new Colonia();

        while (true) {
            if (colonia.numHormiwis() >= 30) break;

            int rate = (int) (Math.random() * 3000) + 500;
            Thread.sleep(rate);
            colonia.generarHormiga();
            System.out.println("TGen = " + (float) rate / 1000 + " Segundos\n");
        }

        System.out.println("DONE");
    }
}
