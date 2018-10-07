package comp3506.assn2.application;

public class AutoTesterTest {
    public static void main(String[] args) {
        try {
            final AutoTester autoTester = new AutoTester("files/shakespeare.txt", "files/shakespeare-index.txt", "");
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
//            String [] titles = {"CYMBELINE", "THE TRAGEDY OF HAMLET", "THE LIFE OF KING HENRY THE FIFTH",
//                    "THE FIRST PART OF KING HENRY THE SIXTH", "THE SECOND PART OF KING HENRY THE SIXTH",
//                    "KING RICHARD THE SECOND", "VENUS AND ADONIS"};
//            String [] requiredWords = {"obscure"};
//            String [] orWords = {"beaver", "hoof"};
//            List<TestingTriple<Integer,Integer,String>> expected = Arrays.asList(new TestingTriple<>(23709,29,"beaver"),    // Hamlet
//                    new TestingTriple<>(27960,25,"obscure"),
//                    new TestingTriple<>(148012,31,"obscure"),  // Venus and Adonis
//                    new TestingTriple<>(148047,33,"hoof"));
//            autoTester.compoundAndOrSearch(titles, requiredWords, orWords);
            //System.out.println(autoTester.sectionIndexes.get("THE FIRST PART OF HENRY THE SIXTH".toLowerCase()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
