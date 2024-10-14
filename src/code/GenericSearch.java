package code;

import java.util.*;

public abstract class GenericSearch {
    protected static String initialState ="";
    public static int  bottleCapacity ;
    public static  Set<String> explored = new HashSet<>();
    public static int count =0;
    public GenericSearch(String initialState) {
        this.initialState = initialState;
    }

    // Abstract method to define the goal test
   // public static boolean isGoal(String state) {
		// TODO Auto-generated method stub
		//return false;
	//}

    // Abstract method to generate possible successors from a given state
    //public static List<Node> expand(Node node) {
		// TODO Auto-generated method stub
	//	return null;
	//}

    // Generic search method
   /* public Node search(String strategy) {
        Deque<Node> frontier = new LinkedList<>(); // Deque supports both stack and queue operations
       
        frontier.add(new Node(initialState, null, null, 0, 0)); // Start with the initial state
        String[] split = initialState.split(";");
        bottleCapacity=Integer.parseInt(split[1]);
        while (!frontier.isEmpty()) {
            Node node;
            
            if (strategy.equals("BFS")) {
                // For BFS, treat the deque as a queue (FIFO): remove from the front
                node = frontier.pollFirst(); 
            } else if (strategy.equals("DFS")) {
                // For DFS, treat the deque as a stack (LIFO): remove from the back
                node = frontier.pollLast(); 
            } else {
                throw new IllegalArgumentException("Invalid strategy: " + strategy);
            }
            System.out.println(node.getState());
            if (isGoal(node.getState())) {
                return node; // Goal reached
            }
           
            explored.add(node.getState());
           // System.out.println(explored.size());
            for (Node child : expand(node)) {
                if (!explored.contains(child.getState())) {
                    if (strategy.equals("BFS")) {
                        // For BFS, add to the end (FIFO behavior)
                        frontier.addLast(child);
                    } else if (strategy.equals("DFS")) {
                        // For DFS, add to the front (LIFO behavior)
                        frontier.addLast(child);
                    }
                }
            }
        }
        return null; // No solution found
    }
*/
 // Helper method for depth-limited DFS
    private static Node depthLimitedSearch(Node node, int limit) {
        // Check if the current node is the goal
        if (WaterSortSearch.isGoal(node.getState())) {
            return node; // Goal reached
        }
         if(node.getAction()==null) {
        	 explored.add(node.getState());
         }
        // If depth limit is reached, return null
        if (node.getDepth() >= limit) {
            return null;
        }

        // Expand the current node
        for (Node child : WaterSortSearch.expand(node)) {
            if (!explored.contains(child.getState())) {
                explored.add(child.getState());  // Mark the child as explored
                Node result = depthLimitedSearch(child, limit);  // Recursive call with the same depth limit
                if (result != null) {
                    return result;  // Return the result if goal is found
                }
            }
        }
        return null; // Return null if no solution found at this depth
    }
    private static int heuristic1(String state) {
        String[] bottles = state.split(";");
        int mixedBottles = 0;
        
        for (int i = 0; i < bottles.length; i++) {  // Skip first two (bottle count, capacity)
            Set<Character> colors = new HashSet<>();
            for (char layer : bottles[i].toCharArray()) {
                if (layer != 'e' && layer != ',') {
                    colors.add(layer);
                }
            }
            if (colors.size() > 1) {
                mixedBottles++;  // This bottle has more than one color
            }
        }
        return mixedBottles;
    }
    private static int heuristic2(String state) {
        String[] bottles = state.split(";");
        int movesRequired = 0;

        for (int i = 2; i < bottles.length; i++) {  // Skip first two (bottle count, capacity)
            Set<Character> colors = new HashSet<>();
            for (char layer : bottles[i].toCharArray()) {
                if (layer != 'e' && layer != ',') {
                    colors.add(layer);
                }
            }
            // If more than one color, it will require moves to empty it
            if (colors.size() > 1) {
                movesRequired++;
            }
        }
        return movesRequired;
    }

    public static void displayMemoryUsage(String message) {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory(); // Total memory allocated to JVM
        long freeMemory = runtime.freeMemory();   // Free memory in JVM
        long usedMemory = totalMemory - freeMemory;  // Used memory
        
        System.out.println(message);
        System.out.println("Used memory: " + usedMemory / (1024 * 1024) + " MB");
        System.out.println("Free memory: " + freeMemory / (1024 * 1024) + " MB");
        System.out.println("Total memory: " + totalMemory / (1024 * 1024) + " MB");
        System.out.println("------------------------------------");
    }

