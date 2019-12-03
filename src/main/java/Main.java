import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void prompt() {

        System.out.println("\n\n");
        System.out.println("#############################################");
        System.out.println("#############################################");
        System.out.println("#   Press 1 to import data.                 #");
        System.out.println("#   Press 2 to cluster imported emails.     #");
        System.out.println("#   Press 3 to cluster a single email.      #");
        System.out.println("#   Press 4 to write the clusters.          #");
        System.out.println("#   Press 0 to exit.                        #");
        System.out.println("#############################################");
        System.out.println("#############################################");
        System.out.println("\n\n");
    }
    public static void main(String[] args) {

        System.out.println("\n" +
                "   ___ _   _   _ ___ _____ ___ ___ ___ _  _  ___   ___ ___ _____   _____ ___ ___ \n" +
                "  / __| | | | | / __|_   _| __| _ \\_ _| \\| |/ __| / __| __| _ \\ \\ / /_ _/ __| __|\n" +
                " | (__| |_| |_| \\__ \\ | | | _||   /| || .` | (_ | \\__ \\ _||   /\\ V / | | (__| _| \n" +
                "  \\___|____\\___/|___/ |_| |___|_|_\\___|_|\\_|\\___| |___/___|_|_\\ \\_/ |___\\___|___|\n" +
                "                                                                                 \n");

        prompt();

        Scanner scanner = new Scanner(System.in);
        int input = scanner.nextInt();

        while (input != 0) {
            if(input == 1) {
                System.out.println("Please enter the location path of the directory of your html files");
                scanner.nextLine();
                String directoryPath = scanner.nextLine();
                ArrayList<Email> emails = DataPreprocessor.importHTMLFiles(directoryPath);
                DataPreprocessor.storeProcessedEmails(emails);
            }
            else if(input == 2) {
                MinHash.populateHashFunctions();

                ArrayList<Email> emails = new ArrayList<Email>();
                String sql = "SELECT * FROM email";
                DatabaseConnector dc = new DatabaseConnector();
                Connection connection = dc.connect();
                try {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    ResultSet rs = statement.executeQuery();
                    while (rs.next()) {
                        Email email = new Email();
                        email.id = rs.getInt(1);
                        email.date = rs.getString(2);
                        email.from = rs.getString(3);
                        email.to = rs.getString(4);
                        email.subject = rs.getString(5);
                        email.content = rs.getString(6);

                        emails.add(email);
                    }

                    dc.close(connection);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                for(Email email: emails) {
                    ClusteringService.clusterSingleEmail(email);
                }
            }
            else if(input == 3) {

            }
            else if(input == 4) {

            }
            else {
                System.out.println("Please press according to the menu.");
            }

            prompt();
            Scanner sc = new Scanner(System.in);
            input = sc.nextInt();
        }
    }
}
