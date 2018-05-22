package messenger.firebase.com.firebasedemoapp;

public class Users {
    public String name,image;

    public Users() {

    }

    public Users(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Users{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
