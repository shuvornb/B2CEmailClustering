import java.sql.*;
import java.util.ArrayList;

public class ClusteringService {

    public static void clusterEmail(Email email) {

        // calculate minhash signature
        ArrayList<String> shingleSet = MinHash.getShingleSet(email.content);
        long[] minhashSignature = MinHash.generateMinHashValues(shingleSet);

        // convert long array into string array
        String[] minhashSignatureArray = new String[10];
        for(int i=0; i<minhashSignature.length; i++) {
            minhashSignatureArray[i] = String.valueOf(minhashSignature[i]);
        }

        // update email table with this minhash value
        DatabaseConnector dc = new DatabaseConnector();
        Connection connection = dc.connect();

        try {
            Array minhashArray = connection.createArrayOf("text", minhashSignatureArray);
            String sql = "UPDATE email SET minhash_signature = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setArray(1, minhashArray);
            statement.setInt(2, email.id);
            statement.execute();

            dc.close(connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // bulk read from cluster table

        // find a possible match based on specific criteria

        // update cluster table with this email
    }
}