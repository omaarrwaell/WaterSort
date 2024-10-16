package code;

import java.util.*;
import code.GenericSearch;

public class WaterSortSearch {

    public static String solve(String intialState,String Strategy,Boolean visualise) {
    	String[] initialSplit = intialState.split(";");
    	int bottleCapacity =Integer.parseInt(initialSplit[1]);
    	List<String> actions = new ArrayList<String>(); 
    	WaterSearchProblem problem = new WaterSearchProblem(intialState,actions ,bottleCapacity);
        Node solution= GenericSearch.search(problem,Strategy);
        if (solution != null) {
               String path = String.join(",", solution.getPath());  // Join the path actions with arrows
               int cost = solution.getCost();
               int exploredSize = GenericSearch.explored.size();
               GenericSearch.explored= new HashSet<>();
                 System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
               
               return path + "; " + cost + "; " + exploredSize;
           } else {
               // Return a message when no solution is found
               return "No solution found with " + ".";
           }
   }
    

    
    public static void main(String[] args) {
        // Example initial state: 5 bottles, 4 layers each, different colors
        String initialState = "5;4;b,y,r,b;b,y,r,r;y,r,b,y;e,e,e,e;e,e,e,e;";
       // String initialState = "5;4;e,e,e,e;r,r,r,r;e,e,b,y;e,b,b,b;e,y,y,y;";
       // String initialState ="3;4;r,y,r,y;y,r,y,r;e,e,e,e;";
      //  String initialState ="3;3;r,g,r;g,r,g;e,e,e";
      

        // Test BFS
        String solution = WaterSortSearch.solve(initialState, "DF", true);
System.out.println(solution);

        // Test BFS
        
       //Node solution = waterSortSearch.bfs();
       // Node solution = waterSortSearch.dfs();
    //    if (solution != null) {
      //      System.out.println("Solution found with BFS: " + solution.getPath());
      //      System.out.println("Cost: " + solution.getCost());
      //      System.out.println("Explored: "+ explored.size());
     //   } else {
     //       System.out.println("No solution found with BFS.");
   //     }

        // You can similarly test DFS, UCS, A*, etc.
    }
//         // You can similarly test DFS, UCS, A*, etc.
//     }

}
