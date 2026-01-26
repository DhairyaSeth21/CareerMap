package com.careermappro.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for UserResourceRating
 */
public class UserResourceRatingId implements Serializable {

    private Integer userId;
    private Integer resourceId;

    // Default constructor
    public UserResourceRatingId() {
    }

    public UserResourceRatingId(Integer userId, Integer resourceId) {
        this.userId = userId;
        this.resourceId = resourceId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserResourceRatingId that = (UserResourceRatingId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(resourceId, that.resourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, resourceId);
    }
}
