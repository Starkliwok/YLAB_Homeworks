package com.Y_LAB.homework.model.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class Audit {

    private long id;

    private Long userId;

    private LocalDateTime localDateTime;

    private String className;

    private String methodName;

    private AuditResult result;

    public Audit(Long userId, String className, String methodName, AuditResult result) {
        this.userId = userId;
        this.localDateTime = LocalDateTime.now();
        this.className = className;
        this.methodName = methodName;
        this.result = result;
    }
}
