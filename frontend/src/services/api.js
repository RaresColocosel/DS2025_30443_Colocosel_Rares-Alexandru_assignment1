// src/services/api.js

// In Docker/Traefik, backend is under /api.
// For local dev, you can set VITE_API_BASE_URL=http://localhost:8080/api
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "/api";

export async function apiRequest(path, { method = "GET", body, token } = {}) {
    const headers = {
        "Content-Type": "application/json",
    };

    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    const res = await fetch(`${API_BASE_URL}${path}`, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
    });

    // No content
    if (res.status === 204) {
        return null;
    }

    let data;
    try {
        data = await res.json();
    } catch (e) {
        data = null;
    }

    if (!res.ok) {
        const message = (data && data.error) || (data && data.message) || res.statusText;
        throw new Error(message);
    }

    return data;
}
