/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp;

import java.util.Stack;

public class History {
    public Stack<Memento> backward = new Stack();
    public Stack<Memento> forward = new Stack();

    public void add(Memento memoto) {
        memoto.exec();
        this.backward.push(memoto);
        this.forward.clear();
    }

    public void clear() {
        this.backward.clear();
        this.forward.clear();
    }

    public String undo() {
        if (this.backward.isEmpty()) {
            return null;
        }
        Memento memoto = this.backward.pop();
        memoto.undo();
        this.forward.push(memoto);
        return memoto.toString();
    }

    public String redo() {
        if (this.forward.isEmpty()) {
            return null;
        }
        Memento memoto = this.forward.pop();
        memoto.exec();
        this.backward.push(memoto);
        return memoto.toString();
    }
}

