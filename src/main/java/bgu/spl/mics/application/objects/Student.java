package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.StudentService;

import java.util.List;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private List<Model> models;
    
    public Student(String name,String dep,Degree stat,List<Model> l){
    	this.name=name;
    	this.department=dep;
    	this.status = stat;
    	this.models=l;
    	this.publications=0;
    	this.papersRead=0;

    }
    public void init(){
        for(Model m:models){
            m.setData();
        }
        StudentService servent= new StudentService("myMasterIs"+name, models, this);
        Thread study=new Thread(servent::run);
        study.start();
    }
    public String getName() {
    	return this.name;
    }
    public String getDepartment() {
    	return this.department;
    }
    public Degree getStatus() {
    	return this.status;
    }
    public int getPublications() {
    	return this.publications;
    }
    public void setPublications(int p) {publications=p;}
    public void setPapersRead(int r) {papersRead=r;}
    public int getPapersRead() {
    	return this.papersRead;
    }
}
