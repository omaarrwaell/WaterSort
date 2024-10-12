package code;

import java.util.*;

public class WaterSortSearch extends GenericSearch {

    public WaterSortSearch(String initialState) {
        super(initialState);
    }

    // Goal test: check if all bottles have a single color or are empty
    @Override
    protected boolean isGoal(String state) {
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
            Set<String> uniqueColors = new HashSet<>();
            String[] layers = bottle.split(",");
            for (String layer : layers) {
                if (!layer.equals("e") && !layer.equals(",")) { // e means empty
                    uniqueColors.add(layer);
                   // System.out.println(uniqueColors);
                }
            }
            if (uniqueColors.size() > 1) { // More than one color means unsolved bottle
                return false;
            }
        }
        return true; // All bottles have single color
    }

    // Expand method: generate all possible pour actions
    @Override
    protected List<Node> expand(Node node) {
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
                if (i != j && canPour(bottles[i], bottles[j])) {
                    // Generate new state after pouring
                    String newState = pour(bottles, i, j);
                    String action = "pour(" + i + "," + j + ")";
                    Node child = new Node(newState, node, action, node.getCost() + 1,node.getDepth()+1);
                    children.add(child);
                }
            }
        }
        return children;
    }

    // Check if we can pour from bottle i to bottle j
    private boolean canPour(String bottleI, String bottleJ) {
        char topLayerI = getTopLayer(bottleI);
        char topLayerJ = getTopLayer(bottleJ);

        // Conditions: i is not empty, j has space, top layer matches or j is empty
        return topLayerI != 'e' && hasSpace(bottleJ) && (topLayerJ == 'e' || topLayerI == topLayerJ);
    }

    // Simulate the pouring from bottle i to j, return the new state
    private String pour(String[] bottles, int i, int j) {
        String[] newBottles = Arrays.copyOf(bottles, bottles.length);
        char topLayerI = getTopLayer(newBottles[i]);
        
        // Transfer as much as possible from i to j
        while (canPour(newBottles[i], newBottles[j])) {
            newBottles[i] = removeTopLayer(newBottles[i]);
            newBottles[j] = addTopLayer(newBottles[j], topLayerI);
        }

        return String.join(";", newBottles);
    }

    // Helper methods: getTopLayer, hasSpace, removeTopLayer, addTopLayer
    private char getTopLayer(String bottle) {
        for (char layer : bottle.toCharArray()) {
            if (layer != 'e' && layer !=',') {
                return layer; // Return first non-empty layer
            }
        }
        return 'e'; // If empty, return 'e'
    }

    private boolean hasSpace(String bottle) {
        return bottle.indexOf('e') != -1; // Check if there's an empty space in the bottle
    }

    private String removeTopLayer(String bottle) {
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

    private String addTopLayer(String bottle, char layer) {
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
       // String initialState = "5;4;y,y,y,y;b,b,b,b;r,r,r,r;e,e,e,e;e,e,e,e;";
        
        WaterSortSearch waterSortSearch = new WaterSortSearch(initialState);

        // Test BFS
        Node solution = waterSortSearch.bfs();
        if (solution != null) {
            System.out.println("Solution found with BFS: " + solution.getPath());
            System.out.println("Cost: " + solution.getCost());
        } else {
            System.out.println("No solution found with BFS.");
        }

        // You can similarly test DFS, UCS, A*, etc.
    }
}
