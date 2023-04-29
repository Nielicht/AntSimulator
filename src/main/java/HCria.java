public class HCria extends Thread {

    String id;
    Colonia colonia;

    public HCria(int id, Colonia colonia) {
        this.id = "HC" + String.format("%04d", id);
        this.colonia = colonia;
    }
}
