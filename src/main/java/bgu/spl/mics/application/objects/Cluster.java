package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.objects.Data.Type;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class Cluster {
	private Queue<GPU> GPUs=new ConcurrentLinkedQueue<GPU>();
	private Queue<CPU> CPUs=new ConcurrentLinkedQueue<CPU>();
	private Queue<DataBatch> unproccessedImages=new ConcurrentLinkedQueue<DataBatch>();
	private Queue<DataBatch> unproccessedText=new ConcurrentLinkedQueue<DataBatch>();
	private Queue<DataBatch> unproccessedTabular=new ConcurrentLinkedQueue<DataBatch>();
	private Queue<String> modelNames=new ConcurrentLinkedQueue<String>();
	private int processedBatchesCount=0;
	private int cpUsedTime=0;
	private int gpUsedTime=0;
	/**
     * Retrieves the single instance of this class.
     */
	private static Cluster cluster=new Cluster();
	private Cluster() {
		this.modelNames=new LinkedList<String>();
		this.cpUsedTime=0;
		this.gpUsedTime=0;
		this.processedBatchesCount=0;
	}
	public static Cluster getInstance() {
		return new Cluster();
	}
	public void regGpu(GPU gpu){GPUs.add(gpu);}
	public void regCpu(CPU cpu){CPUs.add(cpu);}

	public void passToCpu(DataBatch b) {
		if(b.getData().GetType()==Type.Images)
			unproccessedImages.add(b);
		if(b.getData().GetType()==Type.Tabular)
			unproccessedTabular.add(b);
		if(b.getData().GetType()==Type.Text)
			unproccessedText.add(b);
	}
	
	public DataBatch getNextData(int cores) {
		double dice=Math.random();
		if(cores==32) {
			if(!unproccessedImages.isEmpty() & dice>=0.15 )
				return unproccessedImages.remove();
			else if(!unproccessedText.isEmpty() & dice >= 0.3)
				return unproccessedText.remove();
			else if(!unproccessedTabular.isEmpty())
				return unproccessedTabular.remove();
			else return null;
		}
		if(cores==16) {
			if(!unproccessedImages.isEmpty() & dice>=0.4 )
				return unproccessedImages.remove();
			else if(!unproccessedText.isEmpty() & dice >= 0.2)
				return unproccessedText.remove();
			else if(!unproccessedTabular.isEmpty())
				return unproccessedTabular.remove();
			else return null;
		}
		if(cores==8) {
			if(!unproccessedImages.isEmpty() & dice>=0.75 )
				return unproccessedImages.remove();
			else if(!unproccessedText.isEmpty() & dice >= 0.35)
				return unproccessedText.remove();
			else if(!unproccessedTabular.isEmpty())
				return unproccessedTabular.remove();
			else return null;
		}
		if(cores==4) {
			if(!unproccessedImages.isEmpty() & dice>=0.90 )
				return unproccessedImages.remove();
			else if(!unproccessedText.isEmpty() & dice >= 0.4)
				return unproccessedText.remove();
			else if(!unproccessedTabular.isEmpty())
				return unproccessedTabular.remove();
			else return null;
		}
		if(cores==2) {
			if(!unproccessedImages.isEmpty() & dice>=0.9 )
				return unproccessedImages.remove();
			else if(!unproccessedText.isEmpty() & dice >= 0.75)
				return unproccessedText.remove();
			else if(!unproccessedTabular.isEmpty())
				return unproccessedTabular.remove();
			else if(!unproccessedText.isEmpty())
				 return unproccessedText.remove();
			else return null;
		}
		if(cores==1) {
			if(!unproccessedImages.isEmpty() & dice>=0.97 )
				return unproccessedImages.remove();
			else if(!unproccessedText.isEmpty() & dice >= 0.75)
				return unproccessedText.remove();
			else if(!unproccessedTabular.isEmpty())
				return unproccessedTabular.remove();
			else if(!unproccessedText.isEmpty())
				 return unproccessedText.remove();
			else return null;
		}
		return null;
		}
	
	public boolean hasData(){
		if(unproccessedImages.size()!=0|unproccessedTabular.size()!=0|unproccessedText.size()!=0)
			return true;
		else return false;
	}
	
	public void passToGpu(DataBatch b) {
		processedBatchesCount++;
		int dest=b.getOrigin();
		for(GPU g: GPUs) {
			if(g.getId()==dest) {
				g.addProcessedBatch(b);
				break;
			}
		}
	}
	public void saveModel(String name) {
		modelNames.add(name);
	}
	public Queue<String> getTrainedModel() {return modelNames;}
	public void incPu() {cpUsedTime++;}
	public int getUsedCpuTime() {return cpUsedTime;}
	public void incGpu() {gpUsedTime++;}
	public int getUsedGpuTime() {return gpUsedTime;}
	public int getBatchCounter() {return this.processedBatchesCount;}
}
