package com.dsysme.trees.avl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TODO make sure you cannot create cycles
 * Thoughts:
 *  I think it is best to save balance and re calculate by adding one on insert path and rechecking balance.
 *  The insert should return the lowest unbalanced node in the path
 * https://www.tutorialspoint.com/data_structures_algorithms/avl_tree_algorithm.htm
 */
public class AvlTree<T extends Comparable> {

    private Node<T> root;

    public AvlTree(Node<T> root) {
        this.root = root;
    }

    public void insertNode(Node<T> node) {
        if (!node.isLeaf() || !node.isRoot() || node == root) {
            throw new IllegalArgumentException("Can only add detached leaf that is not this.root");
        }

        insertNode(root, node);
        restoreBalance();
    }

    private void restoreBalance() {
        Optional<AvlBalanceDetails<T>> unbalanced = findLowestLeftmostUnbalancedNode(root);
        if (!unbalanced.isPresent()) {
            return;
        }
        Node<T> unbalancedNode = unbalanced.get().getNode();
        RebalanceType rebalanceType = getRebalanceType(unbalanced.get().getNode());
        Node<T> newRoot = rotate(rebalanceType, unbalancedNode);
        if (unbalancedNode == root) {
            this.root = newRoot;
        }
    }

    private Node<T> rotate(RebalanceType rebalanceType, Node<T> unbalancedNode) {
        switch (rebalanceType) {
            case LEFT:
                return rotateLeft(unbalancedNode);
            case RIGHT:
                return rotateRight(unbalancedNode);
            case LEFT_RIGHT:
                return rotateLeftRight(unbalancedNode);
            case RIGHT_LEFT:
                return rotateRightLeft(unbalancedNode);
        }
        return this.root; // TODO we are not supposed to get here
    }
    /**
     * rotate right
     *     C
     *    /
     *   B          =>     B
     *  /                 / \
     * A                 A  C
     *
     */
    public Node<T> rotateRight(Node<T> nodeC) {

        Node<T> nodeB = nodeC.getLeft().get();
        if (nodeC.getParent().isPresent()) {
           Node<T> nodeCParent = nodeC.getParent().get();
           if (nodeCParent.isMyLeftChild(nodeC)) {
               nodeCParent.setLeft(Optional.of(nodeB));
           } else {
               nodeCParent.setRight(Optional.of(nodeB));
           }
        }
        nodeB.setParent(nodeC.getParent());
        nodeC.setParent(Optional.of(nodeB));
        nodeC.setLeft(nodeB.getRight());
        nodeB.setRight(Optional.of(nodeC));
        return nodeB;
    }

    /**
     *  case 2: left rotation needed
     *
     *     A
     *      \
     *       B   =>        B
     *        \           /  \
     *         C         A    C
     *
     */
    public Node<T> rotateLeft(Node<T> nodeA) {
        Node<T> nodeB = nodeA.getRight().get();
        if (nodeA.getParent().isPresent()) {
            Node<T> nodeAParent = nodeA.getParent().get();
            if (nodeAParent.isMyLeftChild(nodeA)) {
                nodeAParent.setLeft(Optional.of(nodeB));
            } else {
                nodeAParent.setRight(Optional.of(nodeB));
            }
        }
        nodeB.setParent(nodeA.getParent());
        nodeA.setParent(Optional.of(nodeB));
        nodeA.setRight(nodeB.getLeft());
        nodeB.setLeft(Optional.of(nodeA));
        return nodeB;
    }

    /*
     * Rotate right and than left
     *     A       A           B
     *      \       \         /  \
     *      C  =>    B   =>  A    C
     *     /          \
     *    B            C
     */
    public Node<T> rotateRightLeft(Node<T> nodeA) {
        Node<T> nodeC = nodeA.getRight().get();
        rotateRight(nodeC);
        rotateLeft(nodeA);
        return nodeA.getParent().get();
    }

