const shell = document.querySelector(".app-shell");
const navToggle = document.querySelector(".mobile-nav-toggle");
const themeToggle = document.querySelector(".theme-toggle");
const darkTheme = "dark";
const lightTheme = "light";

function applyTheme(theme) {
  if (!shell) {
    return;
  }

  const nextTheme = theme === darkTheme ? darkTheme : lightTheme;
  shell.dataset.theme = nextTheme;

  if (themeToggle) {
    const isDark = nextTheme === darkTheme;
    themeToggle.setAttribute("aria-pressed", String(isDark));
    themeToggle.textContent = isDark ? "Light theme" : "Dark theme";
  }
}

if (shell) {
  const storedTheme = window.localStorage.getItem("bdm-theme");
  applyTheme(storedTheme);
}

if (themeToggle) {
  themeToggle.addEventListener("click", () => {
    const nextTheme = shell?.dataset.theme === darkTheme ? lightTheme : darkTheme;
    applyTheme(nextTheme);
    window.localStorage.setItem("bdm-theme", nextTheme);
  });
}

if (navToggle && shell) {
  navToggle.addEventListener("click", () => {
    const isOpen = shell.classList.toggle("nav-open");
    navToggle.setAttribute("aria-expanded", String(isOpen));
    navToggle.textContent = isOpen ? "Close" : "Menu";
  });
}

document.addEventListener("keydown", (event) => {
  if (event.key === "Escape" && shell?.classList.contains("nav-open")) {
    shell.classList.remove("nav-open");
    navToggle?.setAttribute("aria-expanded", "false");
    if (navToggle) {
      navToggle.textContent = "Menu";
    }
  }
});
