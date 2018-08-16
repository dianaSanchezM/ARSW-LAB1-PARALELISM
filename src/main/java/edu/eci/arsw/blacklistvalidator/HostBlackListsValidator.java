/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

    private static final int BLACK_LIST_ALARM_COUNT=5;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int N){
        
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        int ocurrencesCount=0;
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        int checkedListsCount=0;
        int numServers= skds.getRegisteredServersCount()/N;
        ArrayList<SearchThread> threadList= new ArrayList<>();
        
        for (int j=0; j<N; j++){
            if (j==N-1){
                threadList.add(new SearchThread(j*numServers, skds.getRegisteredServersCount(), ipaddress));
            }else{
                threadList.add(new SearchThread(j*numServers, (j+1)*numServers, ipaddress));
            }
        }
        
        for (SearchThread k:threadList){
            k.start();
        }
        
        for (SearchThread k:threadList){
            try {
                k.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(HostBlackListsValidator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        for (SearchThread i:threadList){
            ocurrencesCount+=i.getNumOcur();
            blackListOcurrences.addAll(i.getBlackListOcur());
            checkedListsCount+=i.getBlackListChecked();
        }
        
        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    
    
}
