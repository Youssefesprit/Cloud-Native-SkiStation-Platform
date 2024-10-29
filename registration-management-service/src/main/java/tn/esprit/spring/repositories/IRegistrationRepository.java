package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.query.Param;
import tn.esprit.spring.dto.CourseDTO;
import tn.esprit.spring.entities.*;

import java.util.List;

public interface IRegistrationRepository extends CrudRepository<Registration, Long> {

    long countByNumCourseAndNumWeek(Long numCourse, int numWeek);


    /* @Query("select reg.numWeek from Registration reg " +
            "join Instructor ins " +
            "on reg.course member ins.courses " +
            "where ins.numInstructor = :idIns and reg.course.support = :support")
    List<Integer> numWeeksCourseOfInstructorBySupport(@Param("idIns") Long numInstructor, @Param("support") Support support);
*/
    @Query("select count(distinct r) from Registration r " +
            "where r.numWeek = ?1 and r.numSkier = ?2 and r.numCourse = ?3")
    long countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(int numWeek, Long numSkier, Long numCourse);

  //  long countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(int numWeek, Long numSkier, Long numCourse);






}
