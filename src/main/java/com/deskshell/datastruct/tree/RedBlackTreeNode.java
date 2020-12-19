package com.deskshell.datastruct.tree;

public class RedBlackTreeNode<E extends Comparable<E>> {
    public E elem;
    public RedBlackTreeNode<E> left;
    public RedBlackTreeNode<E> parent;
    public RedBlackTreeNode<E> right;

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private boolean color = RED;

    public RedBlackTreeNode(E elem) {
        this.elem = elem;
        this.color = RED;
    }

    public boolean isRed() {
        return color;
    }

    public boolean isBlack() {
        return !color;
    }

    public void setRed() {
        color = RED;
    }

    public void setBlack() {
        color = BLACK;
    }

    public void switchColor() {
        color = !color;
    }
}
