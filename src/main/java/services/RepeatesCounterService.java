package services;

import dao.ShingleManager;
import entity.CompareResult;
import entity.Document;
import entity.ShingleDTO;
import exception.DBException;
import exception.ServerException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RepeatesCounterService {

    private ShingleService shingleService;
    private Mode mode;
    private static HashMap<String, Mode> modes;
    private static HashMap<Mode, Integer> shingleSize;
    private static HashMap<Mode, Double> correlationMinimal;

    static {
        modes = new HashMap<>();
        modes.put("1", Mode.SOFT);
        modes.put("2", Mode.MIDDLE);
        modes.put("3", Mode.HARD);

        shingleSize = new HashMap<>();
        shingleSize.put(Mode.HARD, 2);
        shingleSize.put(Mode.MIDDLE, 5);
        shingleSize.put(Mode.SOFT, 10);

        correlationMinimal = new HashMap<>();
        correlationMinimal.put(Mode.SOFT, 0.9);
        correlationMinimal.put(Mode.MIDDLE, 0.7);
        correlationMinimal.put(Mode.HARD, 0.5);
    }

    public RepeatesCounterService(String mode) {
        if (modes.containsKey(mode)) {
            System.out.println("Chosen modes is " + modes.get(mode));
            this.mode = modes.get(mode);
        } else {
            System.out.println("System does not contains target mode. Chosen mode is " + Mode.SOFT);
            this.mode = Mode.SOFT;
        }

    }

    public CompareResult analyzeTwoFiles(String path1, String path2) {
        String text1 = ReadTextService.readAllFile(path1);
        String text2 = ReadTextService.readAllFile(path2);
        if (text1.equals("") || text2.equals("")) {
            System.out.println("One of input files is empty");
            return new CompareResult();
        }
        shingleService = new ShingleService(Math.min((Math.min(text1.split(" ").length, text2.split(" ").length)), shingleSize.get(this.mode)));
        List<Integer> file1 = shingleService.genShingle(text1);
        List<Integer> file2 = shingleService.genShingle(text2);
        CompareResult compareResult = new CompareResult();
        compareResult.setClassic(shingleService.compare(file1, file2));
        compareResult.setCorrelation(shingleService.compareByCorrelation(file1, file2));
        compareResult.setDuplicates(compareResult.getCorrelation() >= correlationMinimal.get(this.mode));
        /*System.out.println("For these two files:");
        visuaizeCodeArrays(file1,file2);*/
        return compareResult;
    }

    public boolean serializeText(String path) throws ServerException, SQLException, DBException {
        String text = ReadTextService.readAllFile(path);
        if (text.length() == 0) {
            System.out.println("Due to file is empty, unable to serialize");
            return false;
        }
        int wordsCount = new ShingleService(0).canonize(text).split(" ").length;
        if (wordsCount == 0) {
            System.out.println("File is empty or not exists");
            return false;
        }
        ShingleManager shingleManager = ShingleManager.getInstance();
        shingleManager.createDoc(path, wordsCount);
        Document doc = shingleManager.getDocumentByPath(path);
        System.out.println(doc.getId());
        for (int i = 1; i <= wordsCount; ++i) {
            shingleService = new ShingleService(i);
            List<Integer> shingles = shingleService.genShingle(text);
            shingleManager.serializeDocument(shingles, doc.getId(), i);
        }
        return true;
    }

    public List<CompareResult> compareWithBase(String path) throws ServerException, SQLException, DBException {
        String text = ReadTextService.readAllFile(path);
        List<CompareResult> results = new ArrayList<>();
        if (text.isEmpty()) {
            return results;
        }
        ShingleManager shingleManager = ShingleManager.getInstance();
        List<Document> docs = shingleManager.getDocumentList();
        System.out.println("Target documents from database: ");
        for (Document document : docs) {
            System.out.println(document.getId() + " : " + document.getPath());
        }
        Document checkedDoc = shingleManager.getDocumentByPath(path);
        if (checkedDoc.getId() != 0) {
            System.out.println("Document is serialized");
            for (Document doc : docs) {
                int shingleServiceWordCount = Math.min(shingleSize.get(this.mode), Math.min(doc.getWordsCount(), checkedDoc.getWordsCount()));
                shingleService = new ShingleService(shingleServiceWordCount);
                List<ShingleDTO> shinglesForTarget = shingleManager.getShinglesForDocId(doc.getId(), shingleServiceWordCount);
                if (shinglesForTarget.size() < doc.getWordsCount() - shingleServiceWordCount) {
                    System.out.println("We're sorry, but data of current document can be damaged. We will resynchronize it and continue analysis. Thank you for your patience");
                    serializeText(doc.getPath());
                    shinglesForTarget = shingleManager.getShinglesForDocId(doc.getId(), shingleServiceWordCount);
                }
                List<ShingleDTO> shinglesForChecked = shingleManager.getShinglesForDocId(checkedDoc.getId(), shingleServiceWordCount);
                if (shinglesForChecked.size() < doc.getWordsCount() - shingleServiceWordCount) {
                    System.out.println("We're sorry, but data of current document can be damaged. We will resynchronize it and continue analysis. Thank you for your patience");
                    serializeText(doc.getPath());
                    shinglesForChecked = shingleManager.getShinglesForDocId(doc.getId(), shingleServiceWordCount);
                }
                List<Integer> shingleValuesForTarget = shingleService.extractShingleValuesFromDTO(shinglesForTarget);
                List<Integer> shingleValuesForChecked = shingleService.extractShingleValuesFromDTO(shinglesForChecked);
                CompareResult compareResult = new CompareResult();
                compareResult.setClassic(shingleService.compare(shingleValuesForChecked, shingleValuesForTarget));
                compareResult.setCorrelation(shingleService.compareByCorrelation(shingleValuesForChecked, shingleValuesForTarget));
                compareResult.setDuplicates(compareResult.getCorrelation() >= correlationMinimal.get(this.mode));
                compareResult.setComparedTo(String.valueOf(doc.getId()));
                results.add(compareResult);
            }
        } else {
            System.out.println("Document is not serialized");
            text = ReadTextService.readAllFile(path);
            int wordsCount = new ShingleService(0).canonize(text).split(" ").length;
            if (wordsCount == 0) {
                System.out.println("File is empty or not exists");
                return results;
            }
            for (Document doc : docs) {
                int shingleServiceWordCount = Math.min(Math.min(doc.getWordsCount(), wordsCount), shingleSize.get(this.mode));
                shingleService = new ShingleService(shingleServiceWordCount);
                List<ShingleDTO> shinglesForSerialized = shingleManager.getShinglesForDocId(doc.getId(), shingleServiceWordCount);
                if (shinglesForSerialized.size() < doc.getWordsCount() - shingleServiceWordCount) {
                    System.out.println("We're sorry, but data of current document can be damaged. We will resynchronize it and continue analysis. Thank you for your patience");
                    serializeText(doc.getPath());
                    shinglesForSerialized = shingleManager.getShinglesForDocId(doc.getId(), shingleServiceWordCount);
                }
                List<Integer> shingleValuesForSerialized = shingleService.extractShingleValuesFromDTO(shinglesForSerialized);
                List<Integer> shingleValuesForNotSerialized = shingleService.genShingle(text);
                CompareResult compareResult = new CompareResult();
                compareResult.setClassic(shingleService.compare(shingleValuesForNotSerialized, shingleValuesForSerialized));
                compareResult.setCorrelation(shingleService.compareByCorrelation(shingleValuesForNotSerialized, shingleValuesForSerialized));
                compareResult.setDuplicates(compareResult.getCorrelation() >= correlationMinimal.get(this.mode));
                compareResult.setComparedTo(String.valueOf(doc.getId()));
                results.add(compareResult);
            }
        }
        return results;
    }
    private void visuaizeCodeArrays(List<Integer> list1, List<Integer> list2){
        int size=Math.max(list1.size(),list2.size());
        int minimal=Integer.MAX_VALUE;
        int maximal=Integer.MIN_VALUE;
        for (Integer v1:list1){
            System.out.print(v1+" ");
            minimal=Math.min(minimal,v1);
        }
        System.out.println();
        for (Integer v2:list2){
            System.out.print(v2+" ");
            minimal=Math.min(minimal,v2);
        }
        System.out.println();
        for (Integer v1:list1){
            v1-=minimal;
            System.out.print(v1+" ");
            maximal=Math.max(maximal,v1);
        }
        System.out.println();
        for (Integer v2:list2){
            v2-=minimal;
            System.out.print(v2+" ");
            maximal=Math.max(maximal,v2);
        }
        System.out.println();
        System.out.println(minimal+" "+size+"X"+maximal);
        int[][] graphic = new int[size][maximal];
        int i=-1;
        for (Integer v1:list1){
            graphic[++i][v1]=1;
        }
        i=-1;
        for (Integer v2:list2){
            graphic[++i][v2]=1;
        }
        for (i=0; i<size;++i){
            for (int j=0; j<maximal; ++j){
                System.out.println(graphic[i][j]==1?".":" ");
            }
            System.out.println();
        }
    }
}
