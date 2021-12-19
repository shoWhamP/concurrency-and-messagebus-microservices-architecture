package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.example.messages.*;

import java.util.List;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
	private List<Model> models;
	private Student student;
    
	public StudentService(String name,List<Model> m, Student s) {
        super(name);
        models=m;
        student=s;
        // TODO Implement this
    }

    @Override
    protected void initialize() {
        // TODO Implement this
    	subscribeBroadcast(PublishConferenceBroadcast.class, (PublishConferenceBroadcast pcb)->{
    		List<Model> toRead = pcb.getResults();
    		for(Model modely: toRead) {
    			if(modely.getStudent().getName().equals(student.getName())) {
    				student.setPublications(student.getPublications()+1);
    			student.setPapersRead(student.getPapersRead()+1);	
    			}
    		}
    	}); //end of callback function
    	
    	Thread modelHandler = new Thread(()->{    	for(Model model:models) {
    		TrainModelEvent scar = new TrainModelEvent(model);
    		Future<TrainModelEvent> trained = sendEvent(scar);
    		TestModelEvent testhim= new TestModelEvent(trained.get().getModel());
    		Future<TestModelEvent>  tested = sendEvent(testhim);
    		PublishResultsEvent cnn= new PublishResultsEvent(tested.get().getModel());
    		Future <PublishResultsEvent> publishim = sendEvent(cnn);
    		publishim.get();}
    	});
		subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick)->{
			//System.out.println("Tick number"+ tick.getTime());
			if(tick.getTime()==-1){
				modelHandler.interrupt();//*****************
				terminate();}});
    	modelHandler.start();
    	
    }
}
