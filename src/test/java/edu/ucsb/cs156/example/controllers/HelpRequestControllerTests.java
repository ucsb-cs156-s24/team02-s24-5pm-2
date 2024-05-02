package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.HelpRequest;
import edu.ucsb.cs156.example.repositories.HelpRequestRepository;

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

@WebMvcTest(controllers = HelpRequestController.class)
@Import(TestConfig.class)
public class HelpRequestControllerTests extends ControllerTestCase {

    @MockBean
    HelpRequestRepository helpRequestRepository;

    @MockBean
    UserRepository userRepository;    

        
    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/helprequest/all"))
                            .andExpect(status().is(403)); 
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/helprequest/all"))
                            .andExpect(status().is(200)); 
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_helprequests() throws Exception {

            // arrange
            LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

            HelpRequest helpReq = HelpRequest.builder()
                    .requesterEmail("adrumm@ucsb.edu")
                    .teamId("5pm-5")
                    .tableOrBreakoutRoom("10")
                    .requestTime(ldt)
                    .explanation("testingcontroller")
                    .solved(false)
                    .build();

            LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

            HelpRequest helpReq2 = HelpRequest.builder()
                    .requesterEmail("adrumm@ucsb.edu")
                    .teamId("5pm-5")
                    .tableOrBreakoutRoom("10")
                    .requestTime(ldt)
                    .explanation("controllertest2")
                    .solved(true)
                    .build();

            ArrayList<HelpRequest> expectedHelpRequests = new ArrayList<>();
            expectedHelpRequests.addAll(Arrays.asList(helpReq, helpReq2));

            when(helpRequestRepository.findAll()).thenReturn(expectedHelpRequests);

            MvcResult response = mockMvc.perform(get("/api/helprequest/all"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(helpRequestRepository, times(1)).findAll();
            String expectedJson = mapper.writeValueAsString(expectedHelpRequests);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @Test
    public void logged_out_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/helprequest/post"))
                            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/helprequest/post"))
                            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_helprequest() throws Exception {

            LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

            HelpRequest helpReq = HelpRequest.builder()
                    .requesterEmail("adrumm@ucsb.edu")
                    .teamId("5pm-5")
                    .tableOrBreakoutRoom("10")
                    .requestTime(ldt)
                    .explanation("testingcontroller")
                    .solved(true)
                    .build();

            when(helpRequestRepository.save(eq(helpReq))).thenReturn(helpReq);

            MvcResult response = mockMvc.perform(
                            post("/api/helprequest/post?requesterEmail=adrumm@ucsb.edu&teamId=5pm-5&tableOrBreakoutRoom=10&requestTime=2022-01-03T00:00:00&explanation=testingcontroller&solved=true")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(helpRequestRepository, times(1)).save(helpReq);
            String expectedJson = mapper.writeValueAsString(helpReq);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
            mockMvc.perform(get("/api/helprequest?id=123"))
                            .andExpect(status().is(403)); // logged out users can't get by id
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

            // arrange
            LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

            HelpRequest helpReq = HelpRequest.builder()
                    .requesterEmail("adrumm@ucsb.edu")
                    .teamId("5pm-5")
                    .tableOrBreakoutRoom("10")
                    .requestTime(ldt)
                    .explanation("testingcontroller")
                    .solved(true)
                    .build();

            when(helpRequestRepository.findById(eq(123L))).thenReturn(Optional.of(helpReq));

            MvcResult response = mockMvc.perform(get("/api/helprequest?id=123"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(helpRequestRepository, times(1)).findById(eq(123L));
            String expectedJson = mapper.writeValueAsString(helpReq);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

            // arrange

            when(helpRequestRepository.findById(eq(123L))).thenReturn(Optional.empty());

            MvcResult response = mockMvc.perform(get("/api/helprequest?id=123"))
                            .andExpect(status().isNotFound()).andReturn();

            // assert

            verify(helpRequestRepository, times(1)).findById(eq(123L));
            Map<String, Object> json = responseToJson(response);
            assertEquals("EntityNotFoundException", json.get("type"));
            assertEquals("HelpRequest with id 123 not found", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_edit_an_existing_helprequest() throws Exception {
            // arrange

            LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");
            LocalDateTime ldt2 = LocalDateTime.parse("2023-01-03T00:00:00");

            HelpRequest helpReqOrig = HelpRequest.builder()
                    .requesterEmail("adrumm@ucsb.edu")
                    .teamId("5pm-5")
                    .tableOrBreakoutRoom("10")
                    .requestTime(ldt)
                    .explanation("testingputid")
                    .solved(true)
                    .build();

            HelpRequest helpReqEdited = HelpRequest.builder()
                    .requesterEmail("adrumm@umail.ucsb.edu")
                    .teamId("5pm-44")
                    .tableOrBreakoutRoom("1010")
                    .requestTime(ldt2)
                    .explanation("testingputidupdated")
                    .solved(false)
                    .build();

            String requestBody = mapper.writeValueAsString(helpReqEdited);

            when(helpRequestRepository.findById(eq(123L))).thenReturn(Optional.of(helpReqOrig));

            MvcResult response = mockMvc.perform(
                            put("/api/helprequest?id=123")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding("utf-8")
                                            .content(requestBody)
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            verify(helpRequestRepository, times(1)).findById(123L);
            verify(helpRequestRepository, times(1)).save(helpReqEdited);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(requestBody, responseString);
    }

    
    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_cannot_edit_helprequest_that_does_not_exist() throws Exception {

            LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

            HelpRequest helpReqEdited = HelpRequest.builder()
                    .requesterEmail("adrumm@umail.ucsb.edu")
                    .teamId("5pm-44")
                    .tableOrBreakoutRoom("1010")
                    .requestTime(ldt)
                    .explanation("blahblah")
                    .solved(false)
                    .build();

            String requestBody = mapper.writeValueAsString(helpReqEdited);

            when(helpRequestRepository.findById(eq(123L))).thenReturn(Optional.empty());

            MvcResult response = mockMvc.perform(
                            put("/api/helprequest?id=123")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding("utf-8")
                                            .content(requestBody)
                                            .with(csrf()))
                            .andExpect(status().isNotFound()).andReturn();

            // assert
            verify(helpRequestRepository, times(1)).findById(123L);
            Map<String, Object> json = responseToJson(response);
            assertEquals("HelpRequest with id 123 not found", json.get("message"));

    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_delete_a_helprequest() throws Exception {
            // arrange

            LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

            HelpRequest helpReq = HelpRequest.builder()
                        .requesterEmail("adrumm@umail.ucsb.edu")
                        .teamId("5pm-5")
                        .tableOrBreakoutRoom("10")
                        .requestTime(ldt)
                        .explanation("thisisgettingold")
                        .solved(true)
                        .build();

            when(helpRequestRepository.findById(eq(123L))).thenReturn(Optional.of(helpReq));

            MvcResult response = mockMvc.perform(
                            delete("/api/helprequest?id=123")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(helpRequestRepository, times(1)).findById(123L);
            verify(helpRequestRepository, times(1)).delete(any());

            Map<String, Object> json = responseToJson(response);
            assertEquals("HelpRequest with id 123 deleted", json.get("message"));
    }
    
    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_tries_to_delete_non_existant_helprequest_and_gets_right_error_message()
                    throws Exception {
            // arrange

            when(helpRequestRepository.findById(eq(123L))).thenReturn(Optional.empty());

            MvcResult response = mockMvc.perform(
                            delete("/api/helprequest?id=123")
                                            .with(csrf()))
                            .andExpect(status().isNotFound()).andReturn();

            // assert
            verify(helpRequestRepository, times(1)).findById(123L);
            Map<String, Object> json = responseToJson(response);
            assertEquals("HelpRequest with id 123 not found", json.get("message"));
    }
}