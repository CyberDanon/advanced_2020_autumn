package controller.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class App {

    /**
     * System.in reader
     */
    private static InputStream inputStream = System.in;
    private static Reader inputStreamReader = new InputStreamReader(inputStream);
    private static BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

    private static HashMap<Integer, Command> commands;

    static {
        commands = new HashMap<>();
        commands.put(1, new CompareTwoFilesCommand());
        commands.put(2, new Serialize());
        commands.put(3, new CompareWithBaseCommand());
    }

    public static void main(String[] args) {
        int cmd = 0;
        do {
            hr();
            System.out.println("Put in one of below commands");
            for (Map.Entry<Integer,Command> entry:commands.entrySet()){
                System.out.println(entry.getKey()+" : "+entry.getValue());
            }
            System.out.println("-1 : Exit system");
            hr();
            cmd = getCmd();
            Command command = getPerfomance(cmd);
            command.action(bufferedReader);
        } while (cmd != -1);
        System.out.println("Have a nice day, bye");
    }

    private static int getCmd() {
        try {
            String cmd = bufferedReader.readLine();
            return Integer.parseInt(cmd);
        } catch (IOException e) {
            System.out.println("Unable to read command, try again");
        } catch (NumberFormatException e) {
            System.out.println("Wrong command format");
        }
        return 0;
    }

    private static Command getPerfomance(int key) {
        if (commands.containsKey(key)) {
            return commands.get(key);
        } else {
            return new NoSuchActionCommand();
        }
    }
    private static void hr(){
        System.out.println("================================================");
    }
}
