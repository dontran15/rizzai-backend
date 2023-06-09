package com.nighthawk.spring_portfolio.database.person;

import org.hibernate.validator.internal.util.logging.formatter.ObjectArrayFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import com.nighthawk.spring_portfolio.database.ModelRepository;
import com.nighthawk.spring_portfolio.database.dating.DatingProfile;
import com.nighthawk.spring_portfolio.database.dating.DatingProfileJpaRepository;
import com.nighthawk.spring_portfolio.database.role.RoleJpaRepository;

import java.util.*;

import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api/person")
public class PersonApiController {
    /*
     * #### RESTful API ####
     * Resource: https://spring.io/guides/gs/rest-service/
     */

    // Autowired enables Control to connect HTML and POJO Object to database easily
    // for CRUD
    @Autowired
    private ModelRepository repository;

    // Individual Repositories for People and Dating
    // didn't want to mess up ModelRepository so i didn't add assignment/grade repos
    // there
    @Autowired
    private PersonJpaRepository personRepository;

    @Autowired
    private DatingProfileJpaRepository datingRepository;

    /*
     * GET List of People
     */
    @GetMapping("/all")
    public ResponseEntity<List<Person>> getPeople() {
        return new ResponseEntity<>(personRepository.findAllByOrderByNameAsc(), HttpStatus.OK);
    }

    /*
     * GET individual Person using ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getPerson(@PathVariable long id) {
        Person person = personRepository.findById(id).orElse(null);
        if (person == null) {
            return new ResponseEntity<>("person not found", HttpStatus.OK);
        }

        return new ResponseEntity<>(person, HttpStatus.OK);
    }

    /*
     * DELETE individual Person using ID
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deletePerson(@PathVariable long id) {
        Person person = personRepository.findById(id).orElse(null);
        if (person == null) {
            return new ResponseEntity<>("person not found", HttpStatus.OK);
        }

        DatingProfile profile = datingRepository.findByPerson(person);

        if (profile == null) {
            return new ResponseEntity<>("profile not found (should not happen)", HttpStatus.OK);
        }

        datingRepository.delete(profile);
        personRepository.delete(person);

        return new ResponseEntity<>("" + id + " and dating profile deleted", HttpStatus.OK);
    }

    /*
     * POST Aa record by Requesting Parameters from URI
     * 
     */
    @PostMapping("/post")
    public ResponseEntity<Object> postPerson(@RequestBody final Map<String, Object> map) {

        String email = (String) map.get("email");
        String password = (String) map.get("password");
        String name = (String) map.get("name");
        String dobString = (String) map.get("dobString");
        String pictureUrl = (String) map.get("pictureUrl");

        Date dob;

        Person personTest = personRepository.findByEmail(email);
        if (personRepository.findByEmail(email) != null) {
            return new ResponseEntity<>(email + " already exists, please make a new one", HttpStatus.BAD_REQUEST);
        }

        try {
            dob = new SimpleDateFormat("MM-dd-yyyy").parse(dobString);
        } catch (Exception e) {
            return new ResponseEntity<>(dobString + " error; try MM-dd-yyyy", HttpStatus.BAD_REQUEST);
        }

        Person person = new Person(email, password, name, dob, repository.findRole("ROLE_USER"));
        repository.save(person);

        DatingProfile profile = new DatingProfile(person, pictureUrl);
        datingRepository.save(profile);

        return new ResponseEntity<>(email + " is created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/getPersonRoles")
    public ResponseEntity<?> getPersonRoles(@RequestParam("email") String email) {
        Person person = personRepository.findByEmail(email);
        if (person == null) {
            return new ResponseEntity<>("person not found", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(person.getRoles(), HttpStatus.OK);
    }

    @GetMapping("/getPersonName")
    public ResponseEntity<?> getPersonName(@RequestParam("email") String email) {
        Person person = personRepository.findByEmail(email);
        if (person == null) {
            return new ResponseEntity<>("person not found", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(person.getName(), HttpStatus.OK);
    }

    @GetMapping("/getPersonAge")
    public ResponseEntity<?> getPersonAge(@RequestParam("email") String email) {
        Person person = personRepository.findByEmail(email);
        if (person == null) {
            return new ResponseEntity<>("person not found", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(person.getAge(), HttpStatus.OK);
    }
}