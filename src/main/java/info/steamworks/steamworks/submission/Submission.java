package info.steamworks.steamworks.submission;

import jakarta.persistence.*;

@Entity
@Table
public class Submission {
    @Id
    @SequenceGenerator( name = "submission_sequence", sequenceName = "submission_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "submission_sequence")
    private Integer id;
    private String username;
    private String title;
    private String body;


    public Submission()
    {
        this("null", "null", "null");
    }

    public Submission(String username, String title, String body) {
        this.username = username;
        this.title = title;
        this.body = body;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
