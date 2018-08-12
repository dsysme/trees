package com.dsysme.trees.avl;

public class InsertionAction<T extends Comparable> {

    private Node<T> parentCandidate;
    private InsertionType type;

    public InsertionAction(Node<T> parentCandidate, InsertionType type) {
        this.parentCandidate = parentCandidate;
        this.type = type;
    }

    public Node<T> getParentCandidate() {
        return parentCandidate;
    }

    public InsertionType getType() {
        return type;
    }

}
