package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.input;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

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
        inp.initialaizer();
    }

}