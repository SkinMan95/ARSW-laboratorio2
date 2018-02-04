package edu.eci.arsw.blacklistvalidator;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author hcadavid
 */
public class Main {
    
    public static void main(String a[]){
        HostBlackListsValidator hblv = new HostBlackListsValidator();
        List<Integer> blackListOcurrences;
        // List<Integer> blackListOcurrences = hblv.checkHost("200.24.34.55", 8);
        // List<Integer> blackListOcurrences = hblv.checkHost("200.24.34.55", 8);
        // List<Integer> blackListOcurrences = hblv.checkHost("212.24.24.55", 8);
        // List<Integer> blackListOcurrences = hblv.checkHost("202.24.34.55", 8);
        
        // String address = "212.24.24.55";
        String address = "202.24.34.55";
        
        Runtime rt = Runtime.getRuntime();
        
        List<Integer> numThreads = new ArrayList<>();
        //numThreads.add(1);
        numThreads.add(rt.availableProcessors());
        numThreads.add(2 * rt.availableProcessors());
        numThreads.add(50);
        numThreads.add(100);
        numThreads.add(200);
        numThreads.add(500);
        numThreads.add(1000);
        numThreads.add(2000);
        numThreads.add(3000);
        numThreads.add(6000);
        numThreads.add(12000);
        numThreads.add(24000);
        
        for (Integer numThread : numThreads) {
            long bt = System.currentTimeMillis();
            blackListOcurrences = hblv.checkHost(address, numThread);
            System.out.println("The host was found in the following blacklists:" + blackListOcurrences);
            System.out.println("With " +  numThread + " to process");
            long at = System.currentTimeMillis();
            System.out.println("Time to process: " + ((at - bt)/1000));
        }
    }
    
}
