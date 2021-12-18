package bgu.spl.mics.example.messages;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.Event;

public class TrainModelEvent implements Event<TrainModelEvent> {
	private Model model;
	
	public TrainModelEvent(Model m) {model=m;}
	public Model getModel() {return model;}

}