    /*
     *
     *  Rotate left and then right
     *     C          C        B
     *    /          /        / \
     *   A     =>   B        A   C
     *    \        /
     *    B       A
     *
     */
    public Node<T> rotateLeftRight(Node<T> nodeC) { // TODO not a tree method
        Node<T> nodeA = nodeC.getLeft().get();
        rotateLeft(nodeA);
        rotateRight(nodeC);
        return nodeA.getParent().get();
    }


    private RebalanceType getRebalanceType(Node<T> unbalancedNode) {
        if (unbalancedNode.isLeftUnbalanced()) {
            if (unbalancedNode.getLeft().get().isLeftUnbalanced()) {
                return RebalanceType.RIGHT;
            }
            return RebalanceType.LEFT_RIGHT;
        }
        else if (unbalancedNode.getRight().get().isRightUnbalanced()) {
            return RebalanceType.LEFT;
        }
        return RebalanceType.RIGHT_LEFT;
    }


    public Node<T> getRoot() {
        return root;
    }

    public Optional<AvlBalanceDetails<T>> findLowestLeftmostUnbalancedNode(Node<T> entryNode) {
        Node<T> current = entryNode;
        while (current.getLeft().isPresent())
            current = current.getLeft().get();

        return findLowestLeftmostUnbalancedNodeForLeftBalancedNode(current, entryNode);
    }

    /**
     * node left child is assumed to be balanced
     * @param node
     * @return
     */
    private Optional<AvlBalanceDetails<T>> findLowestLeftmostUnbalancedNodeForLeftBalancedNode(Node<T> node, Node<T> entryPoint) {

        Node<T> current = node;

        // check if right child is balanced
        Optional<AvlBalanceDetails<T>> unbalanced;
        if (current.getRight().isPresent()) {
            unbalanced = findLowestLeftmostUnbalancedNode(current.getRight().get());
            if (unbalanced.isPresent()) {
                // current right side an unbalanced tree
                return unbalanced;
            }
        }

        // check if current node is balanced for a node with balanced children
        AvlBalanceDetails<T> balanceDetails = current.getBalanceDetails();
        if (balanceDetails.is_NOT_Balanced()) {
            // current is the lowest leftmost unbalanced node
            return Optional.of(balanceDetails);
        }

        // check if parent is balanced knowing current is balanced left child
        if (current.getParent().isPresent() && entryPoint != current) {
            return findLowestLeftmostUnbalancedNodeForLeftBalancedNode(current.getParent().get(), entryPoint);
        }

        return Optional.empty();

    }

    @Override
    public String toString() {
        return root.toString();
    }


    private void insertNode(Node<T> currentNode, Node<T> insertNode) {
        if (insertNode.getValue().compareTo(currentNode.getValue()) < 0) {
            // go left
            if (currentNode.getLeft().isPresent()) {
                insertNode(currentNode.getLeft().get(), insertNode);
            } else {
                currentNode.setLeft(Optional.of(insertNode));
                insertNode.setParent(Optional.of(currentNode));
                return;
            }
        } else {
            // go right
            if (currentNode.getRight().isPresent()) {
                insertNode(currentNode.getRight().get(), insertNode);
            } else {
                currentNode.setRight(Optional.of(insertNode));
                insertNode.setParent(Optional.of(currentNode));
                return;
            }
        }
    }

    private void getNodesAsList(List<Node<T>> accumulator, Node<T> entry) {

        accumulator.add(entry);

        if (entry.getRight().isPresent()) {
            getNodesAsList(accumulator, entry.getRight().get());
        }

        if (entry.getParent().isPresent() && entry.getParent().get().isMyLeftChild(entry)) {
            getNodesAsList(accumulator, entry.getParent().get());
        }

    }

    public List<Node<T>> getNodesAsList() {
        List<Node<T>> result = new ArrayList<>();
        Node<T> current = root;
        while (current.getLeft().isPresent()) {
            current = current.getLeft().get();
        }
        getNodesAsList(result, current);
        return result;
    }
}