package com.xml.comparator;

import lombok.*;

@Data
@ToString
@NoArgsConstructor(access =  AccessLevel.PUBLIC)
@EqualsAndHashCode
public class EmployeeDiff {
    private String employeeId;
    private String diffTag;
    private String sourceValue;
    private String targetValue;

    public EmployeeDiff(String employeeId){
        this.employeeId = employeeId;
    }

    public EmployeeDiff(employee sourceBean, employee targetBean){
        if(sourceBean == null && targetBean != null){
            this.employeeId = targetBean.getId();
            setTargetValue("MISSING");
        } else  if(targetBean == null && sourceBean != null){
            this.employeeId = sourceBean.getId();
            setSourceValue("MISSING");
        } else {
            this.employeeId = sourceBean.getId();
            populateDiffTagAndValue(sourceBean,targetBean);
        }
    }

    private void populateDiffTagAndValue(employee sourceBean, employee targetBean){
        if(!sourceBean.getName().equals(targetBean.getName())){
            this.diffTag =  "name";
            this.sourceValue = sourceBean.getName();
            this.targetValue = targetBean.getName();
        } else if(!sourceBean.getDepartment().equals(targetBean.getDepartment())){
            this.diffTag = "department";
            this.sourceValue = sourceBean.getDepartment();
            this.targetValue = targetBean.getDepartment();
        } else if(!sourceBean.getPhone().equals(targetBean.getPhone())){
            this.diffTag = "phone";
            this.sourceValue = sourceBean.getPhone();
            this.targetValue = targetBean.getPhone();
        }

    }
}
