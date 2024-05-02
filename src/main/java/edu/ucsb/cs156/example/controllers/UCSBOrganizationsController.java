package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBOrganizations;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
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

@Tag(name = "UCSBOrganization")
@RequestMapping("/api/UCSBOrganization")
@RestController
@Slf4j
public class UCSBOrganizationsController extends ApiController {

    @Autowired
    UCSBOrganizationsRepository UCSBOrganizationsRepository;

    @Operation(summary= "List all ucsb organizations")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBOrganizations> allOrganizations() {
        Iterable<UCSBOrganizations> orgs = UCSBOrganizationsRepository.findAll();
        return orgs;
    }

    @Operation(summary= "Create a new organization in UCSB")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBOrganizations postUCSBOrganizations(
        @Parameter(name="orgCode") @RequestParam String orgCode,
        @Parameter(name="orgTranslationShort") @RequestParam String orgTranslationShort,
        @Parameter(name="orgTranslation") @RequestParam String orgTranslation,
        @Parameter(name="inactive") @RequestParam boolean inactive
        )
        {

        UCSBOrganizations org = new UCSBOrganizations();
        org.setOrgCode(orgCode);
        org.setOrgTranslationShort(orgTranslationShort);
        org.setOrgTranslation(orgTranslation);
        org.setInactive(inactive);

        UCSBOrganizations savedOrg = UCSBOrganizationsRepository.save(org);
        return savedOrg;
    }
    @Operation(summary= "Get a UCSB Organization")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBOrganizations getById(
            @Parameter(name="orgCode") @RequestParam String orgCode) {
                UCSBOrganizations org = UCSBOrganizationsRepository.findById(orgCode).orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, orgCode));
        return org;
    }

    @Operation(summary= "Delete a UCSBOrganization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteUCSBOrg(
            @Parameter(name="orgCode") @RequestParam String orgCode) {
                UCSBOrganizations org = UCSBOrganizationsRepository.findById(orgCode).orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, orgCode));

        UCSBOrganizationsRepository.delete(org);
        return genericMessage("UCSBOrganization with id %s deleted".formatted(orgCode));
     }
    
    @Operation(summary= "Update a single organizations")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBOrganizations updateCommons(
         @Parameter(name="orgCode") @RequestParam String orgCode,
         @RequestBody @Valid UCSBOrganizations incoming) {

         UCSBOrganizations org = UCSBOrganizationsRepository.findById(orgCode)
         .orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, orgCode));


         
         org.setOrgTranslationShort(incoming.getOrgTranslationShort());
         org.setOrgTranslation(incoming.getOrgTranslation());
         org.setInactive(incoming.getInactive());

         UCSBOrganizationsRepository.save(org);

         return org;
     }

}
    