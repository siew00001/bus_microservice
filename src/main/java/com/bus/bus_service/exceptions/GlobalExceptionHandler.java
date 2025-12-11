package com.bus.bus_service.exceptions;

import com.bus.bus_service.controller.RouteController;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger routelogger = LoggerFactory.getLogger(RouteController.class);

    @ExceptionHandler(BusNumbersNotExistsException.class)
    public ResponseEntity<Map<String, Object>> handleBusNumbersNotExist(BusNumbersNotExistsException e, HttpServletRequest request) {
        routelogger.warn("{} request failed (422) - \"{}\" - Bus(es) {} do not exist.",
            request.getMethod(), request.getRequestURI(), e.getMissingNumbers());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "One or more buses with the given busNumbers do not exist");
        responseBody.put("missing_numbers", e.getMissingNumbers());

        return ResponseEntity.status(422).body(responseBody);
    }

    @ExceptionHandler(BusNumberNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBusNumberNotFound(BusNumberNotFoundException e, HttpServletRequest request) {
        routelogger.warn("{} request failed (404) - \"{}\" - Bus(es) {} do not exist.",
            request.getMethod(), request.getRequestURI(), e.getBusNumber());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "No bus with this busNumber found");
        responseBody.put("missing_Number", e.getBusNumber());

        return ResponseEntity.status(404).body(responseBody);
    }

    @ExceptionHandler(BusNotExistsException.class)
    public ResponseEntity<Map<String, Object>> handleBusNotFound(BusNotExistsException e, HttpServletRequest request) {
        routelogger.warn("{} request failed (404) - \"{}\" - Bus with ID \"{}\" not found",
            request.getMethod(), request.getRequestURI(), e.getBusId());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "No bus with this busId found");
        responseBody.put("missing_Id", e.getBusId());

        return ResponseEntity.status(404).body(responseBody);
    }

    @ExceptionHandler(RouteNotExistsException.class)
    public ResponseEntity<Map<String, Object>> handleRouteNotExists(RouteNotExistsException e, HttpServletRequest request) {
        routelogger.warn("{} request failed (404) - \"{}\" - Route with ID \"{}\" not found",
            request.getMethod(), request.getRequestURI(), e.getRouteId());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "No route with this routeId found");
        responseBody.put("missing_Id", e.getRouteId());

        return ResponseEntity.status(404).body(responseBody);
    }

    @ExceptionHandler(BusIsCurrentlyInUseException.class)
    public ResponseEntity<Map<String, Object>> handleIsCurrentlyInUse(BusIsCurrentlyInUseException e, HttpServletRequest request) {
        routelogger.warn("{} request failed (409) - \"{}\" - Bus {} is still in use by following routes: {}",
                request.getMethod(), request.getRequestURI(), e.getBusId(), e.getRouteIds());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "The bus is still in use by one or more routes.");
        responseBody.put("bus_using_routes", e.getRouteIds());

        return ResponseEntity.status(409).body(responseBody);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleInvalidArgument(MethodArgumentNotValidException e, HttpServletRequest request) {
        routelogger.warn("{} request failed ({}) - \"{}\" - Invalid Data Input",
            request.getMethod(), e.getStatusCode().value(), request.getRequestURI());
        return ResponseEntity.status(400).body(e.getBody());
    }

    @ExceptionHandler(BusNumberAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleBusNumberAlreadyExists(BusNumberAlreadyExistsException e, HttpServletRequest request) {
        routelogger.warn("{} request failed (409) - \"{}\" - Bus number \"{}\" is already in use by another bus.",
            request.getMethod(), request.getRequestURI(), e.getBusNumber());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Bus with this number already exists");
        responseBody.put("bus_Number", e.getBusNumber());

        return ResponseEntity.status(409).body(responseBody);
    }

    @ExceptionHandler(BusNumberAlreadyContainedException.class)
    public ResponseEntity<Map<String, Object>> handleBusNumberAlreadyContained(BusNumberAlreadyContainedException e,
                                                                               HttpServletRequest request) {
        routelogger.warn("{} request failed (409) - \"{}\" - Route id \"{}\" already contains the bus with bus number \"{}\"",
            request.getMethod(), request.getRequestURI(), e.getRouteId(), e.getBusNumber());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "The specified route already contains the specified bus");
        responseBody.put("route_Id", e.getRouteId());
        responseBody.put("bus_Number", e.getBusNumber());
        // Status code 409 = conflict status code
        return ResponseEntity.status(409).body(responseBody);
    }
}
