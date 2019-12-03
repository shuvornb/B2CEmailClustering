import java.util.ArrayList;
import java.util.Random;

public class MinHash {

    public static long[][] coefficients = new long[Constants.NUMBER_OF_HASH_FUNCTIONS][2];
    public static void populateHashFunctions() {
        // generate coefficients of hash functions randomly
        System.out.println("Generating coefficients");
        System.out.println("=======================");
        Random random = new Random();
        for(int i=0; i< Constants.NUMBER_OF_HASH_FUNCTIONS; i++) {
            coefficients[i][0] = random.nextInt(Constants.MAX_SHINGLE_ID);
            coefficients[i][1] = random.nextInt(Constants.MAX_SHINGLE_ID);
        }

        for(int i=0; i< Constants.NUMBER_OF_HASH_FUNCTIONS; i++) {
            System.out.println("Randomly generated coefficients for hash function "
                    + (i+1) + ": a=" + coefficients[i][0] + " & b=" + coefficients[i][1] + "");
        }
    }

    public static long[] generateMinHashValues(ArrayList<String> shingleSet) {
        // generate integer value for each shingle in the list

        //System.out.println("\n");
        //System.out.println("Converting the shingleList to integerShingleList");
        //System.out.println("=================================================");

        ArrayList<Long> integerShingleList = new ArrayList<Long>();
        for(String element: shingleSet) {
            long integerShingle = element.hashCode();
            integerShingleList.add(integerShingle);
            //System.out.println("Integer hashCode for element: \"" + element + "\": " + integerShingle + "");
        }

        // For each element generate values using declared number of hash functions
        // then calculate the minimum value for each element in integerShingleList
        long[] result = new long[Constants.NUMBER_OF_HASH_FUNCTIONS];
        for(int i=0; i<Constants.NUMBER_OF_HASH_FUNCTIONS; i++) {
            long minHashValue = Integer.MAX_VALUE;
            for(long element: integerShingleList) {
                long componentValue = (coefficients[i][0]*element + coefficients[i][1]) % Constants.NEXT_PRIME;
                if(componentValue < minHashValue) minHashValue = componentValue;
            }
            result[i] = minHashValue;
        }

        System.out.println("\n");
        System.out.println("The Signature");
        System.out.println("==============");
        for(int i=0; i< Constants.NUMBER_OF_HASH_FUNCTIONS; i++) {
            System.out.print(result[i] + " ");
        }
        System.out.print("\n");

        return result;
    }

    public static ArrayList<String> getShingleSet(String document) {
        ArrayList<String> shingleSet = new ArrayList<String>();

        // check whether document length is already less than or equal to SHINGLE_LENGTH
        if(document.length() <= Constants.SHINGLE_LENGTH) {
            shingleSet.add(document);
        }
        else {
            StringBuilder shingle = new StringBuilder();
            int j=0;
            for(int i=0; i<document.length(); i++) {
                if(shingle.length() < Constants.SHINGLE_LENGTH) {
                    shingle.append(document.charAt(i));
                }
                else {
                    shingleSet.add(shingle.toString());
                    shingle = new StringBuilder();
                    i=j;
                    j++;
                }
            }
        }
        return shingleSet;
    }

    public static double getMatchingPercentage(String[] signature1, String[] signature2) {
        double counter = 0;
        for(int i=0; i<Constants.NUMBER_OF_HASH_FUNCTIONS; i++) {
            if(signature1[i].equals(signature2[i])) counter++;
        }

        return (counter/Constants.NUMBER_OF_HASH_FUNCTIONS)*100;
    }
}
