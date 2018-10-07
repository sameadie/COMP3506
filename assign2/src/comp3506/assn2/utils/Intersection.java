package comp3506.assn2.utils;

/**
 * Static helper methods for finding logical operations on multiple arrays, specifically, the intersection (AND),
 * union (OR) and not (NOT) of n ArrayLists can be computed
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
            if(pointers.get(i) + 1 != occurrences.get(i).size()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the intersection (AND) of the line numbers of the specified occurences. Assmumes that the
     * ArrayLists in occurences are sorted in ascending order
     *
     * @param occurrences
     *      An ArrayList of ArrayLists of lineNumber, columnNumber pairs to calculate the intersection of
     *
     * @return
     *      A list of the intersecting line numbers
     */
    public static ArrayList<Integer> getIntersections(ArrayList<ArrayList<HashPair<Integer, Integer>>> occurrences) {

        System.out.println("Find intersection of: ");
        for(int i = 0; i < occurrences.size(); i++) {
            System.out.println(occurrences.get(i).toJavaArrayList().toString());
        }

        ArrayList<Integer> intersections = new ArrayList<>();

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

        //Check last value already added
        if((intersections.size() != 0 ) && (intersections.get(intersections.size() - 1).equals(lastValue))) {
            System.out.println("this gives");
            System.out.println(intersections.toJavaArrayList().toString());
            return intersections;
        }

        for(int i = 1; i < occurrences.size(); i++) {
            if(!occurrences.get(i).get(occurrences.get(i).size() - 1).getLeftValue().equals(lastValue)) {
                //Last value is not an intersection
                return intersections;
            }
        }

        System.out.println("this gives");
        System.out.println(intersections.toJavaArrayList().toString());
        //Last value is an intersection
        intersections.append(lastValue);
        return intersections;
    }

    /**
     * Returns the union (OR) of the line numbers of the specified occurrences. Assumes that the
     * ArrayLists in occurrences are sorted in ascending order
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

        if (occurrences.size() == 0) {
            return new ArrayList<>(0);
        }

        //Traverse all lists, finding intersections
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

        } while(occurrences.size() != 0); //!allPointersFinished(occurrences, pointers));

        return unions;
    }

    public static ArrayList<Integer> getNot(ArrayList<Integer> occurrences, ArrayList<ArrayList<HashPair<Integer, Integer>>> notOccurrences) {

        System.out.println("From these:");
        System.out.println(occurrences.toJavaArrayList().toString());

        System.out.println("Remove these: ");
        for(int i = 0; i < notOccurrences.size(); i++) {
            System.out.println(notOccurrences.get(i).toJavaArrayList().toString());
        }
        System.out.println("This gives: ");

        int includedPointer = 0;
        ArrayList<Integer> notPointers = new ArrayList<>(notOccurrences.size());
        for (int i = 0; i < notOccurrences.size(); i++) {
            notPointers.append(0);
        }


        while (includedPointer < occurrences.size()) {
            boolean allGreater = true;
            for (int i = 0; i < notPointers.size(); i++) {
                if (notOccurrences.get(i).get(notPointers.get(i)).getLeftValue() < occurrences.get(includedPointer)) {
                    if(notPointers.get(i) + 1 < notOccurrences.get(i).size()) {
                        notPointers.set(i, notPointers.get(i) + 1);
                    } else {
                        notPointers.remove(i);
                        notOccurrences.remove(i);
                    }
                    allGreater = false;
                } else if (notOccurrences.get(i).get(notPointers.get(i)).getLeftValue().equals(occurrences.get(includedPointer))) {
                    occurrences.remove(includedPointer);
                    allGreater = false;
                    break;
                }
            }

            if (allGreater) {
                includedPointer++;
            }
        }

        System.out.println(occurrences.toJavaArrayList().toString());
        System.out.println("END");

        return occurrences;
    }
}
