package com.careermappro.dto;

public class RecommendedAction {
    private String type; // PROBE, BUILD, APPLY
    private String label;
    private Integer skillId;
    private String skillName;
    private Integer estimatedMinutes;
    private Object payload;

    public RecommendedAction() {}

    public RecommendedAction(String type, String label) {
        this.type = type;
        this.label = label;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public Integer getSkillId() { return skillId; }
    public void setSkillId(Integer skillId) { this.skillId = skillId; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public Integer getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(Integer estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }
    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }
}
