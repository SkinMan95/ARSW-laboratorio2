package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 2110137
 */
public class ParallelIPAddressValidator extends Thread{
    
    private String ipaddress;
    private int initialServer;
    private int finalServer;
    
    private int blacklists;
    private List<Integer> ocurrences;
    private int checkedCount;
    
    HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
    
    public ParallelIPAddressValidator(String ip, int initialServer, int finalServer) {
        this.initialServer = initialServer;
        this.finalServer = finalServer;
        this.ipaddress = ip;
        
        this.ocurrences = new ArrayList<Integer>();
    }
    
    @Override
    public void run() {
        for (int i = initialServer; i < finalServer; i++) {
            checkedCount++;
            if (skds.isInBlackListServer(i, ipaddress)) {
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
