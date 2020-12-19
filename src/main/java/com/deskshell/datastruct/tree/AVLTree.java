package com.deskshell.datastruct.tree;

/**
 * avl tree is a balanced binary search tree.
 *
 * for every node, the difference between the heights of
 * the left and right sub tree is always not more than one.
 *
 * this class implements the insertion and deletion functionality
 * of the avl tree. with regard to the other functionalities,
 * they are the same as those in the binary search tree.
 *
 * @author Leon Xie
 **/
public class AVLTree<E extends Comparable<E>> {
    private static final int ALLOWED_IMBALANCE = 1;

    private int size;
    private AVLTreeNode<E> root;

    /**
     * return the size of the tree
     **/
    public int size() {
        return size;
    }

    /**
     * return the root node of the tree
     **/
    public AVLTreeNode<E> getRoot() {
        return root;
    }

    /**
     * insert the element into the tree
     **/
    public void insert(E elem) {
        AVLTreeNode<E> newNode = new AVLTreeNode<>(elem);

        root = insert(root, newNode);
    }

    /**
     * insert the node into the tree
     **/
    private AVLTreeNode<E>
    insert(AVLTreeNode<E> root, AVLTreeNode<E> newNode) {
        //the new node will be used as the root for the new tree
        if ( root == null ) {
            //the leaf node has height 0 and doesn't need to be balanced
            size++;
            return newNode;
        }

        //determine the insertion position of the new node
        //newNode > root, newNode will be inserted into the right tree
        //newNode < root, newNode will be inserted into the right tree
        //newNode = root, newNode will be inserted into existing node
        int compareResult = newNode.elem.compareTo(root.elem);

        if ( compareResult > 0 ) {
            root.right = insert(root.right, newNode);
        } else if ( compareResult < 0 ) {
            root.left = insert(root.left, newNode);
        } else {
            //do not allow duplicated node
            //no balance is required for this case
            return root;
        }

        //balance the tree so that the difference between the heights
        //of the left and right tree of every node is less than a value
        return balance(root);
    }

    /**
     * remove the first occurrence of the element
     **/
    public void remove(E elem) {
        root = remove(root, elem);
    }

    /**
     * remove the first occurrence of the element
     **/
    private AVLTreeNode<E> remove(AVLTreeNode<E> root, E elem) {
        //there is no node containing the element
        if ( root == null ) {
            return null;
        }

        //determine where to delete the node containing the element
        int compareResult = elem.compareTo(root.elem);

        //if elem < root.elem, the node will be in the left tree
        //if elem > root.elem, the node will be in the right tree
        //if elem = root.elem, the node is the root node
        if ( compareResult > 0 ) {
            root.right = remove(root.right, elem);
        } else if ( compareResult < 0 ) {
            root.left = remove(root.left, elem);
        } else if ( root.left != null && root.right != null ) {
            //replace the root node with the minimum node of the right tree
            root.elem = findMin(root.right).elem;
            root.right = remove(root.right, root.elem);
        } else {
            //replace the root node with its left or right tree
            root = ( root.left != null ) ? root.left : root.right;

            size--;
        }

        return balance(root);
    }

    /**
     * check whether the tree contains an element
     **/
    public boolean contains(E elem) {
        return find(elem) != null;
    }

    /**
     * find the first occurrence of the node containing the specified element
     **/
    private AVLTreeNode<E> find(E elem) {
        AVLTreeNode<E> currNode = root;

        while ( currNode != null ) {
            int compareResult = elem.compareTo(currNode.elem);

            //if elem < currNode, the node will be in the left tree
            //if elem > currNode, the node will be in the right tree
            //if elem = currNode, the node is currNode
            if ( compareResult < 0 ) {
                currNode = currNode.left;
            } else if ( compareResult > 0 ) {
                currNode = currNode.right;
            } else {
                break;
            }
        }

        return currNode;
    }

