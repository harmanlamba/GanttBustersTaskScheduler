package exception;

public class NodeNotExistException extends Exception{
    public NodeNotExistException() {
        super("Node(s) have not been instantiated before an edge to the node has been created \n");
    }
}
