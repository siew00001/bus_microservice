package com.bus.bus_service.controller;

import com.bus.bus_service.dto.Bus;
import com.bus.bus_service.dto.BusMapper;
import com.bus.bus_service.entities.BusEntity;
import com.bus.bus_service.service.BusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/bus")
@Tag(name = "Bus Management", description = "API for managing buses")
public class BusController {

    private final BusMapper busMapper;
    private final BusService busService;

    public BusController(BusMapper busMapper, BusService busService){
        this.busMapper = busMapper;
        this.busService = busService;
    }

    private final Logger logger = LoggerFactory.getLogger(BusController.class);

    @Operation(summary = "Creates a new bus", description = "Adds a new bus to the database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bus successfully created",
            content = { @Content(mediaType = "application/json",
                schema = @Schema(implementation = Bus.class,
                examples = "{\"busId\": 1, \"busNumber\": 124, \"name\": \"Tom\", \"kmPrice\": 3.5, \"averageSpeed\": 50.0}")
            ) }),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json",
        examples = @ExampleObject(
            value = "{\"type\": \"about:blank\", \"title\": \"Bad Request\", \"status\": 400, \"detail\": \"Invalid request content.\", \"instance\": \"/api/v1/bus\"}"
        ))),
        @ApiResponse(responseCode = "409", description = "Bus with this number already exists",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(
                value = "{\"error\": \"Bus with this number already exists\", \"bus_Number\": 124}"
            )))
    })
    @PostMapping
    public ResponseEntity<Bus> postBus(
        @RequestBody(description = "Bus data for creation. The field 'busId' is ignored in the input.",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Bus.class),
            examples = {
                @ExampleObject(
                    name = "New Bus Request (No.: 124)",
                    value = "{\"busNumber\": 124, \"name\": \"Tom\", \"kmPrice\": 3.5, \"averageSpeed\": 50.0}"
                ),
                @ExampleObject(
                    name = "New Bus Request (No.: 122)",
                    value = "{\"busNumber\": 122, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 70.0}"
                ),
                @ExampleObject(
                    name = "New bus request (No.: 123, with specified ID), as the response shows the “busId” field is being ignored.",
                    value = "{\"busId\": 6,\"busNumber\": 123, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 90.0}",
                    summary = "New bus request (No.: 123, with specified ID)"
                ),
                @ExampleObject(
                    name = "Wrong Body",
                    value = "{\"geschwindigkeit\": 50, \"preis\": 2}"
                )
            }
        ))
        @Valid @org.springframework.web.bind.annotation.RequestBody Bus bus) {

        logger.info("Received POST request - \"/api/v1/bus\"");
        Optional<BusEntity> busEntityOptional = busService.createBus(busMapper.toEntity(bus));
        if(busEntityOptional.isPresent()){
            Bus busDTO = busMapper.toDTO(busEntityOptional.get());
            logger.info("POST request successful (200) - \"/api/v1/bus\"");
            return ResponseEntity.ok(busDTO);
        }

        logger.warn("POST request failed (500) - \"/api/v1/bus\" - Internal Server Error");
        return ResponseEntity.internalServerError().build();
    }

    @Operation(summary = "Updates an existing bus", description = "Updates the bus with the given busId in the URL.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bus successfully updated",
            content = { @Content(mediaType = "application/json",
                schema = @Schema(implementation = Bus.class,
                    example = "{\"busNumber\": 124, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 70.0}")
            ) }),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(
                value = "{\"type\": \"about:blank\", \"title\": \"Bad Request\", \"status\": 400, \"detail\": \"Invalid request content.\", \"instance\": \"/api/v1/bus/1\"}"
            ))),
        @ApiResponse(responseCode = "404", description = "No bus with this busId found",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(
                value="{\"error\": \"No bus with this busId found\", \"missing_Id\": 4}"
            ))),
        @ApiResponse(responseCode = "409", description = "Bus with this number already exists",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(
                value = "{\"error\": \"Bus with this number already exists\", \"bus_Number\": 124}"
            )))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Bus> updateBus(@Parameter(
        name = "id",
        description = "The Id of the bus to update")
        @PathVariable(value = "id") Long id,
        @RequestBody(description = "Bus data for updating. The field 'busId' is ignored in the input.",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Bus.class,
            example = "{\"busNumber\": 124, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 70.0}")))
        @Valid @org.springframework.web.bind.annotation.RequestBody Bus bus) {

        logger.info("Received PUT request - \"/api/v1/bus/{}\"", id);
        Optional<BusEntity> updatedBusOptional;
        updatedBusOptional = busService.updateBus(id, busMapper.toEntity(bus));
        if(updatedBusOptional.isPresent()){
            logger.info("PUT request successful (200) - \"/api/v1/bus/{}\"", id);
            return ResponseEntity.ok(busMapper.toDTO(updatedBusOptional.get()));
        }
        logger.warn("PUT request failed (500) - \"/api/v1/bus/{}\" - Internal Server Error", id);
        return ResponseEntity.internalServerError().build();
    }

    @Operation(summary = "Deletes a bus", description = "Deletes the bus with the given busId in the URL.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bus successfully deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "No bus with this busId found",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(
                value="{\"error\": \"No bus with this busId found\", \"missing_Id\": 4}"
            ))),
        @ApiResponse(responseCode = "409", description = "The bus is still in use by one or more routes.",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"The bus is still in use by one or more routes.\", \"bus_using_routes\": [1, 2]}"
                )))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBusById(@Parameter(
        name = "id",
        description = "The Id of the bus to delete")
        @PathVariable(value = "id") Long id
        ) {
        logger.info("Received DELETE request - \"/api/v1/bus/{}\"", id);
        busService.deleteBusById(id);
        logger.info("DELETE request successful (200) - \"/api/v1/bus/{}\"", id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Gets all buses",
        description = "Retrieves a list of all existing buses.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = { @Content(mediaType = "application/json",
                schema = @Schema(implementation = Bus.class,
                    example = "[{\"busId\": 1, \"busNumber\": 124, \"name\": \"Tom\", \"kmPrice\": 3.5, \"averageSpeed\": 50.0}"
                        + ",{\"busId\": 2, \"busNumber\": 129, \"name\": \"Max\", \"kmPrice\": 1.5, \"averageSpeed\": 70.0}]")
            ) })
    })
    @GetMapping
    public ResponseEntity<List<Bus>> getAllBusses() {
        logger.info("Received GET request - \"/api/v1/bus\"");

        List<Bus> busses = mapEntitiesToDTOS(busService.getAllBusses());
        logger.info("GET request successful (200) - \"/api/v1/bus\" - found {} busses", busses.size());
        return ResponseEntity.ok(busses);
    }

    private List<Bus> mapEntitiesToDTOS(List<BusEntity> busEntities) {
        return busEntities.stream().map(busMapper::toDTO).toList();
    }
}
