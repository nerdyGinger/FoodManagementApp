package apps.nerdyginger.pocketpantry.models;

public class RecipeBook {
    private int _ID;
    private String name;
    private String author;
    private String link;
    private String description;
    private String imageUrl;

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public RecipeBook() {
        //empty constructor
    }

    public RecipeBook(int _ID, String name, String author, String link, String description, String imageUrl) {
        this._ID = _ID;
        this.name = name;
        this.author = author;
        this.link = link;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
