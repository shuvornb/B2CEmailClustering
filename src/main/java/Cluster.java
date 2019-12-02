import java.util.Arrays;
import java.util.List;

public class Cluster {
    public int id;
    public String[] minhashSignature;
    public List<String> emailSet;
    public List<String> userEmail;

    public Cluster() {
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "id=" + id +
                ", minhashSignature=" + Arrays.toString(minhashSignature) +
                ", emailSet=" + emailSet +
                ", userEmail=" + userEmail +
                '}';
    }
}