package com.dsysme.trees.avl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AvlTree<T extends Comparable> {

    private Node<T> root;

    public AvlTree(Node<T> root) {
        this.root = root;
    }

    public void addNode(Node<T> node) {
        if (!node.isLeaf() || !node.isRoot()) {
            throw new IllegalArgumentException("Can only add detached leaf");
        }

        InsertionAction insertionAction = getInsertionAction(root, node);
        BalanceOperation balanceOperation = getBalanceOperation(insertionAction);
        balancedInsert(node, insertionAction, balanceOperation);
    }

    @Override
    public String toString() {
        return root.toString();
    }

    /**
     * https://www.tutorialspoint.com/data_structures_algorithms/avl_tree_algorithm.htm
     * @param node
     * @param insertionAction
     * @param balanceOperation
     */
    private void balancedInsert(Node<T> node, InsertionAction<T> insertionAction, BalanceOperation balanceOperation) {

        if (balanceOperation == BalanceOperation.NONE) {
            Insert(node, insertionAction);
            return;
        }

        switch (balanceOperation) {
            case LEFT: {
                this.root = InsertWithLeftRotation(node, insertionAction);
                break;
            }
            case RIGHT: {
                this.root = InsertWithRightRotation(node, insertionAction);
                break;
            }
            case LEFT_RIGHT: {
                this.root = InsertWithLeftRightRotation(node, insertionAction);
                break;
            }
            case RIGHT_LEFT: {
                this.root = InsertWithRightLeftRotation(node, insertionAction);
                break;
            }
        }

    }

    /*
     * Rotate right and than left
     *     A            B
     *      \          /  \
     *      C     =>  A    C
     *     /
     *    B
     */

    private Node<T> InsertWithRightLeftRotation(Node<T> node, InsertionAction<T> insertionAction) {
        Node<T> C = insertionAction.getParentCandidate();
        Node<T> A = C.getParent().get();
        Node<T> B = node;
        Node<T> root = this.root;
        B.setParent(A.getParent());
        if (A.getParent().isPresent()) {
            if (A.getParent().get().isMyLeftChild(B)) {
                A.getParent().get().setLeft(Optional.of(B));
            } else {
                A.getParent().get().setRight(Optional.of(B));
            }
        } else {
            root = B;
        }
        B.setRight(Optional.of(C));
        B.setLeft(Optional.of(A));
        A.setParent(Optional.of(B));
        A.setRight(Optional.empty());
        C.setParent(Optional.of(B));
        C.setLeft(Optional.empty());
        return root;
    }

    /*
     *
     *  Rotate left and then right
     *     C              B
     *    /              / \
     *   A     =>       A   C
     *    \
     *    B
     *
     *    I don't actually insert than rotate just create the final state
     */
    private Node<T> InsertWithLeftRightRotation(Node<T> node, InsertionAction<T> insertionAction) {
        Node<T> A = insertionAction.getParentCandidate();
        Node<T> C = A.getParent().get();
        Node<T> B = node;
        Node<T> newRoot = root;
        B.setParent(C.getParent());
        if (C.getParent().isPresent()) {
            if (C.getParent().get().isMyLeftChild(C)) {
                C.getParent().get().setLeft(Optional.of(B));
            } else {
                C.getParent().get().setRight(Optional.of(B));
            }
        } else {
            newRoot = B;
        }
        B.setRight(Optional.of(C));
        B.setLeft(Optional.of(A));
        A.setParent(Optional.of(B));
        A.setRight(Optional.empty());
        C.setParent(Optional.of(B));
        C.setLeft(Optional.empty());
        return newRoot;
    }

    /*
     * rotate right
     *     C
     *    /
     *   B          =>     B
     *  /                 / \
     * A                 A   C
     *
     * I don't actually insert than rotate just create the final state
     */
    private Node<T> InsertWithRightRotation(Node<T> node, InsertionAction<T> insertionAction) {
        Node<T> B = insertionAction.getParentCandidate();
        Node<T> C = B.getParent().get();
        Node<T> A = node;
        Node<T> newRoot = root;
        B.setParent(C.getParent());
        if (C.getParent().isPresent()) {
            if (C.getParent().get().isMyLeftChild(C)) {
                C.getParent().get().setLeft(Optional.of(B));
            } else {
                C.getParent().get().setRight(Optional.of(B));
            }
        } else {
            newRoot = B;
        }
        C.setParent(Optional.of(B));
        C.setLeft(Optional.empty());
        B.setRight(Optional.of(C));
        B.setLeft(Optional.of(A));
        A.setParent(Optional.of(B));
        return newRoot;
    }

/*
 *  case 2: left rotation needed
 *
 *     A
 *      \
 *       B   =>       B
 *       \           /  \
 *        C         A    C
 *
 *  I don't actually insert than rotate just create the final state
 */
    private Node<T> InsertWithLeftRotation(Node<T> node, InsertionAction<T> insertionAction) {
        Node<T> B = insertionAction.getParentCandidate();
        Node<T> A = B.getParent().get();
        Node<T> C = node;
        Node<T> newRoot = this.root;
        B.setParent(A.getParent());
        if (A.getParent().isPresent()) {
            if (A.getParent().get().isMyLeftChild(A)) {
                A.getParent().get().setLeft(Optional.of(B));
            } else {
                A.getParent().get().setRight(Optional.of(B));
            }
        } else {
            newRoot = B;
        }
        A.setParent(Optional.of(B));
        A.setRight(Optional.empty());
        B.setLeft(Optional.of(A));
        B.setRight(Optional.of(C));
        C.setParent(Optional.of(B));
        return newRoot;
    }

    private void Insert(Node<T> node, InsertionAction insertionAction) {
        Node parent = insertionAction.getParentCandidate();

        if (insertionAction.getType() == InsertionType.INSERT_LEFT) {
            parent.setLeft(Optional.of(node));
            node.setParent(Optional.of(parent));
        }

        if (insertionAction.getType() == InsertionType.INSERT_RIGHT) {
            parent.setRight(Optional.of(node));
            node.setParent(Optional.of(parent));
        }
    }


    private InsertionAction getInsertionAction(Node<T> entry, Node<T> node) {
        if (node.getValue().compareTo(entry.getValue()) < 0) {
            // go left
            if (entry.getLeft().isPresent()) {
                return getInsertionAction(entry.getLeft().get(), node);
            } else {
                return new InsertionAction(entry, InsertionType.INSERT_LEFT);
            }
        } else {
            // go right
            if (entry.getRight().isPresent()) {
                return getInsertionAction(entry.getRight().get(), node);
            } else {
                return new InsertionAction(entry, InsertionType.INSERT_RIGHT);
            }
        }
    }

    private BalanceOperation getBalanceOperation(InsertionAction<T> insertionAction) {
        if (insertionAction.getParentCandidate().isRoot()) {
            return BalanceOperation.NONE;
        }

        if (insertionAction.getType() == InsertionType.INSERT_LEFT) {
            return getBalanceOperationForLeftInsertion(insertionAction);
        }

        //type == InsertionType.INSERT_RIGHT
        return getBalanceOperationForRightInsertion(insertionAction);

    }

    /**
     * +   marks existing node
     * (+) marks insertion candidate
     *  case 1: No need for rebalance
     *
     *     +
     *    / \
     *   +   +
     *       \
     *      (+)
     *
     *  case 2: left rotation needed
     *
     *     +
     *      \
     *       +
     *       \
     *      (+)
     *
     *  case 3: No need for rebalance
     *     +
     *    / \
     *   +  +
     *    \
     *    (+)
     *
     *  case 4: rebalance left and then right
     *     +
     *    /
     *   +
     *    \
     *    (+)
     */
    private BalanceOperation getBalanceOperationForRightInsertion(InsertionAction<T> ia) {
        if (ia.getParentCandidate().getParent().get().isMyRightChild(ia.getParentCandidate())) {
            if (ia.getParentCandidate().getParent().get().getLeft().isPresent()) {
                return BalanceOperation.NONE;
            } else {
                return BalanceOperation.LEFT;
            }
        }
        // parentCandidate is a left child
        if (ia.getParentCandidate().getParent().get().getRight().isPresent()) {
            return BalanceOperation.NONE;
        } else {
            return BalanceOperation.LEFT_RIGHT;
        }

    }

    /**
     * +   marks existing node
     * (+) marks insertion candidate
     *  case 1: No need for rebalance
     *     +
     *    / \
     *   +  +
     *  /
     * (+)
     *
     * case 2: rebalance right
     *     +
     *    /
     *   +
     *  /
     * (+)
     *
     * case 3:  No need for rebalance
     *    +
     *   / \
     *  +  +
     *     /
     *   (+)
     *
     * case 4: rebalance right and than left
     *     +
     *      \
     *      +
     *     /
     *   (+)
     */
    private BalanceOperation getBalanceOperationForLeftInsertion(InsertionAction<T> ia) {
        if (ia.getParentCandidate().getParent().get().isMyLeftChild(ia.getParentCandidate())) {
            if (ia.getParentCandidate().getParent().get().getRight().isPresent()) {
                return BalanceOperation.NONE; // see case 1
            } else {
                return BalanceOperation.RIGHT; // see case 2
            }
        }

        // parentCandidate is a right child
        if (ia.getParentCandidate().getParent().get().getLeft().isPresent()) {
            return BalanceOperation.NONE; // see case 3
        } else {
            return BalanceOperation.RIGHT_LEFT; // see case 4
        }
    }

    public static void main(String[] args) {
//        testInsertWithLeftRightRotation();
//        testInsertWithRightLeftRotation();
//        testInsertWithRightRotation();
//        testInsertWithLeftRotation();
//        test();
        wikipediaExample();

    }

    private static AvlTree<Integer> buildTree(List<Integer> elements) {
        Node<Integer> root = new Node<>(elements.get(0));
        AvlTree<Integer> tree = new AvlTree<>(root);
        elements.subList(1, elements.size()).stream().forEach(element -> tree.addNode(new Node<>(element)));
        return tree;
    }

    private static AvlTree<Character> buildCharacterTree(List<Character> elements) {
        Node<Character> root = new Node<>(elements.get(0));
        AvlTree<Character> tree = new AvlTree<>(root);
        elements.subList(1, elements.size()).stream().forEach(element -> tree.addNode(new Node<>(element)));
        return tree;
    }

    private static void testInsertWithLeftRightRotation() {
        Integer[] arr = {10,6,16,11,14};
        System.out.println("InsertWithLeftRightRotation: "+ Arrays.toString(arr));
        AvlTree<Integer> tree = buildTree(Arrays.asList(arr));
        System.out.println(tree);
    }

    private static void test() {
        Integer[] arr = {10,6,5,2,4};
        System.out.println("InsertWithRightLeftRotation: "+ Arrays.toString(arr));
        AvlTree<Integer> tree = buildTree(Arrays.asList(arr));
        System.out.println(tree);
    }

    private static void testInsertWithRightLeftRotation() {
        Integer[] arr = {10,12,5,2,4};
        System.out.println("InsertWithRightLeftRotation: "+ Arrays.toString(arr));
        AvlTree<Integer> tree = buildTree(Arrays.asList(arr));
        System.out.println(tree);
    }

    private static void testInsertWithLeftRotation() {
        Integer[] arr = {10,6,16,18,20};
        System.out.println("InsertWithLeftRotation: "+ Arrays.toString(arr));
        AvlTree<Integer> tree = buildTree(Arrays.asList(arr));
        System.out.println(tree);
    }

    private static void testInsertWithRightRotation() {
        Integer[] arr = {10,6,5,3,1};
        System.out.println("InsertWithRightRotation: "+ Arrays.toString(arr));
        AvlTree<Integer> tree = buildTree(Arrays.asList(arr));
        System.out.println(tree);
    }

    private static void wikipediaExample() {
        Character [] arr = {'M', 'N', 'O', 'L','K','Q','P','H','I','A'};
        System.out.println("wikipediaExample: "+ Arrays.toString(arr));
        AvlTree<Character> tree = buildCharacterTree(Arrays.asList(arr));
        System.out.println(tree);
    }
}