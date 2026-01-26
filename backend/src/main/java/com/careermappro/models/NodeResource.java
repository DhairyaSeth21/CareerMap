package com.careermappro.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Links constellation nodes to learning resources with priority
 */
@Entity
@Table(name = "node_resources")
@IdClass(NodeResourceId.class)
public class NodeResource {

    @Id
    @Column(name = "node_id")
    @JsonProperty("nodeId")
    private Integer nodeId;

    @Id
    @Column(name = "resource_id")
    @JsonProperty("resourceId")
    private Integer resourceId;

    @Column(name = "priority")
    private Integer priority = 1; // 1 = primary, 2 = alternative, etc.

    @Column(name = "added_at")
    @JsonProperty("addedAt")
    private LocalDateTime addedAt;

    // Constructors
    public NodeResource() {
        this.addedAt = LocalDateTime.now();
    }

    public NodeResource(Integer nodeId, Integer resourceId, Integer priority) {
        this();
        this.nodeId = nodeId;
        this.resourceId = resourceId;
        this.priority = priority;
    }

    // Getters and Setters
    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    @Override
    public String toString() {
        return "NodeResource{" +
                "nodeId=" + nodeId +
                ", resourceId=" + resourceId +
                ", priority=" + priority +
                ", addedAt=" + addedAt +
                '}';
    }
}
