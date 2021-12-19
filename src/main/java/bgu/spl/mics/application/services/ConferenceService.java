package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model.Result;
import bgu.spl.mics.example.messages.PublishConferenceBroadcast;
import bgu.spl.mics.example.messages.PublishResultsEvent;
import bgu.spl.mics.example.messages.TickBroadcast;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    //private List<String> successfulModels=new LinkedList<String>();
	private int date;
	private ConfrenceInformation comicon;
    public ConferenceService(String name,int d, ConfrenceInformation con) {
        super(name);
        date=d;
        comicon=con;
    }

    @Override
    protected void initialize() {
        // TODO Implement this
    	subscribeBroadcast(TickBroadcast.class, (TickBroadcast t)->{
    												if(t.getTime()==-1)
    													terminate();
    												if(t.getTime()==date) {
    													PublishConferenceBroadcast toPublish=new PublishConferenceBroadcast(comicon.getSuccessfulModels());
    													sendBroadcast(toPublish);
														terminate();}
    													});
    	subscribeEvent(PublishResultsEvent.class, (PublishResultsEvent pre)->{
    		if(pre.getModel().getResult()==Result.Good) {
				comicon.addModel(pre.getModel());
				pre.getModel().publish();
			}
			pre.read();
			complete(pre,pre);

    	});
    }
}
