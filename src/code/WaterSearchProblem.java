package code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WaterSearchProblem extends SearchProblem {
	   public  int  bottleCapacity ;
    public WaterSearchProblem(String initialState, List<String> actions, int bottleCapacity) {
        super(initialState, actions);
       this.bottleCapacity = bottleCapacity;
    }
    public  boolean isGoal(String state) {
        String[] bottles = state.split(";");
        boolean firstTwoAreNumbers = true;
        for (int i = 0; i < 2; i++) {
            try {
                Integer.parseInt(bottles[i]); 
            } catch (NumberFormatException e) {
                firstTwoAreNumbers = false;
                break;
            }
        }

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
           
            if (uniqueColors.size() > 1) { //no goal
                return false;
            }
            if (!isCompletelyEmpty && nonEmptyLayersCount < bottleCapacity) {
                return false; // nosaha
            }
        }
        return true; // goal
    }

    public  int getBottleCapacity() {
		return bottleCapacity;
	}
	public  void setBottleCapacity(int bottleCapacity) {
		this.bottleCapacity = bottleCapacity;
	}
	public List<Node> expand(Node node) {
        List<Node> children = new ArrayList<>();
        String currentState = node.getState();
        
        String[] bottles = currentState.split(";");
        boolean firstTwoAreNumbers = true;
        for (int i = 0; i < 2; i++) {
            try {
                Integer.parseInt(bottles[i]); 
            } catch (NumberFormatException e) {
                firstTwoAreNumbers = false; 
                break;
            }
        }

        if (firstTwoAreNumbers) {
            bottles = Arrays.copyOfRange(bottles, 2, bottles.length);
        }
        
        int numBottles = bottles.length;
        
        for (int i = 0; i < numBottles; i++) {
            for (int j = 0; j < numBottles; j++) {
                try {
					if (i != j && canPour(bottles[i], bottles[j])) {
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

    //Helper 1
    private static boolean canPour(String bottleI, String bottleJ) {
        char topLayerI = getTopLayer(bottleI);
        char topLayerJ = getTopLayer(bottleJ);
        return topLayerI != 'e' && hasSpace(bottleJ) && (topLayerJ == 'e' || topLayerI == topLayerJ);
    }

    //Helper 2

    private static char getTopLayer(String bottle) {
        for (char layer : bottle.toCharArray()) {
            if (layer != 'e' && layer !=',') {
                return layer; // not empty
            }
        }
        return 'e'; // empty
    }

    // Space check
    private static boolean hasSpace(String bottle) {
        return bottle.indexOf('e') != -1;
    }

    //Remover

    private static String removeTopLayer(String bottle) {
        String[] layers = bottle.split(",");

        int topIndex = 0;
        while (topIndex < layers.length && layers[topIndex].equals("e")) {
            topIndex++; 
        }

        if (topIndex >= layers.length) {
            return bottle;
        }


        layers[topIndex] = "e"; 

        return String.join(",", layers);
    }

    //ADder
    private static String addTopLayer(String bottle, char layer) {

        int lastEmpty = bottle.lastIndexOf('e');
        
        if (lastEmpty == -1) {
            return bottle; 
        }
        
        char[] layers = bottle.toCharArray();
        layers[lastEmpty] = layer; 
        return new String(layers);
    }


    private static Map<String, String> pour(String[] bottles, int i, int j) {
        String[] newBottles = Arrays.copyOf(bottles, bottles.length);
        char topLayerI = getTopLayer(newBottles[i]);
        
        int layersPoured = 0;
        int totalLayersPoured = 0;

        while (canPour(newBottles[i], newBottles[j])) {
            int availableLayersToPour = countPourableLayers(newBottles[i]);
            int emptySpacesInBottleJ = countEmptySpaces(newBottles[j]);
            
            layersPoured = Math.min(availableLayersToPour, emptySpacesInBottleJ);

            for (int k = 0; k < layersPoured; k++) {
                newBottles[i] = removeTopLayer(newBottles[i]);
                newBottles[j] = addTopLayer(newBottles[j], topLayerI);
            }
            
            totalLayersPoured += layersPoured;
        }

        //Henaa

        int newCost = totalLayersPoured;

        Map<String, String> result = new HashMap<>();
        result.put("newState", String.join(";", newBottles));
        result.put("cost", newCost+"");

        return result;
    }

    private static int countPourableLayers(String bottle) {
        char topLayer = getTopLayer(bottle);
        int count = 0;
        for (char layer : bottle.toCharArray()) {
            if (layer == topLayer) {
                count++;
            } else if (layer == 'e' || layer == ',') {
                continue;
            } else {
                break; 
            }
        }
        return count;
    }
    private static int countEmptySpaces(String bottle) {
        int count = 0;
        for (char layer : bottle.toCharArray()) {
            if (layer == 'e') {
                count++;
            }
        }
        return count;
    }
}
