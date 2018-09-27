package comp3506.assn2.utils;

public class Intersection {

    private static boolean allPointersFinished(ArrayList<ArrayList<Pair<Integer, Integer>>> occurences, ArrayList<Integer> pointers) {
        for(int i = 0; i < occurences.size(); i++) {
            if(pointers.get(i) + 1 != occurences.get(i).size()) {
                return false;
            }
        }

        return true;
    }

    private static boolean aPointersFinished(ArrayList<ArrayList<Pair<Integer, Integer>>> occurences, ArrayList<Integer> pointers) {
        for(int i = 0; i < occurences.size(); i++) {
            if(pointers.get(i) + 1 == occurences.get(i).size()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the intersection of the occurrence lists
     * @param occurences
     * @return
     */
    public static ArrayList<Integer> getIntersections(ArrayList<ArrayList<Pair<Integer, Integer>>> occurences) {
        ArrayList<Integer> intersections = new ArrayList<Integer>();

        //Initialise a pointer to start of every array
        ArrayList<Integer> pointers = new ArrayList<>(occurences.size());
        for(int i = 0; i < occurences.size(); i++) {
            //No intersections for empty list
            if(occurences.get(i).size() == 0) {
                return intersections;
            }
            pointers.append(0);
        }

        //Traverse all lists, finding intersections
        while(!allPointersFinished(occurences, pointers)) {

            int firstValue = occurences.get(0).get(pointers.get(0)).getLeftValue();
            boolean allEqual = true;
            int minimumIndex = 0;
            int minimumValue = firstValue;


            for(int i = 0; i < pointers.size(); i++) {
                //Check for intersection
                if(occurences.get(i).get(pointers.get(i)).getLeftValue() != firstValue) {
                    allEqual = false;
                }

                //Dont increment pointer at end of array
                if(pointers.get(i) + 1 >= occurences.get(i).size()) {
                    continue;
                }

                //Find smallest value - is next value to increment
                if(occurences.get(i).get(pointers.get(i)).getLeftValue() < minimumValue) {
                    minimumIndex = i;
                    minimumValue = occurences.get(i).get(pointers.get(i)).getLeftValue();
                }
            }

            //Found intersection
            if(allEqual) {
                intersections.append(firstValue);
            }

            //If minimum value array has reached end, no more possible intersections
            if(pointers.get(minimumIndex) + 1 == occurences.get(minimumIndex).size()) {
                break;
            }

            //Increment smallest value - passing over duplicates
            do {
                if(pointers.get(minimumIndex) + 1 < occurences.get(minimumIndex).size()) {
                    pointers.set(minimumIndex, pointers.get(minimumIndex) + 1);
                } else { break;}
            } while(occurences.get(minimumIndex).get(pointers.get(minimumIndex)).getLeftValue() == minimumValue);

        }

        return intersections;
    }

    /**
     * Returns the union of the occurrence lists
     * @param occurences
     * @return
     */
    public static ArrayList<Integer> getUnion(ArrayList<ArrayList<Pair<Integer, Integer>>> occurences) {
        ArrayList<Integer> unions = new ArrayList<Integer>();

        //Initialise a pointer to start of every array
        ArrayList<Integer> pointers = new ArrayList<>(occurences.size());
        for(int i = 0; i < occurences.size(); i++) {
            //Remove empty lists
            if(occurences.get(i).size() == 0) {
                occurences.remove(i);
                i--;
                continue;
            }

            pointers.append(0);
        }

        //Traverse all lists, finding intersections
        do {
            int minimumIndex = 0;
            int minimumValue = occurences.get(0).get(pointers.get(0)).getLeftValue();

            outerLoop:
            for(int i = 0; i < pointers.size() - 1; i++) {

                //If duplicate is found, move pointer along without adding to union
                for(int j = i + 1; j < pointers.size(); j++) {
                    if(occurences.get(i).get(pointers.get(i)).getLeftValue() == occurences.get(j).get(pointers.get(j)).getLeftValue()) {
                        pointers.set(i, pointers.get(i) + 1);
                        i--;
                        continue outerLoop;
                    }
                }

                //Find smallest value - is next value to increment
                if(occurences.get(i).get(pointers.get(i)).getLeftValue() < minimumValue) {
                    minimumIndex = i;
                    minimumValue = occurences.get(i).get(pointers.get(i)).getLeftValue();
                }
            }

            unions.append(minimumValue);

            //Move pointer along for minimum value array, removing completed arrays
            if(pointers.get(minimumIndex) + 1 < occurences.get(minimumIndex).size()) {
                pointers.set(minimumIndex, pointers.get(minimumIndex) + 1);
            } else {
                pointers.remove(minimumIndex);
                occurences.remove(minimumIndex);
            }
        } while(!allPointersFinished(occurences, pointers));

        //Add additional elements
        for(int i = 0; i < occurences.size(); i++) {
            unions.append(occurences.get(i).get(occurences.get(i).size() - 1).getLeftValue());
        }

        return unions;

    }
}
