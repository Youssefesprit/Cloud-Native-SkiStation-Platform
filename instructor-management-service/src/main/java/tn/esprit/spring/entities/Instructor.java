package tn.esprit.spring.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Instructor implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long numInstructor;

	String firstName;
	String lastName;
	LocalDate dateOfHire;

	// Store only the IDs of the courses (no direct relationship)
	@ElementCollection
	@CollectionTable(name = "instructor_courses", joinColumns = @JoinColumn(name = "instructor_id"))
	@Column(name = "course_id")
	Set<Long> courseIds; // This will store the IDs of the courses associated with this instructor
}
