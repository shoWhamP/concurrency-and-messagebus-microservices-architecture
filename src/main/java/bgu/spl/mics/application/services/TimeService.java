package bgu.spl.mics.application.services;
import bgu.spl.mics.Broadcast;
import  bgu.spl.mics.*;
import bgu.spl.mics.example.messages.TickBroadcast;
import java.util.*;
/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private int time=0;
	private int speed;
	private int duration;

	public TimeService(int speed, int duration) {
		super("TimeService");
		this.speed = speed;
		this.duration = duration;
	}

	@Override
	protected void initialize() {
		Timer timer = new Timer(true);
		TimerTask tickFactory = new TimerTask() {
			public void run() {
				Broadcast tick = new TickBroadcast(time);
				sendBroadcast(tick);
				duration--;
				time++;
				if (duration == 0) {
					Broadcast finalCountDown = new TickBroadcast(-1);
					sendBroadcast(finalCountDown);
					timer.cancel();
					System.out.println("killing clock");
				}
			}
		};
		Date date = new Date();
		timer.schedule(tickFactory, date, speed);
		terminate();
		}
	}

