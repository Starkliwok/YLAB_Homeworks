package com.Y_LAB.homework.model.audit;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Audit {

    private long id;
    private Long userId;
    private LocalDateTime localDateTime;
    private String action;

    public Audit(Long userId, String action) {
        this.userId = userId;
        this.localDateTime = LocalDateTime.now();
        this.action = action;
    }
}
