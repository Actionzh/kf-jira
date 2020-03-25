package com.demo.kfjira.service;


import com.demo.kfjira.dto.InternalIssue;
import com.demo.kfjira.utils.JsonMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class WebhookService {

    protected final static JsonMapper jsonMapper = JsonMapper.INSTANCE;
    @Autowired
    private IssueService issueService;

    public void handleWebhook(Map<String, Object> params) {
        InternalIssue internalIssue = jsonMapper.fromJson(params.get("message").toString(), InternalIssue.class);
        try {
            issueService.createIssue(internalIssue);
        } catch (UnirestException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
