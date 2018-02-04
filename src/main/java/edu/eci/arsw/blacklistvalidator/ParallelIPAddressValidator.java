package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author 2110137
 */
public class ParallelIPAddressValidator extends Thread{
    
    private final String ipaddress;
    private final int initialServer;
    private final int finalServer;
    
    private int blacklistLimit;
    private final AtomicInteger globalCounter;
    
    private int blacklists;
    private final List<Integer> ocurrences;
    private int checkedCount;
    
    HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
    
    public ParallelIPAddressValidator(String ip, int initialServer, int finalServer, AtomicInteger globalCounter, int blacklistLimit) {
        this.initialServer = initialServer;
        this.finalServer = finalServer;
        this.ipaddress = ip;
        this.globalCounter = globalCounter;
        this.blacklistLimit = blacklistLimit;
        
        this.ocurrences = new ArrayList<>();
    }
    
    @Override
    public void run() {
        for (int i = initialServer; i < finalServer && globalCounter.get() < blacklistLimit ; i++) {
            checkedCount++;
            if (skds.isInBlackListServer(i, ipaddress)) {
                globalCounter.addAndGet(1);
                blacklists++;
                ocurrences.add(i);
            }
        }
    }
    
    public int numBlacklisted() {
        return blacklists;
    }
    
    public List<Integer> getOcurrences() {
        return ocurrences;
    }
    
    public int getCheckedCount() {
        return checkedCount;
    }
    
}
