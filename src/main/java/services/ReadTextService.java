package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadTextService {
    public static String readAllFile(String path){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));) {
            StringBuilder textFromFile = new StringBuilder();
            String readedLine = reader.readLine();
            while (readedLine != null){
                textFromFile.append(readedLine);
                readedLine=reader.readLine();
            }
            System.out.println(textFromFile.toString());
            return textFromFile.toString();
        } catch (IOException e){
            System.out.println("Unable to read from path '"+path+"'");
            //e.printStackTrace();
            return "";
        }
    }
}
