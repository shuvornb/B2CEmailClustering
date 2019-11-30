import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class DataPreprocessor {

    // read emails from file and store into email table of crusher_server database
    public static ArrayList<Email> importHTMLFiles() {
        ArrayList<Email> emailArrayList = new ArrayList<>();
        File directory = new File("C:\\Users\\shuvo\\Desktop\\Dataset\\Takeout\\compressed");
        for (File file : directory.listFiles()) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(file);
                StringBuilder rawEmail = new StringBuilder();
                while (scanner.hasNextLine()) {
                    rawEmail.append(scanner.nextLine());
                }
                emailArrayList.add(getProcessedEmailFromRawEmail(rawEmail.toString()));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            scanner.close();
        }

        for (Email email: emailArrayList) {
            System.out.println(email.toString());
        }

        return emailArrayList;
    }

    private static Email getProcessedEmailFromRawEmail(String rawEmail) {
        String subjectContainer = rawEmail.split("SUBJECT:")[1];
        StringBuilder subject = new StringBuilder();
        for(int i=0;i<subjectContainer.length(); i++) {
            if(subjectContainer.charAt(i) == '<') break;
            subject.append(subjectContainer.charAt(i));
        }

        String fromContainer = rawEmail.split("FROM:")[1];
        StringBuilder from = new StringBuilder();
        for(int i=0;i<fromContainer.length(); i++) {
            if(fromContainer.charAt(i) == '<') break;
            from.append(fromContainer.charAt(i));
        }

        String toContainer = rawEmail.split("TO:")[1];
        StringBuilder to = new StringBuilder();
        for(int i=0;i<toContainer.length(); i++) {
            if(toContainer.charAt(i) == '<') break;
            to.append(toContainer.charAt(i));
        }

        String dateContainer = rawEmail.split("DATE:")[1];
        StringBuilder date = new StringBuilder();
        for(int i=0;i<dateContainer.length(); i++) {
            if(dateContainer.charAt(i) == '<') break;
            date.append(dateContainer.charAt(i));
        }

        Email email = new Email();
        email.date = date.toString();
        email.from = from.toString();
        email.to = to.toString();
        email.subject = subject.toString();
        email.content = rawEmail;

        return email;
    }

    public static void storeProcessedEmails(ArrayList<Email> emails) {
        DatabaseConnector dc = new DatabaseConnector();
        Connection connection = dc.connect();

        String query = "INSERT INTO email(date, sender, receiver, subject, description) "
                + "VALUES(?,?,?,?,?)";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            int count = 0;

            for (Email email : emails) {
                statement.setString(1, email.date);
                statement.setString(2, email.from);
                statement.setString(3, email.to);
                statement.setString(4, email.subject);
                statement.setString(5, email.content);

                statement.addBatch();
                count++;
                // execute every 100 rows or less
                if (count % 100 == 0 || count == emails.size()) {
                    statement.executeBatch();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dc.close(connection);
    }
}