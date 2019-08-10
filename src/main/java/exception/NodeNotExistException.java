package exception;

/**
 * NodeNotExistException - is thrown when a GraphEdge is being created, but the node (either A or B) does not currently
 * exist or has not been found in the node map list (dot input file likely has not instantiated node beforehand).
 */
public class NodeNotExistException extends Exception{
    public NodeNotExistException() {
        super("Node(s) have not been instantiated before an edge to the node has been created \n");
    }
}