    /**
     * balance the tree with 'node' as the root node
     **/
    private AVLTreeNode<E> balance(AVLTreeNode<E> node) {
        if ( node == null ) {
            return null;
        }

        //calculate the height of the left and right tree
        int leftHeight = height(node.left);
        int rightHeight = height(node.right);

        if ( leftHeight - rightHeight > ALLOWED_IMBALANCE ) {
            //the left tree is higher than the right tree
            //so the left tree will be rotated to decrease its height
            int leftLeftHeight = height(node.left.left);
            int leftRightHeight = height(node.left.right);

            if ( leftLeftHeight >= leftRightHeight ) {
                node = singleRotateLeftChild(node);
            } else {
                node = doubleRotateLeftChild(node);
            }
        } else if ( rightHeight - leftHeight > ALLOWED_IMBALANCE ) {
            //the right tree is higher than the left tree
            //so the right tree will be rotated to decrease its height
            int rightLeftHeight = height(node.right.left);
            int rightRightHeight = height(node.right.right);

            if ( rightRightHeight >= rightLeftHeight ) {
                node = singleRotateRightChild(node);
            } else {
                node = doubleRotateRightChild(node);
            }
        }

        //after rotation, the heights of the left and right nodes have changed
        node.height = Math.max(height(node.left), height(node.right)) + 1;

        return node;
    }

    /**
     * get the height of the node
     **/
    private int height(AVLTreeNode<E> node) {
        return node == null ? -1 : node.height;
    }

    /**
     * assume:
     * k2 is child, k1 is parent
     *
     * after node rotation:
     *
     *      k1              k2
     *     / \             / \
     *    k2  C  ---->    A   k1
     *   / \                 / \
     *  A   B               B   C
     *
     * @author Leon Xie
     * @param k1Node the reference of node k1
     *
     **/
    private AVLTreeNode<E> singleRotateLeftChild(AVLTreeNode<E> k1Node) {
        AVLTreeNode<E> k2Node = k1Node.left;

        //single rotate the left child
        k1Node.left = k2Node.right;
        k2Node.right = k1Node;

        //calculate the height of the k1, k2 nodes
        k1Node.height = Math.max(height(k1Node.left), height(k1Node.right)) + 1;
        k2Node.height = Math.max(height(k2Node.left), k1Node.height) + 1;

        return k2Node;
    }

    /**
     * assume:
     * k2 is child, k1 is parent
     *
     * after node rotation:
     *
     *      k1              k2
     *     / \             / \
     *    A  k2  ---->    k1  C
     *      / \          / \
     *     B   C        A   B
     *
     * @author Leon Xie
     * @param k1Node the reference of node k1
     *
     **/
    private AVLTreeNode<E> singleRotateRightChild(AVLTreeNode<E> k1Node) {
        AVLTreeNode<E> k2Node = k1Node.right;

        //single rotate the right child
        k1Node.right = k2Node.left;
        k2Node.left = k1Node;

        //calculate the height of the k1, k2 nodes
        k1Node.height = Math.max(height(k1Node.left), height(k1Node.right)) + 1;
        k2Node.height = Math.max(height(k2Node.right), k1Node.height) + 1;

        return k2Node;
    }

    /**
     * assume:
     * k2 is child, k1 is parent, k3 is right-inside
     *
     * after node rotation:
     *
     *      k1              k3
     *     / \             / \
     *    k2  D  ---->   k2   k1
     *   / \            / \   / \
     *  A   k3         A   B C   D
     *     / \
     *    B   C
     *
     * @author Leon Xie
     * @param k1Node the reference of node k1
     **/
    private AVLTreeNode<E> doubleRotateLeftChild(AVLTreeNode<E> k1Node) {
        k1Node.left = singleRotateRightChild(k1Node.left);

        return singleRotateLeftChild(k1Node);
    }

    /**
     * assume:
     * k2 is child, k1 is parent, k3 is left-inside
     *
     * after node rotation:
     *
     *      k1              k3
     *     / \             / \
     *    A  k2  ---->   k1   k2
     *      / \         / \   / \
     *     k3  D       A   B C   D
     *    / \
     *   B   C
     *
     * @author Leon Xie
     * @param k1Node the reference of node k1
     *
     **/
    private AVLTreeNode<E> doubleRotateRightChild(AVLTreeNode<E> k1Node) {
        k1Node.right = singleRotateLeftChild(k1Node.right);

        return singleRotateRightChild(k1Node);
    }

    /**
     * find the node containing the minimum element.
     *
     * @author Leon Xie
     * @param node should not be null
     **/
    private AVLTreeNode<E> findMin(AVLTreeNode<E> node) {
        //follow the node to the left most child node
        while ( node.left != null ) {
            node = node.left;
        }

        return node;
    }
}
