package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;
    
    public Data(Type t,int s) {
    	this.type=t;
    	this.size=s;
    	this.processed=0;
    }
    
    public Type GetType() {
    	return this.type;
    }
    
    public int getProcessed() {
    	return this.processed;
    }
    
    public int getSize() {
    	return this.size;
    }
}
