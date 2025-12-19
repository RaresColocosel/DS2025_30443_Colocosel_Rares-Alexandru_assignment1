const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "/api";

export async function apiRequest(path, { method = "GET", body, token } = {}) {
    const headers = { "Content-Type": "application/json" };
    if (token) headers["Authorization"] = `Bearer ${token}`;

    const res = await fetch(`${API_BASE_URL}${path}`, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
    });

    if (res.status === 204) return null;

    let data;
    try { data = await res.json(); } catch (e) { data = null; }

    if (!res.ok) {
        const message = (data && data.error) || (data && data.message) || res.statusText;
        throw new Error(message);
    }
    return data;
}

// NEW: Monitoring Data Fetcher
export const getMonitoringData = async (token, deviceId = null) => {
    const query = deviceId ? `?deviceId=${deviceId}` : "";
    return apiRequest(`/monitoring/consumption${query}`, { token });
};