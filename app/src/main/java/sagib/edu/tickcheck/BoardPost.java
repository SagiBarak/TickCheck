package sagib.edu.tickcheck;

/**
 * Created by sagib on 19/06/2017.
 */

public class BoardPost {
    private String title;
    private String contents;

    public BoardPost() {
    }

    public BoardPost(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
