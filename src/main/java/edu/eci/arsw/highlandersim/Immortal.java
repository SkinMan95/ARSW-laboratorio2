package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback = null;

    private int health;

    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private AtomicBoolean isPaused;
    private AtomicBoolean isStopped;

    private Thread originalThread;

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb, AtomicBoolean isPaused, AtomicBoolean isStopped, Thread originalThread) {
        super(name);
        this.updateCallback = ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue = defaultDamageValue;
        this.isPaused = isPaused;
        this.isStopped = isStopped;
        this.originalThread = originalThread;
    }

    public boolean isDead() {
        return health <= 0;
    }
    
    public void run() {
        while (! this.isDead() && !this.isStopped.get()) {
            while (isPaused.get()) {
                synchronized (originalThread) {
                    try {
                        originalThread.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Immortal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            Immortal im;

            int myIndex = immortalsPopulation.indexOf(this);

            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }

            im = immortalsPopulation.get(nextFighterIndex);
            
            assert im != this;

            this.fight(im);

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        assert this.isDead() || isStopped.get();
        ImmortalCleaner.getInstance().removeDeadImmortal(this);
    }

    public void fight(Immortal i2) {
        if (i2.getHealth() > 0) {
            synchronized (i2) {
                i2.changeHealth(i2.getHealth() - defaultDamageValue);
            }
            
            synchronized(this) {
                this.changeHealth(this.getHealth() + defaultDamageValue);
            }
            updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
        } else {
            updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
        }
    }

    public synchronized void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

}
