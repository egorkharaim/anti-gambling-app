package com.bankrolldiscipline.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ShellPageController.class)
class ShellPageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @ParameterizedTest
  @CsvSource({
    "'/', landing, Anti Gambling App",
    "'/dashboard', dashboard, Discipline Dashboard",
    "'/plans', plans, Bankroll Plans",
    "'/profile', profile, Financial Profile",
    "'/sessions', sessions, Game Sessions",
    "'/calendar', calendar, Discipline Calendar",
    "'/statistics', statistics, Statistics"
  })
  void shouldRenderShellPagesWithExpectedModel(
      String path, String activeNav, String pageTitle) throws Exception {
    mockMvc.perform(get(path))
        .andExpect(status().isOk())
        .andExpect(view().name("shell/page"))
        .andExpect(model().attribute("activeNav", activeNav))
        .andExpect(model().attribute("pageTitle", pageTitle));
  }
}
