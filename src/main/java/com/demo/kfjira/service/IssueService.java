package com.demo.kfjira.service;

import com.demo.kfjira.dto.InternalIssue;
import com.mashape.unirest.http.exceptions.UnirestException;

public interface IssueService {
    String createIssue(InternalIssue issue) throws UnirestException;
}
