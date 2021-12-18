package bgu.spl.mics.example.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.List;

public class PublishConferenceBroadcast implements Broadcast {
	private List<Model> goodResults;
	public PublishConferenceBroadcast(List<Model> gm) {goodResults=gm;}
	public List<Model> getResults(){return goodResults;}

}
