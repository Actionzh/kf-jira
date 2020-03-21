package com.demo.kfjira.service.impl;


import com.demo.kfjira.dto.InternalIssue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lexon.xlsImport.service.IssueService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class IssueServiceImpl implements IssueService {

    @Override
    public String createIssue(InternalIssue issue) throws UnirestException {
// The payload definition using the Jackson library
        String description = issue.getSummary();
        String summary = issue.getDescription();
        JsonNodeFactory jnf = JsonNodeFactory.instance;
        ObjectNode payload = jnf.objectNode();
        {
            ObjectNode fields = payload.putObject("fields");
            {
                fields.put("summary", summary);
//
                ObjectNode issuetype = fields.putObject("issuetype");
                {
                    issuetype.put("id", "10602");
                }

                ObjectNode project = fields.putObject("project");
                {
                    project.put("id", "10600");
                }
                fields.put("description", description);

                ObjectNode customfield_10701 = fields.putObject("customfield_10701");
                {
                    customfield_10701.put("id", "10500");
                }
//                fields.put("customfield_10701", "10500");
                ObjectNode reporter = fields.putObject("reporter");
                {
//                    reporter.put("id", "5b10a2844c20165700ede21g");
                    reporter.put("key", "huaming.fang");
                    reporter.put("name", "huaming.fang");
                }

//
                ObjectNode priority = fields.putObject("priority");
                {
                    priority.put("id", "3");
                }
            }
        }

// Connect Jackson ObjectMapper to Unirest
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            @Override
            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

// This code sample uses the  'Unirest' library:
// http://unirest.io/java.html
        HttpResponse<JsonNode> response = Unirest.post("https://jira.leadswarp.com/rest/api/2/issue")
                //直接用户名密码
//                .basicAuth("huaming.fang", "password")
                //header带Authorization方式，两种方式2选1
                .header("Authorization", "Basic aHVhbWluZy5mYW5nOlF3ZXJhc2RmMTAyMQ==")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body(payload)
                .asJson();

        System.out.println(response.getBody());
        return response.getBody().toString();
    }


}
