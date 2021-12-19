package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
	public enum Status {
		 PreTrained, Training, Trained,Tested
	   }
	public enum Result {
		 None, Good, Bad
	   }
	private String name;
	private Data data;
	private Student student;
	private Status status;
	private Result results;
	private boolean published=false;
	private int size;
	private Data.Type type;
	
	Model(String name,Data d, Student s) {
		this.name=name;
		this.data=d;
		this.student=s;
		this.status =Status.PreTrained;
		this.results=Result.None;
	}
	
    public String getName() {
    	return this.name;
    }
	public Data getData() {
		return this.data;
	}
	public Student getStudent() {
		return this.student;
	}
	public Status getStatus() {
		return this.status;
	}
	public void setStatus(Status state) {status=state;}
	public void setResult(Result r) {this.results=r;}
	public Result getResult() {
		return this.results;
	}
	public void publish() {published=true;}
	public boolean isPublished() {return published;}
	public void setData(){this.data = new Data(this.type,this.size);}
	public void init(Student s){
		this.student=s;
		this.status =Status.PreTrained;
		this.results=Result.None;
	}
}
