package comp3506.assn2.application;

import comp3506.assn2.utils.Pair;

public class AutoTesterTest {

    public static void main(String[] args) {
        try {
            AutoTester autoTester = new AutoTester("shakespeare_extract.txt", "", "");
            autoTester.phraseOccurrence("fresh").forEach(phrase -> {
                System.out.println(String.format("(%d, %d)", phrase.getLeftValue(), phrase.getRightValue()));
            });
        } catch (Exception e) {
            System.out.println("Shit fucked up, go back!");
        }

    }
}
