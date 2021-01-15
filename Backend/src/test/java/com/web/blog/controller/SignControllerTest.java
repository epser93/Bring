//package com.web.blog.controller;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//
//import javax.transaction.Transactional;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//public class SignControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    public void signin() throws Exception {
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("uid", "aaaaa@aaaaa.com");
//        params.add("password", "aaaaaaa");
//        mockMvc.perform(post("/sign/in").params(params))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.code").value(0))
//                .andExpect(jsonPath("$.msg").exists())
//                .andExpect(jsonPath("$.data").exists());
//    }
//
//    @Test
//    public void signup() throws Exception {
//        long epochTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("uid", "aaaaa" + epochTime + "@aaaaa.com");
//        params.add("password1", "aaaaaaa");
//        params.add("password2", "aaaaaaa");
//        params.add("name", "aaa" + epochTime);
//        params.add("nickname", "aaa" + epochTime);
//        mockMvc.perform(post("/sign/up").params(params))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.code").value(0))
//                .andExpect(jsonPath("$.msg").exists());
//    }
//}