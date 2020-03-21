package com.demo.kfjira.service;


import com.demo.kfjira.dto.InternalIssue;
import com.demo.kfjira.utils.JsonMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WebhookService {

    protected final static JsonMapper jsonMapper = JsonMapper.INSTANCE;
    @Autowired
    private IssueService issueService;

    public void handleWebhook(Map<String, Object> params) {
        InternalIssue internalIssue = jsonMapper.fromJson(params.get("message").toString(), InternalIssue.class);
        try {
            issueService.createIssue(internalIssue);
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

}
