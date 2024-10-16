package  code;
import java.util.*;
public abstract class SearchProblem {

    private String initialState;
    private List<String> actions;

    public SearchProblem(String initialState, List<String> actions) {
        this.initialState = initialState;
        this.actions=actions;
    }

    public String getInitialState() {
        return initialState;
    }

    public List<String> getActions() {
        return actions;
    }

    public abstract boolean isGoal(String state);

    
    public abstract List<Node> expand(Node node);

}
