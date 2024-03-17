package Common;

/**
 * RequestStatus.java
 * <p>
 * Represents the status of an elevator request.
 * <p>
 * Possible values are: <p>
 * - {@link RequestStatus#PENDING}: Indicates that the request is pending and waiting for processing. <p>
 * - {@link RequestStatus#PASSENGER_PICKED_UP}: Indicates that the passenger has been picked up by the elevator.
 *
 * @version 2.0, March 17, 2024
 */
public enum RequestStatus {
    PENDING, PASSENGER_PICKED_UP
}