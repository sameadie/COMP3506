package comp3506.assn2.utils;

/**
 * Static helper methods for finding logical operations on multiple arrays, specifically, the intersection (AND),
 * union (OR) and not (NOT) of n ArrayLists can be computed
 *
 * @bigO
 *      O(1) space complexity: class contains static methods
 */
public class Intersection {

    /**
     * Helper method to determine if all pointers point to the end of their respective arrays
     *
     * @param occurrences
     *      The arrays being traversed
     * @param pointers
     *      The pointers indexing into the occurrences arrays
     * @return
     *      True iff all pointers point to the end of their arrays, else false
     */
    private static boolean allPointersFinished(ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences, ArrayList<Integer> pointers) {
        for(int i = 0; i < occurrences.size(); i++) {
            //A pointer isn't finished
            if(pointers.get(i) + 1 != occurrences.get(i).size()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the intersection (AND) of the line numbers of the specified occurences. Assmumes that all
     * ArrayLists in occurences are sorted in ascending order.
     *
     * Simultaneously traverses all arrays in ascending order incrementing minimum value, checking for intersections
     *
     * @bigO
     *      O(sum(numElements) * numLists): Compares numLists elements and increments one pointer every loop. Needs
     *          to increment all pointers to their end, so it loops sum(numElements) times
     *
     * @param occurrences
     *      An ArrayList of ArrayLists of lineNumber, columnNumber pairs to calculate the intersection of
     *
     * @return
     *      A list of the intersecting line numbers
     */
    public static ArrayList<Integer> getIntersections(ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences) {

        ArrayList<Integer> intersections = new ArrayList<>();

        //Intersection of one list is itself - extract line numbers from <lineNumber, columnNumber> pairs
        if(occurrences.size() == 1) {
            for(int i = 0; i < occurrences.get(0).size(); i++) {
                intersections.append(occurrences.get(0).get(i).getLeftValue());
            }

            return intersections;
        }

        //Initialise a pointer to start of every array
        ArrayList<Integer> pointers = new ArrayList<>(occurrences.size());
        for(int i = 0; i < occurrences.size(); i++) {
            //No intersections for empty list
            if(occurrences.get(i).size() == 0) {
                return intersections;
            }
            pointers.append(0);
        }


        //Traverse all lists, finding intersections
        while(!allPointersFinished(occurrences, pointers)) {

            int firstValue = occurrences.get(0).get(pointers.get(0)).getLeftValue();
            boolean allEqual = true;
            int minimumIndex = 0;
            int minimumValue = firstValue;


            for(int i = 0; i < pointers.size(); i++) {
                //Check for intersection
                if(occurrences.get(i).get(pointers.get(i)).getLeftValue() != firstValue) {
                    allEqual = false;
                }

                //Dont increment pointer at end of array
                if(pointers.get(i) + 1 >= occurrences.get(i).size()) {
                    continue;
                }

                //Find smallest value - is next value to increment
                if(occurrences.get(i).get(pointers.get(i)).getLeftValue() < minimumValue) {
                    minimumIndex = i;
                    minimumValue = occurrences.get(i).get(pointers.get(i)).getLeftValue();
                }
            }

            //Found intersection
            if(allEqual) {
                intersections.append(firstValue);

                //Move all pointers off last minimumValue to avoid duplicates
                for(int i = 0; i < pointers.size(); i++) {
                    while(occurrences.get(i).get(pointers.get(i)).getLeftValue() == minimumValue) {
                        if(pointers.get(i) + 1 < occurrences.get(i).size()) {
                            pointers.set(i, pointers.get(i) + 1);
                        } else {
                            break;
                        }
                    }
                }
            }

            //If minimum value array has reached end, no more possible intersections
            if(pointers.get(minimumIndex) + 1 == occurrences.get(minimumIndex).size()) {
                break;
            }

            //Increment smallest value - passing over duplicates
            while(occurrences.get(minimumIndex).get(pointers.get(minimumIndex)).getLeftValue() == minimumValue) {
                if(pointers.get(minimumIndex) + 1 < occurrences.get(minimumIndex).size()) {
                    pointers.set(minimumIndex, pointers.get(minimumIndex) + 1);
                } else {
                    break;
                }
            }
        }

        //Check last elements aren't an intersection
        Integer lastValue = occurrences.get(0).get(occurrences.get(0).size() - 1).getLeftValue();

        //Check last value for duplicates
        if((intersections.size() != 0 ) && (intersections.get(intersections.size() - 1).equals(lastValue))) {
            return intersections;
        }

        for(int i = 1; i < occurrences.size(); i++) {
            if(!occurrences.get(i).get(occurrences.get(i).size() - 1).getLeftValue().equals(lastValue)) {
                //Last value is not an intersection
                return intersections;
            }
        }

        //Last value is an intersection
        intersections.append(lastValue);
        return intersections;
    }

    /**
     * Returns the union (OR) of the line numbers of the specified occurrences. Assumes that the
     * ArrayLists in occurrences are sorted in ascending order
     *
     * Simultaneously traverses all arrays in ascending order incrementing minimum value, checking for unions
     *
     * @bigO
     *      O(sum(numElements) * numLists): Compares numLists elements and increments one pointer every loop. Needs
     *          to increment all pointers to their end, so it loops sum(numElements) times
     *
     * @param occurrences
     *      An ArrayList of ArrayLists of lineNumber, columnNumber pairs to calculate the union of
     *
     * @return
     *      A list of the union line numbers
     */
    public static ArrayList<Integer> getUnion(ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences) {
        ArrayList<Integer> unions = new ArrayList<>();

        //Initialise a pointer to start of every array
        ArrayList<Integer> pointers = new ArrayList<>(occurrences.size());
        for(int i = 0; i < occurrences.size(); i++) {

            //Remove empty lists
            if(occurrences.get(i).size() == 0) {
                occurrences.remove(i);
                i--;
                continue;
            }

            pointers.append(0);
        }

        //Union of no lists is empty list
        if (occurrences.size() == 0) {
            return new ArrayList<>(0);
        }

        //Traverse all lists, finding unions
        do {
            int minimumIndex = 0;
            int minimumValue = occurrences.get(0).get(pointers.get(0)).getLeftValue();

            outerLoop:
            for(int i = 0; i < pointers.size(); i++) {

                //If duplicate is found, move pointer along without adding to union
                for(int j = i + 1; j < pointers.size(); j++) {
                    if(occurrences.get(i).get(pointers.get(i)).getLeftValue().equals(occurrences.get(j).get(pointers.get(j)).getLeftValue())) {
                        pointers.set(j, pointers.get(j) + 1);

                        //Remove completed arrays
                        if(pointers.get(j) + 1 > occurrences.get(j).size()) {
                            pointers.remove(j);
                            occurrences.remove(j);
                        }
                        //continue outerLoop;
                        j--;
                    }
                }

                //Find smallest value - is next value to increment
                if(occurrences.get(i).get(pointers.get(i)).getLeftValue() < minimumValue) {
                    minimumIndex = i;
                    minimumValue = occurrences.get(i).get(pointers.get(i)).getLeftValue();
                }
            }

            unions.append(minimumValue);

            //Move pointer along for minimum value array, removing completed arrays
            if(pointers.get(minimumIndex) + 1 < occurrences.get(minimumIndex).size()) {
                pointers.set(minimumIndex, pointers.get(minimumIndex) + 1);
            } else {
                pointers.remove(minimumIndex);
                occurrences.remove(minimumIndex);
            }

            //Jump over duplicates
            while((pointers.get(minimumIndex) > 0) && (pointers.get(minimumIndex) < occurrences.get(minimumIndex).size()) && (occurrences.get(minimumIndex).get(pointers.get(minimumIndex)).equals(occurrences.get(minimumIndex).get(pointers.get(minimumIndex) - 1)))) {
                pointers.set(minimumIndex, pointers.get(minimumIndex) + 1);
            }

        } while(occurrences.size() != 0);

        return unions;
    }

    /**
     * Returns the lineNumbers in occurrences not in notOccurrences. Assumes that the elements in occurrences are in
     * ascending order and the lineNumbers in notOccurrences are in ascending order
     *
     * Traverses occurrences while simultaneously traversing all arrays in notOccurrences, removing common values
     *
     * @bigO
     *      O(numOccurrences * notOccurrences.length + sum(numNotOccurrences)): notOccurrences.length comparisons
     *      are made for each loop in numOccurrences and each element in the lists in notOccurrences have to be visited
     *
     * @param occurrences
     * @param notOccurrences
     * @return
     */
    public static ArrayList<Integer> getNot(ArrayList<Integer> occurrences, ArrayList<ArrayList<HashPair<Integer, Integer>>> notOccurrences) {

        ArrayList<Integer> exclusiveOccurrences = new ArrayList<>();

        //Initialise pointers to start of notOccurrences lists
        int includedPointer = 0;
        ArrayList<Integer> notPointers = new ArrayList<>(notOccurrences.size());
        for (int i = 0; i < notOccurrences.size(); i++) {
            notPointers.append(0);
        }


        //Iterate through occurrences
        while (includedPointer < occurrences.size()) {
            boolean allGreater = true;
            boolean nonEqual = true;
            for (int i = 0; i < notPointers.size(); i++) {
                if (notPointers.get(i) < notOccurrences.get(i).size()) {
                    if (notOccurrences.get(i).get(notPointers.get(i)).getLeftValue() < occurrences.get(includedPointer)) {
                        notPointers.set(i, notPointers.get(i) + 1);
                        allGreater = false;

                        //Found match between occurrences and notOccurrences - remove
                    } else if (notOccurrences.get(i).get(notPointers.get(i)).getLeftValue().equals(occurrences.get(includedPointer))) {
                        nonEqual = false;
                        break;
                    }
                }
            }
            //Increment occurrences
            if (allGreater) {
                if(nonEqual) {
                    exclusiveOccurrences.append(occurrences.get(includedPointer));
                }
                includedPointer++;
            }
        }

        return exclusiveOccurrences;
    }
}
