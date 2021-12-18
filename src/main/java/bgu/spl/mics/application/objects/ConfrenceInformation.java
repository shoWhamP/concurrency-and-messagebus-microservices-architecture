package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.ConferenceService;

import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private List<Model> successfulModels=new LinkedList<Model>();
    public ConfrenceInformation(String name, int date) {
    	this.name=name;
    	this.date=date;
    }

    public void init(){
        ConferenceService dobbi=new ConferenceService(name+"'s houseElf",this.date,this);
        Thread conference=new Thread(()->dobbi.run());
        conference.setName("conference");
        conference.start();
        //dobbi.run();
    }
    public void addModel(Model m) {
    	successfulModels.add(m);
    	m.publish();
    }
    public List<Model> getSuccessfulModels(){return successfulModels;}
    public String getName() {return name;}
    public int getDate() {return date;}
}
