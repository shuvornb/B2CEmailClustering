public class Email {
    public int id;
    public String date;
    public String from;
    public String to;
    public String subject;
    public String content;

    public Email() {
    }

    public Email(int id, String date, String from, String to, String subject, String content) {
        this.id = id;
        this.date = date;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.content = content;
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
}