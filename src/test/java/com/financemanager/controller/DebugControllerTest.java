package com.financemanager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@AutoConfigureMockMvc
class DebugControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void debugSession_authUser_hasPrincipal_andSessionPresent() throws Exception {
        mockMvc.perform(get("/debug/session")
                        .with(SecurityMockMvcRequestPostProcessors.user("alice")))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.sessionId").value(notNullValue()))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.principal").value(is("alice")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.authorities[0]").value("ROLE_USER"));
    }

    @Test
    void debugSession_withSessionAttributes_returnsDetails() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("foo", "bar");

        mockMvc.perform(get("/debug/session")
                        .session(session)
                        .with(SecurityMockMvcRequestPostProcessors.user("bob")))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.sessionId").value(notNullValue()))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.sessionAttributes.foo").value(is("bar")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.principal").value(is("bob")));
    }

    @Test
    void directCall_noSession_noAuth_returnsNulls() {
        DebugController controller = new DebugController();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession(false)).thenReturn(null);

        ResponseEntity<java.util.Map<String,Object>> resp = controller.session(req);
        assertEquals(200, resp.getStatusCode().value());
        assertTrue(resp.getBody().containsKey("sessionId"));
        assertNull(resp.getBody().get("sessionId"));
        assertNull(resp.getBody().get("principal"));
    }
}
