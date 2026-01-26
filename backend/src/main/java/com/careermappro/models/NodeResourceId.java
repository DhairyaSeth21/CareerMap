package com.careermappro.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for NodeResource
 */
public class NodeResourceId implements Serializable {

    private Integer nodeId;
    private Integer resourceId;

    // Default constructor
    public NodeResourceId() {
    }

    public NodeResourceId(Integer nodeId, Integer resourceId) {
        this.nodeId = nodeId;
        this.resourceId = resourceId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeResourceId that = (NodeResourceId) o;
        return Objects.equals(nodeId, that.nodeId) &&
               Objects.equals(resourceId, that.resourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, resourceId);
    }
}
