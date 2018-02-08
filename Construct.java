package algorithms.heuristic.con_imp;

import io.Reader;
import model.Instance;
import util.Constants;
import util.Constants.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Construct {

    Instance instance;
    public List<List<Integer>> schedule=new ArrayList();

    public Construct(Instance instance) {
        this.instance = instance;
    }


    //pure greedy algorithm
    public List greedyHeuristic() throws FileNotFoundException {

        // define the unserved customer list;
        List<Integer> unServed = new ArrayList<Integer>();

        for (int i=0;i<instance.nrCustomers;i++) {
            unServed.add(i+1);
        }
        // define the schedule and build a empty route
        List<List<Integer>> schedule = new ArrayList();
        schedule.add(new ArrayList());

        while(unServed.size()>0){
            // add a new empty route if there is no empty route int the schedule
            if (schedule.get(schedule.size()-1).size()>0){
                schedule.add(new ArrayList());
            }

            //find a customer index randomly
            int randNum = (int)(Math.random()*unServed.size());
            int randCustomerID=unServed.get(randNum);

            File file =new File("F:\\My paper\\RDL_STT\\data\\general_instances_triangle_txt\\10000\\instance_0\\instance_0-triangle.txt");
            Instance instance = Reader.readInstance(file);

            FindGreedyInsert fg= new FindGreedyInsert(instance);

            List greedyInsertList = new ArrayList();
            greedyInsertList.addAll(fg.findGreedyInsert(schedule,randCustomerID));

            if (greedyInsertList.size()==0){
                System.out.println("there is no feasible greedy insertion");
            }else {
                int cusLocID= (int) greedyInsertList.get(0);
                int routeSt=(int)greedyInsertList.get(1);
                int routeStIndex=(int)greedyInsertList.get(2);
                schedule.get(routeSt).add(routeStIndex,cusLocID);
            }

            // delete the visited customers
            unServed.remove(randNum);
        }
        return schedule;
    }


    // calculate the cost for schedule
    public int getCost(List<List<Integer>> schedule){
        int cost=0;

        for (int i=0;i<schedule.size();i++){
            schedule.get(i).add(0,0);
            schedule.get(i).add(schedule.get(i).size(),0);
            int temp=0;
            for (int j=0;j<schedule.get(i).size()-1;j++){
                temp+=instance.traveltimes[schedule.get(i).get(j)][schedule.get(i).get(j+1)];
            }
            cost+=temp;
            schedule.get(i).remove(0);
            schedule.get(i).remove(schedule.get(i).size()-1);
        }
     return cost;
    }


    // generate N=100 solutions and store the one with the lowest cost

    public List getBestInitialSolution() throws FileNotFoundException {

        List<List<List<Integer>>> hundredSolution= new ArrayList();

        for (int i=0;i<100;i++){
          hundredSolution.add(greedyHeuristic());
        }

        //put the solution with lowest cost in the first place
        List sortBySolution = new ArrayList();
        int costBase = getCost(hundredSolution.get(0));

        for (int j=1;j<hundredSolution.size();j++){
            if (costBase<getCost(hundredSolution.get(j))){
                sortBySolution.add(0,hundredSolution.get(0));
                costBase=getCost(hundredSolution.get(0));
            }else{
                sortBySolution.add(0,hundredSolution.get(j));
                costBase=getCost(hundredSolution.get(j));
            }
        }

        List bestInitialSolution = new ArrayList();
        bestInitialSolution.addAll((Collection) sortBySolution.get(0));

      return bestInitialSolution;
    }


    public static void main(String[] args) throws FileNotFoundException {

        File file =new File("F:\\My paper\\RDL_STT\\data\\general_instances_triangle_txt\\10000\\instance_0\\instance_0-triangle.txt");
        Instance instance = Reader.readInstance(file);
        Construct construct= new Construct(instance);

        List list=new ArrayList();
        list=construct.getBestInitialSolution();

        System.out.println("the best initial solution is:"+list);
        System.out.println("the cost of the best initial solution is:"+construct.getCost(list));
    }
}
