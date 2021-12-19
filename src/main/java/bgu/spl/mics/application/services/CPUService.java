package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.example.messages.TickBroadcast;

/**
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private CPU master;
    public CPUService(String name,CPU cpu) {
        super(name);
        master = cpu;
    }

    @Override
    protected void initialize() {
    	subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick)->{
    		if(tick.getTime()==-1)
    			terminate();
    		master.proccessAndSend();
    	});

    }
}
