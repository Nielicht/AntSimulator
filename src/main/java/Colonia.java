import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Colonia {
    public Logger logger;
    private boolean invasorPresente;
    private int obrerasPorGenerar, soldadosyCriasPorGenerar, obrerasID, soldadosID, criasID, iter;
    private AtomicInteger unidadesComidaAlmacen, unidadesComidaComedor;
    private Semaphore tunelEntrada, zonaDeComida;
    private CyclicBarrier invasionAgrupacion;
    private CountDownLatch invasorRepelido;
    private HashMap<Integer, HObrera> obreras;
    private ConcurrentHashMap<Integer, HSoldado> soldados;
    private ConcurrentHashMap<Integer, HCria> crias;

    public Colonia() {
        this.logger = new Logger();
        this.invasorPresente = false;
        this.obrerasPorGenerar = 6000;
        this.soldadosyCriasPorGenerar = 2000;
        this.obrerasID = 1;
        this.soldadosID = 1;
        this.criasID = 1;
        this.iter = 0;
        this.unidadesComidaAlmacen = new AtomicInteger();
        this.unidadesComidaComedor = new AtomicInteger();
        this.tunelEntrada = new Semaphore(2, true);
        this.zonaDeComida = new Semaphore(10, true);
        this.obreras = new HashMap<>();
        this.soldados = new ConcurrentHashMap<>();
        this.crias = new ConcurrentHashMap<>();
    }

    public void triggerInvasion() {
        HashMap<Integer, HSoldado> soldadosActuales = new HashMap<>(soldados);

        this.invasorRepelido = new CountDownLatch(soldadosActuales.size());
        this.invasionAgrupacion = new CyclicBarrier(soldadosActuales.size());

        new Thread(this::invasionManager).start();

        System.out.println("Van a reaccionar " + soldadosActuales.size() + " soldados");

        for (HSoldado soldado : soldadosActuales.values()) {
            soldado.interrupt();
        }

        for (HCria cria : crias.values()) {
            cria.interrupt();
        }
    }

    public void invasionManager() {
        try {
            this.invasorPresente = true;
            System.out.println("¡Ha aparecido un invasor!");
            invasorRepelido.await();
        } catch (InterruptedException ignored) {
        } finally {
            this.invasorPresente = false;
            System.out.println("¡El invasor ha sido repelido!\n");
        }
    }

    public void repelerInvasor() throws BrokenBarrierException, InterruptedException {
        this.invasionAgrupacion.await();
        Thread.sleep(20000);
        this.invasorRepelido.countDown();
    }

    public void accederAlComedor(int min, int max, int comida) throws InterruptedException {
        int tiempoAccedido = (int) (Math.random() * (max - min)) + min;

        Thread.sleep(tiempoAccedido);
        unidadesComidaComedor.addAndGet(comida);
    }

    public void descansar(int min, int max) throws InterruptedException {
        int tiempoDescansando = (int) (Math.random() * (max - min)) + min;
        Thread.sleep(tiempoDescansando);
    }

    public void accederAlAlmacen(int min, int max, int comida) throws InterruptedException {
        int tiempoAlmacenando = (int) (Math.random() * (max - min)) + min;

        zonaDeComida.acquire();
        try {
            Thread.sleep(tiempoAlmacenando);
            unidadesComidaAlmacen.addAndGet(comida);

            synchronized (this) {
                notifyAll();
            }
        } finally {
            zonaDeComida.release();
        }
    }

    public void recogerDelAlmacen(int min, int max, int comida) throws InterruptedException {
        int tiempoRecogiendo = (int) (Math.random() * (max - min)) + min;

        synchronized (this) {
            while (this.unidadesComidaAlmacen.get() - comida < 0) {
                wait();
            }

            Thread.sleep(tiempoRecogiendo);
            this.unidadesComidaAlmacen.addAndGet(-comida);
        }
    }

    public void zonaDeInstruccion(int min, int max) throws InterruptedException {
        int tiempoInstruccion = (int) (Math.random() * (max - min)) + min;

        Thread.sleep(tiempoInstruccion);
    }

    public void accederAlRefugio() throws InterruptedException {
        invasorRepelido.await();
    }

    public void generarHormiga() {
        if (iter == 3) {
            if (soldadosyCriasPorGenerar <= 0) return;

            System.out.println("Generando soldado y cria");

            HSoldado hormigaS = new HSoldado(soldadosID, this);
            HCria hormigaC = new HCria(criasID, this);

            soldados.put(soldadosID, hormigaS);
            crias.put(criasID, hormigaC);

            soldadosID++;
            criasID++;
            soldadosyCriasPorGenerar--;

            iter = 0;

            hormigaS.start();
            hormigaC.start();
        } else {
            if (obrerasPorGenerar <= 0) return;

            System.out.println("Generando obrera");

            HObrera hormiga = new HObrera(obrerasID, this);
            obreras.put(obrerasID, hormiga);
            obrerasID++;
            obrerasPorGenerar--;

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

    public boolean hayInvasor() {
        return this.invasorPresente;
    }
}
