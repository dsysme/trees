package com.dsysme.trees.avl;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Optional;

public class AvlTree<T extends Comparable> {

    private Node<T> root;

    public void addNode(Node<T> node) {
        if (!node.isLeaf() || !node.isRoot()) {
            throw new IllegalArgumentException("Can only add detached leaf");
        }

        InsertionAction insertionAction = getInsertionAction(root, node);
        BalanceOperation balanceOperation = getBalanceOperation(insertionAction);

        insertAndBalance(insertionAction, balanceOperation);
    }

    private void insertAndBalance(InsertionAction insertionAction, BalanceOperation balanceOperation) {
        throw new NotImplementedException();
    }


    private InsertionAction getInsertionAction(Node<T> entry, Node<T> node) {
        if (entry.getValue().compareTo(node) < 0) {
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

    public BalanceOperation getBalanceOperation(InsertionAction<T> insertionAction) {
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
     *    / \
     *   +   +
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

}