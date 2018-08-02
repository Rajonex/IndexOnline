package utils;

import org.mongodb.morphia.annotations.Entity;

@Entity("group_id")
public class GroupId {
    private int groupId;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public GroupId(){}

    public GroupId(int groupId) {
        this.groupId = groupId;
    }
}
