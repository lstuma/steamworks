package info.steamworks.steamworks.submission;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Table
@Entity
public class ImageSubmission extends Submission
{
    protected String img_src;

    public ImageSubmission()
    {
        this("null", "null", "null", "null");
    }
    public ImageSubmission(String username, String title, String body, String imgSrc) {
        this.username = username;
        this.title = title;
        this.body = body;
        this.img_src = imgSrc;
    }

    public String getImg_src() {
        return img_src;
    }
    public void setImg_src(String img_src){
        this.img_src = img_src;
    }
    @Override
    public String getBody()
    {
        return "<img style=\"border-radius: 10px; width: 20%; padding: 0 5px 0 0; margin: 0 15px 0 0;\" src=\"" + img_src + "\"/><span style=\"vertical-align: top;\">" + body + "</span>";
    }

    @Override
    public String toString() {
        return "Submission{" +
                ", id=" + id +
                ", username='" + username + '\'' +
                ", title='" + title + '\'' +
                ", body='" + "<img src=" + img_src + "/>" + body + '\'' +
                '}';
    }
}
