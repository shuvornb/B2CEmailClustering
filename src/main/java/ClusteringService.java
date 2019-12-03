import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ClusteringService {

    public static void clusterSingleEmail(Email email) {

        System.out.println("Got email with id = " + email.id);

        // calculate minhash signature
        ArrayList<String> shingleSet = MinHash.getShingleSet(email.content);
        long[] minhashSignatureLongArray = MinHash.generateMinHashValues(shingleSet);

        // convert long array into string array
        String[] minhashSignatureStringArray = new String[Constants.NUMBER_OF_HASH_FUNCTIONS];
        for(int i=0; i<minhashSignatureLongArray.length; i++) {
            minhashSignatureStringArray[i] = String.valueOf(minhashSignatureLongArray[i]);
        }

        DatabaseConnector dc = new DatabaseConnector();
        Connection connection = dc.connect();

        // update email table with this minhash value
        updateEmailWithMinhashValue(email, minhashSignatureStringArray, connection);

        // bulk read from cluster table
        ArrayList<Cluster> clusters = readAllExistingCluster(connection);

        // find a possible match based on specific criteria
        Cluster cluster = findMatch(clusters, minhashSignatureStringArray);

        // update cluster table with this email
        updateClusterDataWithThisEmail(cluster, email, minhashSignatureStringArray, connection);

        dc.close(connection);
    }

    private static void updateEmailWithMinhashValue(Email email, String[] minhashSignatureStringArray, Connection updateEmailMinhashConnection) {

        try {
            String sql = "UPDATE email SET minhash_signature = ? WHERE id = ?";
            PreparedStatement updateEmailMinhashStatement = updateEmailMinhashConnection.prepareStatement(sql);
            Array minhashSQLArray = updateEmailMinhashConnection.createArrayOf("text", minhashSignatureStringArray);
            updateEmailMinhashStatement.setArray(1, minhashSQLArray);
            updateEmailMinhashStatement.setInt(2, email.id);
            updateEmailMinhashStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Cluster> readAllExistingCluster(Connection bulkReadConnection) {
        ArrayList<Cluster> clusters = new ArrayList<Cluster>();
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Number of clusters found: " + clusters.size());
        return clusters;
    }

    private static Cluster findMatch(ArrayList<Cluster> clusters, String[] minhashSignatureStringArray) {
        for (Cluster cluster: clusters) {
            if(MinHash.getMatchingPercentage(cluster.minhashSignature, minhashSignatureStringArray) >= Constants.MATCHING_THRESHOLD)
                return cluster;
        }
        return null;
    }

    private static void updateClusterDataWithThisEmail(Cluster cluster, Email email, String[] minhashSignatureStringArray, Connection updateClusterConnection) {
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

    public static void writeUsingOutputStream(String content, String filePath) {
        OutputStream os = null;
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            os = new FileOutputStream(file);
            os.write(content.getBytes(), 0, content.length());
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}