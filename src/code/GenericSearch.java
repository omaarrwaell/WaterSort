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
        Deque<Node> frontier = new LinkedList<>(); // Deque supports both stack and queue operations
        Set<String> explored = new HashSet<>();
        frontier.add(new Node(initialState, null, null, 0, 0)); // Start with the initial state

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

            if (isGoal(node.getState())) {
                return node; // Goal reached
            }
            
            explored.add(node.getState());

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


    // Breadth-First Search (BFS) implementation
    public Node bfs() {
        return search("BFS");
    }

    // Depth-First Search (DFS) implementation
    public Node dfs() {
        return search("DFS");
    }
}
