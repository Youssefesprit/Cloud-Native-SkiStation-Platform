package tn.esprit.spring.dto;


import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SubscriptionDTO {
    Long numSub;
    LocalDate startDate;
    LocalDate endDate;
    Float price;
    TypeSubscription typeSub;
}
