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


    /**
     * Returns the occurences of the intersection
     * @param occurences
     * @return
     */
    public static ArrayList<Integer> getIntersections(ArrayList<ArrayList<Pair<Integer, Integer>>> occurences) {
        ArrayList<Integer> intersections = new ArrayList<Integer>();

        //Initialise a pointer to start of every array
        ArrayList<Integer> pointers = new ArrayList<>(occurences.size());
        for(int i = 0; i < pointers.size(); i++) {
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


            //Increment smallest value - passing over duplicates
            do {
                pointers.set(minimumIndex, pointers.get(minimumIndex++) + 1);
            } while(occurences.get(minimumIndex).get(pointers.get(minimumIndex)).getLeftValue() == minimumValue);

        }

        return intersections;
    }

}
