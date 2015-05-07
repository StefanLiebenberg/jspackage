package org.slieb.jspackage.service.handlers;

import spark.ExceptionHandler;
import spark.Request;
import spark.Response;


public class GeneralExceptionHandler implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request request, Response response) {
        response.status(500);
        response.body("Error occured: " + e.getMessage());
    }
}
