const duiRoot = document.querySelector("#dui-root");
const duiStatus = document.querySelector("[data-dui-status]");
const localWhitelist = new Set([
  "landingHero",
  "section",
  "metricCard",
  "warningBlock",
  "profileForm",
  "planForm",
  "activityForm",
  "dashboardSummary",
  "progressBar",
  "timeline",
  "statStrip",
  "textBlock",
  "fallback",
]);

const allowedProps = {
  landingHero: ["title", "body"],
  section: ["title", "body"],
  metricCard: ["title", "body"],
  warningBlock: ["title", "body", "level"],
  profileForm: [],
  planForm: [],
  activityForm: [],
  dashboardSummary: ["title", "period", "status", "limit", "spent", "remaining"],
  progressBar: ["label", "value", "level"],
  timeline: ["title", "items"],
  statStrip: ["title", "items"],
  textBlock: ["body"],
  fallback: ["title", "body"],
};

const storageKey = "bdm-local-flow";
const defaultState = {
  profile: {
    currency: "USD",
    monthlyIncome: "3200.00",
    mandatoryExpenses: "1800.00",
    savingsGoal: "350.00",
    emergencyContribution: "150.00",
    allocationPercentage: "10",
    timeZone: Intl.DateTimeFormat().resolvedOptions().timeZone || "UTC",
  },
  period: "WEEK",
  limit: "80.00",
  spent: "0.00",
  currentLimit: "",
};
const screenIntents = new Set(["profile", "plans", "sessions", "calendar", "statistics"]);

function readState() {
  try {
    const parsed = JSON.parse(window.localStorage.getItem(storageKey));
    return {
      ...defaultState,
      ...parsed,
      profile: {
        ...defaultState.profile,
        ...(parsed?.profile || {}),
      },
    };
  } catch {
    return defaultState;
  }
}

function writeState(nextState) {
  window.localStorage.setItem(storageKey, JSON.stringify(nextState));
}

function setStatus(message) {
  if (duiStatus) {
    duiStatus.textContent = message;
  }
}

function validateDuiResponse(payload) {
  if (!payload || typeof payload !== "object" || Array.isArray(payload)) {
    throw new Error("DUI payload must be an object.");
  }
  if (payload.version !== "1") {
    throw new Error("Unsupported DUI payload version.");
  }
  if (typeof payload.screen !== "string" || payload.screen.trim() === "") {
    throw new Error("DUI screen must be a string.");
  }
  if (!Array.isArray(payload.whitelist)) {
    throw new Error("DUI whitelist must be an array.");
  }
  const serverWhitelist = new Set(payload.whitelist);
  for (const type of serverWhitelist) {
    if (!localWhitelist.has(type)) {
      throw new Error(`Server whitelist contains unsupported component: ${type}`);
    }
  }
  if (!Array.isArray(payload.components)) {
    throw new Error("DUI components must be an array.");
  }
  payload.components.forEach((component) => validateComponent(component, serverWhitelist));
  return payload;
}

function validateComponent(component, serverWhitelist) {
  if (!component || typeof component !== "object" || Array.isArray(component)) {
    throw new Error("DUI component must be an object.");
  }
  if (!serverWhitelist.has(component.type) || !localWhitelist.has(component.type)) {
    throw new Error(`Blocked component: ${component.type}`);
  }
  if (!component.props || typeof component.props !== "object" || Array.isArray(component.props)) {
    throw new Error(`Component ${component.type} props must be an object.`);
  }
  const propsWhitelist = allowedProps[component.type] || [];
  Object.keys(component.props).forEach((property) => {
    if (!propsWhitelist.includes(property)) {
      throw new Error(`Blocked property: ${component.type}.${property}`);
    }
  });
  if (!Array.isArray(component.children)) {
    throw new Error(`Component ${component.type} children must be an array.`);
  }
  Object.values(component.props).forEach((value) => {
    const isSafeValue = typeof value === "string"
        || typeof value === "number"
        || typeof value === "boolean"
        || (Array.isArray(value) && value.every((item) => typeof item === "string"));
    if (!isSafeValue) {
      throw new Error(`Blocked non-scalar property in ${component.type}`);
    }
  });
  component.children.forEach((child) => validateComponent(child, serverWhitelist));
}

