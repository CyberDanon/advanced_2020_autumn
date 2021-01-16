package controller.console;

import java.io.BufferedReader;

public class NoSuchActionCommand extends Command{

    @Override
    public void action(BufferedReader bufferedReader) {
        System.out.println("No such action, try again!");
    }
}
