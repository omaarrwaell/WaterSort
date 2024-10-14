package code;

import java.util.*;
import code.GenericSearch;
public class WaterSortSearch extends GenericSearch {

    public WaterSortSearch(String initialState) {
        super(initialState);
    }

    // Goal test: check if all bottles have a single color or are empty
    
    public static boolean isGoal(String state) {
        // Parse state and check if each bottle has only one color
        String[] bottles = state.split(";");
        boolean firstTwoAreNumbers = true;
        for (int i = 0; i < 2; i++) {
            try {
                Integer.parseInt(bottles[i]); // Try to parse as integer
            } catch (NumberFormatException e) {
                firstTwoAreNumbers = false; // If parsing fails, set to false
                break; // No need to check further
            }
        }

        // Remove the first two elements only if they are numbers
        if (firstTwoAreNumbers) {
            bottles = Arrays.copyOfRange(bottles, 2, bottles.length);
        }//fdf
        
        
        for (String bottle : bottles) {
        	  int nonEmptyLayersCount = 0;
        	  boolean isCompletelyEmpty = true;
            Set<Character> uniqueColors = new HashSet<>();
            for (char layer : bottle.toCharArray()) {
                if (layer != 'e' && layer != ',') {  // e means empty
                    uniqueColors.add(layer);
                    nonEmptyLayersCount++;
                    isCompletelyEmpty = false; 
                   // System.out.println(uniqueColors);
                }
            }
           
            if (uniqueColors.size() > 1) { // More than one color means unsolved bottle
                return false;
            }
            if (!isCompletelyEmpty && nonEmptyLayersCount < bottleCapacity) {
                return false; // If any bottle is not fully filled, return false
            }
        }
        return true; // All bottles have single color
    }

    // Expand method: generate all possible pour actions
   
