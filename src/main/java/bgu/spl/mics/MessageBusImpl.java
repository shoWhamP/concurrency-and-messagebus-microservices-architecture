package bgu.spl.mics;

import bgu.spl.mics.example.messages.PublishConferenceBroadcast;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private HashMap<Class<? extends Event>,Queue<MicroService>> evenToSubs = new HashMap<Class<? extends Event>,Queue<MicroService>>();
	private HashMap<MicroService,Queue<Message>> micsToMsgs = new HashMap<MicroService,Queue<Message>>();
	private HashMap<Class<? extends Broadcast>,Queue<MicroService>> broadcasToSubs = new HashMap<Class<? extends Broadcast>,Queue<MicroService>>();
	private HashMap<Event,Future> evenToFuture = new HashMap<Event,Future>();
	private static MessageBusImpl instance=null;

	private MessageBusImpl() {
		instance=this;
		// TODO Auto-generated constructor stub
	}
	@Override
	public synchronized <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub
		if(evenToSubs.get(type)==null) {
			Queue <MicroService> q = new ConcurrentLinkedQueue<MicroService>();
			q.add(m);
			evenToSubs.put(type, q);
		}
		else {
			evenToSubs.get(type).add(m);
		}
	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub
		if(broadcasToSubs.get(type)==null) {
			Queue <MicroService> q = new ConcurrentLinkedQueue<MicroService>();
			q.add(m);
			broadcasToSubs.put(type, q);
		}
		else {
			broadcasToSubs.get(type).add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub
		evenToFuture.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub
		//need to notify all the mcis that were subscribed incase they're waiting***
		Queue <MicroService> micsQ=broadcasToSubs.get(b.getClass());
		if(b.getClass()== PublishConferenceBroadcast.class){
			System.out.println("publish results from conference"+((PublishConferenceBroadcast) b).getResults().isEmpty());
			}
		for(MicroService m: micsQ) {
			if(micsToMsgs.get(m)!=null)
				micsToMsgs.get(m).add(b);
			synchronized (m){
			m.notify();}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		while(evenToSubs.get(e.getClass())==null){}//???? maybe helps with that error you get when you try to send tick broacast and no one is registered yet
		if(evenToSubs.get(e.getClass())!=null) {
			// need to send the right microservice the event according to the robin shit
			MicroService m=evenToSubs.get(e.getClass()).remove();// remove the mics that get the current message
			micsToMsgs.get(m).add(e);//adds the message to the mics msgQ
			evenToSubs.get(e.getClass()).add(m);//re-adding the mics to the event micsQ- now it is last to recieve event
			synchronized (m){//need to notify m incase he is wating in awaitmessage***
				m.notify();
			}
			Future<T>f=new Future<T>();
			evenToFuture.put(e, f);
			return f;
		}
		return null;
	}

	@Override
	public synchronized void register(MicroService m) {
		// TODO Auto-generated method stub
		Queue <Message> q= new ConcurrentLinkedQueue<Message>();
		micsToMsgs.put(m, q);
		

	}

	@Override
	public synchronized void unregister(MicroService m) {
		// TODO Auto-generated method stub
		if(micsToMsgs.get(m)!=null) {
			micsToMsgs.remove(m);
		}
		for(Queue q:broadcasToSubs.values()){
			if(q.contains(m))
				q.remove(m);
		}
		for(Queue q:evenToSubs.values()){
			if(q.contains(m))
				q.remove(m);
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		if(micsToMsgs.get(m)==null)
			throw new IllegalStateException("mics was not regestired");
		while(micsToMsgs.get(m).isEmpty()) {
			synchronized (m){
				m.wait();
			}
		}
		return micsToMsgs.get(m).remove();
	}
	
	public static MessageBusImpl getInstance() {
		if(instance==null)
		{
			instance = new MessageBusImpl();
		}
		return instance;
	}
}
