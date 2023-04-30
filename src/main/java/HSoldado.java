public class HSoldado extends Thread {

    String id;
    Colonia colonia;
    int iter;

    public HSoldado(int id, Colonia colonia) {
        this.id = "HS" + String.format("%04d", id);
        this.iter = 6;
        this.colonia = colonia;
    }

    void protegerColonia() {
    }

    void instruccion() {
        colonia.zonaDeInstruccion(2000, 8000);
    }

    void descansar() {
        colonia.descansar(2000, 2000);
    }

    void comer() {
        colonia.accederAlComedor(3000, 3000, -1);
        System.out.println("Hormiga " + this.id + " procedió a la comisión\n");
    }

    @Override
    public void run() {
        colonia.accederTunelEntrada();

        while (true) {
            if (colonia.hayInvasor()) {
                protegerColonia();
            } else if (this.iter > 0) {
                instruccion();
                descansar();
                this.iter--;
            } else {
                comer();
                this.iter = 6;
            }
        }
    }
}
