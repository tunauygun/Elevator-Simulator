package Common;

/**
 * SystemRequestType.java
 * <p>
 * Represents different types of system requests related to elevator control.
 *
 * @version 1.0, March 17, 2024
 */
public enum SystemRequestType {
    REGISTER_ELEVATOR_CONTROLLER,
    NEW_PRIMARY_REQUEST,
    IS_STOP_REQUIRED,
    PROCESSES_REQUESTS_AT_CURRENT_FLOOR,
    PROCESS_COMPLETED_REQUESTS,
    SET_FLOOR_DIRECTION_LAMPS,
    SET_FLOOR_LAMPS,
    ADD_NEW_REQUEST,
    STATUS_REQUEST
}
