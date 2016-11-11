package net.aegistudio.mpp;

import java.util.Stack;

/**
 * History provides a unified interface for redo and undo.
 * @author aegistudio
 */

public class History {
	public Stack<Memento> backward = new Stack<Memento>();
	public Stack<Memento> forward = new Stack<Memento>();
	
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
		if(backward.isEmpty()) return null;
		Memento memoto = backward.pop();
		memoto.undo();
		forward.push(memoto);
		return memoto.toString();
	}
	
	public String redo() {
		if(forward.isEmpty()) return null;
		Memento memoto = forward.pop();
		memoto.exec();
		backward.push(memoto);
		return memoto.toString();
	}
}
