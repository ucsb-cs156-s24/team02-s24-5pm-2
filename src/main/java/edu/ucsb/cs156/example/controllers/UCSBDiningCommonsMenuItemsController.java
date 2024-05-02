package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.controllers.ApiController;
import edu.ucsb.cs156.example.entities.UCSBDate;
//import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItems;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
//import edu.ucsb.cs156.example.repositories.UCSBDateRepository;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.time.LocalDateTime;

@Tag(name = "ucsbdiningcommonsmenuitems")
@RequestMapping("/api/ucsbdiningcommonsmenuitems")
@RestController
@Slf4j

public class UCSBDiningCommonsMenuItemsController extends ApiController{
    @Autowired
    UCSBDiningCommonsMenuItemsRepository ucsbDiningCommonsMenuItemsRepository;
    @Operation(summary= "List all ucsb dining commons menu items")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBDiningCommonsMenuItems> allUCSBDiningCommonsMenuItems() {
        Iterable<UCSBDiningCommonsMenuItems> diningcommonsmenuitems = ucsbDiningCommonsMenuItemsRepository.findAll();
        return diningcommonsmenuitems;
    }

    @Operation(summary= "Create a new dining commons menu item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBDiningCommonsMenuItems postUCSBDiningCommonsMenuItems(
            @Parameter(name="diningCommonsCode") @RequestParam String diningCommonsCode,
            @Parameter(name="name") @RequestParam String name,
            @Parameter(name="station") @RequestParam String station)
            //@Parameter(name="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("localDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime localDateTime)
            throws JsonProcessingException {
        
        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        //log.info("localDateTime={}", localDateTime);

        UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItems = new UCSBDiningCommonsMenuItems();
        ucsbDiningCommonsMenuItems.setDiningCommonsCode(diningCommonsCode);
        ucsbDiningCommonsMenuItems.setName(name);
        ucsbDiningCommonsMenuItems.setStation(station);

        UCSBDiningCommonsMenuItems savedUcsbDiningCommonsMenuItems = ucsbDiningCommonsMenuItemsRepository.save(ucsbDiningCommonsMenuItems);
        return savedUcsbDiningCommonsMenuItems;
    }
}
