package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CPUService;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

	private Cluster cluster=Cluster.getInstance();
	private int cores;
	private DataBatch currentToProccess=null;
	private int Speed;
	

	public CPU(int coresNum){
		this.cores=coresNum;
		this.Speed = 32/cores;
		init();
	}

	public void init(){
		CPUService R2D2=new CPUService("im a robot with"+cores +" cores", this);
		Thread proccessor=new Thread(R2D2::run);
		proccessor.start();
		//R2D2.run();
	}

	public synchronized void proccessAndSend() {
		Data.Type dataType;
		/*if(cluster.hasData()|| currentToProccess!=null)//case there's no loaded Data
		{
			System.out.println("proccessing...");
			cluster.incPu();
			if (currentToProccess == null) {
				currentToProccess = cluster.getNextData(cores);	
			}
			dataType = currentToProccess.getData().GetType();
			currentToProccess.increaseTick();
			if (dataType == Data.Type.Images){
				if (currentToProccess.geTicks() == Speed*4) {
					currentToProccess.resetTick();
					cluster.passToGpu(currentToProccess);
					System.out.println("proccessed!");
					currentToProccess=null;
	
				}

			}

			if (dataType == Data.Type.Text){
				if (currentToProccess.geTicks() == Speed*2) {
					currentToProccess.resetTick();
					cluster.passToGpu(currentToProccess);
					currentToProccess=null;
				}
			}

			if (dataType == Data.Type.Tabular){
				if (currentToProccess.geTicks() == Speed) {
					currentToProccess.resetTick();
					cluster.passToGpu(currentToProccess);
					currentToProccess=null;
				}
			}
		}*/
		if (currentToProccess == null) {
			if (cluster.hasData()) {
				currentToProccess = cluster.getNextData(cores);
			}
		}
		if (currentToProccess != null) {
			cluster.incPu();
			dataType = currentToProccess.getData().GetType();
			currentToProccess.increaseTick();
			//System.out.println("proccessed!");
			if (dataType == Data.Type.Images) {
				if (currentToProccess.geTicks() == Speed * 4) {
					currentToProccess.resetTick();
					cluster.passToGpu(currentToProccess);
					currentToProccess = null;
				}
			}

			if (dataType == Data.Type.Text) {
				if (currentToProccess.geTicks() == Speed * 2) {
					currentToProccess.resetTick();
					cluster.passToGpu(currentToProccess);
					currentToProccess = null;
				}
			}

			if (dataType == Data.Type.Tabular) {
				if (currentToProccess.geTicks() == Speed) {
					currentToProccess.resetTick();
					cluster.passToGpu(currentToProccess);
					currentToProccess = null;
				}
			}
		}
	}

}

