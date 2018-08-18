package com.dsysme.trees.avl;

import java.util.List;
import java.util.Optional;

public class Node<T extends Comparable> {

    private T value;
    private Optional<Node<T>> parent;
    private Optional<Node<T>> left;
    private Optional<Node<T>> right;

    public Node(T value) {
        this.value = value;
        this.left = Optional.empty();
        this.right = Optional.empty();
        this.parent = Optional.empty();
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Optional<Node<T>> getParent() {
        return parent;
    }

    public void setParent(Optional<Node<T>> parent) {
        this.parent = parent;
    }

    public Optional<Node<T>> getLeft() {
        return left;
    }

    public void setLeft(Optional<Node<T>> left) {
        this.left = left;
    }

    public Optional<Node<T>> getRight() {
        return right;
    }

    public void setRight(Optional<Node<T>> right) {
        this.right = right;
    }

    public boolean isRoot() {
        return !parent.isPresent();
    }

    public boolean isLeaf() {
        return !(left.isPresent() || right.isPresent());
    }

    public AvlBalanceDetails<T> getBalanceDetails() {
        int longestPathLeft =  getLeft().isPresent() ? getLeft().get().getLengthLongestPath() + 1: 0;
        int longestPathRight = getRight().isPresent() ? getRight().get().getLengthLongestPath() + 1: 0;

        return new AvlBalanceDetails<T>(this, longestPathLeft, longestPathRight);
    }

    public boolean isMyLeftChild(Node<T> node) {
        return (left.isPresent() && left.get() == node);
    }

    public boolean isMyRightChild(Node<T> node) {
        return (right.isPresent() && right.get() == node);
    }

    public int getLeftWeight() {
        if (!getLeft().isPresent()) {
            return 0;
        }
        return getLeft().get().getWeight() + 1;
    }

    public int getRightWeight() {
        if (!getRight().isPresent()) {
            return 0;
        }
        return getRight().get().getWeight() + 1;
    }

    public boolean isLeftUnbalanced() {
        return getLeftWeight() > getRightWeight();
    }

    public boolean isRightUnbalanced() {
        return getRightWeight() > getLeftWeight();
    }

    public int getWeight() {
        return Math.max(getLeftWeight(), getRightWeight());
    }

    public int getLengthLongestPath() {
        if (isLeaf()) {
            return 0;
        }
        if (left.isPresent()) {
            return right.isPresent()
                    ? Math.max(left.get().getLengthLongestPath() + 1, right.get().getLengthLongestPath() + 1) : left.get().getLengthLongestPath() + 1;
        }
        return right.get().getLengthLongestPath() + 1;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s"
                , getLeft().isPresent() ? "[" + getLeft().get() + "]" : ""
                , getValue(), getRight().isPresent() ? "[" + getRight().get() + "]": "");
    }
}
