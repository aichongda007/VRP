package algorithms.heuristic.con_imp;

import model.Instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FindGreedyInsert {

    Instance instance;
    public FindGreedyInsert(Instance instance) {
        this.instance = instance;
    }



    // find cudtomer ID according his location ID
    public int findCID(int locaID){
        int cID=0;
        for (int customerID=0;customerID<instance.cuslocs.length;customerID++){
            for (int cusLocNum=0;cusLocNum<instance.cuslocs[customerID].length;cusLocNum++){
                if (instance.cuslocs[customerID][cusLocNum]==locaID)
                    cID=customerID;
                break;
            }
            break;
        }
        return cID;
    }


    // calculate total load of a route
    public int calculateTotalLoad(List routeList) {
        int totalLoad = 0;
        if (routeList.size() == 0) {
            totalLoad = 0;
        } else {
            for (int i = 0; i < routeList.size(); i++) {
                totalLoad += instance.demands[findCID((int) routeList.get(i))];
            }
        }
        return totalLoad;
    }


    public int calculateArrivalTime(List list,int ind){
        int [] arrivalTime= new int[list.size()];

        arrivalTime[0] = instance.traveltimes[0][(int) list.get(0)];
        for (int i=0;i<list.size()-1;i++){
            arrivalTime[i+1]=arrivalTime[i]+instance.traveltimes[(int)list.get(i)][(int) list.get(i+1)];
        }
    return arrivalTime[ind];
    }

    // feasible insert to satisfy the timewindows (any route/the location of selected customer/the index of route list )
    public boolean feasibleInsertForTimewindows(List list,int cus_locID,int index) {
       // int[] arrivalTime = new int[list.size() + 1];
        int after_routeTime = 0;
        boolean a=true;
        /*
        // calculate the total time before the location is added
        if (list.size()==0){
            before_routeTime=0;
        }else{
            for (int i = 0; i < list.size() - 1; i++) {
                before_routeTime += instance.traveltimes[(int) list.get(i)][(int) list.get(i + 1)];
            }
            before_routeTime+=instance.traveltimes[0][(int) list.get(0)] + instance.traveltimes[(int) list.get(list.size() - 1)][0];
        }
        */

        // calculate the total time after the location is added
        List AfterList=new ArrayList();
        AfterList.addAll(list);
        AfterList.add(index, cus_locID);
        for (int i = 0; i < AfterList.size() - 1; i++) {
            after_routeTime += instance.traveltimes[(int) AfterList.get(i)][(int) AfterList.get(i + 1)];
        }
            after_routeTime += instance.traveltimes[0][(int) AfterList.get(0)] + instance.traveltimes[(int) AfterList.get(AfterList.size() - 1)][0];

        // arrive the location between the start time and end time
        for (int i = 0; i < AfterList.size(); i++) {
            if (calculateArrivalTime(AfterList,i) >=instance.timewindows[(int) AfterList.get(i)][0] && calculateArrivalTime(AfterList,i) <= instance.timewindows[(int) AfterList.get(i)][1]){
                if (after_routeTime <= instance.duration){
                    a=true;
                }else {
                    a=false;
                }
            }else {
                a=false;
            }
        }
    return a;
    }


    //返回一个集合，带三个元素，第一个是要插入的客户的位置ID，路径以及该路径中插入位置
    public List findGreedyInsert(List<List<Integer>> schedule, int randCustomerID){

        // store the best candidate insertion
        List bestCandidateList = new ArrayList();
        // store all the feasible candidate insertion
        List<List<Integer>> candidateList =new ArrayList();

        int [] before_routeTime=new int [schedule.size()];

        for (int j=0;j<schedule.size();j++){
            if (schedule.get(j).size()==0) {
                before_routeTime[j] = 0;
            }else{
                for (int k=0;k<schedule.get(j).size()-1;k++){
                    before_routeTime[j] += instance.traveltimes[schedule.get(j).get(k)][schedule.get(j).get(k+1)];
                }
                    before_routeTime[j] +=instance.traveltimes[0][schedule.get(j).get(0)] + instance.traveltimes[schedule.get(j).get(schedule.get(j).size()-1)][0];
            }
        }


        //loop through each location of selected customer randomly
        for(int i=0; i<instance.cuslocs[randCustomerID].length;i++){
            for (int j=0;j<schedule.size();j++){
                //判断是否超过路径最大容量
                if (instance.demands[randCustomerID]+calculateTotalLoad(schedule.get(j))<=instance.capacity){
                    if (schedule.get(j).size()==0){
                        if(feasibleInsertForTimewindows(schedule.get(j), instance.cuslocs[randCustomerID][i], 0)){
                            List modelList = new ArrayList();
                            modelList.addAll(schedule.get(j));

                            modelList.add(0, instance.cuslocs[randCustomerID][i]);
                            int after_routeTime = 0;
                            after_routeTime = instance.traveltimes[0][(int) modelList.get(0)]+instance.traveltimes[(int) modelList.get(0)][0];

                            List medList = new ArrayList();
                            // locationID of selected customer
                            medList.add(instance.cuslocs[randCustomerID][i]);
                            // route number
                            medList.add(j);
                            //index of route number
                            medList.add(0);
                            int costChange = after_routeTime-0;
                            medList.add(costChange);

                            candidateList.add(medList);
                        }
                    }
                    if (schedule.get(j).size()>0){
                        for (int k = 0; k <= schedule.get(j).size(); k++) {
                            //判断是否满足时间窗内及路径周期约束
                            if (feasibleInsertForTimewindows(schedule.get(j), instance.cuslocs[randCustomerID][i], k)) {

                                List modelList = new ArrayList();
                                modelList.addAll(schedule.get(j));

                                modelList.add(k, instance.cuslocs[randCustomerID][i]);
                                int after_routeTime = 0;

                                for (int num = 0; num < modelList.size() - 1; num++) {
                                    after_routeTime += instance.traveltimes[(int) modelList.get(num)][(int) modelList.get(num + 1)];
                                }
                                    after_routeTime += instance.traveltimes[0][(int) modelList.get(0)] + instance.traveltimes[(int) modelList.get(modelList.size() - 1)][0];

                                List medList = new ArrayList();
                                // locationID of selected customer
                                medList.add(instance.cuslocs[randCustomerID][i]);
                                // route number
                                medList.add(j);
                                //index of route number
                                medList.add(k);
                                int costChange = after_routeTime - before_routeTime[j];
                                medList.add(costChange);

                                candidateList.add(medList);
                            }
                        }
                    }
                }else{
                    continue;
                }
            }
        }
        // sort collection by the cost change
        Collections.sort(candidateList, new Comparator<List>() {

            @Override
            public int compare(List o1, List o2) {
                return (int)o1.get(1) - (int)o2.get(1);
            }
        });

        if (candidateList.size()==0){
            System.out.println("there is no feasible candidate insertion");
        }else{
            bestCandidateList.add(candidateList.get(0).get(0));
            bestCandidateList.add(candidateList.get(0).get(1));
            bestCandidateList.add(candidateList.get(0).get(2));
            bestCandidateList.add(candidateList.get(0).get(3));
        }
        return bestCandidateList;
    }
}
