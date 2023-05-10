package Logica;

import GUI.GUIUpdater;
import GUI.Interfaz;

import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Colonia {

    // Logs
    public Logger logger;

    // Misceláneo
    private Generador generador;
    private volatile boolean invasorPresente, estaPausado;
    private int obrerasPorGenerar, soldadosyCriasPorGenerar, obrerasID, soldadosID, criasID, iter;

    // Sincronización
    private AtomicInteger unidadesComidaAlmacen, unidadesComidaComedor;
    private Semaphore tunelSalida, limiteHormigasEnAlmacen;
    private Object monitor;
    private CyclicBarrier invasionAgrupacion;
    private CountDownLatch invasorRepelido, pausa;

    // Hormigas
    private ConcurrentHashMap<Integer, HObrera> obreras;
    private ConcurrentHashMap<Integer, HSoldado> soldados;
    private ConcurrentHashMap<Integer, HCria> crias;

    // GUI
    public CopyOnWriteArrayList<String> buscandoComida;
    public CopyOnWriteArrayList<String> zonaDeAlmacenaje;
    public CopyOnWriteArrayList<String> transportandoAlComedor;
    public CopyOnWriteArrayList<String> zonaDeInstruccion;
    public CopyOnWriteArrayList<String> zonaDeDescanso;
    public CopyOnWriteArrayList<String> zonaParaComer;
    public CopyOnWriteArrayList<String> zonaParaRefugiarse;
    public CopyOnWriteArrayList<String> repeliendoInvasor;
    public GUIUpdater guiUpdater;

    public Colonia(Interfaz controlador, Generador generador) {
        this.logger = new Logger();
        this.generador = generador;
        this.invasorPresente = false;
        this.estaPausado = false;
        this.obrerasPorGenerar = 6000;
        this.soldadosyCriasPorGenerar = 2000;
        this.obrerasID = 1;
        this.soldadosID = 1;
        this.criasID = 1;
        this.iter = 0;
        this.unidadesComidaAlmacen = new AtomicInteger(0);
        this.unidadesComidaComedor = new AtomicInteger(0);
        this.monitor = new Object();
        this.tunelSalida = new Semaphore(2, true);
        this.limiteHormigasEnAlmacen = new Semaphore(10, true);
        this.obreras = new ConcurrentHashMap<>();
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
        controlador.linkColonia(this);
        this.guiUpdater = new GUIUpdater(controlador, this);

        Thread hiloGUIUpdater = new Thread(this.guiUpdater);
        hiloGUIUpdater.setDaemon(true);
        hiloGUIUpdater.start();
    }

    public void realizarPausa() {
        while (estaPausado) {
            try {
                this.pausa.await();
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void triggerInvasion() {
        if (invasorPresente || estaPausado) return;

        HashMap<Integer, HSoldado> soldadosActuales = new HashMap<>(soldados);
        this.invasorRepelido = new CountDownLatch(soldadosActuales.size());
        this.invasionAgrupacion = new CyclicBarrier(soldadosActuales.size());
        this.invasorPresente = true;
        this.logger.log("Ha aparecido un invasor");

        new Thread(this::invasionManager).start();
        for (HSoldado soldado : soldadosActuales.values()) {
            soldado.interrupt();
        }
        for (HCria cria : crias.values()) {
            cria.interrupt();
        }
    }

    public void invasionManager() {
        try {
            invasorRepelido.await();
        } catch (InterruptedException ignored) {
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        } finally {
            this.invasorPresente = false;
            this.logger.log("El invasor ha sido repelido");
        }
    }

    public void repelerInvasor(String id) {
        while (invasorPresente) {
            try {
                this.repeliendoInvasor.add(id);
                this.invasionAgrupacion.await();
                Thread.sleep(20000);
                this.invasorRepelido.countDown();
                while (invasorPresente) Thread.onSpinWait();
            } catch (BrokenBarrierException | InterruptedException e) {
                System.out.println("Realizando pausa");
                realizarPausa();
            } finally {
                this.repeliendoInvasor.remove(id);
            }
        }
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

    public void accederAlAlmacen(int min, int max, int comida, String id) {
        int tiempoAlmacenando = (int) (Math.random() * (max - min)) + min;
        boolean recoger = comida < 0, done = false;

        while (!done) {
            try {
                limiteHormigasEnAlmacen.acquireUninterruptibly();
                this.zonaDeAlmacenaje.add(id);
                Thread.sleep(tiempoAlmacenando);
                if (unidadesComidaAlmacen.addAndGet(comida) < 0 && recoger) {
                    unidadesComidaAlmacen.addAndGet(-comida);
                } else {
                    unidadesComidaAlmacen.addAndGet(comida);
                    done = true;
                }
            } catch (InterruptedException e) {
                realizarPausa();
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

    public void accederAlRefugio(String id) {
        while (invasorPresente) {
            try {
                this.zonaParaRefugiarse.add(id);
                invasorRepelido.await();
                while (invasorPresente) Thread.onSpinWait();
            } catch (InterruptedException e) {
                realizarPausa();
            } finally {
                this.zonaParaRefugiarse.remove(id);
            }
        }
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

    public synchronized void accederTunelEntrada() {
        boolean accedido = false;
        while (!accedido) {
            try {
                Thread.sleep(100);
                accedido = true;
            } catch (InterruptedException ignored) {
                realizarPausa();
            }
        }
    }

    public void accederTunerSalida() {
        boolean accedido = false;
        while (!accedido) {
            try {
                tunelSalida.acquireUninterruptibly();
                Thread.sleep(100);
                accedido = true;
            } catch (InterruptedException e) {
                realizarPausa();
            } finally {
                tunelSalida.release();
            }
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

    public void pausa() {
        if (estaPausado) return;

        this.pausa = new CountDownLatch(1);
        this.estaPausado = true;
        this.generador.interrupt();

        for (HObrera obrera : obreras.values()) {
            obrera.interrupt();
        }

        for (HSoldado soldado : soldados.values()) {
            soldado.interrupt();
        }

        for (HCria cria : crias.values()) {
            cria.interrupt();
        }

        this.logger.log("El programa fue pausado");
    }

    public boolean estaPausado() {
        return estaPausado;
    }

    public void reanudar() {
        if (invasorPresente) invasionAgrupacion.reset();
        this.estaPausado = false;
        pausa.countDown();
        this.logger.log("El programa fue reanudado");
    }
}
