package com.medic_manager.app.common;

public class LoggerTextUtil {
    private static final String LIST_ALL_ENTITIES = "List all entities of %s.";
    private static final String CHECKING_IF_TO_INVALID = "Checking if TO invalid.";
    private static final String CREATE_NEW_ENTITY = "Creating new %s.";
    private static final String ERROR_NULL_OR_INCORRECT_TO_PASSED_AS_ARGUMENT_TO_METHOD = "ERROR: null or incorrect TO passed as argument to method";
    private static final String ERROR_ENTITY_WITH_PROPERTY_ALREADY_EXIST = "ERROR: Cannot create new %s with %s because it already exist.";

    private LoggerTextUtil() {
    }

    public static String getListAllEntities(Class<?> name) {
        return LIST_ALL_ENTITIES.formatted(name.getSimpleName());
    }

    public static String getCheckingIfToInvalid() {
        return CHECKING_IF_TO_INVALID;
    }

    public static String getCreateNewEntity(Class<?> name) {
        return CREATE_NEW_ENTITY.formatted(name.getSimpleName());
    }

    public static String getErrorNullOrIncorrectTOPassedAsArgumentToMethod() {
        return ERROR_NULL_OR_INCORRECT_TO_PASSED_AS_ARGUMENT_TO_METHOD;
    }

    public static String getErrorEntityWithPropertyAlreadyExist(Class<?> name, String property) {
        return ERROR_ENTITY_WITH_PROPERTY_ALREADY_EXIST.formatted(name.getSimpleName(), property);
    }
}