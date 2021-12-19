package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.objects.Data.Type;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

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
	private Queue<DataBatch> unproccessedImages=new LinkedBlockingQueue<DataBatch>();
	private Queue<DataBatch> unproccessedText=new LinkedBlockingQueue<DataBatch>();
	private Queue<DataBatch> unproccessedTabular=new LinkedBlockingQueue<DataBatch>();
	private Queue<String> modelNames=new LinkedBlockingQueue<String>();
	private AtomicInteger processedBatchesCount=new AtomicInteger();
	private AtomicInteger cpUsedTime=new AtomicInteger();
	private AtomicInteger gpUsedTime=new AtomicInteger();
	/**
     * Retrieves the single instance of this class.
     */
	private static Cluster instance=null;
	private Cluster() {
	}
	public static Cluster getInstance() {
		if(instance==null){
			instance=new Cluster();
		}
		return instance;
	}
	public void regGpu(GPU gpu){GPUs.add(gpu);}
	public void regCpu(CPU cpu){CPUs.add(cpu);}

	public void passToCpu(DataBatch b) {
		if(b.getData().GetType()==Type.Images){
			unproccessedImages.add(b);
			}
		if(b.getData().GetType()==Type.Tabular){
			unproccessedTabular.add(b);
			;}
		if(b.getData().GetType()==Type.Text){
			unproccessedText.add(b);
			;}
		//System.out.println("data received!"+unproccessedImages.isEmpty()+unproccessedText.isEmpty()+unproccessedTabular.isEmpty());
	}
	
	public synchronized DataBatch getNextData(int cores) {
		double dice=Math.random();
		if(cores==32) {
			if(!unproccessedImages.isEmpty() & dice>=0.6)
				return unproccessedImages.remove();
			else if(!unproccessedText.isEmpty() & dice >= 0.4)
				return unproccessedText.remove();
			else if(!unproccessedTabular.isEmpty())
				return unproccessedTabular.remove();
			return null;
		}
		if(cores==16) {
			if(!unproccessedImages.isEmpty() & dice>=0.4 )
				return unproccessedImages.remove();
			else if(!unproccessedText.isEmpty() & dice >= 0.5)
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
		if(!unproccessedImages.isEmpty()||!unproccessedTabular.isEmpty()||!unproccessedText.isEmpty())
			return true;
		//System.out.println("return false from hasData");
		return false;
	}
	
	public void passToGpu(DataBatch b) {
		processedBatchesCount.incrementAndGet();
		int dest=b.getOrigin();
		for(GPU g: GPUs) {
			if(g.getId()==dest) {
				g.addProcessedBatch(b);
				//System.out.println(processedBatchesCount);
				break;
			}
		}
	}
	public void saveModel(String name) {
		modelNames.add(name);
	}
	public Queue<String> getTrainedModel() {return modelNames;}
	public void incPu() {cpUsedTime.incrementAndGet();}
	public AtomicInteger getUsedCpuTime() {return cpUsedTime;}
	public void incGpu() {gpUsedTime.incrementAndGet();}
	public int getUsedGpuTime() {return gpUsedTime.incrementAndGet();}
	public AtomicInteger getBatchCounter() {return this.processedBatchesCount;}
}
