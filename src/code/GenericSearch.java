package code;

import java.util.*;

public abstract class GenericSearch {
    protected static String initialState ="";
    
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
    private static Node depthLimitedSearch(Node node, int limit,WaterSearchProblem problem) {
        // Check if the current node is the goal
        if (problem.isGoal(node.getState())) {
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
        for (Node child : problem.expand(node)) {
            if (!explored.contains(child.getState())) {
                explored.add(child.getState());  // Mark the child as explored
                Node result = depthLimitedSearch(child, limit,problem);  // Recursive call with the same depth limit
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

    public static Node search(WaterSearchProblem problem,String strategy) {
    	// displayMemoryUsage("Before search starts:");
        Deque<Node>  dequeFrontier = null;  // Use for BFS and DFS
        PriorityQueue<Node> pqFrontier = null;  // Use for UCS
        initialState = problem.getInitialState();
        String[] split = initialState.split(";");
        int bottleCapacity = problem.getBottleCapacity();
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
                Node result = depthLimitedSearch(new Node(initialState, null, null, 0, 0), depthLimit,problem);
                if (result != null) {
                    return result;  // Return the solution if found
                }
                depthLimit++;  // Increase the depth limit for the next iteration
            }
        }
        else if (strategy.startsWith("AS1")) { // For A* strategy
            pqFrontier = new PriorityQueue<>(Comparator.comparingInt(node -> node.getCost() + heuristic_AS1(node)));
            pqFrontier.add(new Node(initialState, null, null, 0, 0)); // Start with the initial state
        }
        else if (strategy.startsWith("AS2")) { // For A* strategy
            pqFrontier = new PriorityQueue<>(Comparator.comparingInt(node -> node.getCost() + heuristic_AS2(node)));
            pqFrontier.add(new Node(initialState, null, null, 0, 0)); // Start with the initial state
        }
        else if (strategy.equals("GR1")) {
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
           // displayMemoryUsage("During search (current node: " + node.getState() + "):");
            // Check if the goal state is reached
            if (problem.isGoal(node.getState())) {
            	initialState="";
            	bottleCapacity=0;
            	//displayMemoryUsage("After goal is reached:");
            	count =0;
            	System.out.println("Goalllllllllllllllllllll"+node);
                return node; // Goal reached
            }
            List<Node> children = problem.expand(node);
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
                    else if(strategy.equals("AS1")){
                        pqFrontier.add(child); 

                    }
                    else if(strategy.equals("AS2")){
                        pqFrontier.add(child); 

                    }
                }
                
            }
        }

        return null; // No solution found
    }

    public static int heuristic_AS2(Node node) {
        String state = node.getState();
        String[] bottles = state.split(";");
        int totalMisplacedLayers = 0;
    
        // Iterate over each bottle
        for (String bottle : bottles) {
            // Count how many layers are misplaced in this bottle
            char[] layers = bottle.toCharArray();
            char topColor = ' '; // This will hold the color of the top layer
            boolean foundTopColor = false;
    
            // Count layers until we find an empty layer or the bottle is full
            for (char c : layers) {
                if (c == 'e') {
                    // Once we hit an empty layer, stop counting
                    break;
                }
                if (!foundTopColor) {
                    // Set the top color for the first layer we encounter
                    topColor = c;
                    foundTopColor = true;
                } else {
                    // If the current layer is different from the top color, count it as misplaced
                    if (c != topColor) {
                        totalMisplacedLayers++;
                    }
                }
            }
        }
        return totalMisplacedLayers; // Return the total number of misplaced layers
    }
    

    public static int heuristic_AS1(Node node) {
        String state = node.getState();
        String[] bottles = state.split(";");
    
        int nonUniformBottles = 0;
        for (String bottle : bottles) {
            Set<Character> uniqueColors = new HashSet<>();
            for (char c : bottle.toCharArray()) {
                if (c != 'e') {  // Ignore empty layers
                    uniqueColors.add(c);
                }
            }
            // If the bottle contains more than one color, it is non-uniform
            if (uniqueColors.size() > 1) {
                nonUniformBottles++;
            }
        }
        return nonUniformBottles; // Heuristic value is the number of non-uniform bottles
    }
    

}