function text(value, fallback = "") {
  return typeof value === "string" ? value : fallback;
}

function createElement(tag, options = {}, children = []) {
  const element = document.createElement(tag);
  if (options.className) {
    element.className = options.className;
  }
  if (options.textContent) {
    element.textContent = options.textContent;
  }
  if (options.attrs) {
    Object.entries(options.attrs).forEach(([name, value]) => {
      element.setAttribute(name, value);
    });
  }
  children.forEach((child) => element.append(child));
  return element;
}

function panel(kicker, title, body, className = "dui-card") {
  return createElement("article", { className }, [
    createElement("p", { className: "panel-kicker", textContent: kicker }),
    createElement("h4", { textContent: title }),
    createElement("p", { textContent: body }),
  ]);
}

function renderComponent(component) {
  const props = component.props;
  switch (component.type) {
    case "dashboardSummary":
      return renderDashboardSummary(props);
    case "profileForm":
      return renderProfileForm();
    case "planForm":
      return renderPlanForm();
    case "activityForm":
      return renderActivityForm();
    case "warningBlock":
      return renderWarningBlock(props);
    case "progressBar":
      return renderProgressBar(props);
    case "timeline":
      return renderTimeline(props);
    case "statStrip":
      return renderStatStrip(props);
    case "section":
      return renderSection(component);
    case "metricCard":
      return panel("Metric", text(props.title), text(props.body));
    case "fallback":
      return renderFallbackComponent(props);
    default:
      return panel("Blocked", "Component not rendered", "The component type is not allowed.");
  }
}

function renderDashboardSummary(props) {
  return createElement("article", { className: "dui-card dui-summary" }, [
    createElement("header", {}, [
      createElement("div", {}, [
        createElement("p", { className: "panel-kicker", textContent: text(props.period) }),
        createElement("h4", { textContent: text(props.title) }),
      ]),
      createElement("span", { className: "dui-status", textContent: text(props.status) }),
    ]),
    summaryRow("Limit", text(props.limit)),
    summaryRow("Spent", text(props.spent)),
    summaryRow("Remaining", text(props.remaining)),
  ]);
}

function summaryRow(label, value) {
  return createElement("div", { className: "summary-row" }, [
    createElement("span", { textContent: label }),
    createElement("strong", { className: "dui-value", textContent: value }),
  ]);
}

function renderProfileForm() {
  const state = readState();
  return formCard("Financial profile", "Money fields are sent as strings and parsed in Java.", [
    input("Monthly income", "monthlyIncome", state.profile.monthlyIncome),
    input("Mandatory expenses", "mandatoryExpenses", state.profile.mandatoryExpenses),
    input("Savings goal", "savingsGoal", state.profile.savingsGoal),
    input("Emergency contribution", "emergencyContribution", state.profile.emergencyContribution),
    input("Allocation %", "allocationPercentage", state.profile.allocationPercentage),
    input("Currency", "currency", state.profile.currency, { readonly: true }),
    input("Time zone", "timeZone", state.profile.timeZone),
  ]);
}

function renderPlanForm() {
  const state = readState();
  return formCard("Planning period", "Choose a week or month and set a strict active limit.", [
    select("Period", "period", state.period, ["WEEK", "MONTH"]),
    input("Planned limit", "limit", state.limit),
    input("Current active limit", "currentLimit", state.currentLimit, {
      placeholder: "empty for first plan",
    }),
  ]);
}

function renderActivityForm() {
  const state = readState();
  return formCard("Bankroll activity", "Record current spending and evaluate warning state.", [
    input("Spent in this period", "spent", state.spent),
    createElement("button", {
      className: "button button-primary form-submit",
      textContent: "Evaluate local plan",
      attrs: { type: "submit" },
    }),
  ]);
}

function formCard(title, body, fields) {
  return createElement(
      "form",
      { className: "dui-card dui-form", attrs: { "data-dui-form": "" } },
      [
        createElement("div", { className: "dui-card-heading" }, [
          createElement("p", { className: "panel-kicker", textContent: "Local mock flow" }),
          createElement("h4", { textContent: title }),
          createElement("p", { textContent: body }),
        ]),
        createElement("div", { className: "field-grid" }, fields),
      ]);
}

