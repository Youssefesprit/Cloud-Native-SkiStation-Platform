package tn.esprit.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long numCourse;
    private int level;
    private TypeCourse typeCourse;
    private Support support;
    private Float price;
    private int timeSlot;
}
