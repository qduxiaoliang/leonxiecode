package com.deskshell.datastruct.tree;

/**
 * red black tree is a balanced binary search tree.
 *
 * it has the below four red black rules:
 * 1. every node either has color red or has color black.
 * 2. the root node always has color black.
 * 3. if a node has color red, its children must has color black.
 * 4. every path from a node to the leaf node or a null node must
 *    has the same number of black nodes.
 *
 * this four rules ensures that the red black tree is a balanced
 * binary search tree. this class implements the insertion and
 * deletion functionality of the red black tree. with regard to
 * the other functionalities, they are the same as those in the
 * binary search tree.
 *
 * @author Leon Xie
 **/
public class RedBlackTree<E extends Comparable<E>> {
    private int size;
    private RedBlackTreeNode<E> root;

    /**
     * return the size of the tree
     **/
    public int size() {
        return size;
    }

    /**
     * return the root node of the tree
     **/
    public RedBlackTreeNode<E> getRoot() {
        return root;
    }

    /**
     * insert the element into the tree
     *
     * @author Leon Xie
     * @param elem the element to be inserted into the tree
     * @throws NullPointerException
     *    if the specified element is null, this exception will be thrown.
     **/
    public void insert(E elem) {
        //null element is not allowed
        if ( elem == null ) { throw new NullPointerException(); }

        //by default, the new node has color red
        RedBlackTreeNode<E> newNode = new RedBlackTreeNode<>(elem);

        if ( root == null ) {
            root = newNode;
            size++;
        } else {
            insert(root, newNode);
        }

        //enforce red black rule 2
        root.setBlack();
    }

    /**
     * insert the node into the tree
     *
     * @author Leon Xie
     * @param currNode the root node of the tree
     * @param newNode the new node to be inserted
     **/
    private void
    insert(RedBlackTreeNode<E> currNode, RedBlackTreeNode<E> newNode) {
        while ( currNode != null ) {
            /*
             * before flip:
             * X is red, T is red, P is black
             *
             * after flip:
             * X is black, T is black, P is red
             */
            flipColorForInsert(currNode);

            /*
             * if newNode = currNode, newNode will be skipped
             * if newNode < currNode, newNode will be inserted in the left tree
             * if newNode > currNode, newNode will be inserted in the right tree
             */
            int compareResult = newNode.elem.compareTo(currNode.elem);

            if ( compareResult > 0 ) {
                if ( currNode.right == null ) {
                    //insert the new node
                    //balance the tree if red black rule 3 is violated
                    setRight(currNode, newNode);
                    balanceForInsert(newNode);

                    size++;
                    break;
                } else {
                    currNode = currNode.right;
                }
            } else if ( compareResult < 0 ) {
                if ( currNode.left == null ) {
                    //insert the new node
                    //balance the tree if red black rule 3 is violated
                    setLeft(currNode, newNode);
                    balanceForInsert(newNode);

                    size++;
                    break;
                } else {
                    currNode = currNode.left;
                }
            } else {
                //do not allow duplicated node
                break;
            }
        }
    }

