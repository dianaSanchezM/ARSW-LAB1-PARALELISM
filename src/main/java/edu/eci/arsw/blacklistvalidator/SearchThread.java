/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.ArrayList;

/**
 *
 * @author 2108310
 */
public class SearchThread extends Thread{
    private int from;
    private int to;
    private String host;
    private int numOcur;
    private ArrayList<Integer> blackListOcur;
    private HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
    private int blackListChecked;

    public SearchThread(int from, int to, String host) {
        this.from = from;
        this.to = to;
        this.host = host;
        numOcur = 0;
        blackListOcur = new ArrayList<>();
        blackListChecked = 0;
    }

    @Override
    public void run() {
        for (int i=from; i<to; i++){
            blackListChecked++;
            if (skds.isInBlackListServer(i, host)){
                numOcur++;
                blackListOcur.add(i);
            }
        }
    }

    public int getNumOcur() {
        return numOcur;
    }

    public ArrayList<Integer> getBlackListOcur() {
        return blackListOcur;
    }

    public void setBlackListOcur(ArrayList<Integer> blackListOcur) {
        this.blackListOcur = blackListOcur;
    }

    public int getBlackListChecked() {
        return blackListChecked;
    }
    
}
