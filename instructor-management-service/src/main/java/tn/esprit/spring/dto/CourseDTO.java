package tn.esprit.spring.dto;

import lombok.Data;

@Data
public class CourseDTO {
    private Long numCourse;
    private int level;
    private String typeCourse;
    private String support;
    private Float price;
    private int timeSlot;
}
