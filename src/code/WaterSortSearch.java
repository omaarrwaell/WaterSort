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
        for (String bottle : bottles) {
            Set<Character> uniqueColors = new HashSet<>();
            for (char layer : bottle.toCharArray()) {
                if (layer != 'e') { // e means empty
                    uniqueColors.add(layer);
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
        int numBottles = bottles.length;
        
        // Loop over every possible pair of bottles (i, j) where i pours into j
        for (int i = 0; i < numBottles; i++) {
            for (int j = 0; j < numBottles; j++) {
                if (i != j && canPour(bottles[i], bottles[j])) {
                    // Generate new state after pouring
                    String newState = pour(bottles, i, j);
                    String action = "pour(" + i + "," + j + ")";
                    Node child = new Node(newState, node, action, node.getCost() + 1);
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
            if (layer != 'e') {
                return layer; // Return first non-empty layer
            }
        }
        return 'e'; // If empty, return 'e'
    }

    private boolean hasSpace(String bottle) {
        return bottle.indexOf('e') != -1; // Check if there's an empty space in the bottle
    }

    private String removeTopLayer(String bottle) {
        int lastIndex = bottle.lastIndexOf('e') - 1; // Find the topmost non-empty layer
        if (lastIndex < 0) {
            return bottle; // If all layers are empty, return as is
        }
        char[] layers = bottle.toCharArray();
        layers[lastIndex] = 'e'; // Set top layer to empty
        return new String(layers);
    }

    private String addTopLayer(String bottle, char layer) {
        int firstEmpty = bottle.indexOf('e'); // Find the first empty space
        if (firstEmpty == -1) {
            return bottle; // If no empty space, return as is
        }
        char[] layers = bottle.toCharArray();
        layers[firstEmpty] = layer; // Add the layer to the first empty space
        return new String(layers);
    }
    
    public static void main(String[] args) {
        // Example initial state: 5 bottles, 4 layers each, different colors
        String initialState = "5;4;b,y,r,b;b,y,r,r;y,r,b,y;e,e,e,e;e,e,e,e;";
        
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
