package controller.console;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public abstract class Command {
    protected String name;
    /**
     * System.in reader
     */
    public abstract void action(BufferedReader bufferedReader);
    public String toString(){
        return name;
    };
}
