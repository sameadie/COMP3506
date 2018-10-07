package comp3506.assn2.utils;

import org.junit.Assert;
import org.junit.Test;

public class IntersectionTest {

    private ArrayList<HashPair<Integer, Integer>> getOccurrencesWithLineNumbers(Integer[] lineNumbers) {
        ArrayList<HashPair<Integer, Integer>> occurrences = new ArrayList<>(lineNumbers.length);
        for (int i = 0; i < lineNumbers.length; i++) {
            occurrences.append(new HashPair<>(lineNumbers[i], 1));
        }
        return occurrences;
    }

    private boolean isLineNumberInOccurrences(ArrayList<HashPair<Integer, Integer>> occurrences, Integer lineNumber) {
        for (int i = 0; i < occurrences.size(); i++) {
            if (occurrences.get(i).getLeftValue().equals(lineNumber)) {
                return true;
            }
        }

        return false;
    }

    private void testOccurrencesForIntersection(ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences) {
        System.out.println("Finding intersection of : ");

        for(int i = 0; i < occurrences.size(); i++) {
            System.out.println(occurrences.get(i).toJavaArrayList().toString());
        }

        ArrayList<Integer> intersection = Intersection.getIntersections(occurrences);
        ArrayList<Integer> intersectionCheck = new ArrayList<>();

        for (int i = 0; i < occurrences.get(0).size(); i++) {
            Integer lineNumber = occurrences.get(0).get(i).getLeftValue();
            boolean inAll = true;
            for (int j = 1; j < occurrences.size(); j++) {
                if (!isLineNumberInOccurrences(occurrences.get(j), lineNumber)) {
                    inAll = false;
                    break;
                }
            }

            if (inAll && (!intersectionCheck.contains(lineNumber))) {
                intersectionCheck.append(lineNumber);
            }

        }

        System.out.println("Test: " + intersection.toJavaArrayList().toString());
        System.out.println("Comp: " + intersectionCheck.toJavaArrayList().toString());

        Assert.assertEquals(intersection.size(), intersectionCheck.size());
        for (int i = 0; i < intersectionCheck.size(); i++) {
            Assert.assertTrue(intersection.contains(intersectionCheck.get(i)));
        }


    }

    private void testOccurrencesForUnion(ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences) {
        System.out.println("Finding union of: ");

        for(int i = 0; i < occurrences.size(); i++) {
            System.out.println(occurrences.get(i).toJavaArrayList().toString());
        }

        ArrayList<Integer> unionCheck = new ArrayList<>();
        for (int i = 0; i < occurrences.size(); i++) {
            for (int j = 0; j < occurrences.get(i).size(); j++) {
                Integer lineNumber = occurrences.get(i).get(j).getLeftValue();
                if (!unionCheck.contains(lineNumber)) {
                    unionCheck.append(lineNumber);
                }
            }
        }

        ArrayList<Integer> union = Intersection.getUnion(occurrences);

        System.out.println("Test: " + union.toJavaArrayList().toString());
        System.out.println("Comp: " + unionCheck.toJavaArrayList().toString());

        Assert.assertEquals(union.size(), unionCheck.size());
        for (int i = 0; i < unionCheck.size(); i++) {
            Assert.assertTrue(union.contains(unionCheck.get(i)));
        }
    }

    private void testOccurrencesForNot(ArrayList<Integer> occurences, ArrayList<ArrayList<HashPair<Integer, Integer>>> notOccurrences) {

        System.out.println("Removing : ");
        for(int i = 0; i < notOccurrences.size(); i++) {
            System.out.println(notOccurrences.get(i).toJavaArrayList().toString());
        }

        System.out.println("from ");

        System.out.println(occurences.toJavaArrayList().toString());

        ArrayList<Integer> notCheck = new ArrayList<>();
        for(int i = 0; i < occurences.size(); i++) {
            boolean isValidOccurence = true;
            for(int j = 0; j < notOccurrences.size(); j++) {
                if(isLineNumberInOccurrences(notOccurrences.get(j), occurences.get(i))) {
                    isValidOccurence = false;
                }
            }

            if(isValidOccurence) {
                notCheck.append(occurences.get(i));
            }
        }

        ArrayList<Integer> not = Intersection.getNot(occurences, notOccurrences);

        System.out.println("test: " + not.toJavaArrayList().toString());
        System.out.println("comp: " + notCheck.toJavaArrayList().toString());

        Assert.assertEquals(not.size(), notCheck.size());
        for (int i = 0; i < notCheck.size(); i++) {
            Assert.assertTrue(not.contains(notCheck.get(i)));
        }
    }

