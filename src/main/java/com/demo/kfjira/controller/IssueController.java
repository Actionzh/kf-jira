package com.demo.kfjira.controller;

import com.demo.kfjira.dto.InternalIssue;
import com.demo.kfjira.service.IssueService;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("issue")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @PostMapping("create")
    public String create(@RequestBody InternalIssue issue) throws UnirestException {
        return issueService.createIssue(issue);
    }
}
