package bgu.spl.mics.application.objects;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Model.Result;
import bgu.spl.mics.application.objects.Student.Degree;
import bgu.spl.mics.application.services.GPUService;

import java.util.LinkedList;
import java.util.Queue;
/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
	private static int id=0;
    public enum Type {RTX3090, RTX2080, GTX1080}
    private int capacity;
    private Type type;
    private Queue <DataBatch> vRam;
    private Queue <DataBatch> unprocessed;
    private Cluster cluster;
    private int trainDuration;
    private Model model=null;
    private int GpuId;
    private int batchestoTrain=0;
    
    GPU(Type t){
    	this.type=t;
    	this.cluster=Cluster.getInstance();
    	// define capacity on basis of type
    	if(type==Type.RTX3090) {
    		this.capacity=32;
    		this.trainDuration=1;}
    	else if(type==Type.RTX2080) {
    		this.capacity=16;
    		this.trainDuration=2;}
    	else {this.capacity=8;
    		this.trainDuration=4;
    	}
    	this.vRam=new LinkedList<DataBatch>();//check if its ok to do that
    	this.unprocessed=new LinkedList<DataBatch>();
    	this.GpuId=id;
    	id++;
		init();
    	//******* need to start running his servant

    }

	public void init(){
		MicroService bitch=new GPUService("my gpu owner id is"+id+"and im his bitch",this);
		Thread engine = new Thread(()->bitch.run());
		engine.start();
		//bitch.run();
	}

    public void sendData() {//maybe we will have a thread that will be incharge of this
    	//sends data to cluster.
    	while(!unprocessed.isEmpty()) {
    		if(capacity!=0) {
    			cluster.passToCpu(unprocessed.remove());
    			capacity--;}
    		else {
    			synchronized (unprocessed){
				try {
    			unprocessed.wait();
    			} catch(InterruptedException e) {}}
    		}
    	}
    	
    }
    
    public void addProcessedBatch(DataBatch d) {
    	//this is the method the cluster uses to add processed dataBatch
    	if(capacity==0)
    		throw new IllegalStateException("yaben shel zona havRam mfotszts");
    	vRam.add(d);
    }
    
    public boolean trainModel() {//maybe we will have a thread that will be incharge of this
    	//this is the method the gpu service uses to train the model with processed data need to be used only if vRam is not empty
    	if(!vRam.isEmpty()) {
    		cluster.incGpu();
    		vRam.peek().increaseTick();
    		if(vRam.peek().geTicks()==trainDuration) {
    			vRam.remove();
    			capacity++;//we can now send another dataBatch to be trained.
				synchronized (unprocessed){
						unprocessed.notify();
				}
    			batchestoTrain--;
    		}
    		if(batchestoTrain==0) {
    			cluster.saveModel(model.getName());
    			return true;
    		}
    	}
    	return false;
    }
     
    public void divideToBatches(Model model) {
    	//this is the method the gpu services uses to divide the data to batches and initialize the unprocessed collection
    	this.model=model;
    	int i=0;
    	while(i<model.getData().getSize()) {
    		DataBatch d=new DataBatch(model.getData(),i,GpuId);
    		unprocessed.add(d);
    		i=i+1000;
    	}
    	batchestoTrain=unprocessed.size();
    }
    
    public Model testModel(Model m) {
    	double result=Math.random();
    	if(m.getStudent().getStatus()== Degree.MSc) {
    		if(result<=0.6)
    			m.setResult(Result.Good);
    		else m.setResult(Result.Bad);
    	}
    	else {
    		if(result<=0.8)
    			m.setResult(Result.Good);
    		else m.setResult(Result.Bad);
    	}
    	return m;
    }
    
    //public int batchesLeft() {return batchestoTrain;}
    public int getId() {return GpuId;}
    //public Model getModel() {return model;}
}
