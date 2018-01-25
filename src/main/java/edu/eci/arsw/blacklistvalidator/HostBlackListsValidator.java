package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT = 5;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @param numThreads number of threads to check
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int numThreads){
        
        LinkedList<Integer> blackListOcurrences = new LinkedList<>();
        
        int ocurrencesCount = 0;
        
        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
        
        int checkedListsCount = 0;
        
        if (numThreads <= 0) {
            numThreads = skds.getRegisteredServersCount();
        }
        
        List<ParallelIPAddressValidator> validators = new ArrayList<>();
        
        int size = skds.getRegisteredServersCount();
        int delta = size / numThreads;
        for (int i = 0; i < size; i += delta) {
            validators.add(new ParallelIPAddressValidator(ipaddress, i, Math.min(i+delta, size)));
//            System.out.println("Intervalo: " + i + " ; " + Math.min(i+delta, size));
        }
        
        System.out.println("Real number of threads: " + validators.size());
        
        for (ParallelIPAddressValidator val : validators) {
            val.start();
        }
        
        for (ParallelIPAddressValidator val : validators) {
            try {
                val.join();
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, "ERROR: " + ex.getMessage());
            }
        }
        
        for (int i = 0; i < numThreads && ocurrencesCount < BLACK_LIST_ALARM_COUNT; i++) {
            ParallelIPAddressValidator val = validators.get(i);
            ocurrencesCount += val.numBlacklisted();
            blackListOcurrences.addAll(val.getOcurrences());
            checkedListsCount += val.getCheckedCount();
        }
        
        if (ocurrencesCount >= BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        } else {
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
}
