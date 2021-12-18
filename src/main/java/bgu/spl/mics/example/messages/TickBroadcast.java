package bgu.spl.mics.example.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
	private int time;
	public TickBroadcast(int t) {time=t;}
	public int getTime() {return time;}
}
