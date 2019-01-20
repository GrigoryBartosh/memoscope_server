package ru.memoscope.dataBase;

import java.util.Objects;

public class PostLink {
    private final long groupId;
    private final long postId;

    public PostLink(long groupId, long postId) {
        this.groupId = groupId;
        this.postId = postId;
    }

    public long getGroupId() {
        return groupId;
    }

    public long getPostId() {
        return postId;
    }

    @Override
    public String toString() {
        return String.format("{groupId: %s, postId: %s}", groupId, postId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostLink postLink = (PostLink) o;
        return groupId == postLink.groupId &&
                postId == postLink.postId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, postId);
    }
}
