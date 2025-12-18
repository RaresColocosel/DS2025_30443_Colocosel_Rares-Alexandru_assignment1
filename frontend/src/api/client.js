// Generic API client used everywhere in the frontend.

const API_BASE = import.meta.env.VITE_API_BASE || "/api";

export async function apiRequest(
    path,
    { method = "GET", body, token, extraHeaders = {} } = {}
) {
    const headers = {
        Accept: "application/json",
        ...extraHeaders,
    };

    let payload;
    if (body !== undefined && body !== null) {
        headers["Content-Type"] = "application/json";
        payload = JSON.stringify(body);
    }

    // ---- cache-buster for GET requests (avoids 304 with empty body) ----
    let url = `${API_BASE}${path}`;
    if (method === "GET") {
        const sep = url.includes("?") ? "&" : "?";
        url = `${url}${sep}_=${Date.now()}`;
    }

    const res = await fetch(url, {
        method,
        headers: {
            ...headers,
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
        body: payload,
    });

    const contentType = res.headers.get("content-type") || "";
    const isJson = contentType.toLowerCase().includes("application/json");

    // 304 (Not Modified) and 204 (No Content) -> no body to parse
    const hasBody = res.status !== 204 && res.status !== 304 && isJson;

    let data = null;
    if (hasBody) {
        try {
            data = await res.json();
        } catch (e) {
            console.error("Failed to parse JSON", e);
        }
    }

    if (res.status >= 400) {
        const msg =
            (data && (data.error || data.message)) ||
            `HTTP ${res.status} ${res.statusText}`;
        throw new Error(msg);
    }

    return data;
}
