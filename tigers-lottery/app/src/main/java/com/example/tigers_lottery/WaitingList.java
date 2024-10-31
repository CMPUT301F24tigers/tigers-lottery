package com.example.tigers_lottery;

import com.example.tigers_lottery.JoinedEvents.Entrant;

import java.util.ArrayList;
import java.util.List;

public class WaitingList {
    private List<Entrant> waitingList;
    private List<Entrant> cancelledList;

    public WaitingList(){
        this.waitingList=new ArrayList<>();
        this.cancelledList=new ArrayList<>();
    }

    public void addEntrant(Entrant entrant){
        waitingList.add(entrant);
    }

    public void removeEntrant(int i){
        if(i>=0 && i < waitingList.size()){
            Entrant cancelledEntrant = waitingList.get(i);
            cancelledList.add(cancelledEntrant);
            waitingList.remove(i);
        }
    }

    public void removeEntrant(String userName){
        for(int i=0; i<waitingList.size(); i++){
            if(waitingList.get(i).getUserName().equals(userName)){
                Entrant cancelledEntrant = waitingList.get(i);
                cancelledList.add(cancelledEntrant);
                waitingList.remove(i);
                break;
            }
        }
    }

    public Entrant getEntrant(int i){
        if(i>=0 && i < waitingList.size()){
            return waitingList.get(i);
        } return null;
    }

    public int Size(){
        return waitingList.size();
    }
}
