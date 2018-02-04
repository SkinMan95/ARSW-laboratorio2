package edu.eci.arsw.highlandersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alejandro Anzola email: alejandro.anzola@mail.escuelaing.edu.co
 */
public class ImmortalCleaner extends Thread {

    public static final int CYCLE_SLEEP_TIME = 100;

    private static ImmortalCleaner instance;

    private final AtomicBoolean isStopped;
    
    private final List<Immortal> immortalList;
    private final Queue<Immortal> readyToRemove;

    private ImmortalCleaner(List<Immortal> list, AtomicBoolean isStopped) {
        this.immortalList = list;
        readyToRemove = new ConcurrentLinkedQueue<>();
        this.isStopped = isStopped;
    }

    public static ImmortalCleaner getInstance() {
        assert instance != null;
        return instance;
    }

    public static ImmortalCleaner getInstance(List<Immortal> list, AtomicBoolean isStopped) {
        if (instance == null) {
            instance = new ImmortalCleaner(list, isStopped);
        }
        return instance;
    }

    public void removeDeadImmortal(Immortal im) {
        if (im.isDead()) {
            assert immortalList.contains(im);
            readyToRemove.offer(im);
        }
    }

    @Override
    public void run() {
        while (immortalList.size() > 1 && ! isStopped.get()) {
            if (!readyToRemove.isEmpty()) {
                synchronized (immortalList) {
                    while (!readyToRemove.isEmpty()) {
                        immortalList.remove(readyToRemove.poll());
                    }
                }
            }

            try {
                Thread.sleep(CYCLE_SLEEP_TIME);
            } catch (InterruptedException ex) {
                Logger.getLogger(ImmortalCleaner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
