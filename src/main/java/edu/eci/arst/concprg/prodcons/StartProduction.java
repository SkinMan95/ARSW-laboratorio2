package edu.eci.arst.concprg.prodcons;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class StartProduction {
    
    public static void main(String[] args) {
        
        int stockLimit = 2000;
        Queue<Integer> queue = new LinkedBlockingQueue<>(stockLimit);
        
        new Producer(queue,stockLimit).start();
        
        //let the producer create products for 5 seconds (stock).
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(StartProduction.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        new Consumer(queue).start();
    }
}