    @Test
    public void testNoIntersection() {
        ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences = new ArrayList<>();

        //Add occurrences to list
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 4, 7, 10, 13, 16}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{2, 5, 8, 11, 14, 17, 19}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{3, 6, 9, 15, 18}));

        testOccurrencesForIntersection(occurrences);
    }

    @Test
    public void testIntersectionAtEnds() {
        ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences = new ArrayList<>();

        //Add occurrences to list
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 7, 10, 13, 17}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 5, 8, 11, 14, 17}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 6, 9, 12, 15, 17}));

        testOccurrencesForIntersection(occurrences);
    }

    @Test
    public void testIntersectionGeneralCase() {
        ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences = new ArrayList<>();

        //Add occurrences to list
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 4, 5, 7, 8, 9, 10, 11, 13, 17}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 5, 8, 11, 14, 17}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 6, 9, 11, 15, 17}));

        testOccurrencesForIntersection(occurrences);
    }

    @Test
    public void testIntersectionDuplicates() {
        ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences = new ArrayList<>();

        //Add occurrences to list
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 1, 5, 6, 7, 11, 11}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 5, 8, 11, 11, 14, 17}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 6, 9, 11, 15, 17}));

        testOccurrencesForIntersection(occurrences);
    }

    @Test
    public void testIntersectionEmptyCase() {
        ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences = new ArrayList<>();

        //Add occurrences to list
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 4, 5, 7, 8, 9, 10, 11, 13, 17}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 6, 9, 11, 15, 17}));

        testOccurrencesForIntersection(occurrences);
    }

    @Test
    public void testIntersectionSingleCase() {
        ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences = new ArrayList<>();

        //Add occurrences to list
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 4, 5, 7, 8, 9, 10, 11, 13, 17}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{11}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 6, 9, 11, 15, 17}));

        testOccurrencesForIntersection(occurrences);
    }

    @Test
    public void testIntersectionSingleOccurrence() {
        ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences = new ArrayList<>();

        //Add occurrences to list
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 4, 5, 7}));

        testOccurrencesForIntersection(occurrences);
    }

    @Test
    public void testNoUnion() {
        ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences = new ArrayList<>();

        //Add occurrences to list
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{}));

        testOccurrencesForUnion(occurrences);
    }

    @Test
    public void testUnionNoDuplicates() {
        ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences = new ArrayList<>();

        //Add occurrences to list
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 4, 7, 10, 13, 16, 18, 19}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{2, 5, 8, 11, 14}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{3, 6, 9, 12, 15, 17, 20}));

        testOccurrencesForUnion(occurrences);
    }


    @Test
    public void testUnionDuplicatesAtEnds() {
        ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences = new ArrayList<>();

        //Add occurrences to list
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 1, 7, 10, 14}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 5, 8, 11, 14}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 6, 9, 12, 14}));

        testOccurrencesForUnion(occurrences);
    }


    @Test
    public void testUnionDuplicatesAtEndsMultiple() {
        ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences = new ArrayList<>();

        //Add occurrences to list
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 1, 4, 7, 10, 13}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 1, 5, 8, 11, 14}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 1, 1, 1, 9, 12, 15}));

        testOccurrencesForUnion(occurrences);
    }


    @Test
    public void testGeneralCase() {
        ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences = new ArrayList<>();

        //Add occurrences to list
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 4, 7, 8, 9, 11, 10, 13}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{2, 5, 11, 11, 14}));
        occurrences.append(getOccurrencesWithLineNumbers(new Integer[]{3, 6, 9, 12, 15, 16, 17, 18, 19}));

        testOccurrencesForUnion(occurrences);
    }

    @Test
    public void testSingleNot() {
        ArrayList<Integer> occurrences = new ArrayList<>();
        occurrences.append(1);
        occurrences.append(1);
        occurrences.append(2);
        occurrences.append(3);
        occurrences.append(4);
        occurrences.append(4);
        occurrences.append(7);

        ArrayList<ArrayList<HashPair<Integer, Integer>>> notOccurrences = new ArrayList<>();
        //notOccurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 2, 3, 4}));
        notOccurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 2, 3, 4}));

        testOccurrencesForNot(occurrences, notOccurrences);
    }

    @Test
    public void testTwoNotsButSame() {
        ArrayList<Integer> occurrences = new ArrayList<>();
        occurrences.append(1);
        occurrences.append(1);
        occurrences.append(2);
        occurrences.append(3);
        occurrences.append(4);
        occurrences.append(4);
        occurrences.append(7);

        ArrayList<ArrayList<HashPair<Integer, Integer>>> notOccurrences = new ArrayList<>();
        notOccurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 2, 3, 4}));
        notOccurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 2, 3, 4}));

        testOccurrencesForNot(occurrences, notOccurrences);
    }

    @Test
    public void testTwoNotButDifferent() {
        ArrayList<Integer> occurrences = new ArrayList<>();
        occurrences.append(1);
        occurrences.append(1);
        occurrences.append(2);
        occurrences.append(3);
        occurrences.append(4);
        occurrences.append(4);
        occurrences.append(7);

        ArrayList<ArrayList<HashPair<Integer, Integer>>> notOccurrences = new ArrayList<>();
        notOccurrences.append(getOccurrencesWithLineNumbers(new Integer[]{2, 4}));
        notOccurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 3, 4}));

        testOccurrencesForNot(occurrences, notOccurrences);
    }

    @Test
    public void testDuplicates() {
        ArrayList<Integer> occurrences = new ArrayList<>();
        occurrences.append(1);
        occurrences.append(1);
        occurrences.append(2);
        occurrences.append(3);
        occurrences.append(4);
        occurrences.append(4);
        occurrences.append(5);
        occurrences.append(6);
        occurrences.append(7);
        occurrences.append(7);

        ArrayList<ArrayList<HashPair<Integer, Integer>>> notOccurrences = new ArrayList<>();
        notOccurrences.append(getOccurrencesWithLineNumbers(new Integer[]{2, 4, 7}));
        notOccurrences.append(getOccurrencesWithLineNumbers(new Integer[]{1, 3, 4}));

        testOccurrencesForNot(occurrences, notOccurrences);
    }

}
