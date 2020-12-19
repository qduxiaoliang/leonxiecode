package com.deskshell.datastruct.tree;

public class AVLTreeNode<E> {
    public E elem;
    public int height;
    public AVLTreeNode<E> left;
    public AVLTreeNode<E> right;

    public AVLTreeNode(E elem) {
        this.elem = elem;
    }
}
