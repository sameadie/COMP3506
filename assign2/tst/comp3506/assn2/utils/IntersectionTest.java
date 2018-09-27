package comp3506.assn2.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class IntersectionTest  {

    private ArrayList<ArrayList<Pair<Integer, Integer>>> occurences;
    private String filename = "files/shakespeare_extract.txt";
    private BufferedReader documentReader;


    @Test
    public void getIntersections() throws Exception {
        String[] words = new String[]{"thee", "i", "this"};
        documentReader = new BufferedReader(new FileReader(filename));
        OccurenceTrie documentTrie = OccurenceTrie.formTrieFromFile(documentReader);
        documentReader.close();

        this.occurences = new ArrayList<>(words.length);

        for(int i = 0; i < words.length; i++) {
            this.occurences.append(documentTrie.getOccurences(words[i]));
        }

        java.util.ArrayList<Integer> intersections = Intersection.getIntersections(this.occurences).toJavaArrayList();
        System.out.println(intersections.toString());

        //Reopen file
        documentReader = new BufferedReader(new FileReader("files/shakespeare_extract.txt"));
        String line;
        int lineNumber = 1;
        List<String> lineWords;

        while((line = documentReader.readLine()) != null) {
            line = line.toLowerCase();
            line = line.replaceAll("[^a-z0-9()' ]", "");
            lineWords = Arrays.asList(line.split(" "));

            boolean containsAll = true;
            //Check all lines in intersection contain all specified words and all other lines dont
            for(int i = 0; i < words.length; i++) {
                if (!lineWords.contains(words[i])) {
                    containsAll = false;
                    break;
                }
            }

            //if(intersections.toJavaArrayList().contains(lineNumber) != containsAll) {
            //    System.out.println(String.format("Discrepancy on line '%s': %d", line, lineNumber));
            //}

            Assert.assertEquals(intersections.contains(lineNumber), containsAll);

            lineNumber++;
        }
        documentReader.close();
    }

    @Test
    public void getUnions() throws Exception {
        String[] words = new String[]{"dyer's", "askance", "quenched"};
        documentReader = new BufferedReader(new FileReader(filename));
        OccurenceTrie documentTrie = OccurenceTrie.formTrieFromFile(documentReader);
        documentReader.close();

        this.occurences = new ArrayList<>(words.length);

        for(int i = 0; i < words.length; i++) {
            this.occurences.append(documentTrie.getOccurences(words[i]));
        }

        java.util.ArrayList<Integer> unions = Intersection.getUnion(this.occurences).toJavaArrayList();
        System.out.println(unions.toString());

        //Reopen file
        documentReader = new BufferedReader(new FileReader("files/shakespeare_extract.txt"));
        String line;
        int lineNumber = 1;
        List<String> lineWords;

        while((line = documentReader.readLine()) != null) {
            line = line.toLowerCase();
            line = line.replaceAll("[^a-z0-9()' ]", "");
            lineWords = Arrays.asList(line.split(" "));

            boolean oneEqual = false;

            //Check all lines in union contain at least one of the words
            for(int i = 0; i < words.length; i++) {
                if(lineWords.contains(words[i])) {
                    oneEqual = true;
                    break;
                }
            }

            Assert.assertEquals(unions.contains(lineNumber), oneEqual);

            lineNumber++;
        }
        documentReader.close();
    }

}