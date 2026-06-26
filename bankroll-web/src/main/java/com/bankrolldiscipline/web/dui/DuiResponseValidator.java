package com.bankrolldiscipline.web.dui;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** Validates outgoing JSON before it can be rendered by the browser. */
@Component
public final class DuiResponseValidator {

  private static final int MAX_COMPONENTS = 20;
  private static final int MAX_DEPTH = 4;

  /** Validates the response and all nested components. */
  public boolean isValid(DuiIntentResponse response) {
    try {
      validate(response);
      return true;
    } catch (RuntimeException exception) {
      return false;
    }
  }

  private void validate(DuiIntentResponse response) {
    Objects.requireNonNull(response, "DUI response must not be null");
    requireText(response.version(), "DUI version must not be blank");
    requireText(response.screen(), "DUI screen must not be blank");
    Objects.requireNonNull(response.whitelist(), "DUI whitelist must not be null");
    Objects.requireNonNull(response.components(), "DUI components must not be null");
    Objects.requireNonNull(response.messages(), "DUI messages must not be null");
    if (!"1".equals(response.version())
        || response.components().isEmpty()
        || response.components().size() > MAX_COMPONENTS
        || !DuiComponentRegistry.allowedTypes().containsAll(response.whitelist())) {
      throw new IllegalArgumentException("DUI response shape is invalid");
    }
    response.components().forEach(component -> validateComponent(component, 0));
  }

  private void validateComponent(DuiComponent component, int depth) {
    Objects.requireNonNull(component, "DUI component must not be null");
    if (depth > MAX_DEPTH || !DuiComponentRegistry.isAllowed(component.type())) {
      throw new IllegalArgumentException("Component is not allowed: " + component.type());
    }
    Objects.requireNonNull(component.props(), "DUI component props must not be null");
    Objects.requireNonNull(component.children(), "DUI component children must not be null");
    validateProps(component.type(), component.props());
    validateChildren(component.children(), depth);
  }

  private void validateProps(String type, Map<String, Object> props) {
    for (Map.Entry<String, Object> entry : props.entrySet()) {
      if (!DuiComponentRegistry.isAllowedProperty(type, entry.getKey())
          || !isAllowedScalar(entry.getValue())) {
        throw new IllegalArgumentException("DUI property is not allowed");
      }
    }

    Object level = props.get("level");
    if (level instanceof String levelText && !DuiComponentRegistry.isAllowedLevel(levelText)) {
      throw new IllegalArgumentException("DUI semantic level is not allowed");
    }
  }

  private void validateChildren(List<DuiComponent> children, int depth) {
    if (children.size() > MAX_COMPONENTS) {
      throw new IllegalArgumentException("DUI component has too many children");
    }
    children.forEach(child -> validateComponent(child, depth + 1));
  }

  private boolean isAllowedScalar(Object value) {
    if (value instanceof String text) {
      return !text.isBlank();
    }
    if (value instanceof Number number) {
      return Double.isFinite(number.doubleValue());
    }
    if (value instanceof Boolean) {
      return true;
    }
    if (value instanceof List<?> list) {
      return list.size() <= 8 && list.stream().allMatch(item -> item instanceof String);
    }
    return false;
  }

  private void requireText(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(message);
    }
  }
}
