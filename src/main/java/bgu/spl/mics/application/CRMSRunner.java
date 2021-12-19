package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;


import java.io.*;
import java.util.LinkedList;
import java.util.List;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {

        Gson gson = new Gson();
        Reader reader = null;
        try{
            reader =new FileReader(args[0]);
        }catch(FileNotFoundException e){
        }
        input inp = new Gson().fromJson(reader,input.class);
        //**************initialize everything*****
        List<Thread> threadList = new LinkedList<Thread>();
        Cluster cluster = Cluster.getInstance();

        for(String s:inp.getGpus())
        {
            GPU gpu = new GPU(GPU.Type.valueOf(s));
            cluster.regGpu(gpu);
            MicroService bitch=new GPUService("my gpu owner id is"+gpu.getId()+"and im his bitch",gpu);
            Thread engine = new Thread(()->bitch.run());
            engine.start();
            threadList.add(engine);
        }

        for(int c:inp.getCpus())
        {
            CPU cpu = new CPU(c);
            cluster.regCpu(cpu);
            CPUService R2D2=new CPUService("im a robot with"+c +" cores", cpu);
            Thread proccessor=new Thread(R2D2::run);
            proccessor.start();
            threadList.add(proccessor);
        }

        for(ConfrenceInformation c:inp.getConfrenceInformations())
        {
            c.init();
            ConferenceService dobbi=new ConferenceService(c.getName()+"'s houseElf",c.getDate(),c);
            Thread conference=new Thread(()->dobbi.run());
            conference.setName("conference");
            conference.start();
            threadList.add(conference);
        }

        for(Student s:inp.getStudents()){
            s.init();
            StudentService servent= new StudentService("myMasterIs"+s.getName(), s.getModels(), s);
            Thread study=new Thread(servent::run);
            study.start();
            threadList.add(study);
        }

        TimeService slave=new TimeService(inp.getTickTime(),inp.getDuration());
        Thread clock = new Thread(()->{slave.run();});
        clock.start();
        threadList.add(clock);
        //***********************
        //******joins
        for(Thread t:threadList){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //*******
        //*********output file**********
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("outputTest.json"));
            writer.write("{ \n");
            //print students
            writer.write("\t\"students\": [");
            int i=0;
            for(Student s:inp.getStudents())
            {
                i++;
                writer.write(" \n \t\t{");
                writer.write("\n \t\t\t \"name\": \""+s.getName()+"\",");
                writer.write("\n \t\t\t \"department\": \""+s.getDepartment()+"\",");
                writer.write("\n \t\t\t \"status\": \""+s.getStatus()+"\",");
                writer.write("\n \t\t\t \"publication:\": \""+s.getPublications()+"\",");
                writer.write("\n \t\t\t \"papersRead:\": \""+s.getPapersRead()+"\",");
                writer.write("\n \t\t\t \"trainedModels:\": [");
                int j=0;
                if(s.getModels().size()==0)
                    writer.write("]");
                else// case models empty
                {
                    for(Model m:s.getModels())
                    {
                        j++;
                        writer.write("\n\t\t\t\t{");
                        writer.write("\n\t\t\t\t\t\"name\": \""+m.getName()+"\",");
                        writer.write("\n\t\t\t\t\t\"data\": {");
                        writer.write("\n\t\t\t\t\t\t\"type\": \""+m.getData().GetType()+"\",");
                        writer.write("\n\t\t\t\t\t\t\"size\": "+m.getData().getSize());
                        writer.write("\n\t\t\t\t\t},");
                        writer.write("\n\t\t\t\t\t\"status\": \""+m.getStatus()+"\",");
                        writer.write("\n\t\t\t\t\t\"result\": \""+m.getResult()+"\"");
                        writer.write("\n\t\t\t\t}");
                        if(j!=s.getModels().size())
                            writer.write(",");

                    }//end of models
                    writer.write("\n \t\t\t]");
                }
                writer.write("\n \t\t}");
                if(i!=inp.getStudents().size())
                    writer.write(",");
            }//end print students
            writer.write("\n\t],");
            //print confrences
            writer.write("\n\t\"conferences\": [");
            i=0;
            for(ConfrenceInformation c:inp.getConfrenceInformations())
            {
                i++;
                writer.write("\n\t\t{");
                writer.write("\n\t\t\t\"name\":" +"\""+c.getName()+"\",");
                writer.write("\n\t\t\t\"date\":" +"\""+c.getDate()+"\",");
                writer.write("\n\t\t\t\"publications\": [");
                int j=0;
                if(c.getSuccessfulModels().size()==0)//case publications empty
                    writer.write("]");
                else
                {
                    for(Model m:c.getSuccessfulModels())//publications
                    {
                        j++;
                        writer.write("\n\t\t\t\t{");
                        writer.write("\n\t\t\t\"name\": \""+m.getName()+"\",");
                        writer.write("\n\t\t\t\"data\": {");
                        writer.write("\n\t\t\t\t\"type\": \""+m.getData().GetType()+"\",");
                        writer.write("\n\t\t\t\t\"size\": "+m.getData().getSize());
                        writer.write("\n\t\t\t\t},");
                        writer.write("\n\t\t\t\t\t\"status\": \""+m.getStatus()+"\",");
                        writer.write("\n\t\t\t\t\t\"result\": \""+m.getResult()+"\"");
                        writer.write("\n\t\t\t\t}");
                        if(j!=c.getSuccessfulModels().size())
                            writer.write(",");
                    }//end of publicatoins
                    writer.write("\n\t\t\t]");
                }

                writer.write("\n\t\t}");
                if(i!=inp.getConfrenceInformations().size())
                    writer.write(",");
            }
            writer.write("\n\t],");
            //CpuTimeUsed
            writer.write("\n\"cpuTimeUsed\": "+ Cluster.getInstance().getUsedCpuTime()+",");
            //GpuTimeUsed
            writer.write("\n\"gpuTimeUsed\": "+ Cluster.getInstance().getUsedGpuTime()+",");
            //BatchesProccessed
           writer.write("\n\"batchesProcessed\": "+ cluster.getBatchCounter());
            writer.write("\n}");
            writer.close();

        }catch (IOException e) {
            e.printStackTrace();
        }


    }

}