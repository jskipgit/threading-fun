package com.ironyard;


import com.ironyard.dto.ResponseObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

//import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
//@SpringBootTest(classes = WarRestController.class)
@SpringBootTest
@WebAppConfiguration
public class SpringJpaWebApplicationTests
{
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;


    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    protected String json(Object o) throws IOException
    {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    @Test
    public void contextLoads()
    {
        System.out.println("Hi I am there!");
    }

    @Before
    public void setUp() throws Exception
    {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void startGame_TypicalData() throws Exception
    {
        ResponseObject responseObject = new ResponseObject();
        Integer numberOfPlayers = 5;
        MvcResult result = this.mockMvc.perform(get("/rest/cards/startgame/" + numberOfPlayers.toString())
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.responseString", is( responseObject.getResponseString() )))
                .andReturn();

        System.out.println("response json=" + result.getResponse().getContentAsString() + "#");
    }


    @Test
    public void startGame_NonPositiveNumberOfPlayers() throws Exception
    {
        ResponseObject responseObject = new ResponseObject();
        Integer numberOfPlayers = -3;
        MvcResult result = this.mockMvc.perform(get("/rest/cards/startgame/" + numberOfPlayers.toString())
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.responseString", is( responseObject.getResponseString() )))
                .andReturn();

        System.out.println("response json=" + result.getResponse().getContentAsString() + "#");
    }


    @Test
    public void turn() throws Exception
    {

    }

}
