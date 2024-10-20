package com.example.tigers_lottery;

import java.util.ArrayList;
import java.util.List;

public class WaitingList {
    private List<Entrant> waitingList;

    public WaitingList(){
        this.waitingList=new ArrayList<>();
    }

    public void addEntrant(Entrant entrant){
        waitingList.add(entrant);
    }

    public void removeEntrant(int i){
        if(i>=0 && i < waitingList.size()){
            waitingList.remove(i);
        }
    }

    public void removeEntrant(String userName){
        for(int i=0; i<waitingList.size(); i++){
            if(waitingList.get(i).getUserName().equals(userName)){
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
