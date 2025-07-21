package com.tulip.host.service;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenericSpecification<T> {

    public Specification<T> filterBy(Map<String, Object> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters != null) {
                for (Map.Entry<String, Object> entry : filters.entrySet()) {
                    String fieldName = entry.getKey();
                    Object value = entry.getValue();

                    // Skip null or empty values
                    if (value == null || (value instanceof String && ((String) value).isEmpty())) {
                        continue;
                    }

                    try {
                        // Handle field name mapping for SystemDocument
                        if (root.getJavaType().equals(com.tulip.host.domain.SystemDocument.class)) {
                            if ("std".equals(fieldName)) {
                                fieldName = "classDetail.id";
                            }
                        }

                        // Handle nested fields (e.g., "uploads.id")
                        if (fieldName.contains(".")) {
                            String[] parts = fieldName.split("\\.");
                            Path<Object> path = root.get(parts[0]);
                            for (int i = 1; i < parts.length; i++) {
                                path = path.get(parts[i]);
                            }

                            // Handle type conversion for nested fields
                            Object convertedValue = value;
                            if (value instanceof Number && path.getJavaType().equals(Long.class)) {
                                convertedValue = ((Number) value).longValue();
                            }

                            predicates.add(criteriaBuilder.equal(path, convertedValue));
                        } else {
                            // Handle direct fields (e.g., "type", "createdDateTime")
                            Field field = getField(root.getJavaType(), fieldName);
                            if (field != null) {
                                Class<?> fieldType = field.getType();
                                Object convertedValue = convertValue(fieldType, value);

                                if (isRangeField(fieldType) && value instanceof String) {
                                    // Handle range queries for dates/numbers (e.g., "min:100", "max:2025-01-01")
                                    if (((String) value).startsWith("min:")) {
                                        Object minValue = parseValue(fieldType, ((String) value).substring(4));
                                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(fieldName), (Comparable) minValue));
                                    } else if (((String) value).startsWith("max:")) {
                                        Object maxValue = parseValue(fieldType, ((String) value).substring(4));
                                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(fieldName), (Comparable) maxValue));
                                    } else if (convertedValue != null) {
                                        predicates.add(criteriaBuilder.equal(root.get(fieldName), convertedValue));
                                    }
                                } else if (convertedValue != null) {
                                    // Exact match for strings, enums, IDs, etc.
                                    predicates.add(criteriaBuilder.equal(root.get(fieldName), convertedValue));
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Log error and skip invalid field
                        System.err.println("Invalid field or value: " + fieldName + ", " + e.getMessage());
                    }
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

        private Field getField(Class<?> clazz, String fieldName) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                // Try superclass if field not found
                if (clazz.getSuperclass() != null) {
                    return getField(clazz.getSuperclass(), fieldName);
                }
                return null;
            }
        }

        private boolean isRangeField(Class<?> fieldType) {
            return Number.class.isAssignableFrom(fieldType) ||
                fieldType == LocalDateTime.class ||
                fieldType == java.util.Date.class;
        }

        private Object parseValue(Class<?> fieldType, String value) {
            if (fieldType == LocalDateTime.class) {
                return LocalDateTime.parse(value);
            } else if (fieldType == Double.class || fieldType == double.class) {
                return Double.parseDouble(value);
            } else if (fieldType == Integer.class || fieldType == int.class) {
                return Integer.parseInt(value);
            }
            return value; // Default to string or other types
        }

    private Object convertValue(Class<?> fieldType, Object value) throws Exception {
        if (value instanceof String && fieldType.isEnum()) {
            // Convert String to enum instance
            return Enum.valueOf((Class<? extends Enum>) fieldType, (String) value);
        }
        return value; // Return original value for non-enum fields
    }
}
