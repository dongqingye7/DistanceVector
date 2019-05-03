//Name: Dongqing Ye
//UTA ID: 1001403301
//Lab2-Distance Vector Routing
//reference: https://github.com/adamvh/VectorRouting

import java.util.*;
import java.io.*;

class DistanceVector {
   public static final int MAX_NODES = 6;
   public static int[][]master_DV=new int[MAX_NODES][MAX_NODES];
   public static boolean singleStep;
   static Scanner scanner = new Scanner(System.in);
    /**
     * @param args the command line arguments
     * this will read in the input file
     */
    public static void main(String[] args) {
        int[][] nodeInfo = readInput(args[0]); // has the info for the links between the routers
        Router[] routers = new Router[MAX_NODES]; // set of routers
        boolean stable = false; // assume the system is not stable and needs to run
        int cycles = 0; // cycles until stable
        String choice;

        System.out.print("Would you like to chooes single step mode? (y/n): ");
        // Get what the user wants to do.
        choice = scanner.nextLine();

        if(choice.equalsIgnoreCase("y") || choice.equalsIgnoreCase("yes")){
            singleStep = true;//Enable single step
        }else{
            singleStep = false; //disable single step
        }
        
        // Create a list of routers with input node infomation
        for(int i = 0; i < routers.length; i++ ){
            // Call the router constructor
            routers[i] = new Router();
            routers[i].router(i+1, nodeInfo);    
            //master distance vector has all router information
            master_DV[i]=routers[i].getRouterRow(); 
            //print initial link state tables
            if(singleStep){
              System.out.println("Initial link state table for Router" + (i+1) );
              routers[i].printDistanceVectorTable();
            }
            
        }

        //when user choose single step mode
          if( singleStep ){
            while( !stable ){
                 System.out.print("Would you like to adjust the cost of any link in the network? (y/n): ");
                  // ask user if need to change the link cost
                  choice = scanner.nextLine();
                  if(choice.equalsIgnoreCase("y") || choice.equalsIgnoreCase("yes")){
                      //change the link cost 
                      changeCost(routers);
                  }
                System.out.println("Please press enter to continue.");
                choice = scanner.nextLine();
                //update each router tables information
                stable = shareTables(routers);
                cycles++;

            }
            //the system reach to stable state
            scanner.close();
            System.out.println("The system is now stable.");
            System.out.println("The nodes are not getting any new information");
            System.out.println("It took the system " + cycles + " cycles");
          }else{
            // the simulation runs without intervention
            long startTime = System.nanoTime();
            while( !stable ){
                //update each router tables information
                stable = shareTables(routers);
                cycles++;
            }
            System.out.println("The system is now stable.");
            System.out.println("The nodes are not getting any new information");
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            System.out.println("It took the system " + cycles + " cycles and " + (duration/1000000) + " milliseconds to reach stability");
            printDVTable();
        }
    }
    //allow user to change link cost information
    public static void changeCost(Router[] routers){ 
        String s;
        System.out.print("Enter the first node: ");
        s = scanner.nextLine();
        int node1 = Integer.parseInt(s); 
        System.out.print("Enter the second node: ");
        s = scanner.nextLine();
        int node2 = Integer.parseInt(s); 
        System.out.print("Enter the cost of this link: ");
        s = scanner.nextLine();
        int cost=Integer.parseInt(s); 
        routers[node1-1].changeNodeInfo(node2,cost);
        routers[node2-1].changeNodeInfo(node1,cost);
        master_DV[node1-1]=routers[node1-1].getRouterRow();
        master_DV[node2-1]=routers[node2-1].getRouterRow();
    }
    //exchange information between routers and update table information
    public static boolean shareTables(Router[] routers){
        int i, j;
        int[] list;    // contains the list of who needs what from who
        int[] vector;   // contains the information of the neighboring router
       
        boolean change=false;
        int detect=0;
        // This is looping through the routers
        for( i = 0; i < MAX_NODES; i++ ){
            list = routers[i].getNeighborTable();
            for( j = 0; j < list.length; j++ ){
                if( list[j] > 0 ){
                    //get neighbor node information from masterDV
                    vector = master_DV[j];
                    //base on the neighbor node information, update node information
                    change=routers[i].updateDistanceVectorTable(vector, j+1);
                    //check if has any changes 
                    if ( change ){
                        detect++;
                    }
                    if(singleStep){
                      System.out.println("Router " + (i+1) + " updated with router " + (j+1) );
                    }
                    
                }
            }
            if(singleStep){
              routers[i].printDistanceVectorTable(); 
            }
             
        }
          
        if( detect == 0 ){
            // system is stable
            return true;
        }
        else{
            // system is not stable
            return false;
        }
    }

    //read data from the input file return the node information
    public static int[][] readInput(String fileName){
        //
        int i = 0; 
        int j = 0;
        int[][] nodeInfo;           
        nodeInfo = new int[24][3]; 
        try {
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            // Read data from the file line by line and store it in the array
            while ((line = bufferedReader.readLine()) != null) {
                // we know how the file will be formatted
		            String[] lines = line.split(" ");
                for( j = 0; j < 3; j++ ){
                    // convert string to in
                    nodeInfo[i][j] = Integer.parseInt(lines[j]);
                }
                i++;
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return the created array to the main function
        return nodeInfo;
    }
    
    //print masterDV table which is final stable table
    public static void printDVTable(){
        int i, j;
        System.out.println("-----------------------");
        System.out.println("Distance Vector Table");
        System.out.println("-----------------------");
        System.out.println("    1  2  3  4  5  6");
        System.out.println("  ---------------------");

        for( i = 0; i < master_DV.length; i++ ){
            System.out.print( i+1 + "| ");
            for( j = 0; j < master_DV[i].length; j++ ){
                if( master_DV[i][j] > 9 ){
                    System.out.print(master_DV[i][j] + " ");
                }
                else{
                    System.out.print(" " + master_DV[i][j] + " ");
                }
                
            }
            System.out.println(" |");
        }
        System.out.println("  --------------------");
    }
}