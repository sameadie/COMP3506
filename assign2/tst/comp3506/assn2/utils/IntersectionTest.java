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

        Assert.assertEquals(intersection.size(), intersectionCheck.size());
        for (int i = 0; i < intersectionCheck.size(); i++) {
            Assert.assertTrue(intersection.contains(intersectionCheck.get(i)));
        }


    }

    private void testOccurrencesForUnion(ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences) {
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

        Assert.assertEquals(union.size(), unionCheck.size());
        for (int i = 0; i < unionCheck.size(); i++) {
            Assert.assertTrue(union.contains(unionCheck.get(i)));
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
}