    public static Node search(String initialState1,String strategy,Boolean visualise) {
    	 displayMemoryUsage("Before search starts:");
        Deque<Node>  dequeFrontier = null;  // Use for BFS and DFS
        PriorityQueue<Node> pqFrontier = null;  // Use for UCS
        initialState = initialState1;
        String[] split = initialState.split(";");
        bottleCapacity = Integer.parseInt(split[1]);
        // Initialize the frontier based on the search strategy
      
        if (strategy.equals("BF") || strategy.equals("DF")) {
            dequeFrontier = new LinkedList<>();
            dequeFrontier.add(new Node(initialState, null, null, 0, 0)); // Start with the initial state
        } else if (strategy.equals("UC")) {
            pqFrontier = new PriorityQueue<>(Comparator.comparingInt(Node::getCost));
            pqFrontier.add(new Node(initialState, null, null, 0, 0)); // Start with the initial state
        }   else if (strategy.equals("ID")) {
            // For IDS, we'll repeatedly run depth-limited search with increasing depth
            int depthLimit = 0;  // Start with depth 0
            while (true) {
                explored.clear();  // Clear explored set between depth limits
                Node result = depthLimitedSearch(new Node(initialState, null, null, 0, 0), depthLimit);
                if (result != null) {
                    return result;  // Return the solution if found
                }
                depthLimit++;  // Increase the depth limit for the next iteration
            }
        }else if (strategy.equals("GR1")) {
            // Greedy Search using the combined heuristic (H1 + H2)
        	 pqFrontier = new PriorityQueue<>((node1, node2) -> {
                 // Compare nodes based on Heuristic 1
                 return Integer.compare(heuristic1(node1.getState()), heuristic1(node2.getState()));
             });
            pqFrontier.add(new Node(initialState, null, null, 0, 0)); // Start with the initial state
        } else if (strategy.equals("GR2")) {
            // Greedy Search using the combined heuristic (H1 + H2)
        	 pqFrontier = new PriorityQueue<>((node1, node2) -> {
                 // Compare nodes based on Heuristic 1
                 return Integer.compare(heuristic2(node1.getState()), heuristic2(node2.getState()));
             });
            pqFrontier.add(new Node(initialState, null, null, 0, 0)); // Start with the initial state
        } 
        else {
            throw new IllegalArgumentException("Invalid strategy: " + strategy);
        }

        // Extract bottle capacity from the initial state
      

        // Main search loop
        while ((dequeFrontier != null && !dequeFrontier.isEmpty()) || (pqFrontier != null && !pqFrontier.isEmpty())) {
            Node node;

            // Select the node based on the strategy
            if (strategy.equals("BF")) {
                node = dequeFrontier.pollFirst(); // For BFS, remove from the front (FIFO)
            } else if (strategy.equals("DF")) {
                node = dequeFrontier.pollFirst(); // For DFS, remove from the back (LIFO)
            } else {
                node = pqFrontier.poll(); // For UCS, remove the lowest cost node
            }

            // Print the state for debugging purposes (optional)
            System.out.println(node.getState());
            displayMemoryUsage("During search (current node: " + node.getState() + "):");
            // Check if the goal state is reached
            if (WaterSortSearch.isGoal(node.getState())) {
            	initialState="";
            	bottleCapacity=0;
            	displayMemoryUsage("After goal is reached:");
            	count =0;
                return node; // Goal reached
            }
            List<Node> children = WaterSortSearch.expand(node);
            // Mark the state as explored
            explored.add(node.getState());
            if (strategy.equals("DF")) {
                // For DFS, add children to the front of the deque in reverse order
                for (int i = children.size() - 1; i >= 0; i--) {
                    Node child = children.get(i);
                    if (!explored.contains(child.getState())) {
                        dequeFrontier.addFirst(child);  // Add to the front for DFS (LIFO)
                    }
                }
            }
            // Expand the node and add its children to the frontier
            for (Node child : children) {
                if (!explored.contains(child.getState())) {
                    // Add the child to the frontier based on the strategy
                    if (strategy.equals("BF")) {
                        dequeFrontier.addLast(child);  // BFS (FIFO behavior)
                    } else  if(strategy.equals("UC")){
                        pqFrontier.add(child);  // UCS (Priority Queue behavior)
                    }else if (strategy.equals("GR1")) {
                        pqFrontier.add(child);  // Greedy Search (Priority Queue based on combined heuristic)
                    }else if (strategy.equals("GR2")) {
                        pqFrontier.add(child);  // Greedy Search (Priority Queue based on combined heuristic)
                    }
                }
                
            }
        }

        return null; // No solution found
    }
    
    public static String solve(String intialState,String Strategy,Boolean visualise) {
    	 Node solution=search(intialState,Strategy,visualise);
    	 if (solution != null) {
    	        String path = String.join(",", solution.getPath());  // Join the path actions with arrows
    	        int cost = solution.getCost();
    	        int exploredSize = explored.size();
    	        explored= new HashSet<>();

    	        // Return a single string with values separated by semicolons
    	        return path + "; " + cost + "; " + exploredSize;
    	    } else {
    	        // Return a message when no solution is found
    	        return "No solution found with " + ".";
    	    }
    }

}
