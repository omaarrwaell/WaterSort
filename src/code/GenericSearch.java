package code;
import com.sun.management.OperatingSystemMXBean;
import java.util.*;
import java.lang.management.ManagementFactory;

public abstract class GenericSearch {
    static OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
        OperatingSystemMXBean.class);
    protected static String initialState ="";
    
     public static  HashSet<String> explored = new HashSet<>();
    // public static ArrayList<String> explored=new ArrayList<>();
    public static int count =0;
    public GenericSearch(String initialState) {
        this.initialState = initialState;
    }

 // Helper method for depth-limited DFS
    private static Node depthLimitedSearch(Node node, int limit,WaterSearchProblem problem) {
        if (problem.isGoal(node.getState())) {
            displayMemoryUsage("After goal is reached:");

            return node; // Wesel el goal
        }
         if(node.getAction()==null) {
        	 explored.add(node.getState());
         }
        // Deptj limit
        if (node.getDepth() >= limit) {
            return null;
        }

        // Expand node
        for (Node child : problem.expand(node)) {
            if (!explored.contains(child.getState())) {
                explored.add(child.getState());
                Node result = depthLimitedSearch(child, limit,problem); //Recurssion
                if (result != null) {
                    return result; 
                }
            }
        }
        return null; // No solution at this depth
    }

    //Mixed bottles
    private static int heuristic1(String state) {
        String[] bottles = state.split(";");
        int mixedBottles = 0;
        
        for (int i = 0; i < bottles.length; i++) {  // Skip (bottle count, capacity)
            Set<Character> colors = new HashSet<>();
            for (char layer : bottles[i].toCharArray()) {
                if (layer != 'e' && layer != ',') {
                    colors.add(layer);
                }
            }
            if (colors.size() > 1) {
                mixedBottles++;  // The bottle is mixed
            }
        }
        return mixedBottles;
    }

    //Emptying
    private static int heuristic2(String state) {
        String[] bottles = state.split(";");
        int movesRequired = 0;

        for (int i = 2; i < bottles.length; i++) { 
            Set<Character> colors = new HashSet<>();
            for (char layer : bottles[i].toCharArray()) {
                if (layer != 'e' && layer != ',') {
                    colors.add(layer);
                }
            }
            // more than one colour
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
        System.out.print("CPU Usage:" + osBean.getProcessCpuLoad());
        System.out.println("------------------------------------");
    }

    public static Node search(WaterSearchProblem problem,String strategy) {
        displayMemoryUsage("Before search starts:");
        Deque<Node>  deque = null;  // Use for BFS and DFS
        PriorityQueue<Node> PQ = null;  // Use for UCS
        initialState = problem.getInitialState();
        String[] split = initialState.split(";");
        int bottleCapacity = problem.getBottleCapacity();
        // Initialize the frontier based on the search strategy
      
        if (strategy.equals("BF") || strategy.equals("DF")) {
            deque = new LinkedList<>();
            deque.add(new Node(initialState, null, null, 0, 0));
        } else if (strategy.equals("UC")) {
            PQ = new PriorityQueue<>(Comparator.comparingInt(Node::getCost));
            PQ.add(new Node(initialState, null, null, 0, 0));
        }   else if (strategy.equals("ID")) {
            //IDS
            int depthLimit = 0;  //depth 0
            while (true) {
                explored.clear(); //Heree
                Node result = depthLimitedSearch(new Node(initialState, null, null, 0, 0), depthLimit,problem);
                if (result != null) {
                    return result; 
                }
                depthLimit++;  // Increase depth
            }
        }
        else if (strategy.startsWith("AS1")) { // A* 1
            PQ = new PriorityQueue<>(Comparator.comparingInt(node -> node.getCost() + heuristic_AS1(node)));
            PQ.add(new Node(initialState, null, null, 0, 0)); 
        }
        else if (strategy.startsWith("AS2")) { //A* 2
            PQ = new PriorityQueue<>(Comparator.comparingInt(node -> node.getCost() + heuristic_AS2(node)));
            PQ.add(new Node(initialState, null, null, 0, 0)); 
        }
        else if (strategy.equals("GR1")) {
        	 PQ = new PriorityQueue<>((node1, node2) -> {
                //editedd
                 return Integer.compare(heuristic1(node1.getState()), heuristic1(node2.getState()));
             });
            PQ.add(new Node(initialState, null, null, 0, 0)); 
        } else if (strategy.equals("GR2")) {
        	 PQ = new PriorityQueue<>((node1, node2) -> {
                //hr2
                 return Integer.compare(heuristic2(node1.getState()), heuristic2(node2.getState()));
             });
            PQ.add(new Node(initialState, null, null, 0, 0));
        } 
        else {
            throw new IllegalArgumentException("Invalid strategy: " + strategy);
        }
      

        // Main search loop
        while ((deque != null && !deque.isEmpty()) || (PQ != null && !PQ.isEmpty())) {
            Node node;

            //Select node

            if (strategy.equals("BF")) {
                node = deque.pollFirst(); // BFS = Fifo
            } else if (strategy.equals("DF")) {
                node = deque.pollFirst(); // DFS = LIFO
            } else {
                node = PQ.poll();
            }

           // System.out.println(node.getState());
           //displayMemoryUsage("During search (current node: " + node.getState() + "):");
            
            explored.add(node.getState());

            if (problem.isGoal(node.getState())) {
            	initialState="";
            	bottleCapacity=0;
            	displayMemoryUsage("After goal is reached:");
            	count =0;
            	System.out.println("Goalllllllllllllllllllll"+node);
                return node;
            }
            List<Node> children = problem.expand(node);
            if (strategy.equals("DF")) {
                //reverse
                for (int i = children.size() - 1; i >= 0; i--) {
                    Node child = children.get(i);
                    if (!explored.contains(child.getState())) {
                        deque.addFirst(child); 

                    }
                }
            }
            for (Node child : children) {
                if (!explored.contains(child.getState())) {

                    if (strategy.equals("BF")) {
                        deque.addLast(child);
                    } else  if(strategy.equals("UC")){
                        PQ.add(child);
                    }else if (strategy.equals("GR1")) {
                        PQ.add(child); 
                    }else if (strategy.equals("GR2")) {
                        PQ.add(child);
                    }
                    else if(strategy.equals("AS1")){
                        PQ.add(child); 


                    }
                    else if(strategy.equals("AS2")){
                        PQ.add(child); 

                    }
               }
                
            }
        }

        return null; // No solution
    }

    public static int heuristic_AS2(Node node) {
        String state = node.getState();
        String[] bottles = state.split(";");
        int totalMisplacedLayers = 0;
    
        for (String bottle : bottles) {
            char[] layers = bottle.toCharArray();
            char topColor = ' ';
            boolean foundTopColor = false;
    
            for (char c : layers) {
                if (c == 'e') {
                    break;
                }
                if (!foundTopColor) {
                    topColor = c;
                    foundTopColor = true;
                } else {
                    if (c != topColor) {
                        totalMisplacedLayers++;
                    }
                }
            }
        }
        return totalMisplacedLayers;
    }
    
    //non uniform
    public static int heuristic_AS1(Node node) {
        String state = node.getState();
        String[] bottles = state.split(";");
    
        int nonUniformBottles = 0;
        for (String bottle : bottles) {
            Set<Character> uniqueColors = new HashSet<>();
            for (char c : bottle.toCharArray()) {
                if (c != 'e') {
                    uniqueColors.add(c);
                }
            }
            if (uniqueColors.size() > 1) {
                nonUniformBottles++;
            }
        }
        return nonUniformBottles;
    }
    

}
