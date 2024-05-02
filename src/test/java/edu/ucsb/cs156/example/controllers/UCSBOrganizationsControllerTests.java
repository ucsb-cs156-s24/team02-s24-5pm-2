package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBOrganizations;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBOrganizationsController.class)
@Import(TestConfig.class)
public class UCSBOrganizationsControllerTests extends ControllerTestCase {

    @MockBean
    UCSBOrganizationsRepository ucsbOrganizationsRepository;

    @MockBean
    UserRepository userRepository;

    // Tests for GET

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/UCSBOrganization/all")).andExpect(status().is(403));  
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
        mockMvc.perform(get("/api/UCSBOrganization/all")).andExpect(status().is(200)); 
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_ucsborganization() throws Exception {

       

        UCSBOrganizations KRC = UCSBOrganizations.builder()
                    .orgCode("KRC")
                    .orgTranslationShort("KOREAN RADIO CL")
                    .orgTranslation("KOREAN RADIO CLUB")
                    .inactive(false)
                    .build();

        UCSBOrganizations SKY = UCSBOrganizations.builder()
                    .orgCode("SKY")
                    .orgTranslationShort("SKYDIVING CLUB")
                    .orgTranslation("SKYDIVING CLUB AT UCSB")
                    .inactive(false)
                    .build();

        

        ArrayList<UCSBOrganizations> expectedOrgs = new ArrayList<>();
        expectedOrgs.addAll(Arrays.asList(KRC, SKY));

        when(ucsbOrganizationsRepository.findAll()).thenReturn(expectedOrgs);

        
        MvcResult response = mockMvc.perform(get("/api/UCSBOrganization/all")).andExpect(status().isOk()).andReturn();


        verify(ucsbOrganizationsRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedOrgs);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

   
    //These are  tests for posts
    @Test
    public void logged_out_users_cannot_post() throws Exception {
    mockMvc.perform(post("/api/UCSBOrganization/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
    mockMvc.perform(post("/api/UCSBOrganization/post"))
                .andExpect(status().is(403)); 
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_organizations() throws Exception {
    

    UCSBOrganizations KRC = UCSBOrganizations.builder()
                .orgCode("KRC")
                .orgTranslationShort("koreanradiocl")
                .orgTranslation("koreanradioclub")
                .inactive(true)
                .build();

    when(ucsbOrganizationsRepository.save(eq(KRC))).thenReturn(KRC);


    MvcResult response = mockMvc.perform(
                
                post("/api/UCSBOrganization/post?orgCode=KRC&orgTranslationShort=koreanradiocl&orgTranslation=koreanradioclub&inactive=true")
                                .with(csrf()))
                .andExpect(status().isOk()).andReturn();


    verify(ucsbOrganizationsRepository, times(1)).save(KRC);
    String expectedJson = mapper.writeValueAsString(KRC);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
    }

    //These are  tests for Get
    @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/UCSBOrganization?orgCode=carrillo"))
                                .andExpect(status().is(403)); 
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {


                UCSBOrganizations org = UCSBOrganizations.builder()
                                .orgCode("ortega")
                                .orgTranslationShort("Ortega")
                                .orgTranslation("Ortega")
                                .inactive(false)
                                .build();

                when(ucsbOrganizationsRepository.findById(eq("ortega"))).thenReturn(Optional.of(org));

                
                MvcResult response = mockMvc.perform(get("/api/UCSBOrganization?orgCode=ortega"))
                                .andExpect(status().isOk()).andReturn();

                

                verify(ucsbOrganizationsRepository, times(1)).findById(eq("ortega"));
                String expectedJson = mapper.writeValueAsString(org);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

              

                when(ucsbOrganizationsRepository.findById(eq("ortega"))).thenReturn(Optional.empty());

               
                MvcResult response = mockMvc.perform(get("/api/UCSBOrganization?orgCode=ortega"))
                                .andExpect(status().isNotFound()).andReturn();

                

                verify(ucsbOrganizationsRepository, times(1)).findById(eq("ortega"));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("UCSBOrganizations with id ortega not found", json.get("message"));
        }

        // Tests for DELETE 

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_org() throws Exception {
                

                UCSBOrganizations KRC = UCSBOrganizations.builder()
                                 .orgCode("KRC")
                                 .orgTranslationShort("koreanradioclub")
                                 .orgTranslation("koreanradioclub")
                                 .inactive(true)
                                 .build();

                when(ucsbOrganizationsRepository.findById(eq("KRC"))).thenReturn(Optional.of(KRC));

                
                MvcResult response = mockMvc.perform(
                    delete("/api/UCSBOrganization?orgCode=KRC").with(csrf())).andExpect(status().isOk()).andReturn();

                 
                    verify(ucsbOrganizationsRepository, times(1)).findById("KRC");
                    verify(ucsbOrganizationsRepository, times(1)).delete(any());

                    Map<String, Object> json = responseToJson(response);
                    assertEquals("UCSBOrganization with id KRC deleted", json.get("message"));
         }

         @WithMockUser(roles = { "ADMIN", "USER" })
         @Test
         public void admin_tries_to_delete_non_existant_organizations_and_gets_right_error_message()
                         throws Exception {
                 

                 when(ucsbOrganizationsRepository.findById(eq("munger-hall"))).thenReturn(Optional.empty());

                
                 MvcResult response = mockMvc.perform(
                                 delete("/api/UCSBOrganization?orgCode=munger-hall")
                                                 .with(csrf()))
                                 .andExpect(status().isNotFound()).andReturn();

               
                 verify(ucsbOrganizationsRepository, times(1)).findById("munger-hall");
                 Map<String, Object> json = responseToJson(response);
                 assertEquals("UCSBOrganizations with id munger-hall not found", json.get("message"));
         }

        // TESTS for PUT

         @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_organization() throws Exception {
              

                UCSBOrganizations carrilloOrig = UCSBOrganizations.builder()
                                
                                 .orgCode("KRC")
                                 .orgTranslationShort("koreanradioclub")
                                 .orgTranslation("koreanradioclub")
                                 .inactive(true)
                                 .build();
                                

                UCSBOrganizations carrilloEdited = UCSBOrganizations.builder()
                                 .orgCode("KRC")
                                 .orgTranslationShort("koreanradioclubs")
                                 .orgTranslation("koreanradioclubs")
                                 .inactive(false)
                                 .build();

                String requestBody = mapper.writeValueAsString(carrilloEdited);

                when(ucsbOrganizationsRepository.findById(eq("KRC"))).thenReturn(Optional.of(carrilloOrig));
                // when(ucsbOrganizationsRepository.findById(eq("KRCS"))).thenReturn(Optional.of(carrilloEdited));

                MvcResult response = mockMvc.perform(
                                put("/api/UCSBOrganization?orgCode=KRC")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                
                verify(ucsbOrganizationsRepository, times(1)).findById("KRC");
                verify(ucsbOrganizationsRepository, times(1)).save(carrilloEdited); 
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }


         @WithMockUser(roles = { "ADMIN", "USER" })
         @Test
         public void admin_cannot_edit_org_that_does_not_exist() throws Exception {
                 

                 UCSBOrganizations editedCommons = UCSBOrganizations.builder()
                                 .orgCode("munger-hall")
                                 .orgTranslationShort("Munger Hall")
                                 .orgTranslation("Munger Hall")
                                 .inactive(false)
                                 .build();

                 String requestBody = mapper.writeValueAsString(editedCommons);

                 when(ucsbOrganizationsRepository.findById(eq("munger-hall"))).thenReturn(Optional.empty());

               
                 MvcResult response = mockMvc.perform(
                                 put("/api/UCSBOrganization?orgCode=munger-hall")
                                                 .contentType(MediaType.APPLICATION_JSON)
                                                 .characterEncoding("utf-8")
                                                 .content(requestBody)
                                                 .with(csrf()))
                                 .andExpect(status().isNotFound()).andReturn();

                
                 verify(ucsbOrganizationsRepository, times(1)).findById("munger-hall");
                 Map<String, Object> json = responseToJson(response);
                 assertEquals("UCSBOrganizations with id munger-hall not found", json.get("message"));

         }


}