package code;

import java.util.*;

public abstract class GenericSearch {
    protected String initialState;

    public GenericSearch(String initialState) {
        this.initialState = initialState;
    }

    // Abstract method to define the goal test
    protected abstract boolean isGoal(String state);

    // Abstract method to generate possible successors from a given state
    protected abstract List<Node> expand(Node node);

    // Generic search method
    public Node search(String strategy) {
        Queue<Node> frontier = new LinkedList<>();
        Set<String> explored = new HashSet<>();
        frontier.add(new Node(initialState, null, null, 0));

        while (!frontier.isEmpty()) {
            Node node = frontier.poll();
            if (isGoal(node.getState())) {
                return node; // Goal reached
            }
            explored.add(node.getState());

            for (Node child : expand(node)) {
                if (!explored.contains(child.getState())) {
                    frontier.add(child); // Add child nodes to frontier
                }
            }
        }
        return null; // No solution found
    }

    // Breadth-First Search (BFS) implementation
    public Node bfs() {
        return search("BFS");
    }

    // Depth-First Search (DFS) implementation
    public Node dfs() {
        return search("DFS");
    }
}
