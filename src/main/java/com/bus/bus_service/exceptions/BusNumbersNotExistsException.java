package com.bus.bus_service.exceptions;

import java.util.List;

public class BusNumbersNotExistsException extends RuntimeException {

    List<Integer> missingNumbers;
    public BusNumbersNotExistsException(List<Integer> missingNumbers) {
        super("One or more buses could not be found.");
        this.missingNumbers = missingNumbers;
    }

    public List<Integer> getMissingNumbers() {
        return missingNumbers;
    }
}