package tn.esprit.spring.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@FieldDefaults(level=AccessLevel.PRIVATE)
@Entity
public class Skier implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long numSkier;

	String firstName;
	String lastName;
	LocalDate dateOfBirth;
	String city;

	// Store the subscriptionId instead of the entire Subscription entity
	Long subscriptionId;

	// Store piste IDs for external relationship
	@ElementCollection
	@CollectionTable(name = "skier_pistes", joinColumns = @JoinColumn(name = "numSkier"))
	@Column(name = "numPiste")
	private Set<Long> pisteIds;

	// Store registration IDs for related registrations
	@ElementCollection
	@CollectionTable(name = "skier_registrations", joinColumns = @JoinColumn(name = "numSkier"))
	@Column(name = "registrationId")
	private Set<Long> registrationIds;






}
