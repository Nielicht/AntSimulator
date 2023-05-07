package Logica;

import GUI.Interfaz;

import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Colonia {

    // Logs
    public Logger logger;

    // Misceláneo
    private boolean invasorPresente;
    private int obrerasPorGenerar, soldadosyCriasPorGenerar, obrerasID, soldadosID, criasID, iter;

    // Sincronización
    private AtomicInteger unidadesComidaAlmacen, unidadesComidaComedor;
    private Semaphore tunelEntrada, limiteHormigasEnAlmacen;
    private CyclicBarrier invasionAgrupacion;
    private CountDownLatch invasorRepelido;

    // Hormigas
    private HashMap<Integer, HObrera> obreras;
    private ConcurrentHashMap<Integer, HSoldado> soldados;
    private ConcurrentHashMap<Integer, HCria> crias;

    // GUI
    public CopyOnWriteArrayList<String> buscandoComida;
    private CopyOnWriteArrayList<String> zonaDeAlmacenaje;
    public CopyOnWriteArrayList<String> transportandoAlComedor;
    public CopyOnWriteArrayList<String> zonaDeInstruccion;
    public CopyOnWriteArrayList<String> zonaDeDescanso;
    public CopyOnWriteArrayList<String> zonaParaComer;
    private CopyOnWriteArrayList<String> zonaParaRefugiarse;
    private CopyOnWriteArrayList<String> repeliendoInvasor;
    public GUIUpdater guiUpdater;

    public Colonia(Interfaz controlador) {
        this.logger = new Logger();
        this.invasorPresente = false;
        this.obrerasPorGenerar = 6000;
        this.soldadosyCriasPorGenerar = 2000;
        this.obrerasID = 1;
        this.soldadosID = 1;
        this.criasID = 1;
        this.iter = 0;
        this.unidadesComidaAlmacen = new AtomicInteger(0);
        this.unidadesComidaComedor = new AtomicInteger(0);
        this.tunelEntrada = new Semaphore(2, true);
        this.limiteHormigasEnAlmacen = new Semaphore(10, true);
        this.obreras = new HashMap<>();
        this.soldados = new ConcurrentHashMap<>();
        this.crias = new ConcurrentHashMap<>();

        this.buscandoComida = new CopyOnWriteArrayList<>();
        this.zonaDeAlmacenaje = new CopyOnWriteArrayList<>();
        this.transportandoAlComedor = new CopyOnWriteArrayList<>();
        this.zonaDeInstruccion = new CopyOnWriteArrayList<>();
        this.zonaDeDescanso = new CopyOnWriteArrayList<>();
        this.zonaParaComer = new CopyOnWriteArrayList<>();
        this.zonaParaRefugiarse = new CopyOnWriteArrayList<>();
        this.repeliendoInvasor = new CopyOnWriteArrayList<>();
        this.guiUpdater = new GUIUpdater(controlador, this);

        Thread hiloGUIUpdater = new Thread(this.guiUpdater);
        hiloGUIUpdater.setDaemon(true);
        hiloGUIUpdater.start();
    }

    public void triggerInvasion() {
        HashMap<Integer, HSoldado> soldadosActuales = new HashMap<>(soldados);
        this.invasorRepelido = new CountDownLatch(soldadosActuales.size());
        this.invasionAgrupacion = new CyclicBarrier(soldadosActuales.size());

        new Thread(this::invasionManager).start();
        this.logger.log("Ha aparecido un invasor");
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
            invasorRepelido.await();
        } catch (InterruptedException ignored) {
        } finally {
            this.invasorPresente = false;
            this.logger.log("El invasor ha sido repelido");
        }
    }

    public void repelerInvasor(String id) throws BrokenBarrierException, InterruptedException {
        this.repeliendoInvasor.add(id);
        this.invasionAgrupacion.await();
        Thread.sleep(20000);
        this.invasorRepelido.countDown();
        this.repeliendoInvasor.remove(id);
    }

    public void accederAlComedor(int min, int max, int comida, String id) throws InterruptedException {
        int tiempoAccedido = (int) (Math.random() * (max - min)) + min;

        this.zonaParaComer.add(id);
        Thread.sleep(tiempoAccedido);
        unidadesComidaComedor.addAndGet(comida);
        this.zonaParaComer.remove(id);
    }

    public void descansar(int min, int max, String id) throws InterruptedException {
        int tiempoDescansando = (int) (Math.random() * (max - min)) + min;
        this.zonaDeDescanso.add(id);
        Thread.sleep(tiempoDescansando);
        this.zonaDeDescanso.remove(id);
    }

    public void accederAlAlmacen(int min, int max, int comida, String id) throws InterruptedException {
        int tiempoAlmacenando = (int) (Math.random() * (max - min)) + min;

        limiteHormigasEnAlmacen.acquire();
        this.zonaDeAlmacenaje.add(id);
        try {
            Thread.sleep(tiempoAlmacenando);
            unidadesComidaAlmacen.addAndGet(comida);

            synchronized (this) {
                notifyAll();
            }
        } finally {
            limiteHormigasEnAlmacen.release();
            this.zonaDeAlmacenaje.remove(id);
        }
    }

    public void recogerDelAlmacen(int min, int max, int comida, String id) throws InterruptedException {
        int tiempoRecogiendo = (int) (Math.random() * (max - min)) + min;

        synchronized (this) {
            while (this.unidadesComidaAlmacen.get() - comida < 0) {
                wait();
            }

            limiteHormigasEnAlmacen.acquire();
            this.zonaDeAlmacenaje.add(id);
            try {
                Thread.sleep(tiempoRecogiendo);
                this.unidadesComidaAlmacen.addAndGet(-comida);
            } finally {
                limiteHormigasEnAlmacen.release();
                this.zonaDeAlmacenaje.remove(id);
            }
        }
    }

    public void zonaDeInstruccion(int min, int max, String id) throws InterruptedException {
        int tiempoInstruccion = (int) (Math.random() * (max - min)) + min;

        this.zonaDeInstruccion.add(id);
        Thread.sleep(tiempoInstruccion);
        this.zonaDeInstruccion.remove(id);
    }

    public void accederAlRefugio(String id) throws InterruptedException {
        this.zonaParaRefugiarse.add(id);
        invasorRepelido.await();
        this.zonaParaRefugiarse.remove(id);
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

    public Object[] recolectarDatos() {
        Object[] datos = new Object[10];

        datos[0] = this.buscandoComida;
        datos[1] = this.zonaDeAlmacenaje;
        datos[2] = this.transportandoAlComedor;
        datos[3] = this.zonaDeInstruccion;
        datos[4] = this.zonaDeDescanso;
        datos[5] = this.zonaParaComer;
        datos[6] = this.zonaParaRefugiarse;
        datos[7] = this.repeliendoInvasor;
        datos[8] = this.unidadesComidaAlmacen;
        datos[9] = this.unidadesComidaComedor;

        return datos;
    }
}
