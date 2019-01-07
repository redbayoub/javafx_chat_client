package chatapp.classes.model;


import java.util.List;

public class Group {

    private int group_id;
    private String name;

    private List<User> members;

    public Group() {
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "Group{" +
                "group_id=" + group_id +
                ", name='" + name + '\'' +
                ", members=" + members +
                '}';
    }
}
