package com.bankrolldiscipline.web.dui;

import java.util.List;
import java.util.Map;

/**
 * A small declarative UI component DTO for safe mock dashboard rendering.
 *
 * @param type component type, restricted by {@link DuiComponentRegistry}
 * @param props primitive component properties
 * @param children optional nested components
 */
public record DuiComponent(String type, Map<String, Object> props, List<DuiComponent> children) {

  public DuiComponent(String type, Map<String, Object> props) {
    this(type, props, List.of());
  }
}
