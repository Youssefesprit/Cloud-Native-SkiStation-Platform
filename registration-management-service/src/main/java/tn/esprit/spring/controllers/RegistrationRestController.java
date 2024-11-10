package tn.esprit.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Registration;
import tn.esprit.spring.services.IRegistrationServices;

import java.util.List;

@Tag(name = "\uD83D\uDDD3️Registration Management")
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationRestController {
    private final IRegistrationServices registrationServices;

    @Operation(description = "Add Registration and Assign to Skier")
    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/addAndAssignToSkier/{numSkieur}")
    public Registration addAndAssignToSkier(@RequestBody Registration registration,
                                                     @PathVariable("numSkieur") Long numSkieur)
    {
        return  registrationServices.addRegistrationAndAssignToSkier(registration,numSkieur);
    }
    @Operation(description = "Assign Registration to Course")
    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/assignToCourse/{numRegis}/{numCourse}")
    public Registration assignToCourse( @PathVariable("numRegis") Long numRegistration,
                                        @PathVariable("numCourse") Long numCourse){
        return registrationServices.assignRegistrationToCourse(numRegistration, numCourse);
    }


    @Operation(description = "Add Registration and Assign to Skier and Course")
    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/addAndAssignToSkierAndCourse/{numSkieur}/{numCourse}")
    public Registration addAndAssignToSkierAndCourse(@RequestBody Registration registration,
                                                     @PathVariable("numSkieur") Long numSkieur,
                                                     @PathVariable("numCourse") Long numCourse)
    {
        return  registrationServices.addRegistrationAndAssignToSkierAndCourse(registration,numSkieur,numCourse);
    }
    @Operation(description = "Retrieve Registration by Id")
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/get/{id-registration}")
    public Registration getById(@PathVariable("id-registration") Long numRegistration) {
        return registrationServices.retrieveRegistration(numRegistration);
    }
}
