import java.util.ArrayList;
import java.util.Random;

public class MinHash {
    private static final int MAX_SHINGLE_ID = Integer.MAX_VALUE;
    private static final long NEXT_PRIME = 2147483659L;
    private static final int NUMBER_OF_HASH_FUNCTIONS = 10;
    private static long[][] coefficients = new long[NUMBER_OF_HASH_FUNCTIONS][2];

    public static void populateHashFunctions() {
        // generate coefficients of hash functions randomly
        System.out.println("Generating coefficients");
        System.out.println("=======================");
        Random random = new Random();
        for(int i=0; i< NUMBER_OF_HASH_FUNCTIONS; i++) {
            coefficients[i][0] = random.nextInt(MAX_SHINGLE_ID);
            coefficients[i][1] = random.nextInt(MAX_SHINGLE_ID);
        }

        for(int i=0; i< NUMBER_OF_HASH_FUNCTIONS; i++) {
            System.out.println("Randomly generated coefficients for hash function "
                    + (i+1) + ": a=" + coefficients[i][0] + " & b=" + coefficients[i][1] + "");
        }
    }

    public static long[] generateMinHashValues(ArrayList<String> shingleSet) {
        // generate integer value for each shingle in the list

        System.out.println("\n");
        System.out.println("Converting the shingleList to integerShingleList");
        System.out.println("=================================================");

        ArrayList<Long> integerShingleList = new ArrayList<>();
        for(String element: shingleSet) {
            long integerShingle = element.hashCode();
            integerShingleList.add(integerShingle);
            System.out.println("Integer hashCode for element: \"" + element + "\": " + integerShingle + "");
        }

        // For each element generate values using declared number of hash functions
        // then calculate the minimum value for each element in integerShingleList
        long[] result = new long[NUMBER_OF_HASH_FUNCTIONS];
        for(int i=0; i<NUMBER_OF_HASH_FUNCTIONS; i++) {
            long minHashValue = Integer.MAX_VALUE;
            for(long element: integerShingleList) {
                long componentValue = (coefficients[i][0]*element + coefficients[i][1]) % NEXT_PRIME;
                if(componentValue < minHashValue) minHashValue = componentValue;
            }
            result[i] = minHashValue;
        }

        System.out.println("\n");
        System.out.println("The Signature");
        System.out.println("==============");
        for(int i=0; i< NUMBER_OF_HASH_FUNCTIONS; i++) {
            System.out.print(result[i]);
        }

        return result;
    }
}
