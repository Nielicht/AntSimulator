import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Colonia {
    int numObreras, numSoldadosyCrias;
    int obrerasID, soldadosID, criasID, iter;
    AtomicInteger unidadesComida, unidadesComidaComedor;
    Semaphore tunelEntrada, zonaDeComida;
    HashMap<Integer, HObrera> obreras;
    HashMap<Integer, HSoldado> soldados;
    HashMap<Integer, HCria> crias;

    public Colonia() {
        numObreras = 6000;
        numSoldadosyCrias = 2000;
        obrerasID = 1;
        soldadosID = 1;
        criasID = 1;
        iter = 0;
        unidadesComida = new AtomicInteger();
        unidadesComidaComedor = new AtomicInteger();
        tunelEntrada = new Semaphore(2, true);
        zonaDeComida = new Semaphore(10, true);
        obreras = new HashMap<>();
        soldados = new HashMap<>();
        crias = new HashMap<>();
    }

    public void accederAlComedor(int min, int max, int comida) {
        int tiempoAccedido = (int) (Math.random() * (max - min)) + min;

        try {
            Thread.sleep(tiempoAccedido);
            unidadesComidaComedor.addAndGet(comida);
        } catch (InterruptedException ignored) {
        }
    }

    public void descansar(int min, int max) {
        int tiempoDescansando = (int) (Math.random() * (max - min)) + min;

        try {
            Thread.sleep(tiempoDescansando);
        } catch (InterruptedException ignored) {
        }
    }

    public void accederAlAlmacen(int min, int max, int comida) {
        int tiempoAlmacenando = (int) (Math.random() * (max - min)) + min;

        try {
            zonaDeComida.acquire();
            Thread.sleep(tiempoAlmacenando);
            unidadesComida.addAndGet(comida);
        } catch (InterruptedException ignored) {
        }
    }



    public void activarHormiwi(int id) {
        HObrera hormiwi = obreras.get(id);
        System.out.println(hormiwi.toString());
        hormiwi.start();
    }

    public int numHormiwis() {
        return obreras.size();
    }

    public void generarHormiga() {
        if (iter == 3) {
            if (numSoldadosyCrias <= 0) {
                return;
            }

            System.out.println("Generando soldado y cria");

            HSoldado hormigaS = new HSoldado(soldadosID, this);
            HCria hormigaC = new HCria(criasID, this);

            soldados.put(soldadosID, hormigaS);
            crias.put(criasID, hormigaC);

            soldadosID++;
            criasID++;
            numSoldadosyCrias--;

            iter = 0;

            hormigaS.start();
            hormigaC.start();
        } else {
            if (numObreras <= 0) {
                return;
            }

            System.out.println("Generando obrera");

            HObrera hormiga = new HObrera(obrerasID, this);
            obreras.put(obrerasID, hormiga);
            obrerasID++;
            numObreras--;

            iter++;

            hormiga.start();
        }
    }

    public void accederTunelEntrada() {
        try {
            tunelEntrada.acquire();
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        } finally {
            tunelEntrada.release();
        }
    }

    public synchronized void accederTunerSalida() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
    }
}
