package bgu.spl.mics.example.messages;

import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.Event;

public class PublishResultsEvent implements Event<PublishResultsEvent> {
	private Model model;
	private boolean reachedConfrence=false;
	public PublishResultsEvent(Model model){
		this.model=model;
		}
	public Model getModel() {return model;}
	public void read() {
		reachedConfrence=true;
	}

}
