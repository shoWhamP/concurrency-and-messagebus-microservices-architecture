package bgu.spl.mics.example.messages;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.Event;

public class TestModelEvent implements Event<TestModelEvent> {
	private String result;
	private Model model;
	public TestModelEvent(Model m) {
		model=m;
	}
	public Model getModel() {return model;}
	public void setResult(String s) {result=s;}//the gpumics uses it
	public String getResult() {return result;}
}
