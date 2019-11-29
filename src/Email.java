public class Email {
    public String date;
    public String from;
    public String to;
    public String subject;
    public String content;

    public Email() {
    }

    public Email(String date, String from, String to, String subject, String content) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Email{" +
                "date='" + date + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}