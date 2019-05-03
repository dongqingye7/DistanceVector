//Name: Dongqing Ye
//UTA ID: 1001403301
//Lab2-Distance Vector Routing
//reference: https://github.com/adamvh/VectorRouting

public class Router {
    private int routerID;
    private int[][] distanceVectorTable;
    public static final int MAX_NODES = 6;
        
    // router constructor
    public void router(int routerID, int[][] nodeInfo){
        int i, j, k, l;
        this.routerID = routerID;
        this.distanceVectorTable = new int[MAX_NODES][MAX_NODES];  
        //initiallize table
        for( i = 0; i < MAX_NODES; i++ ){
            for( j = 0; j < MAX_NODES; j++ ){
                // Assuming all routers have a cost of 0 to themselves
                if( i == j ){
                    this.distanceVectorTable[i][j] = 0;
                }
                else{
                    // 16 indicates an infinite time
                    this.distanceVectorTable[i][j] = 16;
                }
            }
        }
        
        // Fill in the data to the routing table from the input file
        // loop though the input file data
        for( i = 0; i < nodeInfo.length; i++ ){
            for( j = 0; j < nodeInfo[0].length-1; j++ ){
                if( routerID == nodeInfo[i][j]){
                    k = routerID - 1;
                    if( j == 0 ){
                        l = nodeInfo[i][1] - 1;
                        this.distanceVectorTable[k][l] = nodeInfo[i][2];
                        this.distanceVectorTable[l][k] = nodeInfo[i][2]; 
                    }
                    if( j == 1 ){
                        l = nodeInfo[i][0] - 1;
                        this.distanceVectorTable[k][l] = nodeInfo[i][2];
                        this.distanceVectorTable[l][k] = nodeInfo[i][2]; 
                    }
                }
            }
        }
    }
    
    public int getID(){
        return routerID;
    }
    public int[] getRouterRow(){
        return this.distanceVectorTable[this.routerID-1];

    }
    public void changeNodeInfo(int nei_id, int cost){
        this.distanceVectorTable[this.routerID-1][nei_id-1]=cost;
        this.distanceVectorTable[nei_id-1][this.routerID-1]=cost;
    }

    //Print distance vector table
    public void printDistanceVectorTable(){
        int i, j;
        System.out.println("----------------------------------");
        System.out.println("Distance Vector Table of Router"+this.routerID);
        System.out.println("----------------------------------");
        System.out.println("    1  2  3  4  5  6");
        System.out.println("  ---------------------");

        for( i = 0; i < this.distanceVectorTable.length; i++ ){
            System.out.print( i+1 + "| ");
            for( j = 0; j < this.distanceVectorTable[i].length; j++ ){
                if( this.distanceVectorTable[i][j] > 9 ){
                    System.out.print(this.distanceVectorTable[i][j] + " ");
                }
                else{
                    System.out.print(" " + this.distanceVectorTable[i][j] + " ");
                }
                
            }
            System.out.println(" |");
        }
        System.out.println("  --------------------");
    }
    
    // return distance vector table
    public int[][] getDistanceVectorTable(){
        return this.distanceVectorTable;
    }

    // update distance vector table
    public boolean updateDistanceVectorTable(int[] vector,int nei_id){
        int i=routerID-1;
        int j;
        int change = 0;
        this.distanceVectorTable[nei_id-1]=vector;
        //run algorithm to find shortest path
        for( j = 0; j < this.distanceVectorTable[i].length; j++ ){
              int s_to_d= this.distanceVectorTable[i][j];
              int s_to_nei=this.distanceVectorTable[i][nei_id-1];
              int nei_to_d=vector[j];
          if(s_to_d>s_to_nei+nei_to_d){
              this.distanceVectorTable[i][j] = s_to_nei+nei_to_d; 
              this.distanceVectorTable[j][i] = s_to_nei+nei_to_d;              
              change++;
          }
        }
        //check if there is any updates
        if( change > 0 ){
            return true;
        }
        else{
            return false;
        }
    }
    
    // This will tell the main program what nodes need to share their information with this router
    public int[] getNeighborTable(){
        int i;
        int[] tableNeeded;
        tableNeeded = new int[MAX_NODES];
        
        // Init the needed tables with zeros
        for( i = 0; i < tableNeeded.length; i++ ){
            tableNeeded[i] = 0;
        }
        
        // If a router is connected, then it will have a cost that is not 16 or 0
        // this will return a list of what routers we can get the tables from
        for( i = 0; i < this.distanceVectorTable[0].length; i++ ){
            if( this.distanceVectorTable[this.routerID-1][i] < 16 ){
                tableNeeded[i] = this.distanceVectorTable[this.routerID-1][i];
            }
        }
        return tableNeeded;
  }

}