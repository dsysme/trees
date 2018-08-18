package com.dsysme.trees.avl;

public class AvlBalanceDetails<T extends Comparable> {
    private Node<T> node;
    private int leftWeight;
    private int rightWeight;

    public AvlBalanceDetails(Node<T> node, int leftWeight, int rightWeight) {
        this.node = node;
        this.leftWeight = leftWeight;
        this.rightWeight = rightWeight;
    }

    public Node<T> getNode() {
        return node;
    }

    public int getLeftWeight() {
        return leftWeight;
    }

    public int getRightWeight() {
        return rightWeight;
    }

    public boolean isLeftUnbalanced() {
        return leftWeight > rightWeight;
    }

    public boolean is_NOT_Balanced() {
        return Math.abs(leftWeight - rightWeight) > 1;
    }

    public boolean isBalanced() {
        return Math.abs(leftWeight - rightWeight) < 2;
    }

    public boolean isRightUnbalanced() {
        return rightWeight > leftWeight;
    }
}
