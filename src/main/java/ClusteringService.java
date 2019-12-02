import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ClusteringService {
    private static final double MATCHING_THRESHOLD = 50.0;

    public static void clusterEmail(Email email) {

        // calculate minhash signature
        ArrayList<String> shingleSet = MinHash.getShingleSet(email.content);
        long[] minhashSignatureLongArray = MinHash.generateMinHashValues(shingleSet);

        // convert long array into string array
        String[] minhashSignatureStringArray = new String[MinHash.NUMBER_OF_HASH_FUNCTIONS];
        for(int i=0; i<minhashSignatureLongArray.length; i++) {
            minhashSignatureStringArray[i] = String.valueOf(minhashSignatureLongArray[i]);
        }

        // update email table with this minhash value
        updateEmailWithMinhashValue(email, minhashSignatureStringArray);

        // bulk read from cluster table
        ArrayList<Cluster> clusters = readAllExistingCluster();

        // find a possible match based on specific criteria
        Cluster cluster = findMatch(clusters, minhashSignatureStringArray);

        // update cluster table with this email
        updateClusterDataWithThisEmail(cluster, email, minhashSignatureStringArray);
    }

    private static void updateClusterDataWithThisEmail(Cluster cluster, Email email, String[] minhashSignatureStringArray) {
        DatabaseConnector dc = new DatabaseConnector();
        Connection updateClusterConnection = dc.connect();
        if(cluster == null) {
            String insertClusterQuery = "INSERT INTO cluster(minhash_signature, email, user_email) VALUES(?, ?, ?)";
            try {
                PreparedStatement insertClusterStatement = updateClusterConnection.prepareStatement(insertClusterQuery);
                Array minhashSignatureSQLArray = updateClusterConnection.createArrayOf("text", minhashSignatureStringArray);
                String[] emailArray = new String[]{String.valueOf(email.id)};
                Array emailSQLArray = updateClusterConnection.createArrayOf("text", emailArray);
                String[] userEmailArray = new String[]{email.to};
                Array userEmailSQLArray = updateClusterConnection.createArrayOf("text", userEmailArray);

                insertClusterStatement.setArray(1, minhashSignatureSQLArray);
                insertClusterStatement.setArray(2, emailSQLArray);
                insertClusterStatement.setArray(3, userEmailSQLArray);
                insertClusterStatement.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            // update emailSet
            cluster.emailSet.add(String.valueOf(email.id));

            // update userEmail
            if(!cluster.userEmail.contains(email.to)) cluster.userEmail.add(email.to);

            // update the cluster row
            try {
                Array emailSQLArray = updateClusterConnection.createArrayOf("text", cluster.emailSet.toArray());
                Array userEmailSQLArray = updateClusterConnection.createArrayOf("text", cluster.userEmail.toArray());
                String updateClusterQuery = "UPDATE cluster SET email = ?, user_email = ? WHERE id = ?";

                PreparedStatement updateCLusterStatement = updateClusterConnection.prepareStatement(updateClusterQuery);
                updateCLusterStatement.setArray(1, emailSQLArray);
                updateCLusterStatement.setArray(2, userEmailSQLArray);
                updateCLusterStatement.setInt(3, cluster.id);
                updateCLusterStatement.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static ArrayList<Cluster> readAllExistingCluster() {
        ArrayList<Cluster> clusters = new ArrayList<Cluster>();
        DatabaseConnector dc = new DatabaseConnector();
        Connection bulkReadConnection = dc.connect();
        String bulkReadQuery = "SELECT * FROM cluster";
        try {
            PreparedStatement bulkReadStatement = bulkReadConnection.prepareStatement(bulkReadQuery);
            ResultSet bulkReadResultSet = bulkReadStatement.executeQuery();

            while (bulkReadResultSet.next()) {
                Cluster cluster = new Cluster();
                cluster.id = bulkReadResultSet.getInt(1);
                cluster.minhashSignature = (String[])bulkReadResultSet.getArray(2).getArray();
                cluster.emailSet = new ArrayList<String>(Arrays.asList((String[])bulkReadResultSet.getArray(3).getArray()));
                cluster.userEmail = new ArrayList<String>(Arrays.asList((String[])bulkReadResultSet.getArray(4).getArray()));

                clusters.add(cluster);
            }

            dc.close(bulkReadConnection);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Number of clusters found: " + clusters.size());
        return clusters;
    }

    private static void updateEmailWithMinhashValue(Email email, String[] minhashSignatureStringArray) {
        System.out.println("ID of email which will be updated: " + email.id);
        System.out.println("Email will be updated with following minhash signature");
        System.out.println("=======================================================");
        for (String str: minhashSignatureStringArray) {
            System.out.print(str + " ");
        }

        DatabaseConnector dc = new DatabaseConnector();
        Connection updateEmailMinhashConnection = dc.connect();

        try {
            Array minhashSQLArray = updateEmailMinhashConnection.createArrayOf("text", minhashSignatureStringArray);
            String sql = "UPDATE email SET minhash_signature = ? WHERE id = ?";
            PreparedStatement updateEmailMinhashstatement = updateEmailMinhashConnection.prepareStatement(sql);
            updateEmailMinhashstatement.setArray(1, minhashSQLArray);
            updateEmailMinhashstatement.setInt(2, email.id);
            updateEmailMinhashstatement.execute();

            dc.close(updateEmailMinhashConnection);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Cluster findMatch(ArrayList<Cluster> clusters, String[] minhashSignatureStringArray) {
        for (Cluster cluster: clusters) {
            if(MinHash.getMatchingPercentage(cluster.minhashSignature, minhashSignatureStringArray) >= MATCHING_THRESHOLD)
                return cluster;
        }
        return null;
    }
}