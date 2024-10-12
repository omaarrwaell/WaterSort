package code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    private String state;
    private Node parent;
    private String action;
    private int cost;

    public Node(String state, Node parent, String action, int cost) {
        this.state = state;
        this.parent = parent;
        this.action = action;
        this.cost = cost;
    }

    public String getState() {
        return state;
    }

    public Node getParent() {
        return parent;
    }

    public String getAction() {
        return action;
    }

    public int getCost() {
        return cost;
    }

    // Trace the path back from the current node to the root (initial state)
    public List<String> getPath() {
        List<String> path = new ArrayList<>();
        Node currentNode = this;
        while (currentNode.getParent() != null) {
            path.add(currentNode.getAction());
            currentNode = currentNode.getParent();
        }
        Collections.reverse(path);
        return path;
    }
}
