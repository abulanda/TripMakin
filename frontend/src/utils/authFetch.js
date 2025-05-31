export async function authFetch(url, options = {}, retry = true) {
  const res = await fetch(url, { credentials: "include", ...options });
  if (res.status === 401 && retry) {
    const refreshRes = await fetch("/api/auth/refresh", { method: "POST", credentials: "include" });
    if (refreshRes.ok) {
      return authFetch(url, options, false);
    } else {
      localStorage.removeItem("userId");
      localStorage.removeItem("payload");
      window.location.href = "/login?sessionExpired=1";
      throw new Error("Sesja wygasła. Zaloguj się ponownie.");
    }
  }
  return res;
}