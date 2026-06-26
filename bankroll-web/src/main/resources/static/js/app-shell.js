const appShell = document.querySelector(".app-shell");
const navToggle = document.querySelector(".mobile-nav-toggle");
const themeToggle = document.querySelector(".theme-toggle");
const darkTheme = "dark";
const lightTheme = "light";

function applyTheme(theme) {
  if (!appShell) {
    return;
  }

  const nextTheme = theme === lightTheme ? lightTheme : darkTheme;
  appShell.dataset.theme = nextTheme;

  if (themeToggle) {
    const isDark = nextTheme === darkTheme;
    themeToggle.setAttribute("aria-pressed", String(isDark));
    themeToggle.textContent = isDark ? "Light theme" : "Dark theme";
  }
}

if (appShell) {
  applyTheme(window.localStorage.getItem("bdm-theme"));
}

if (themeToggle) {
  themeToggle.addEventListener("click", () => {
    const nextTheme = appShell?.dataset.theme === darkTheme ? lightTheme : darkTheme;
    applyTheme(nextTheme);
    window.localStorage.setItem("bdm-theme", nextTheme);
  });
}

if (navToggle && appShell) {
  navToggle.addEventListener("click", () => {
    const isOpen = appShell.classList.toggle("nav-open");
    navToggle.setAttribute("aria-expanded", String(isOpen));
    navToggle.textContent = isOpen ? "Close" : "Menu";
  });
}

document.addEventListener("keydown", (event) => {
  if (event.key === "Escape" && appShell?.classList.contains("nav-open")) {
    appShell.classList.remove("nav-open");
    navToggle?.setAttribute("aria-expanded", "false");

    if (navToggle) {
      navToggle.textContent = "Menu";
    }
  }
});
