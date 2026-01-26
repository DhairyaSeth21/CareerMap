package com.careermappro.dto;

import com.careermappro.entities.UserSkillState.SkillStatus;

public class FrontierNode {
    private Integer skillId;
    private String skillName;
    private SkillStatus status;
    private Double confidence;
    private Double unlockPotential;
    private Double demandWeight;
    private Double score;
    private String why;

    public FrontierNode() {}

    public FrontierNode(Integer skillId, String skillName, SkillStatus status, Double confidence) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.status = status;
        this.confidence = confidence;
    }

    public Integer getSkillId() { return skillId; }
    public void setSkillId(Integer skillId) { this.skillId = skillId; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public SkillStatus getStatus() { return status; }
    public void setStatus(SkillStatus status) { this.status = status; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public Double getUnlockPotential() { return unlockPotential; }
    public void setUnlockPotential(Double unlockPotential) { this.unlockPotential = unlockPotential; }
    public Double getDemandWeight() { return demandWeight; }
    public void setDemandWeight(Double demandWeight) { this.demandWeight = demandWeight; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getWhy() { return why; }
    public void setWhy(String why) { this.why = why; }
}
