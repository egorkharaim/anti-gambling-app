package com.bankrolldiscipline.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the initial application shell and placeholder pages while
 * application/domain contracts are still stabilizing.
 */
@Controller
public class ShellPageController {

  /** Renders the placeholder dashboard page. */
  @GetMapping("/")
  public String dashboard(Model model) {
    populatePage(
        model,
        "dashboard",
        "Discipline Dashboard",
        "Today at a glance",
        "A calm command center for limits, warnings, and active session context.");
    return "shell/page";
  }

  /** Renders the placeholder financial profile page. */
  @GetMapping("/profile")
  public String profile(Model model) {
    populatePage(
        model,
        "profile",
        "Financial Profile",
        "Income and protection rules",
        "This screen will host your profile inputs once the first use case"
            + " contract is locked.");
    return "shell/page";
  }

  /** Renders the placeholder bankroll plans page. */
  @GetMapping("/plans")
  public String plans(Model model) {
    populatePage(
        model,
        "plans",
        "Bankroll Plans",
        "Weekly and monthly boundaries",
        "Plan creation and reduction flows will plug into this shell after"
            + " domain rules are merged.");
    return "shell/page";
  }

  /** Renders the placeholder sessions page. */
  @GetMapping("/sessions")
  public String sessions(Model model) {
    populatePage(
        model,
        "sessions",
        "Game Sessions",
        "Start, finish, and review",
        "This area is reserved for session tracking, timeline views, and warning summaries.");
    return "shell/page";
  }

  /** Renders the placeholder discipline calendar page. */
  @GetMapping("/calendar")
  public String calendar(Model model) {
    populatePage(
        model,
        "calendar",
        "Discipline Calendar",
        "Pattern visibility",
        "Daily status mapping will appear here once the application layer"
            + " exposes calendar use cases.");
    return "shell/page";
  }

  /** Renders the placeholder statistics page. */
  @GetMapping("/statistics")
  public String statistics(Model model) {
    populatePage(
        model,
        "statistics",
        "Statistics",
        "Signal over noise",
        "Aggregated period metrics, not client-side guesses, will eventually power this view.");
    return "shell/page";
  }

  /** Populates shared page metadata for the generic shell template. */
  private void populatePage(
      Model model, String activeNav, String title, String eyebrow, String description) {
    model.addAttribute("activeNav", activeNav);
    model.addAttribute("pageTitle", title);
    model.addAttribute("pageEyebrow", eyebrow);
    model.addAttribute("pageDescription", description);
  }
}
