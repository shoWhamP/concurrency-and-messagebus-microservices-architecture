package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
	//enum Status{Raw,Processed}
	private Data data;
	private int start_index;
	//private Status status;
	private int ticksPassed=0;
	private int origin;
	
	DataBatch(Data d, int start, int id){
		this.data=d;
		this.start_index=start;
		this.origin=id;
	}
	
	public Data getData() {return this.data;}
	public int getStart() {return this.start_index;}
	public int getOrigin() {return this.origin;}
	public void increaseTick() {ticksPassed++;}
	public void resetTick() {ticksPassed=0;}
	public int geTicks() {return ticksPassed;}
}
	
