package controller.console;

import dao.ShingleManager;
import entity.CompareResult;
import exception.DBException;
import exception.ServerException;
import services.RepeatesCounterService;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Serialize extends Command {

    public Serialize() {
        name = "Serialize text to database";
    }

    @Override
    public void action(BufferedReader bufferedReader) {
        try {
            System.out.println("Put in file full qualified path");
            String path = bufferedReader.readLine();
            RepeatesCounterService repeatesCounterService = new RepeatesCounterService("");
            if (ShingleManager.getInstance().getDocumentByPath(path).getId() != 0) {
                System.out.println("Document is already serialized");
                return;
            }
            if (repeatesCounterService.serializeText(path)) {
                System.out.println("Text from path was successfully serialized");
            } else {
                System.out.println("Serialization is unable, try again later");
            }
        } catch (IOException e) {
            System.out.println("Read is unable, try again later");
        } catch (DBException | SQLException | ServerException e) {
            e.printStackTrace();
        }
    }
}
