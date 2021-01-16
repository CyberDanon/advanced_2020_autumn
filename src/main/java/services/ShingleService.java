package services;

import entity.ShingleDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ShingleService {

    private static final String[] STOP_SYMBOLS = {".", ",", "!", "?", ":", ";", "-", "\\", "/", "*", "(", ")", "+", "@", "#", "$", "%", "^", "&", "=", "'", "\"", "[", "]", "{", "}", "|", "\\n\\r"};
    private static final String[] STOP_WORDS_RU = {"это", "как", "так", "и", "в", "над", "к", "до", "не", "на", "но", "за", "то", "с", "ли", "а", "во", "от", "со", "для", "о", "же", "ну", "вы", "бы", "что", "кто", "он", "она"};

    private final int shingleLen;

    public ShingleService(int shingleLen) {
        this.shingleLen = shingleLen;
    }

    public String canonize(String str) {
        for (String stopSymbol : STOP_SYMBOLS) {
            str = str.replace(stopSymbol, "");
        }

        for (String stopWord : STOP_WORDS_RU) {
            str = str.replace(" " + stopWord + " ", " ");
        }

        return str;
    }

    public List<Integer> genShingle(String strNew) {
        ArrayList<Integer> shingles = new ArrayList<Integer>();
        String str = canonize(strNew.toLowerCase());
        String[] words = str.split(" ");
        int shinglesNumber = words.length - shingleLen;

        for (int i = 0; i <= shinglesNumber; i++) {
            StringBuilder shingle = new StringBuilder();

            for (int j = 0; j < shingleLen; j++) {
                shingle.append(words[i + j]).append(" ");
            }

            shingles.add(shingle.toString().hashCode());
        }

        return shingles;
    }

    public double compare(List<Integer> shingles1, List<Integer> shingles2) {
        if (shingles1 == null || shingles2 == null) {
            return 0.0;
        }

        long shingles1Size = shingles1.size();
        long shingles2Size = shingles2.size();

        double similarShinglesNumber = 0;
        long start = System.currentTimeMillis();
        for (Integer integer : shingles1) {
            for (Integer value : shingles2) {
                if (integer.equals(value)) {
                    similarShinglesNumber++;
                }
            }
        }
        System.out.println("Classic analysis finished, time took: " + (System.currentTimeMillis() - start) + " milliseconds");
        return Math.min(100, ((similarShinglesNumber / ((shingles1Size + shingles2Size) / 2.0)) * 100));
    }

    public double compareByCorrelation(List<Integer> source1, List<Integer> source2) {
        ArrayList<Double> textShinglesLocal1 = new ArrayList<>();
        for (Integer val : source1) {
            textShinglesLocal1.add(Double.valueOf(val));
        }
        textShinglesLocal1.sort(new Comparator<Double>() {

            @Override
            public int compare(Double o1, Double o2) {
                return (int) (o1 - o2);
            }
        });
        ArrayList<Double> textShinglesLocal2 = new ArrayList<>();
        for (Integer val : source2) {
            textShinglesLocal2.add(Double.valueOf(val));
        }
        textShinglesLocal2.sort(new Comparator<Double>() {

            @Override
            public int compare(Double o1, Double o2) {
                return (int) (o1 - o2);
            }
        });
        Double sumTextShingles1 = 0d, sumTextShingles2 = 0d, avgTextShingles1 = 0d, avgTextShingles2 = 0d;
        long start = System.currentTimeMillis();
        long size = Math.min(source1.size(), source2.size());
        for (int i = 0; i < size; ++i) {
            sumTextShingles1 += textShinglesLocal1.get(i);
            sumTextShingles2 += textShinglesLocal1.get(i);
        }
        avgTextShingles2 = sumTextShingles2 / size;
        avgTextShingles1 = sumTextShingles1 / size;
        double sqrDeviationTextShingles1 = 0d, sqrDeviationTextShingles2 = 0d, fullDeviation = 0d;
        for (int i = 0; i < size; ++i) {
            textShinglesLocal1.set(i, avgTextShingles1 - textShinglesLocal1.get(i));
            sqrDeviationTextShingles1 += Math.pow(textShinglesLocal1.get(i), 2);
            textShinglesLocal2.set(i, avgTextShingles2 - textShinglesLocal2.get(i));
            sqrDeviationTextShingles2 += Math.pow(textShinglesLocal2.get(i), 2);
            fullDeviation += textShinglesLocal1.get(i) * textShinglesLocal2.get(i);
        }
        System.out.println("Correlation analysis finished, time took: " + (System.currentTimeMillis() - start) + " milliseconds");
        if ((fullDeviation == 0) || (sqrDeviationTextShingles2 == 0) || (sqrDeviationTextShingles1 == 0)) {
            return 0;
        }
        return fullDeviation / (Math.sqrt(sqrDeviationTextShingles2 * sqrDeviationTextShingles1));
    }

    public List<Integer> extractShingleValuesFromDTO(List<ShingleDTO> shingles) {
        List<Integer> values = new ArrayList<>();
        for (ShingleDTO shingleDTO : shingles) {
            values.add(shingleDTO.getShingleText());
        }
        return values;
    }
}