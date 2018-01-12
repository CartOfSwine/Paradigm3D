package replay;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.ListIterator;

public class Tick<V> implements Iterable<V>, Serializable{

	private static final long serialVersionUID = 1L;
	private LinkedList<V> states;
	private int tickNum;
	
	public Tick(int tickNum) {
		states = new LinkedList<>();
		this.tickNum = tickNum;
	}
	
	public void recordState(V o) {
		states.addLast(o);
	}
	
	public LinkedList<V> getStates(){
		return this.states;
	}
	
	@Override
	public ListIterator<V> iterator() {
		return states.listIterator();
	}
	
	public ListIterator<V> iterator(int startIndex) {
		return states.listIterator(startIndex);
	}
	
	public int getTickNum() {
		return this.tickNum;
	}
}