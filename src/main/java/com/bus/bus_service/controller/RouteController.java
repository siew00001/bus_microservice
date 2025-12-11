package com.bus.bus_service.controller;

import com.bus.bus_service.dto.Bus;
import com.bus.bus_service.dto.Route;
import com.bus.bus_service.dto.RouteCreationDTO;
import com.bus.bus_service.dto.RouteMapper;
import com.bus.bus_service.entities.BusEntity;
import com.bus.bus_service.entities.RouteEntity;
import com.bus.bus_service.service.BusService;
import com.bus.bus_service.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Route Management", description = "API for creating, updating, deleting routes. " +
        "Also allows to connect buses to routes")
@RequestMapping("/api/v1/route")
public class RouteController {

    private final RouteService routeService;
    private final RouteMapper routeMapper;
    private final BusService busService;

    public RouteController(RouteService routeService, RouteMapper routeMapper, BusService busService){
        this.routeService = routeService;
        this.routeMapper = routeMapper;
        this.busService = busService;
    }

    private final Logger logger = LoggerFactory.getLogger(RouteController.class);

    @Operation(summary = "Creates a new route", description = "Adds a new route with the given start, destination and bus-list to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Route successfully created - all buses are added to the route",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Route.class,
                                    example = "{\"routeId\": 1, \"start\": \"HTW-Saar\", \"destination\": \"Saarbrücken Hauptbahnhof\", "
                                            + "\"buses\": [{\"busId\": 1, \"busNumber\": 122, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 70}]}")
                    ) }),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                        value = "{\"type\": \"about:blank\", \"title\": \"Bad Request\", \"status\": 400, \"detail\": \"Invalid request content.\", \"instance\": \"/api/v1/bus\"}"
                    ))),
            @ApiResponse(responseCode = "422", description = "One or more buses with the given busNumbers do not exist",
                    content = { @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"One or more buses with the given busNumbers do not exist\", \"missing_numbers\": [41, 12]}"
                            )
                            )})
    })
    @PostMapping
    public ResponseEntity<Route> postRoute(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Route data for creation. The “busNumber” field specifies the buses that should use this route.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RouteCreationDTO.class),
                    examples = {
                            @ExampleObject(
                                    summary = "New Route with existing buses (124, 122)",
                                    value = "{\"start\": \"HTW-Saar\", \"destination\": \"Saarbrücken Hauptbahnhof\", \"busNumber\": [124, 122]}",
                                    name = "You need to create the example buses with the Number 124 and 122 for this request to work."
                            ),
                            @ExampleObject(
                                    summary = "New Route with one existing bus (123)",
                                    value = "{\"start\": \"HTW-Saar\", \"destination\": \"Rathaus\", \"busNumber\": [123]}",
                                    name = "You need to create the example bus with the Number 123 for this request to work."
                            ),
                            @ExampleObject(
                                    summary = "New Route with an non-existing bus)",
                                    value = "{\"start\": \"HTW-Saar\", \"destination\": \"Saarbrücken Hauptbahnhof\", \"busNumber\": [122, 355]}",
                                    name = "This test demonstrates an invalid request that returns a status code 404."
                            )
                    }
            ))
                                           @Valid @RequestBody RouteCreationDTO routeCreationDTO) {
        logger.info("Received POST request - \"/api/v1/route\"");
        List<Integer> busNumber = routeCreationDTO.busNumber();
        Optional<RouteEntity> routeEntityOptional = routeService.buildRoute(busNumber, routeCreationDTO.start(), routeCreationDTO.destination());
        if(routeEntityOptional.isPresent()){
            RouteEntity routeEntity = routeEntityOptional.get();
            Route routeDto = routeMapper.toDTO(routeService.createRoute(routeEntity));
            logger.info("POST request successful (200) - \"/api/v1/route\"");
            return ResponseEntity.ok(routeDto);
        }

        return ResponseEntity.internalServerError().build();
    }

    @Operation(summary = "Updates an existing route", description = "Updates an existing route with the given start, " +
            "destination and bus-list with the specified routeId in the URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Route successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Route.class,
                                    example = "{\"routeId\": 1, \"start\": \"HTW-Saar\", \"destination\": \"Saarbrücken Hauptbahnhof\", "
                                            + "\"buses\": [{\"busId\": 1, \"busNumber\": 122, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 70}]}")
                    ) }),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                        value = "{\"type\": \"about:blank\", \"title\": \"Bad Request\", \"status\": 400, \"detail\": \"Invalid request content.\", \"instance\": \"/api/v1/route/1\"}"
                    ))),
            @ApiResponse(responseCode = "404", description = "No route with the given Id",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                        value="{\"error\": \"No route with this routeId found\", \"missing_Id\": 4}"
                    ))),
            @ApiResponse(responseCode = "422", description = "One or more buses with the given busNumbers do not exist",
                    content = { @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{\"error\": \"One or more buses with the given busNumbers do not exist\", \"missing_numbers\": [41, 12]}"
                                    )
                    })})
    })
    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(
            @Parameter(
            name = "id",
            description = "The Id of the route to update",
            example = "2") @PathVariable(value = "id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Route data for updating. The field 'routeId' is ignored in the input.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RouteCreationDTO.class,
                                    example = "{\"start\": \"HTW-Saar\", \"destination\": \"Johanneskirche\", \"busNumber\": [123]}")))
            @Valid @RequestBody RouteCreationDTO routeCreationDTO) {
        logger.info("Received PUT request - \"/api/v1/route/{}\"", id);
        Optional<RouteEntity> updatedRouteOptional = routeService.buildRoute(routeCreationDTO.busNumber(), routeCreationDTO.start(), routeCreationDTO.destination());
        if(updatedRouteOptional.isPresent()){
            Optional<RouteEntity> routeOptional = routeService.updateRoute(id, updatedRouteOptional.get());
            if (routeOptional.isPresent()) {
                logger.info("PUT request successful (200) - \"/api/v1/route/{}\"", id);
                return ResponseEntity.ok(routeMapper.toDTO(routeOptional.get()));
            }
        }
        logger.warn("PUT request failed (500) - \"/api/v1/route/{}\" - Internal Server Error", id);
        return ResponseEntity.internalServerError().build();
    }

    @Operation(summary = "Adds a bus to a route", description = "Updates an existing route by adding the specified bus to it's "
        + "bus-list. <br /> For the example to work, you must to add the example bus with the number 123 and an example "
        + "route with the ID 1")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Route successfully updated - bus successfully added",
            content = { @Content(mediaType = "application/json",
                schema = @Schema(implementation = Route.class,
                    example = "{\"routeId\": 1, \"start\": \"HTW-Saar\", \"destination\": \"Saarbrücken Hauptbahnhof\", "
                        + "\"buses\": [{\"busId\": 1, \"busNumber\": 124, \"name\": \"Tom\", \"kmPrice\": 3.5, \"averageSpeed\": 50},"
                        + "{\"busId\": 2, \"busNumber\": 122, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 70},"
                        + "{\"busId\": 3, \"busNumber\": 123, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 90}]}")
            ) }),
        @ApiResponse(responseCode = "404",
            content = @Content(mediaType = "application/json",
                examples = {@ExampleObject(
                    name = "No route with this routeId found",
                    value="{\"error\": \"No route with this routeId found\", \"missing_Id\": 4}"
                ),
                @ExampleObject(
                    name = "No bus with this busNumber found",
                    value="{\"error\": \"No bus with this busNumber found\", \"missing_Number\": 4}"
                )}
            )),
        @ApiResponse(responseCode = "409", description = "One or more buses with the given busNumbers do not exist",
            content = { @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        value = "{\"bus_Number\": 123, \"route_Id\": 1, \"error\": \"The specified route already contains the specified bus\"}"
                    )
                })})
    })
    @PutMapping("/add_bus/{bus_number}/to_route/{id}")
    public ResponseEntity<Route> addBusToRoute(
        @Parameter(
            name = "id",
            description = "The Id of the route to update",
            example = "1")
        @PathVariable(value = "id") Long id,
        @Parameter(
            name = "bus_number",
            description = "The bus number of the bus to be added to the route",
            example = "123")
        @PathVariable(value = "bus_number") Integer busNumber){
        logger.info("Received PUT request - \"/api/v1/route/add_bus/{}/to_route/{}\"", busNumber, id);
        Optional<BusEntity> busEntityOptional = busService.getBusByBusNumber(busNumber);
        Optional<RouteEntity> routeEntityOptional = routeService.getRouteById(id);

        if (busEntityOptional.isEmpty() || routeEntityOptional.isEmpty()){
            logger.warn("PUT request failed (500) - \"/api/v1/add_bus/{}/to_route/{}\" - Internal Server Error", busNumber, id);
            return ResponseEntity.internalServerError().build();
        }

        RouteEntity routeEntity = routeEntityOptional.get();
        Optional<RouteEntity> updatedRouteEntityOptional = routeService.addBusToRoute(routeEntity, busEntityOptional.get());
        if(updatedRouteEntityOptional.isEmpty()){
            logger.warn("PUT request failed (500) - \"/api/v1/add_bus/{}/to_route/{}\" - Internal Server Error", busNumber, id);
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(routeMapper.toDTO(updatedRouteEntityOptional.get()));
    }

    @Operation(summary = "Deletes a route", description = "Deletes the route with the given routeId in the URL.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Route successfully deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "No route with this routeId found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(
                    value="{\"error\": \"No route with this routeId found\", \"missing_Id\": 4}"
                )))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRouteById(
        @Parameter(
            name = "id",
            description = "The Id of the route to delete")
        @PathVariable(value = "id") Long id) {
        logger.info("Received DELETE request - \"/api/v1/route/{}\"", id);
        routeService.deleteRouteById(id);
        logger.info("DELETE request successful (200) - \"/api/v1/route/{}\"", id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Gets all routes",
        description = "Retrieves a list of all existing routes. Each route has a bus list showing which bus uses that route.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = { @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Get Request without filtering",
                        value = "[{\"routeId\": 1, \"start\": \"HTW-Saar\", \"destination\": \"Saarbrücken Hauptbahnhof\", "
                            + "\"buses\": [{\"busId\": 1, \"busNumber\": 124, \"name\": \"Tom\", \"kmPrice\": 3.5, \"averageSpeed\": 50},"
                            + "{\"busId\": 2, \"busNumber\": 122, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 70}]},"
                            + "{\"routeId\": 2, \"start\": \"HTW-Saar\", \"destination\": \"Rathaus\", \"buses\": ["
                            + "{\"busId\": 3, \"busNumber\": 123, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 90}]}]"),
                    @ExampleObject(
                        name = "Get Request with speed-filtering",
                        value = "[{\"routeId\": 2, \"start\": \"HTW-Saar\", \"destination\": \"Rathaus\", \"buses\": ["
                            + "{\"busId\": 3, \"busNumber\": 123, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 90}]}]"),
                    @ExampleObject(
                        name = "Get Request with price-sorting",
                        value = "[{\"routeId\": 1, \"start\": \"HTW-Saar\", \"destination\": \"Saarbrücken Hauptbahnhof\", "
                            + "\"buses\": [{\"busId\": 2, \"busNumber\": 122, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 70}]},"
                            + "{\"routeId\": 2, \"start\": \"HTW-Saar\", \"destination\": \"Rathaus\", \"buses\": ["
                            + "{\"busId\": 3, \"busNumber\": 123, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 90}]},"
                            + "{\"routeId\": 1, \"start\": \"HTW-Saar\", \"destination\": \"Saarbrücken Hauptbahnhof\", "
                            + "\"buses\":[{\"busId\": 1, \"busNumber\": 124, \"name\": \"Tom\", \"kmPrice\": 3.5, \"averageSpeed\": 50}]}]")
                }
            )}
        )})
    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes(
        @Parameter(
            name = "filter",
            description = "A boolean parameter that specifies whether filtering is enabled."
                + "<br />Example URL: https://bus.edu.smef.io/api/v1/route?filter=true")
        @RequestParam(required = false, defaultValue = "false") Boolean filter,
        @Parameter(
            name = "sort",
            description = "A boolean parameter that specifies whether sorting is enabled."
                + "<br />Example URL: https://bus.edu.smef.io/api/v1/route?sort=true")
        @RequestParam(required = false, defaultValue = "false") Boolean sort,
        @Parameter(
            name = "criteria",
            description = "The criteria for sorting or filtering.",
            examples = {
                @ExampleObject(
                    name = "Price",
                    value = "price"
                ),
                @ExampleObject(
                    name = "Speed",
                    value = "speed"
                )
            })
        @RequestParam(required = false, defaultValue = "price") String criteria) {
        logger.info("Received GET request - \"/api/v1/route?filter={}&sort={}&criteria={}\"", filter, sort, criteria);

        List<Route> routes;

        if (filter) {
            routes = mapEntitiesToDTOS(routeService.filterRoutes(criteria, routeService.getAllRoutes()));
        } else if (sort) {
            routes = mapEntitiesToDTOS(routeService.sortRoutes(criteria, routeService.getAllRoutes()));
        } else {
            routes = mapEntitiesToDTOS(routeService.getAllRoutes());
        }
        logger.info("GET request successful (200) - \"/api/v1/route?filter={}&sort={}&criteria={}\" - found {} routes",
                filter, sort, criteria, routes.size());
        return ResponseEntity.ok(routes);
    }

    @Operation(summary = "Gets all routes starting from a specified location",
        description = "Retrieves a list of all existing routes that start at the specified location. "
            + "Each route has a bus list showing which bus uses that route.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = { @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Get Request with start = \"HTW-Saar\"",
                        value = "[{\"routeId\": 1, \"start\": \"HTW-Saar\", \"destination\": \"Saarbrücken Hauptbahnhof\", "
                            + "\"buses\": [{\"busId\": 1, \"busNumber\": 124, \"name\": \"Tom\", \"kmPrice\": 3.5, \"averageSpeed\": 50},"
                            + "{\"busId\": 2, \"busNumber\": 122, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 70}]},"
                            + "{\"routeId\": 2, \"start\": \"HTW-Saar\", \"destination\": \"Rathaus\", \"buses\": ["
                            + "{\"busId\": 3, \"busNumber\": 123, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 90}]}]")
                }
            )}
        )})
    @GetMapping("/from/{start}")
    public ResponseEntity<List<Route>> getRoutesFrom(
        @Parameter(
            name="start",
            description = "The route start location.",
            example = "HTW-Saar"
        )
        @PathVariable(value = "start") String start,
        @Parameter(
            name = "filter",
            description = "A boolean parameter that specifies whether filtering is enabled.")
        @RequestParam(required = false, defaultValue = "false") Boolean filter,
        @Parameter(
            name = "sort",
            description = "A boolean parameter that specifies whether sorting is enabled.")
        @RequestParam(required = false, defaultValue = "false") Boolean sort,
        @Parameter(
            name = "criteria",
            description = "The criteria for sorting or filtering.",
            examples = {
                @ExampleObject(
                    name = "Price",
                    value = "price"
                ),
                @ExampleObject(
                    name = "Speed",
                    value = "speed"
                )
            })
        @RequestParam(required = false, defaultValue = "price") String criteria) {
        logger.info("Received GET request - \"/api/v1/route/from/{}?filter={}&sort={}&criteria={}\"",
                start, filter, sort, criteria);
        List<Route> routes;
        if (filter) {
            routes = mapEntitiesToDTOS(routeService.filterRoutes(criteria, routeService.getRoutesByStart(start)));
        } else if (sort) {
            routes = mapEntitiesToDTOS(routeService.sortRoutes(criteria, routeService.getRoutesByStart(start)));
        } else {
            routes = mapEntitiesToDTOS(routeService.getRoutesByStart(start));
        }
        logger.info("GET request successful (200) - \"/api/v1/route/from/{}?filter={}&sort={}&criteria={}\" - found {} routes",
                start, filter, sort, criteria, routes.size());
        return ResponseEntity.ok(routes);
    }

    @Operation(summary = "Gets all routes leading to a specified location",
        description = "Retrieves a list of all existing routes leading to the specified location. "
            + "Each route has a bus list showing which bus uses that route.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = { @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Get Request with destination = \"Saarbrücken Hauptbahnhof\"",
                        value = "[{\"routeId\": 1, \"start\": \"HTW-Saar\", \"destination\": \"Saarbrücken Hauptbahnhof\", "
                            + "\"buses\": [{\"busId\": 1, \"busNumber\": 124, \"name\": \"Tom\", \"kmPrice\": 3.5, \"averageSpeed\": 50},"
                            + "{\"busId\": 2, \"busNumber\": 122, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 70}]}]")
                }
            )}
        )})
    @GetMapping("/to/{destination}")
    public ResponseEntity<List<Route>> getRoutesTo(
        @Parameter(
            name = "destination",
            description = "The route destination",
            example = "Saarbrücken Hauptbahnhof"
        )
        @PathVariable(value = "destination") String destination,
        @Parameter(
            name = "filter",
            description = "A boolean parameter that specifies whether filtering is enabled.")
        @RequestParam(required = false, defaultValue = "false") Boolean filter,
        @Parameter(
            name = "sort",
            description = "A boolean parameter that specifies whether sorting is enabled.")
        @RequestParam(required = false, defaultValue = "false") Boolean sort,
        @Parameter(
            name = "criteria",
            description = "The criteria for sorting or filtering.",
            examples = {
                @ExampleObject(
                    name = "Price",
                    value = "price"
                ),
                @ExampleObject(
                    name = "Speed",
                    value = "speed"
                )
            })
        @RequestParam(required = false, defaultValue = "price") String criteria) {
        logger.info("Received GET request - \"/api/v1/route/to/{}?filter={}&sort={}&criteria={}\"",
                destination,filter, sort, criteria);
        List<Route> routes;
        if (filter) {
            routes = mapEntitiesToDTOS(routeService.filterRoutes(criteria, routeService.
                    getRoutesByDestination(destination)));
        } else if (sort) {
            routes = mapEntitiesToDTOS(routeService.sortRoutes(criteria, routeService.
                    getRoutesByDestination(destination)));
        } else {
            routes = mapEntitiesToDTOS(routeService.getRoutesByDestination(destination));
        }
        logger.info("GET request successful (200) - \"/api/v1/route/to/{}?filter={}&sort={}&criteria={}\" - found {} routes",
                destination,filter, sort, criteria, routes.size());
        return ResponseEntity.ok(routes);
    }

    @Operation(summary = "Gets all routes starting from a specified location and leading to a specified location",
        description = "Retrieves a list of all existing routes starting from a specified location and leading to a specified location. "
            + "Each route has a bus list showing which bus uses that route.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = { @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Get Request with start = \"HTW-Saar\" and destination = \"Rathaus\"",
                        value = "[{\"routeId\": 2, \"start\": \"HTW-Saar\", \"destination\": \"Rathaus\", \"buses\": ["
                            + "{\"busId\": 3, \"busNumber\": 123, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 90}]}]")
                }
            )}
        )})
    @GetMapping("/from/{start}/to/{destination}")
    public ResponseEntity<List<Route>> getRoutesFromTo(
        @Parameter(
            name = "start",
            description = "The route start",
            example = "HTW-Saar"
        )
        @PathVariable(value = "start") String start,
        @Parameter(
            name = "destination",
            description = "The route destination",
            example = "Rathaus"
        )
        @PathVariable(value = "destination") String destination,
        @Parameter(
            name = "filter",
            description = "A boolean parameter that specifies whether filtering is enabled.")
        @RequestParam(required = false, defaultValue = "false") Boolean filter,
        @Parameter(
            name = "sort",
            description = "A boolean parameter that specifies whether sorting is enabled.")
        @RequestParam(required = false, defaultValue = "false") Boolean sort,
        @Parameter(
            name = "criteria",
            description = "The criteria for sorting or filtering.",
            examples = {
                @ExampleObject(
                    name = "Price",
                    value = "price"
                ),
                @ExampleObject(
                    name = "Speed",
                    value = "speed"
                )
            })
        @RequestParam(required = false, defaultValue = "price") String criteria) {
        logger.info("Received GET request - \"/api/v1/route/from/{}/to/{}?filter={}&sort={}&criteria={}\"",
                start, destination, filter, sort, criteria);
        List<Route> routes;
        if (filter) {
            routes = mapEntitiesToDTOS(routeService.filterRoutes(criteria,routeService.
                    getRoutesByStartAndDestination(start, destination)));
        } else if (sort) {
            routes = mapEntitiesToDTOS(routeService.sortRoutes(criteria, routeService.
                    getRoutesByStartAndDestination(start, destination)));
        } else {
            routes = mapEntitiesToDTOS(routeService.getRoutesByStartAndDestination(start, destination));
        }
        logger.info("GET request successful (200) - \"/api/v1/route/from/{}/to/{}?filter={}&sort={}&criteria={}\" " +
                        "- found {} routes",
                start, destination,filter, sort, criteria, routes.size());
        return ResponseEntity.ok(routes);

    }

    private List<Route> mapEntitiesToDTOS(List<RouteEntity> routeEntities) {
        return routeEntities.stream().map(routeMapper::toDTO).toList();
    }
}

