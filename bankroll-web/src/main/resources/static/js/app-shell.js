const shell = document.querySelector(".app-shell");
const navToggle = document.querySelector(".mobile-nav-toggle");

if (shell) {
  const storedTheme = window.localStorage.getItem("bdm-theme");
  if (storedTheme === "dark" || storedTheme === "light") {
    shell.dataset.theme = storedTheme;
  }
}

if (navToggle && shell) {
  navToggle.addEventListener("click", () => {
    const isOpen = shell.classList.toggle("nav-open");
    navToggle.setAttribute("aria-expanded", String(isOpen));
    navToggle.textContent = isOpen ? "Close" : "Menu";
  });
}
