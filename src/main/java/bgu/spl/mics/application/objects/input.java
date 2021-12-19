package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.TimeService;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
public class input {



    @SerializedName("Students")
    @Expose
    private List<Student> students = null;
    @SerializedName("GPUS")
    @Expose
    private List<String> gpus = null;
    @SerializedName("CPUS")
    @Expose
    private List<Integer> cpus = null;
    @SerializedName("Conferences")
    @Expose
    private List<ConfrenceInformation> conferences = null;
    @SerializedName("TickTime")
    @Expose
    private Integer tickTime;
    @SerializedName("Duration")
    @Expose
    private Integer duration;

    private List<GPU> Ogpus;

    private List<CPU> Ocpus;

    private Cluster cluster=Cluster.getInstance();

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<String> getGpus() {
        return gpus;
    }

    public void setGpus(List<String> gpus) {
        this.gpus = gpus;
    }

    public List<Integer> getCpus() {
        return cpus;
    }

    public void setCpus(List<Integer> cpus) {
        this.cpus = cpus;
    }

    public List<ConfrenceInformation> getConfrenceInformations() {
        return conferences;
    }

    public void setConfrenceInformations(List<ConfrenceInformation> conferences) {
        this.conferences = conferences;
    }

    public Integer getTickTime() {
        return tickTime;
    }

    public void setTickTime(Integer tickTime) {
        this.tickTime = tickTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void initialaizer(){
        for(String s:gpus)
        {
            cluster.regGpu(new GPU(GPU.Type.valueOf(s)));
        }
        for(int c:cpus)
        {
            cluster.regCpu(new CPU(c));
        }

        for(ConfrenceInformation c:conferences)
            c.init();
        for(Student s:students){
            s.init();}
        TimeService slave=new TimeService(tickTime,duration);
        Thread clock = new Thread(()->{slave.run();});
        clock.start();
    }
}
