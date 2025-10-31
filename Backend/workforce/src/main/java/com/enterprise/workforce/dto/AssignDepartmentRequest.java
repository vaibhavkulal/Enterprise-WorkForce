package com.enterprise.workforce.dto;

import lombok.Data;

@Data
public class AssignDepartmentRequest {
    private Long employeeId;
    private Long departmentId;
}
