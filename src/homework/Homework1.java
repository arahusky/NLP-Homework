package homework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Source code for computing entropy (First task of the homework).
 *
 * The results are printed into results1.html file, which contains all most
 * important information computed.
 *
 * @author Jakub Naplava
 */
public class Homework1 {

    private static final String englishText = "TEXTEN1.txt";
    private static final String czechText = "TEXTCZ1.txt";

    public static void main(String[] args) throws IOException, CloneNotSupportedException {

        PrintWriter writer = new PrintWriter(new File("results1.html"));
        writer.println("<h1> Homework NLP </h1> ");

        //measureEntropyAndMessUp(englishText, writer, "English");
        measureEntropyAndMessUp(czechText, writer, "Czech");

        /*printTextStatistics(englishText);
         printTextStatistics(czechText);*/
        writer.close();
    }

    /**
     * Measures entropy of given text, messes it up and measures entropy again.
     * The results are printed as html file using given writer.
     */
    private static void measureEntropyAndMessUp(String fileName, PrintWriter writer, String textLanguage) throws IOException, CloneNotSupportedException {
        //parse text and count its entropy
        Text text = new Text(new FileInputStream(fileName));
        double entropy = countEntropy(text);

        writer.println("<h2>" + textLanguage + " text</h2>");
        writer.println("Original conditional entropy of word distribution is <b>" + entropy + "</b><br />");
        writer.println("Original perplexity is <b>" + Math.pow(2, entropy) + "</b>");

        List<Double> messUpProbs = Arrays.asList(0.1, 0.05, 0.01, 0.001, 0.0001, 0.00001);
        int repCount = 10;

        //first messing up characters 
        writer.println("<h3> Character messups</h3>");
        writer.println("<table border=\"1\"><thead><th>Messup</th><th>Min entropy</th><th>Average entropy</th><th>Max entropy</th><tbody>");
        for (double messUpProb : messUpProbs) {
            List<Double> entropyValues = new ArrayList<>();
            for (int repNum = 0; repNum < repCount; repNum++) {
                Text messedText = text.clone();
                messedText.messUp(0, messUpProb);
                entropy = countEntropy(messedText);
                entropyValues.add(entropy);
                System.out.println(entropy);
            }
            printStats(writer, messUpProb, entropyValues);
        }
        writer.println("</tbody></table>");

        //then messing up whole words 
        writer.println("<h3> Words messups</h3>");
        writer.println("<table border=\"1\"><thead><th>Messup</th><th>Min entropy</th><th>Average entropy</th><th>Max entropy</th><tbody>");
        for (double messUpProb : messUpProbs) {
            List<Double> entropyValues = new ArrayList<>();
            for (int repNum = 0; repNum < repCount; repNum++) {
                Text messedText = text.clone();
                messedText.messUp(messUpProb, 0);
                entropy = countEntropy(messedText);
                entropyValues.add(entropy);
                System.out.println(entropy);
            }
            printStats(writer, messUpProb, entropyValues);
        }
        writer.println("</tbody></table>");

    }

    /**
     * Counts entropy of given text.
     */
    public static double countEntropy(Text text) {
        ProbabilityContainer probs = new ProbabilityContainer(text);
        EntropyCounter entropyCounter = new EntropyCounter(probs);

        double entropy = 0;
        Set<Pair> pairs = probs.getDistinctPairs();
        for (Pair pair : pairs) {
            entropy += entropyCounter.getConditionalEntropy(pair.second, pair.first);
        }

        return entropy;
    }

    /**
     * Prints statistics (i.e. min, max and avg. entropy) about each 'messuped'
     * measurement.
     */
    private static void printStats(PrintWriter writer, double messupProb, List<Double> entropyValues) {
        writer.println("<tr>");
        writer.println("<td>" + (messupProb * 100) + "%</td>");

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double avg = 0;

        for (double val : entropyValues) {
            if (val < min) {
                min = val;
            }
            if (val > max) {
                max = val;
            }
            avg += val;
        }

        avg /= entropyValues.size();

        writer.println("<td>" + min + "</td>");
        writer.println("<td>" + avg + "</td>");
        writer.println("<td>" + max + "</td>");

        writer.println("</tr>");
    }

    /**
     * Prints statistics of given text. This method is used to have better
     * insight into the difference of the Czech and the English text.
     */
    private static void printTextStatistics(String fileName) throws IOException {
        Text text = new Text(new FileInputStream(fileName));

        Text.TextStatistics stats = text.getTextStatistics();

        System.out.println("Stats for " + fileName);
        System.out.println("Number of words " + stats.getWordCount());
        System.out.println("Number of unique words " + stats.getNumUniqueWords());
        System.out.println("Number of character " + stats.getTotalCharacters());
        System.out.println("Average word length " + stats.getAvgCharacterPerWord());

        System.out.println("Number of words with frequency 1: " + stats.getNumWordsWithGivenFrequency(1));
        System.out.println("Number of words with frequency 2: " + stats.getNumWordsWithGivenFrequency(2));
        System.out.println("Number of words with frequency 3: " + stats.getNumWordsWithGivenFrequency(3));
    }
}
