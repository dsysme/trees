package com.dsysme.trees.avl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class AvlTreeTest {

    private static AvlTree<Character> buildCharacterTree(List<Character> elements) {
        Node<Character> root = new Node<>(elements.get(0));
        AvlTree<Character> tree = new AvlTree<>(root);
        elements.subList(1, elements.size()).stream().forEach(element -> tree.insertNode(new Node<>(element)));
        return tree;
    }

    @Test
    void simpleInsertLeftToRoot() {
        Node<Character> nodeM = new Node<>('M');
        AvlTree<Character> tree = new AvlTree<Character>(nodeM);
        Node<Character> nodeA = new Node<>('A');

        tree.insertNode(nodeA);
        Assertions.assertTrue(nodeM.getLeft().get() == nodeA);
        Assertions.assertTrue(nodeA.getParent().get() == nodeM);
        Assertions.assertFalse(nodeM.getParent().isPresent());
        Assertions.assertFalse(nodeM.getRight().isPresent());
        Assertions.assertFalse(nodeA.getRight().isPresent());
        Assertions.assertFalse(nodeA.getLeft().isPresent());
    }

    @Test
    void simpleInsertRightToRoot() {
        Node<Character> nodeM = new Node<>('M');
        AvlTree<Character> tree = new AvlTree<Character>(nodeM);
        Node<Character> nodeP = new Node<>('P');

        tree.insertNode(nodeP);
        Assertions.assertFalse(nodeM.getParent().isPresent());
        Assertions.assertFalse(nodeM.getLeft().isPresent());
        Assertions.assertTrue(nodeM.getRight().get() == nodeP);
        Assertions.assertFalse(nodeP.getLeft().isPresent());
        Assertions.assertFalse(nodeP.getRight().isPresent());
        Assertions.assertTrue(nodeP.getParent().get() == nodeM);

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
    @Test
    public void testLeftRightRotation() {

        Node<Character> nodeB = new Node<>('B');
        Node<Character> nodeA = new Node<>('A');
        Node<Character> nodeC = new Node<>('C');

        AvlTree<Character> tree = new AvlTree<>(nodeC);
        tree.insertNode(nodeA);
        tree.insertNode(nodeB);

        Assertions.assertTrue(tree.toString().equals("[ A ] B [ C ]"));
    }

    @Test
    void simpleInsertWithRotateLeft() {
        Node<Character> nodeP = new Node<>('P');
        AvlTree<Character> tree = new AvlTree<Character>(nodeP);
        Node<Character> nodeM = new Node<>('M');
        tree.insertNode(nodeM);

        Assertions.assertTrue(tree.getRoot().getValue().charValue() == 'P');
        Assertions.assertTrue(tree.getRoot().getLeft().get().getValue().charValue() == 'M');
        Assertions.assertTrue(tree.getRoot().getLeft().get().getParent().get().getValue().charValue() == 'P');
        Assertions.assertFalse(tree.getRoot().getParent().isPresent());
        Assertions.assertFalse(tree.getRoot().getRight().isPresent());
        Assertions.assertFalse(tree.getRoot().getLeft().get().getLeft().isPresent());
        Assertions.assertFalse(tree.getRoot().getLeft().get().getRight().isPresent());
    }

    @Test
    void insertNodeWithRotateLeftRootIsUnbalancedNode() {
        Character [] arr = {'M', 'N'};
        AvlTree<Character> tree = buildCharacterTree(Arrays.asList(arr));
        Assertions.assertTrue(tree.getRoot().getValue().charValue() == 'M');

        Node<Character> nodeP = new Node<>('P');
        tree.insertNode( nodeP);
        Node<Character> root = tree.getRoot();
        Assertions.assertTrue(root.getValue().charValue() == 'N');
        Assertions.assertTrue(root.getLeft().isPresent());
        Assertions.assertTrue(root.getRight().isPresent());
        Assertions.assertFalse(root.getParent().isPresent());

        // root's children parent is set
        Assertions.assertTrue(root.getLeft().get().getParent().isPresent());
        Assertions.assertTrue(root.getRight().get().getParent().isPresent());

        // left child has no children
        Assertions.assertFalse(root.getLeft().get().getLeft().isPresent());
        Assertions.assertFalse(root.getLeft().get().getRight().isPresent());

        // right child has no children
        Assertions.assertFalse(root.getRight().get().getLeft().isPresent());
        Assertions.assertFalse(root.getRight().get().getRight().isPresent());

        // the tree is sorted
        Assertions.assertTrue(root.getValue().compareTo(root.getRight().get().getValue()) < 0);
        Assertions.assertTrue(root.getValue().compareTo(root.getLeft().get().getValue()) > 0);

    }

    boolean allNodesAreBalanced(AvlTree tree) {
        List<Node> allNodes = tree.getNodesAsList();
        return allNodes.stream().allMatch(node -> node.getBalanceDetails().isBalanced());
    }

    @Test
    void insertNodeWithRotateRightUnbalancedNodeIsNotRoot() {
        Character [] arr = {'N', 'P', 'M', 'L',};
        AvlTree<Character> tree = buildCharacterTree(Arrays.asList(arr));
        Node<Character> root = tree.getRoot();
        Assertions.assertTrue(root.getValue().charValue() == 'N');
        Node<Character> nodeK = new Node<>('K');
        tree.insertNode(nodeK);
        Assertions.assertTrue(allNodesAreBalanced(tree));
        Assertions.assertTrue(root.getValue().charValue() == 'N');
        Assertions.assertTrue(root.getLeft().get().getValue().charValue() == 'L');
        Assertions.assertTrue(root.getLeft().get().getRight().get().getValue().charValue() == 'M');
        Assertions.assertTrue(root.getLeft().get().getRight().get().getParent().get().getValue().charValue() == 'L');
        Assertions.assertTrue(root.getLeft().get().getLeft().get().getValue().charValue() == 'K');
        Assertions.assertTrue(root.getLeft().get().getLeft().get().getParent().get().getValue().charValue() == 'L');
    }

    @Test
    void insertNodeRotateRightLeftWithChildren() {
        Character [] arr = {'M', 'N', 'O', 'L','K','Q','P','H','I','J'};
        AvlTree<Character> tree = buildCharacterTree(Arrays.asList(arr));
        Assertions.assertTrue(allNodesAreBalanced(tree));
        Assertions.assertTrue(tree.toString().equals("[[[ H ] I [ J ]] K [ L [ M ]]] N [[ O ] P [ Q ]]"));
    }

    @Test
    void insertNodeRotateRightWithChildren() {
        // This example is taken from https://en.wikipedia.org/wiki/AVL_tree#/media/File:AVL_Tree_Example.gif
        // it has left rotation, right rotation, right-left rotation, left-right rotation, right rotation with children
        Character [] arr = {'M', 'N', 'O', 'L','K','Q','P','H','I','A'};
        AvlTree<Character> tree = buildCharacterTree(Arrays.asList(arr));
        Assertions.assertTrue(allNodesAreBalanced(tree));
        Assertions.assertTrue(tree.toString().equals("[[[ A ] H ] I [[ K ] L [ M ]]] N [[ O ] P [ Q ]]"));
    }

    public AvlTree<Integer> buildIntegerTree(List<Integer> elements) {
        Node<Integer> root = new Node<>(elements.get(0));
        AvlTree<Integer> tree = new AvlTree<>(root);
        elements.subList(1, elements.size()).stream().forEach(element -> tree.insertNode(new Node<>(element)));
        return tree;
    }

    @Test
    void testInsertWithLeftRightRotation() {
        Integer[] arr = {10,6,16,11,14};
        AvlTree<Integer> tree = buildIntegerTree(Arrays.asList(arr));
        Assertions.assertTrue(allNodesAreBalanced(tree));
        Assertions.assertTrue(tree.toString().equals("[ 6 ] 10 [[ 11 ] 14 [ 16 ]]"));
    }

    @Test
    void testInsertWithRotateLeftRightUnbalanceNodeIsNOTRoot() {
        Integer[] arr = {10,6,5,2,4};
        AvlTree<Integer> tree = buildIntegerTree(Arrays.asList(arr));
        Assertions.assertTrue(allNodesAreBalanced(tree));
        Assertions.assertTrue(tree.toString().equals("[[ 2 ] 4 [ 5 ]] 6 [ 10 ]"));
    }

    @Test
    void testInsertWithLeftRotation() {
        Integer[] arr = {10,6,16,18,20};
        AvlTree<Integer> tree = buildIntegerTree(Arrays.asList(arr));
        Assertions.assertTrue(allNodesAreBalanced(tree));
        Assertions.assertTrue(tree.toString().equals("[ 6 ] 10 [[ 16 ] 18 [ 20 ]]"));
    }

    @Test
    void testInsertWithDoubleRightRotation() {
        Integer[] arr = {10,6,5,3,1};
        AvlTree<Integer> tree = buildIntegerTree(Arrays.asList(arr));
        Assertions.assertTrue(allNodesAreBalanced(tree));
        Assertions.assertTrue(tree.toString().equals("[[ 1 ] 3 [ 5 ]] 6 [ 10 ]"));
    }
}