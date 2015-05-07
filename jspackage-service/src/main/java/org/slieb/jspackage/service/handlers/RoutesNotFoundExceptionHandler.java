package org.slieb.jspackage.service.handlers;


import spark.ExceptionHandler;
import spark.Request;
import spark.Response;

public class RoutesNotFoundExceptionHandler implements ExceptionHandler {


    @Override
    public void handle(Exception e, Request request, Response response) {
        response.status(404);
        response.body("The requested resource was not found.");
    }

    public static class ResourceNotFound extends Exception {

    }
}
