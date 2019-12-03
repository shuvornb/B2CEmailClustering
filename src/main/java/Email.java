public class Email {
    public int id;
    public String date;
    public String from;
    public String to;
    public String subject;
    public String content;

    public Email() {
    }

    @Override
    public String toString() {
        return "Email{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public String simpleToString() {
        return "Email{" +
                ", date='" + date + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}