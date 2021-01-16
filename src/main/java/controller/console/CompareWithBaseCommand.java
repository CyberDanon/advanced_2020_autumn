package controller.console;

import entity.CompareResult;
import exception.DBException;
import exception.ServerException;
import services.RepeatesCounterService;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CompareWithBaseCommand extends Command {
    public CompareWithBaseCommand(){
        name = "Compare with base";
    }
    @Override
    public void action(BufferedReader bufferedReader) {
        try {
            System.out.println("Put in file full qualified path");
            String path = bufferedReader.readLine();
            System.out.println("Select mode: 1 - Soft, 2 - Middle, 3 - Hard");
            String mode = bufferedReader.readLine();
            RepeatesCounterService repeatesCounterService = new RepeatesCounterService(mode);
            List<CompareResult> resultList = repeatesCounterService.compareWithBase(path);
            System.out.println("Files analysis results:");
            for (CompareResult compareResult : resultList) {
                System.out.println();
                System.out.println("Compared to: "+compareResult.getComparedTo());
                System.out.print("Classic analysis result: " + compareResult.getClassic() + "%, ");
                System.out.println("Correlation: " + compareResult.getCorrelation()*100 + "%, ");
                System.out.println("Duplication analysis results(can texts be defined as duplicates): " + compareResult.isDuplicates());
            }
        } catch (IOException e) {
            System.out.println("Read is unable, try again later");
        } catch (DBException | SQLException | ServerException e) {
            e.printStackTrace();
        }
    }
}