function input(label, name, value, options = {}) {
  return createElement("label", { className: "field" }, [
    createElement("span", { textContent: label }),
    createElement("input", {
      attrs: {
        name,
        value,
        autocomplete: "off",
        placeholder: options.placeholder || "",
        ...(options.readonly ? { readonly: "readonly" } : {}),
      },
    }),
  ]);
}

function select(label, name, value, options) {
  const selectElement = createElement("select", { attrs: { name } });
  options.forEach((option) => {
    const optionElement = createElement("option", { textContent: option, attrs: { value: option } });
    if (option === value) {
      optionElement.selected = true;
    }
    selectElement.append(optionElement);
  });
  return createElement("label", { className: "field" }, [
    createElement("span", { textContent: label }),
    selectElement,
  ]);
}

function renderWarningBlock(props) {
  const level = text(props.level, "neutral");
  const tone = level === "safe" ? "success" : level;
  return panel(
      level,
      text(props.title),
      text(props.body),
      `dui-card dui-tone-${tone}`);
}

function renderProgressBar(props) {
  const value = text(props.value, "0%");
  return createElement("article", { className: "dui-card" }, [
    createElement("p", { className: "panel-kicker", textContent: text(props.label) }),
    createElement("div", { className: "progress-track" }, [
      createElement("div", {
        className: `progress-fill progress-${text(props.level, "safe")}`,
        attrs: { style: `width: ${clampPercent(value)}%;` },
      }),
    ]),
    createElement("strong", { className: "dui-value", textContent: value }),
  ]);
}

function renderFallbackComponent(props) {
  return createElement("article", { className: "dui-card fallback-panel" }, [
    createElement("div", { className: "dui-card-heading" }, [
      createElement("p", { className: "panel-kicker", textContent: "Safety check" }),
      createElement("h4", { textContent: text(props.title, "Safe fallback") }),
      createElement("p", { textContent: text(props.body) }),
    ]),
    createElement("div", { className: "fallback-actions" }, [
      createElement("button", {
        className: "button button-primary",
        textContent: "Restore normal dashboard",
        attrs: { type: "button", "data-dui-intent": "dashboard" },
      }),
      createElement("span", {
        textContent: "This is a deliberate developer safety test, not user data loss.",
      }),
    ]),
  ]);
}

function renderTimeline(props) {
  const items = Array.isArray(props.items) ? props.items : [];
  return createElement("article", { className: "dui-card dui-timeline" }, [
    createElement("p", { className: "panel-kicker", textContent: "Flow" }),
    createElement("h4", { textContent: text(props.title, "Timeline") }),
    createElement("ol", {}, items.map((item, index) => {
      return createElement("li", {}, [
        createElement("span", {
          className: "timeline-index",
          textContent: String(index + 1).padStart(2, "0"),
        }),
        createElement("strong", { textContent: text(item) }),
      ]);
    })),
  ]);
}

function renderStatStrip(props) {
  const items = Array.isArray(props.items) ? props.items : [];
  return createElement("article", { className: "dui-card dui-stat-strip" }, [
    createElement("p", { className: "panel-kicker", textContent: "Overview" }),
    createElement("h4", { textContent: text(props.title, "Overview") }),
    createElement("div", { className: "stat-strip-grid" }, items.map((item) => {
      const [value, ...labelParts] = text(item).split(" ");
      return createElement("div", { className: "stat-pill" }, [
        createElement("strong", { textContent: value || item }),
        createElement("span", { textContent: labelParts.join(" ") || "status" }),
      ]);
    })),
  ]);
}

function renderSection(component) {
  return createElement("article", { className: "dui-card dui-section-card" }, [
    createElement("div", { className: "dui-card-heading" }, [
      createElement("p", { className: "panel-kicker", textContent: "Context" }),
      createElement("h4", { textContent: text(component.props.title) }),
      createElement("p", { textContent: text(component.props.body) }),
    ]),
    ...component.children.map(renderComponent),
  ]);
}

