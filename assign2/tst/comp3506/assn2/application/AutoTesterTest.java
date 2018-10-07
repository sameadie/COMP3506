package comp3506.assn2.application;

public class AutoTesterTest {
    public static void main(String[] args) {
        try {
            final AutoTester autoTester = new AutoTester("shakespeare_extract.txt", "", "");
            //autoTester.printTrieOccurences();
            //ArrayList<String> words = new ArrayList<>(Arrays.asList(new String[] {"this", "test", "construction"}));
            //words.forEach(word -> {
            //    System.out.print(String.format("%s -> {", word));
            //    System.out.println(autoTester.phraseOccurrence(word).toString());
            //    autoTester.phraseOccurrence(word).forEach(phrase -> {
            //        System.out.print(String.format("(%d, %d), ", phrase.getLeftValue(), phrase.getRightValue()));
            //    });
            //    System.out.println("}");
            //});
            //autoTester.prefixOccurrence("the").forEach(occurence -> {
            //    System.out.println(String.format("(%d, %d)", occurence.getLeftValue(), occurence.getRightValue()));
            //});
            String[] words = new String[]{"thee", "I"};
            String[] notWords = new String[]{"this"};
            autoTester.wordsNotOnLine(words, notWords);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
