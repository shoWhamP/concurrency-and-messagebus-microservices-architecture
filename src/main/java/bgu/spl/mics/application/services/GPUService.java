package bgu.spl.mics.application.services;
import bgu.spl.mics.application.objects.Model.Status;
//import src.main.java.bgu.spl.mics.example.messages.TrainModelEvent;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.example.messages.*;
import java.util.*;
/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
	private GPU myGpu;
	private MessageBusImpl bus=MessageBusImpl.getInstance();
	private TrainModelEvent inProgress=null;
	private Queue<TrainModelEvent> gym=new LinkedList<TrainModelEvent>();
    public GPUService(String name, GPU gpu) {
        super(name);
        this.myGpu=gpu;
    }

    @Override
    protected void initialize() {
        // TODO Implement this
    	subscribeBroadcast(TickBroadcast.class,(TickBroadcast tick)->{
    											if(tick.getTime()==-1)//need to end messageloop;
    												terminate();
    											if(myGpu.trainModel()) {
    												Model m=inProgress.getModel();
    												m.setStatus(Status.Trained);
    												TrainModelEvent trained=new TrainModelEvent(m);
    												bus.complete(inProgress, trained);
    												if(!gym.isEmpty()) {
    													inProgress=gym.remove();
    													Thread t1 = new Thread(()->{myGpu.divideToBatches(inProgress.getModel());
    													inProgress.getModel().setStatus(Status.Training);
        												myGpu.sendData();});//he is responsible of sending data to cluster.
        												t1.start();
    												}
    												}
    	});//controls what happens when we get a tick.
    	subscribeEvent(TrainModelEvent.class, (TrainModelEvent trainee)->{
    											gym.add(trainee);
    											if(inProgress==null || inProgress.getModel().getStatus()==Status.Trained ) {
    												this.inProgress=gym.remove();
    												Thread t1 = new Thread(()->{myGpu.divideToBatches(trainee.getModel());
    												trainee.getModel().setStatus(Status.Training);
    												myGpu.sendData();});//he is responsible of sending data to cluster.
    												t1.start();}
    																		
    	});//controls what happens when you get tme.
    	subscribeEvent(TestModelEvent.class, (TestModelEvent testee)->{
    										Model m=myGpu.testModel(testee.getModel());
    										m.setStatus(Status.Tested);
    										TestModelEvent tested=new TestModelEvent(m);
    										bus.complete(testee, tested);
    	});

    }
}
