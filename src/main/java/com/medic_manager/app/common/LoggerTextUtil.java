package com.medic_manager.app.common;

public class LoggerTextUtil {
    private static final String LIST_ALL_ENTITIES = "List all entities of %s.";
    private static final String CHECKING_IF_TO_INVALID = "Checking if TO invalid.";
    private static final String CREATE_NEW_ENTITY = "Creating new %s with values %s.";
    private static final String UPDATE_ENTITY = "Updating %s with values %s.";
    private static final String GET_ENTITY_BY_ID = "Getting entity of %s with ID: %d.";
    private static final String ERROR_NULL_OR_INCORRECT_TO_PASSED_AS_ARGUMENT_TO_METHOD = "ERROR: null or incorrect TO passed as argument to method.";
    private static final String ERROR_NULL_PASSED_AS_ARGUMENT_TO_METHOD = "ERROR: null passed as argument to method.";
    private static final String ERROR_ENTITY_WITH_PROPERTY_ALREADY_EXIST = "ERROR: Cannot create new/update %s with %s because it already exist.";
    private static final String ERROR_ENTITY_WITH_ID_NOT_FOUND = "ERROR: entity of %s with ID: %d not found.";

    private LoggerTextUtil() {
    }

    public static String getListAllEntities(Class<?> name) {
        return LIST_ALL_ENTITIES.formatted(name.getSimpleName());
    }

    public static String getCheckingIfToInvalid() {
        return CHECKING_IF_TO_INVALID;
    }

    public static String getCreateNewEntity(Class<?> name, Record entityTo) {
        return CREATE_NEW_ENTITY.formatted(name.getSimpleName(), entityTo);
    }

    public static String getUpdateEntity(Class<?> name, Record entityTo) {
        return UPDATE_ENTITY.formatted(name.getSimpleName(), entityTo);
    }

    public static String getErrorNullOrIncorrectTOPassedAsArgumentToMethod() {
        return ERROR_NULL_OR_INCORRECT_TO_PASSED_AS_ARGUMENT_TO_METHOD;
    }

    public static String getErrorEntityWithPropertyAlreadyExist(Class<?> name, String property) {
        return ERROR_ENTITY_WITH_PROPERTY_ALREADY_EXIST.formatted(name.getSimpleName(), property);
    }

    public static String getGetEntityById(Class<?> name, Long id) {
        return GET_ENTITY_BY_ID.formatted(name.getSimpleName(), id);
    }

    public static String getErrorNullPassedAsArgumentToMethod() {
        return ERROR_NULL_PASSED_AS_ARGUMENT_TO_METHOD;
    }

    public static String getErrorEntityWithIdNotFound(Class<?> name, Long id) {
        return ERROR_ENTITY_WITH_ID_NOT_FOUND.formatted(name.getSimpleName(), id);
    }
}