    /**
     * remove the element from the tree
     **/
    public void remove(E elem) {
        RedBlackTreeNode<E> node = root;

        while ( node != null && elem != null ) {
            //balance the tree so that the node has color red
            balanceForDelete(node, elem);

            /*
             * if elem < node, the element is in the left tree
             * if elem > node, the element is in the right tree
             * if elem = node, the element is in the node
             */
            int compareResult = elem.compareTo(node.elem);

            if ( compareResult < 0 ) {
                node = node.left;
            } else if ( compareResult > 0 ) {
                node = node.right;
            } else if ( node.right != null ) {
                //replace this node with the minimum node of the right tree
                node.elem = findMin(node.right).elem;

                //next delete the minimum node of the right tree
                elem = node.elem;
                node = node.right;
            } else if ( node.left != null ) {
                //replace this node with the maximum node of the right tree
                node.elem = findMax(node.left).elem;

                //next delete the maximum node of the left tree
                elem = node.elem;
                node = node.left;
            } else {
                //the element node is always a leaf
                RedBlackTreeNode<E> parent = parentOf(node);

                if ( parent == null ) {
                    //the element node is root
                    root = null;
                } else {
                    if ( node == parent.left ) {
                        parent.left = null;
                    } else {
                        parent.right = null;
                    }
                }

                size--;
                break;
            }
        }

        //enforce the red black rule 2
        if ( root != null ) {
            root.setBlack();
        }
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
    private RedBlackTreeNode<E> find(E elem) {
        RedBlackTreeNode<E> currNode = root;

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
     * balance the node so that it has color red
     * or its next child has color red.
     *
     * case 1A:
     * X is root, A is child, B is child
     * X is black, A is black, B is black
     *
     *   X              |X|
     *  / \    ---->    / \
     * A   B           A   B
     *
     * case 1B:
     * X is root, A is child, B is child
     * X is black, A is red, B is black
     *
     *    X              A
     *   / \              \
     * |A|  B    ---->    |X|
     *                      \
     *                       B
     *
     * X is root, A is child, B is child
     * X is black, A is black, B is red
     *
     *   X                   B
     *  / \                 /
     * A |B|    ---->     |X|
     *                    /
     *                   A
     *
     * case 2A1:
     * X is child, P is parent, T is sibling
     * X and T both have two children with color black
     *
     * X is black, P is red, B is black
     *
     *  |P|              P
     *  / \    ---->    / \
     * X   T          |X| |T|
     *
     * case 2A2:
     * X is child, P is parent, T is sibling, B is outside
     * P is red, X is black, T is black, B is red
     *
     *  |P|              |T|
     *  / \    ---->     / \
     * X   T            P   B
     *      \          /
     *      |B|      |X|
     *----------------------------------------------
     *     |P|             |T|
     *     / \   ---->     / \
     *    T   X           B   P
     *   /                     \
     * |B|                     |X|
     *
     * case 2A3:
     * X is child, P is parent, T is sibling, B is inside
     * P is red, X is black, T is black, B is red
     *
     *  |P|              |B|
     *  / \    ---->     / \
     * X   T            P   T
     *    /            /
     *  |B|          |X|
     *----------------------------------------------
     *  |P|             |B|
     *  / \   ---->     / \
     * T   X           T   P
     *  \                  \
     *  |B|                |X|
     *
     * @author Leon Xie
     * @param  node the node X that will be changed to have color red
     * @param  elem the element that will be deleted
     **/
    private void balanceForDelete(RedBlackTreeNode<E> node, E elem) {
        //if X is red, that's what we need
        //color change and rotation can be avoided
        if ( isRed(node) ) { return; }

        if ( isRoot(node) ) {
            if ( isBlack(node.left) && isBlack(node.right) ) {
                //case 1A:
                //root node X has two children, both of which are black
                colorCase1AForDelete(node);
            } else if ( hasTwoChild(node) && hasRedChild(node) ) {
                //case 1B:
                //root node X has two children, one of which is red
                rotateCase2BForDelete(node, elem);
            }
        } else {
            if ( hasTwoBlackChild(node) ) {
                //case 2A
                //node X has two children, both of which are black
                RedBlackTreeNode<E> sibling = siblingOf(node);
                RedBlackTreeNode<E> parent = parentOf(node);

                if ( hasTwoBlackChild(sibling) ) {
                    //case 2A1
                    //node T has two children, both of which are black
                    colorCase2A1ForDelete(parent);
                } else if ( isRed(leftOf(sibling)) ) {
                    if ( sibling == leftOf(parent) ) {
                        //case 2A2
                        //node T is left node, which has red left child
                        colorCase2A2ForDelete(
                            node, parent, sibling, sibling.left);
                        rotateLeftLeft(parent);
                    } else {
                        //case 2A3
                        //node T is right node, which has red left child
                        colorCase2A3ForDelete(node, parent);
                        rotateRightLeft(parent);
                    }
                } else if ( isRed(rightOf(sibling)) ) {
                    if ( sibling == leftOf(parent) ) {
                        //case 2A3
                        //node T is left node, which has red right child
                        colorCase2A3ForDelete(node, parent);
                        rotateLeftRight(parent);
                    } else {
                        //case 2A2
                        //node T is right child, which has red right child
                        colorCase2A2ForDelete(
                            node, parent, sibling, sibling.right);
                        rotateRightRight(parent);
                    }
                }
            } else if ( hasTwoChild(node) && hasRedChild(node) ) {
                //case 2B:
                //node X has two children, one of which is red
                rotateCase2BForDelete(node, elem);
            }
        }
    }

    /**
     * case 1A:
     * X is root, A is left, B is right
     *
     * before color change:
     * X is black, A is black, B is black
     *
     * after color change:
     * X is red, A is black, B is black
     *
     *   X              |X|
     *  / \    ---->    / \
     * A   B           A   B
     *
     * @author Leon Xie
     * @param  node the reference of node X
     **/
    private void colorCase1AForDelete(RedBlackTreeNode<E> node) {
        setRed(node);
    }

    /**
     * case 2A1:
     * P is parent, X is child, T is sibling
     *
     * before color change:
     * P is red, X is black, T is black
     *
     * after color change:
     * P is black, X is red, T is red
     *
     *  |P|              P
     *  / \    ---->    / \
     * X   T          |X| |T|
     *
     * @author Leon Xie
     * @param  node the reference of node P
     **/
    private void colorCase2A1ForDelete(RedBlackTreeNode<E> node) {
        setBlack(node);
        setRed(node.left);
        setRed(node.right);
    }

    /**
     * case 2A2:
     * P is parent, X is child, T is sibling, B is outside
     *
     * before color change:
     * P is red, X is black, T is black, B is red
     *
     * after color change:
     * P is black, X is red, T is red, B is black
     *
     *  |P|              |T|
     *  / \    ---->     / \
     * X   T            P   B
     *      \          /
     *      |B|      |X|
     *----------------------------------------------
     *     |P|             |T|
     *     / \   ---->     / \
     *    T   X           B   P
     *   /                     \
     * |B|                     |X|
     *
     * @author Leon Xie
     * @param  node the reference of node X
     * @param  parent the reference of node P
     * @param  sibling the reference of node T
     * @param  sibChild the reference of node B
     **/
    private void colorCase2A2ForDelete(RedBlackTreeNode<E> node, RedBlackTreeNode<E> parent,
                                       RedBlackTreeNode<E> sibling, RedBlackTreeNode<E> sibChild) {
        setRed(node);
        setBlack(parent);
        setRed(sibling);
        setBlack(sibChild);
    }

    /**
     * case 2A3:
     * P is parent, X is child, T is sibling, B is inside
     *
     * before color change:
     * P is red, X is black, T is black, B is red
     *
     * after color change:
     * P is black, X is red, T is black, B is red
     *
     *  |P|              |B|
     *  / \    ---->     / \
     * X   T            P   T
     *    /            /
     *  |B|          |X|
     *----------------------------------------------
     *  |P|             |B|
     *  / \   ---->     / \
     * T   X           T   P
     *  \                  \
     *  |B|                |X|
     *
     * @author Leon Xie
     * @param  node the reference of node X
     * @param  parent the reference of node P
     **/
    private void colorCase2A3ForDelete(RedBlackTreeNode<E> node, RedBlackTreeNode<E> parent) {
        setRed(node);
        setBlack(parent);
    }

    /**
     * case 2B:
     * X is parent, A is left, B is right
     *
     * before color change:
     * X is black, either A or B is red
     *
     * after color change:
     * X is red, A is black, B is black
     *
     * after rotation:
     *    X              A
     *   / \              \
     * |A|  B    ---->    |X|
     *                      \
     *                       B
     *---------------------------------
     *   X                   B
     *  / \                 /
     * A |B|    ---->     |X|
     *                    /
     *                   A
     *
     * @author Leon Xie
     * @param  node the reference of node X
     * @param  elem the element that will be deleted
     **/
    private void rotateCase2BForDelete(RedBlackTreeNode<E> node, E elem) {
        int compareResult = elem.compareTo(node.elem);

        if ( compareResult >= 0 && isBlack(node.right) ) {
            //delete will go to the right node, which is black
            //we need to rotate so that the current node is red
            setRed(node);
            setBlack(node.left);

            rotateLeftLeft(node);
        } else if ( compareResult < 0 && isBlack(node.left) ) {
            //delete will go to the left node, which is black
            //we need to rotate so that the current node is red
            setRed(node);
            setBlack(node.right);

            rotateRightRight(node);
        }
    }

    /**
     * find the minimum node in the tree.
     *
     * @author Leon Xie
     * @param  node must not be null.
     **/
    private RedBlackTreeNode<E> findMin(RedBlackTreeNode<E> node) {
        //follow the node to the left most child node
        while ( node.left != null ) {
            node = node.left;
        }

        return node;
    }

    /**
     * find the maximum node in the tree
     *
     * @author Leon Xie
     * @param  node must not be null.
     **/
    private RedBlackTreeNode<E> findMax(RedBlackTreeNode<E> node) {
        //follow the node to the left most child node
        while ( node.right != null ) {
            node = node.right;
        }

        return node;
    }

    /**
     * before flip:
     * X is red, T is red, P is black
     *
     * after flip:
     * X is black, T is black, P is red
     *
     *    P             |P|
     *   / \     --->   / \
     * |X| |T|         X   T
     *
     * @author Leon Xie
     * @param  node the reference of node P
     **/
    private void flipColorForInsert(RedBlackTreeNode<E> node) {
        if ( isBlack(node) && isRed(node.left) && isRed(node.right) ) {
            //enforce red black rule 2
            if ( !isRed(node) ) { setRed(node); }

            setBlack(leftOf(node));
            setBlack(rightOf(node));

            //balance the tree if red black rule 3 is violated
            balanceForInsert(node);
        }
    }

    /**
     * balance the red black tree after color flip or node insertion
     *
     * assume:
     * X is child, P is parent, G is grandparent, S is sibling
     *
     * before rotation and color change:
     *
     *       G          G         G          G
     *      / \        / \       / \        / \
     *    |P|  S     |P|  S     S  |P|     S  |P|
     *   /             \           /            \
     * |X|             |X|       |X|            |X|
     *
     * X is red, P is red, G is black, S is black
     *
     * after rotation and color change:
     *
     *    P           X            X           P
     *   / \         / \          / \         / \
     * |X| |G|     |P| |G|      |G| |P|     |G| |X|
     *       \           \      /           /
     *        S           S    S           S
     *
     * the root node is black, the children is red
     *
     * note:
     * for color flip, S is black node
     * for node insertion, S is null node
     *
     * @author Leon Xie
     * @param  node the reference of node X
     **/
    private void balanceForInsert(RedBlackTreeNode<E> node) {
        RedBlackTreeNode<E> parent = parentOf(node);
        RedBlackTreeNode<E> grandparent = grandparentOf(node);

        //after color flip or node insertion, red black rule 3 is violated
        if ( isRed(node) && isRed(parent) ) {
            //X is child, P is parent, G is grandparent, S is sibling
            //X is red, P is red, G is black, S is black
            if ( parent == leftOf(grandparent) ) {
                if ( node == leftOf(parent) ) {
                    //case 1:
                    //X-P-G is left-left relationship
                    rotateLeftLeftForInsert(grandparent);
                } else {
                    //case 2:
                    //X-P-G is left-right relationship
                    rotateLeftRightForInsert(grandparent);
                }
            } else {
                if ( node == rightOf(parent) ) {
                    //case 3:
                    //X-P-G is right-right relationship
                    rotateRightRightForInsert(grandparent);
                } else {
                    //case 4:
                    //X-P-G is right-left relationship
                    rotateRightLeftForInsert(grandparent);
                }
            }
        }
    }

    /**
     * assume:
     * X is child, P is parent, G is grandparent, S is sibling
     * X is red, P is red, G is black, S is black. X-P-G is left-left relationship
     *
     * after node rotation:
     *
     *      G               P
     *     / \             / \
     *   |P|  S  ---->   |X| |G|
     *   /                     \
     * |X|                      S
     *
     * after color changes:
     * X is red, P is black, G is red, S is black
     *
     * note:
     * for color flip, S is black node
     * for node insertion, S is null node
     *
     * @author Leon Xie
     * @param  node the reference of node G
     **/
    private RedBlackTreeNode<E> rotateLeftLeftForInsert(RedBlackTreeNode<E> node) {
        setRed(node);
        setBlack(leftOf(node));

        return rotateLeftLeft(node);
    }

    /**
     * assume:
     * X is child, P is parent, G is grandparent, S is sibling
     * X is red, P is red, G is black, S is black. X-P-G is right-right relationship
     *
     * after node rotation
     *
     *   G                P
     *  / \              / \
     * S  |P|   ---->  |G| |X|
     *      \          /
     *      |X|       S
     *
     * after color changes,
     * X is red, P is black, G is red, S is black
     *
     * note:
     * for color flip, S is black node
     * for node insertion, S is null node
     *
     * @author Leon Xie
     * @param  node the reference of node G
     **/
    private RedBlackTreeNode<E> rotateRightRightForInsert(RedBlackTreeNode<E> node) {
        setRed(node);
        setBlack(rightOf(node));

        return rotateRightRight(node);
    }

    /**
     * assume:
     * X is child, P is parent, G is grandparent, S is sibling
     * X is red, P is red, G is black, S is black. X-P-G is left-right relationship
     *
     * after node rotation
     *
     *    G                X
     *   / \              / \
     * |P|  S   ---->   |P| |G|
     *   \                    \
     *   |X|                   S
     *
     * after color changes,
     * X is black, P is red, G is red, S is black
     *
     * note:
     * for color flip, S is black node
     * for node insertion, S is null node
     *
     * @author Leon Xie
     * @param  node the reference of node G
     **/
    private RedBlackTreeNode<E> rotateLeftRightForInsert(RedBlackTreeNode<E> node) {
        setRed(node);
        setBlack(rightOf(leftOf(node)));

        return rotateLeftRight(node);
    }

    /**
     * assume:
     * X is child, P is parent, G is grandparent, S is sibling
     * X is red, P is red, G is black, S is black. X-P-G is right-left relationship
     *
     * after node rotation
     *
     *   G               X
     *  / \             / \
     * S  |P|  ---->  |G| |P|
     *    /             \
     *  |X|              S
     *
     * after color changes,
     * X is black, P is red, G is red, S is black
     *
     * note:
     * for color flip, S is black node
     * for node insertion, S is null node
     *
     * @author Leon Xie
     * @param  node the reference of node G
     **/
    private RedBlackTreeNode<E> rotateRightLeftForInsert(RedBlackTreeNode<E> node) {
        setRed(node);
        setBlack(leftOf(rightOf(node)));

        return rotateRightLeft(node);
    }

    /**
     * assume:
     * X is child, P is parent, G is grandparent. X-P-G is left-left relationship
     *
     * after node rotation:
     *
     *      G              P
     *     /              / \
     *    P    ---->     X   G
     *   /
     *  X
     *
     * @author Leon Xie
     * @param  k1Node the reference of node G
     **/
    private RedBlackTreeNode<E> rotateLeftLeft(RedBlackTreeNode<E> k1Node) {
        RedBlackTreeNode<E> k2Node = k1Node.left;
        RedBlackTreeNode<E> k1Parent = parentOf(k1Node);

        //set the right node of k2 as the left node of k1
        setLeft(k1Node, k2Node.right);

        //set k1 as the right node of k2
        setRight(k2Node, k1Node);

        //set k2 as the child of the original root
        replaceChild(k1Parent, k1Node, k2Node);

        return k2Node;
    }

    /**
     * assume:
     * X is child, P is parent, G is grandparent. X-P-G is right-right relationship
     *
     * after node rotation:
     *
     *  G                P
     *   \              / \
     *    P   ---->    G   X
     *     \
     *      X
     *
     * @author Leon Xie
     * @param  k1Node the reference of node G
     **/
    private RedBlackTreeNode<E> rotateRightRight(RedBlackTreeNode<E> k1Node) {
        RedBlackTreeNode<E> k2Node = k1Node.right;
        RedBlackTreeNode<E> k1Parent = parentOf(k1Node);

        //set the left node of k2 as the right node of k1
        setRight(k1Node, k2Node.left);

        //set k1 as the left node of k2
        setLeft(k2Node, k1Node);

        //set k2 as the child of the original root
        replaceChild(k1Parent, k1Node, k2Node);

        return k2Node;
    }

    /**
     * assume:
     * X is child, P is parent, G is grandparent. X-P-G is left-right relationship
     *
     * after node rotation:
     *
     *    G              X
     *   /              / \
     *  P     ---->    P   G
     *   \
     *    X
     *
     * @author Leon Xie
     * @param  k1Node the reference of node G
     **/
    private RedBlackTreeNode<E> rotateLeftRight(RedBlackTreeNode<E> k1Node) {
        rotateRightRight(k1Node.left);

        return rotateLeftLeft(k1Node);
    }

    /**
     * assume:
     * X is child, P is parent, G is grandparent. X-P-G is right-left relationship
     *
     * after node rotation:
     *
     *  G                X
     *   \              / \
     *    P    ---->   G   P
     *   /
     *  X
     *
     * @author Leon Xie
     * @param  k1Node the reference of node G
     **/
    private RedBlackTreeNode<E> rotateRightLeft(RedBlackTreeNode<E> k1Node) {
        rotateLeftLeft(k1Node.right);

        return rotateRightRight(k1Node);
    }

    /**
     * return true if node is not null and has color red
     **/
    private boolean isRed(RedBlackTreeNode<E> node) {
        return node != null && node.isRed();
    }

    /**
     * return true if node is not null and has color black
     **/
    private boolean isBlack(RedBlackTreeNode<E> node) {
        return node != null && node.isBlack();
    }

    /**
     * return true if node has two non-null children
     **/
    private boolean hasTwoChild(RedBlackTreeNode<E> node) {
        return node != null && node.left != null && node.right != null;
    }

    /**
     * return true if node has one child with color red
     **/
    private boolean hasRedChild(RedBlackTreeNode<E> node) {
        return isRed(node.left) || isRed(node.right);
    }

    /**
     * return true if node has two children both with color black or null
     **/
    private boolean hasTwoBlackChild(RedBlackTreeNode<E> node) {
        return (node != null) && (
            (node.left == null && node.right == null) ||
            (isBlack(node.left) && isBlack(node.right)));
    }

    /**
     * return true if node is the root node of the tree
     **/
    private boolean isRoot(RedBlackTreeNode<E> node) {
        return node != null && node.parent == null;
    }

    /**
     * set node to have color red
     **/
    private void setRed(RedBlackTreeNode<E> node) {
        if ( node != null ) { node.setRed(); }
    }

    /**
     * set node to have color black
     **/
    private void setBlack(RedBlackTreeNode<E> node) {
        if ( node != null ) { node.setBlack(); }
    }

    /**
     * set the left child of 'node' pointing to 'left'
     * set the parent of 'left' pointing to 'node'
     **/
    private void setLeft(RedBlackTreeNode<E> node, RedBlackTreeNode<E> left) {
        if ( node != null ) { node.left = left; }
        setParent(left, node);
    }

    /**
     * set the right child of 'node' pointing to 'right'
     * set the parent of 'right' pointing to 'node'
     **/
    private void setRight(RedBlackTreeNode<E> node, RedBlackTreeNode<E> right) {
        if ( node != null ) { node.right = right; }
        setParent(right, node);
    }

    /**
     * set the parent of 'node' pointing to 'parent'
     **/
    private void setParent(RedBlackTreeNode<E> node, RedBlackTreeNode<E> parent) {
        if ( node != null ) { node.parent = parent; }
    }

    /**
     * replace the child 'oldChild' of 'parent' with 'newChild'
     **/
    private void replaceChild(RedBlackTreeNode<E> parent,
        RedBlackTreeNode<E> oldChild, RedBlackTreeNode<E> newChild) {

        if ( parent == null ) {
            //if parent is null, this means the old node was root
            //the root node is changed to point to the new node.
            setParent(newChild, null);
            root = newChild;
        } else {
            if ( leftOf(parent) == oldChild ) {
                setLeft(parent, newChild);
            } else {
                setRight(parent, newChild);
            }
        }
    }

    /**
     * get the left child of 'node'
     **/
    private RedBlackTreeNode<E> leftOf(RedBlackTreeNode<E> node) {
        return (node == null) ? null : node.left;
    }

    /**
     * get the right child of 'node'
     **/
    private RedBlackTreeNode<E> rightOf(RedBlackTreeNode<E> node) {
        return (node == null) ? null : node.right;
    }

    /**
     * get the parent of 'node'
     **/
    private RedBlackTreeNode<E> parentOf(RedBlackTreeNode<E> node) {
        return (node == null) ? null : node.parent;
    }

    /**
     * get the grandparent of 'node'
     **/
    private RedBlackTreeNode<E> grandparentOf(RedBlackTreeNode<E> node) {
        return parentOf(parentOf(node));
    }

    /**
     * get the sibling of 'node'
     **/
    private RedBlackTreeNode<E> siblingOf(RedBlackTreeNode<E> node) {
        RedBlackTreeNode<E> parent = parentOf(node);

        return (node == leftOf(parent)) ? rightOf(parent) : leftOf(parent);
    }
}
