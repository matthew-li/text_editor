/** A state of the program's text designed for undos and redos.
 *	@author Matthew E. Li
 */
public class State {
	
	/* The time, in milliseconds, at which the state was created. */
	private long creationTime;
	/* The text saved in the state. */
	private String text;
	
	/** Instantiate a new State with SAVEDTEXT. */
	public State(String savedText) {
		creationTime = System.currentTimeMillis();
		text = savedText;
	}
	
	/** Instantiate a new State with SAVEDTEXT and STARTTIME. */
	public State(String savedText, long startTime) {
		creationTime = startTime;
		text = savedText;
	}
	
	/** Return the time, in milliseconds, at which the state was created. */
	public long getCreationTime() {
		return creationTime;
	}
	
	/** Return the text saved in the state. */
	public String getText() {
		return text;
	}
	
	/** Set the text saved in the state to NEWTEXT. */
	public void setText(String newText) {
		text = newText;
	}
	
	/** Return the difference, in seconds, between the creation  times of two states. */
	public static long differenceSeconds(State x, State y) {
		return Math.abs(x.getCreationTime() - y.getCreationTime()) / 1000;
	}
	
}
