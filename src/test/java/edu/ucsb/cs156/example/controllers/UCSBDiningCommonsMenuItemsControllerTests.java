package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
//import main.java.edu.ucsb.cs156.example.controllers.UCSBDiningCommonsMenuItemsController;
import edu.ucsb.cs156.example.ControllerTestCase;
//import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItems;
//import edu.ucsb.cs156.example.repositories.UCSBDateRepository;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemsRepository;

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

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBDiningCommonsMenuItemsController.class)
@Import(TestConfig.class)
public class UCSBDiningCommonsMenuItemsControllerTests extends ControllerTestCase {
     @MockBean
        UCSBDiningCommonsMenuItemsRepository ucsbDiningCommonsMenuItemsRepository;

        @MockBean
        UserRepository userRepository;

        // Tests for GET /api/ucsbdiningcommonsmenuitems/all
        
        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems/all"))
                                .andExpect(status().is(200)); // logged
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_ucsbdiningcommonsmenuitems() throws Exception {

                // arrange
                //LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                UCSBDiningCommonsMenuItems ucsbD1 = UCSBDiningCommonsMenuItems.builder()
                                .name("BakedPestoPastawithChicken")
                                .station("EntreeSpecials")
                                .diningCommonsCode("ortega")
                                .build();

                //LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

                UCSBDiningCommonsMenuItems ucsbD2 = UCSBDiningCommonsMenuItems.builder()
                                .name("TofuBanhMiSandwich(v)")
                                .station("EntreeSpecials")
                                .diningCommonsCode("ortega")
                                .build();

                ArrayList<UCSBDiningCommonsMenuItems> expectedDiningCommonsMenuItems = new ArrayList<>();
                expectedDiningCommonsMenuItems.addAll(Arrays.asList(ucsbD1, ucsbD2));

                when(ucsbDiningCommonsMenuItemsRepository.findAll()).thenReturn(expectedDiningCommonsMenuItems);

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbDiningCommonsMenuItemsRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedDiningCommonsMenuItems);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for POST /api/ucsbdiningcommonsmenuitems/post...

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbdiningcommonsmenuitems/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbdiningcommonsmenuitems/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_ucsbdiningcommonsmenuitems() throws Exception {
                // arrange

                //LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                 UCSBDiningCommonsMenuItems ucsbD1 = UCSBDiningCommonsMenuItems.builder()
                                .name("BakedPestoPastawithChicken")
                                .station("EntreeSpecials")
                                .diningCommonsCode("ortega")
                                .build(); 
        
                when(ucsbDiningCommonsMenuItemsRepository.save(eq(ucsbD1))).thenReturn(ucsbD1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/ucsbdiningcommonsmenuitems/post?name=BakedPestoPastawithChicken&station=EntreeSpecials&diningCommonsCode=ortega")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbDiningCommonsMenuItemsRepository, times(1)).save(ucsbD1);
                String expectedJson = mapper.writeValueAsString(ucsbD1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }
}