    public static List<Node> expand(Node node) {
        List<Node> children = new ArrayList<>();
        String currentState = node.getState();
        
        // Parse the current state into bottle layers
        String[] bottles = currentState.split(";");
        boolean firstTwoAreNumbers = true;
        for (int i = 0; i < 2; i++) {
            try {
                Integer.parseInt(bottles[i]); // Try to parse as integer
            } catch (NumberFormatException e) {
                firstTwoAreNumbers = false; // If parsing fails, set to false
                break; // No need to check further
            }
        }

        // Remove the first two elements only if they are numbers
        if (firstTwoAreNumbers) {
            bottles = Arrays.copyOfRange(bottles, 2, bottles.length);
        }
        
        int numBottles = bottles.length;
        
        // Loop over every possible pair of bottles (i, j) where i pours into j
        for (int i = 0; i < numBottles; i++) {
            for (int j = 0; j < numBottles; j++) {
                try {
					if (i != j && canPour(bottles[i], bottles[j])) {
					    // Generate new state after pouring
					    Map<String, String> result = pour(bottles, i, j);
					    String newState = result.get("newState");
					    String action = "pour_" + i + "_" + j ;
					    Node child = new Node(newState, node, action, node.getCost() + Integer.parseInt(result.get("cost")),node.getDepth()+1);
					    children.add(child);
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
        return children;
    }

    // Check if we can pour from bottle i to bottle j
    private static boolean canPour(String bottleI, String bottleJ) {
        char topLayerI = getTopLayer(bottleI);
        char topLayerJ = getTopLayer(bottleJ);

        // Conditions: i is not empty, j has space, top layer matches or j is empty
        return topLayerI != 'e' && hasSpace(bottleJ) && (topLayerJ == 'e' || topLayerI == topLayerJ);
    }

    // Simulate the pouring from bottle i to j, return the new state
    private static Map<String, String> pour(String[] bottles, int i, int j) {
        String[] newBottles = Arrays.copyOf(bottles, bottles.length);
        char topLayerI = getTopLayer(newBottles[i]);
        
        int layersPoured = 0; // Track the number of layers poured
        int totalLayersPoured = 0; // Store total layers poured for the cost

        // Transfer as much as possible from i to j
        while (canPour(newBottles[i], newBottles[j])) {
            // Count how many layers can be poured in this action
            int availableLayersToPour = countPourableLayers(newBottles[i]);
            int emptySpacesInBottleJ = countEmptySpaces(newBottles[j]);
            
            // Calculate the actual number of layers to pour, which is the minimum between available layers and empty spaces
            layersPoured = Math.min(availableLayersToPour, emptySpacesInBottleJ);
            
            // Pour the layers and update the bottles
            for (int k = 0; k < layersPoured; k++) {
                newBottles[i] = removeTopLayer(newBottles[i]);
                newBottles[j] = addTopLayer(newBottles[j], topLayerI);
            }
            
            // Accumulate the layers poured in this action
            totalLayersPoured += layersPoured;
        }

        // Update the cost of the node based on the layers poured
        int newCost = totalLayersPoured;

        // Create a HashMap to store the new state and cost
        Map<String, String> result = new HashMap<>();
        result.put("newState", String.join(";", newBottles)); // Add new state to the HashMap
        result.put("cost", newCost+""); // Add the cost to the HashMap

        // Return the HashMap with both the new state and the cost
        return result;
    }

    // Count the number of pourable layers from a bottle (same topmost color)
    private static int countPourableLayers(String bottle) {
        char topLayer = getTopLayer(bottle);
        int count = 0;
        for (char layer : bottle.toCharArray()) {
            if (layer == topLayer) {
                count++;
            } else if (layer == 'e' || layer == ',') {
                continue;
            } else {
                break; // Stop counting when the topmost different color is found
            }
        }
        return count;
    }

    // Count how many empty spaces ('e') are in a bottle
    private static int countEmptySpaces(String bottle) {
        int count = 0;
        for (char layer : bottle.toCharArray()) {
            if (layer == 'e') {
                count++;
            }
        }
        return count;
    }

    // Helper methods: getTopLayer, hasSpace, removeTopLayer, addTopLayer
    private static char getTopLayer(String bottle) {
        for (char layer : bottle.toCharArray()) {
            if (layer != 'e' && layer !=',') {
                return layer; // Return first non-empty layer
            }
        }
        return 'e'; // If empty, return 'e'
    }

    private static boolean hasSpace(String bottle) {
        return bottle.indexOf('e') != -1; // Check if there's an empty space in the bottle
    }

    private static String removeTopLayer(String bottle) {
        // Split the bottle string by commas to separate the layers
        String[] layers = bottle.split(",");

        // Find the index of the first non-empty layer
        int topIndex = 0;
        while (topIndex < layers.length && layers[topIndex].equals("e")) {
            topIndex++; // Move right until we find a non-empty layer
        }

        // If all layers are empty, return the bottle as is
        if (topIndex >= layers.length) {
            return bottle;
        }

        // Set the top layer to empty (replace the first non-empty layer with 'e')
        layers[topIndex] = "e"; // Set the identified layer to empty

        // Join the layers back into a single string with commas
        return String.join(",", layers);
    }

    private static String addTopLayer(String bottle, char layer) {
        // Find the last empty space
        int lastEmpty = bottle.lastIndexOf('e'); // Get the last index of 'e'
        
        // If no empty space is found, return the bottle as is
        if (lastEmpty == -1) {
            return bottle; 
        }
        
        char[] layers = bottle.toCharArray();
        layers[lastEmpty] = layer; // Add the layer to the last empty space
        return new String(layers); // Return the new bottle configuration as a string
    }

    
    public static void main(String[] args) {
        // Example initial state: 5 bottles, 4 layers each, different colors
        String initialState = "5;4;b,y,r,b;b,y,r,r;y,r,b,y;e,e,e,e;e,e,e,e;";
       // String initialState = "5;4;e,e,e,e;r,r,r,r;e,e,b,y;e,b,b,b;e,y,y,y;";
       // String initialState ="3;4;r,y,r,y;y,r,y,r;e,e,e,e;";
        WaterSortSearch waterSortSearch = new WaterSortSearch(initialState);

        // Test BFS
        String solution = WaterSortSearch.solve(initialState, "AS2", true);
        System.out.println(solution);
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
}
