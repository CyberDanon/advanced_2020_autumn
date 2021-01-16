package controller.console;


import controller.console.Command;
import entity.CompareResult;
import services.RepeatesCounterService;

import java.io.BufferedReader;
import java.io.IOException;

public class CompareTwoFilesCommand extends Command {

    public CompareTwoFilesCommand() {
        name = "Compare two files";
    }

    @Override
    public void action(BufferedReader bufferedReader) {
        try {
            System.out.println("Put in first file full qualified path");
            String path1 = bufferedReader.readLine();
            System.out.println("Put in second file full qualified path");
            String path2 = bufferedReader.readLine();
            System.out.println("Select mode: 1 - Soft, 2 - Middle, 3 - Hard");
            String mode = bufferedReader.readLine();
            RepeatesCounterService repeatesCounterService = new RepeatesCounterService(mode);
            CompareResult compareResult = repeatesCounterService.analyzeTwoFiles(path1, path2);
            System.out.println("Files analysis results:");
            System.out.print("Classic analysis result: " + compareResult.getClassic() + "%, ");
            System.out.println("Correlation: " + compareResult.getCorrelation()*100 + "%, ");
            System.out.println("Duplication analysis results(can texts be defined as duplicates): " + compareResult.isDuplicates());
        } catch (IOException e) {
            System.out.println("Read is unable, try again later");
        }
    }
}
