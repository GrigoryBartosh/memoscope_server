package ru.memoscope.dataBase;

public class PostLink {
    private long groupId;
    private long postId;

    public PostLink(long groupId, long postId) {
        this.groupId = groupId;
        this.postId = postId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public long getPostId() {
        return postId;
    }

    @Override
    public String toString() {
        return String.format("{groupId: %s, postId: %s}\n", groupId, postId);
    }
}