function clampPercent(value) {
  const numeric = Number.parseFloat(value.replace("%", ""));
  if (Number.isNaN(numeric)) {
    return 0;
  }
  return Math.max(0, Math.min(100, numeric));
}

function renderPayload(payload) {
  const validated = validateDuiResponse(payload);
  duiRoot.replaceChildren(...validated.components.map(renderComponent));
  setStatus(validated.fallback ? "Fallback test rendered" : "Validated");
  bindControlState();
}

function renderLocalFallback(message) {
  duiRoot.replaceChildren(renderFallbackComponent({
    title: "Renderer rejected the payload",
    body: message,
  }));
  setStatus("Fallback test rendered");
}

async function loadInitialScreen() {
  if (!duiRoot) {
    return;
  }
  const screen = document.querySelector(".app-shell")?.dataset.page || "dashboard";
  await loadScreen(screen);
}

async function loadScreen(screen) {
  if (!duiRoot) {
    return;
  }
  try {
    const response = await fetch(`/api/dui/intent?screen=${encodeURIComponent(screen)}`);
    renderPayload(await response.json());
  } catch (error) {
    renderLocalFallback(error.message);
  }
}

function collectState() {
  const formData = new FormData();
  document.querySelectorAll("[data-dui-form]").forEach((form) => {
    new FormData(form).forEach((value, key) => formData.set(key, value));
  });
  const previous = readState();
  return {
    profile: {
      currency: formData.get("currency") || previous.profile.currency,
      monthlyIncome: formData.get("monthlyIncome") || previous.profile.monthlyIncome,
      mandatoryExpenses: formData.get("mandatoryExpenses") || previous.profile.mandatoryExpenses,
      savingsGoal: formData.get("savingsGoal") || previous.profile.savingsGoal,
      emergencyContribution:
          formData.get("emergencyContribution") || previous.profile.emergencyContribution,
      allocationPercentage:
          formData.get("allocationPercentage") || previous.profile.allocationPercentage,
      timeZone: formData.get("timeZone") || previous.profile.timeZone,
    },
    period: formData.get("period") || previous.period,
    limit: formData.get("limit") || previous.limit,
    spent: formData.get("spent") || previous.spent,
    currentLimit: formData.get("currentLimit") || previous.currentLimit,
  };
}

async function submitState(nextState) {
  try {
    const response = await fetch("/api/dui/intent", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ screen: "dashboard", ...nextState }),
    });
    const payload = await response.json();
    renderPayload(payload);
    if (!payload.messages?.includes("Limit increase was rejected.") && !payload.fallback) {
      writeState({ ...nextState, currentLimit: nextState.limit });
    } else {
      writeState(nextState);
    }
  } catch (error) {
    renderLocalFallback(error.message);
  }
}

function scenario(intent) {
  const base = readState();
  if (intent === "warning") {
    return { ...base, limit: "100.00", spent: "92.00", currentLimit: "100.00" };
  }
  if (intent === "limit-reduction") {
    return { ...base, limit: "60.00", spent: "30.00", currentLimit: "90.00" };
  }
  return { ...base, limit: "80.00", spent: "20.00", currentLimit: "" };
}

document.addEventListener("submit", (event) => {
  if (event.target.matches("[data-dui-form]")) {
    event.preventDefault();
    submitState(collectState());
  }
});

document.addEventListener("click", (event) => {
  const button = event.target.closest("[data-dui-intent]");
  if (button) {
    document.querySelectorAll("[data-dui-intent]").forEach((item) => {
      item.classList.remove("is-active");
    });
    button.classList.add("is-active");
    if (button.dataset.duiIntent === "dashboard") {
      window.localStorage.removeItem(storageKey);
      loadScreen("dashboard");
      return;
    }
    if (screenIntents.has(button.dataset.duiIntent)) {
      loadScreen(button.dataset.duiIntent);
      return;
    }
    submitState(scenario(button.dataset.duiIntent));
  }
});

function bindControlState() {
  document.querySelectorAll("[data-dui-form] input, [data-dui-form] select").forEach((field) => {
    field.addEventListener("input", () => setStatus("Local edits pending"));
    field.addEventListener("change", () => setStatus("Local edits pending"));
  });
}

loadInitialScreen();
