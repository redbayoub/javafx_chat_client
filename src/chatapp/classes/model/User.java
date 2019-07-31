package chatapp.classes.model;

import java.io.Serializable;
import java.util.Objects;


public class User implements Serializable {
    private int id;

    private String username;


    private String password_sha1;
    private String email;

    private boolean is_connected;
    private boolean is_confirmed;
    private String profile_image_path;

    private Object UserData;

    public Object getUserData() {
        return UserData;
    }

    public void setUserData(Object userData) {
        UserData = userData;
    }

    public User() {
    }


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword_sha1() {
        return password_sha1;
    }

    public void setPassword_sha1(String password_sha1) {
        this.password_sha1 = password_sha1;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isIs_connected() {
        return is_connected;
    }

    public void setIs_connected(boolean is_connected) {
        this.is_connected = is_connected;
    }

    public String getProfile_image_path() {
        return profile_image_path;
    }

    public void setProfile_image_path(String profile_image_path) {
        this.profile_image_path = profile_image_path;
    }

    public boolean isIs_confirmed() {
        return is_confirmed;
    }

    public void setIs_confirmed(boolean is_confirmed) {
        this.is_confirmed = is_confirmed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password_sha1='" + password_sha1 + '\'' +
                ", email='" + email + '\'' +
                ", is_connected=" + is_connected +
                ", is_confirmed=" + is_confirmed +
                ", profile_image_path='" + profile_image_path + '\'' +
                '}';
    }
}
