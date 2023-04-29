public class HSoldado extends Thread {

    String id;
    Colonia colonia;
    int iter;

    public HSoldado(int id, Colonia colonia) {
        this.id = "HS" + String.format("%04d", id);
        this.iter = 6;
        this.colonia = colonia;
    }
}
