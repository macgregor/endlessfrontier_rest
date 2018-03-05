package com.macgregor.ef.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.macgregor.ef.dataload.annotations.Translate;
import io.swagger.annotations.ApiModel;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "unit_skill")
@JacksonXmlRootElement(localName = "unitSkill")
@ApiModel(value="UnitSkill", description="UnitSkill model describing unit skill in Endless Frontier.")
public class UnitSkill {

    @Id
    @Column(name="id", nullable = false)
    @NotNull
    @JsonProperty
    @JacksonXmlProperty(localName = "id")
    private Integer id;

    @Column(name = "skill_code", nullable = false)
    @NotBlank
    @JsonProperty
    @JacksonXmlProperty(localName = "skillCode")
    private String skillCode;

    @Column(name="img_index", nullable = false)
    @NotNull
    @JsonProperty
    @JacksonXmlProperty(localName = "imgIndex")
    private Integer imgIndex;

    @Column(name="base_value", nullable = false)
    @NotNull
    @JsonProperty
    @JacksonXmlProperty(localName = "baseValue")
    private Integer baseValue;

    @Column(name = "desc", nullable = false)
    @NotBlank
    @JsonProperty
    @JacksonXmlProperty(localName = "desc")
    @Translate(key="UNITSKILL_DESC_{id}")
    private String desc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSkillCode() {
        return skillCode;
    }

    public void setSkillCode(String skillCode) {
        this.skillCode = skillCode;
    }

    public Integer getImgIndex() {
        return imgIndex;
    }

    public void setImgIndex(Integer imgIndex) {
        this.imgIndex = imgIndex;
    }

    public Integer getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(Integer baseValue) {
        this.baseValue = baseValue;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "UnitSkill{" +
                "id=" + id +
                ", skillCode='" + skillCode + '\'' +
                ", imgIndex=" + imgIndex +
                ", baseValue=" + baseValue +
                ", desc='" + desc + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnitSkill unitSkill = (UnitSkill) o;

        if (id != null ? !id.equals(unitSkill.id) : unitSkill.id != null) return false;
        if (skillCode != null ? !skillCode.equals(unitSkill.skillCode) : unitSkill.skillCode != null) return false;
        if (imgIndex != null ? !imgIndex.equals(unitSkill.imgIndex) : unitSkill.imgIndex != null) return false;
        if (baseValue != null ? !baseValue.equals(unitSkill.baseValue) : unitSkill.baseValue != null) return false;
        return desc != null ? desc.equals(unitSkill.desc) : unitSkill.desc == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (skillCode != null ? skillCode.hashCode() : 0);
        result = 31 * result + (imgIndex != null ? imgIndex.hashCode() : 0);
        result = 31 * result + (baseValue != null ? baseValue.hashCode() : 0);
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        return result;
    }
}
