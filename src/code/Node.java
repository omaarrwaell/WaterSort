package code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    private String state;
    private Node parent;
    private String action;
    private int depth;
    private int cost;

    public Node(String state, Node parent, String action, int cost,int depth) {
        this.state = state;
        this.parent = parent;
        this.action = action;
        this.cost = cost;
        this.depth = depth;
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
    public int getDepth() {
    	return depth;
    }

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
    @Override
    public String toString() {
        return "Node{" +
                "state='" + state + '\'' +
                ", action='" + action + '\'' +
                ", cost=" + cost +
                ", depth=" + depth +
                '}';
    }
}
