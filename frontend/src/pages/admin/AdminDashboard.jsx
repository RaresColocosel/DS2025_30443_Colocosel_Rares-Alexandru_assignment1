import { useEffect, useState } from "react";
import { useAuth } from "../../auth/AuthContext";
import { apiRequest } from "../../services/api";
import NavBar from "../../components/Navbar";
import { useNavigate } from "react-router-dom"; // Import useNavigate

export default function AdminDashboard() {
    const { token } = useAuth();
    const navigate = useNavigate(); // Initialize hook

    const [users, setUsers] = useState([]);
    const [devices, setDevices] = useState([]);
    // ... existing state for forms ...
    const [userForm, setUserForm] = useState({ id: null, username: "", fullName: "", email: "", role: "CLIENT" });
    const [deviceForm, setDeviceForm] = useState({ id: null, name: "", description: "", maxConsumptionW: "" });
    const [selectedUserIdForAssign, setSelectedUserIdForAssign] = useState("");
    const [selectedDeviceIdForAssign, setSelectedDeviceIdForAssign] = useState("");
    const [error, setError] = useState("");

    // Load users & devices on mount
    useEffect(() => {
        const fetchData = async () => {
            try {
                const [usersData, devicesData] = await Promise.all([
                    apiRequest("/users", { token }),
                    apiRequest("/devices", { token }),
                ]);
                setUsers(usersData || []);
                setDevices(devicesData || []);
            } catch (err) {
                setError(err.message || "Failed to load admin data");
            }
        };
        fetchData();
    }, [token]);

    // ... (Keep all your existing CRUD handlers: handleUserSubmit, handleDeviceSubmit, etc.) ...
    // Assuming you have the previous code, I will focus on the Return statement where the button goes.

    // ... insert CRUD handlers here ...
    const handleUserChange = (field, value) => setUserForm(prev => ({...prev, [field]: value}));
    const handleUserReset = () => setUserForm({ id: null, username: "", fullName: "", email: "", role: "CLIENT" });
    const handleUserEdit = (u) => setUserForm({ id: u.id, username: u.username, fullName: u.fullName||"", email: u.email||"", role: u.role });
    const handleUserSubmit = async (e) => {
        e.preventDefault(); setError("");
        try {
            const body = { username: userForm.username, fullName: userForm.fullName, email: userForm.email, role: userForm.role };
            if(userForm.id) {
                await apiRequest(`/users/${userForm.id}`, { method: "PUT", body, token });
                setUsers(prev => prev.map(u => u.id === userForm.id ? { ...u, ...body } : u));
            } else {
                const created = await apiRequest("/users", { method: "POST", body, token });
                setUsers(prev => [...prev, created]);
            }
            handleUserReset();
        } catch(err) { setError(err.message); }
    };
    const handleUserDelete = async (id) => {
        if(!confirm("Delete?")) return;
        try { await apiRequest(`/users/${id}`, { method: "DELETE", token }); setUsers(prev => prev.filter(u => u.id !== id)); } catch(e) { setError(e.message); }
    };

    const handleDeviceChange = (field, value) => setDeviceForm(prev => ({...prev, [field]: value}));
    const handleDeviceReset = () => setDeviceForm({ id: null, name: "", description: "", maxConsumptionW: "" });
    const handleDeviceEdit = (d) => setDeviceForm({ id: d.id, name: d.name, description: d.description||"", maxConsumptionW: d.maxConsumptionW||"" });
    const handleDeviceSubmit = async (e) => {
        e.preventDefault(); setError("");
        try {
            const body = { name: deviceForm.name, description: deviceForm.description, maxConsumptionW: Number(deviceForm.maxConsumptionW) };
            if(deviceForm.id) {
                await apiRequest(`/devices/${deviceForm.id}`, { method: "PUT", body, token });
                setDevices(prev => prev.map(d => d.id === deviceForm.id ? { ...d, ...body } : d));
            } else {
                const created = await apiRequest("/devices", { method: "POST", body, token });
                setDevices(prev => [...prev, created]);
            }
            handleDeviceReset();
        } catch(err) { setError(err.message); }
    };
    const handleDeviceDelete = async (id) => {
        if(!confirm("Delete?")) return;
        try { await apiRequest(`/devices/${id}`, { method: "DELETE", token }); setDevices(prev => prev.filter(d => d.id !== id)); } catch(e) { setError(e.message); }
    };

    const handleAssign = async (e) => {
        e.preventDefault();
        try {
            await apiRequest(`/devices/${selectedDeviceIdForAssign}/assign`, { method: "POST", body: { userId: selectedUserIdForAssign }, token });
            alert("Assigned!");
        } catch(err) { setError(err.message); }
    };

    return (
        <div className="page">
            <NavBar />

            <main className="page-content">
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "20px" }}>
                    <h2>Admin Dashboard</h2>
                </div>

                {error && <p className="error-text">{error}</p>}

                <div className="grid-2">
                    {/* USERS Section */}
                    <section className="panel">
                        <h3>Manage Users</h3>
                        <form onSubmit={handleUserSubmit} className="panel-form">
                            <div className="form-row">
                                <label>Username <input value={userForm.username} onChange={e => handleUserChange("username", e.target.value)} required /></label>
                                <label>Full Name <input value={userForm.fullName} onChange={e => handleUserChange("fullName", e.target.value)} /></label>
                            </div>
                            <div className="form-row">
                                <label>Email <input type="email" value={userForm.email} onChange={e => handleUserChange("email", e.target.value)} /></label>
                                <label>Role
                                    <select value={userForm.role} onChange={e => handleUserChange("role", e.target.value)}>
                                        <option value="CLIENT">CLIENT</option>
                                        <option value="ADMIN">ADMIN</option>
                                    </select>
                                </label>
                            </div>
                            <div className="form-actions">
                                <button type="submit" className="btn-primary">{userForm.id ? "Update" : "Create"}</button>
                                <button type="button" className="btn-secondary" onClick={handleUserReset}>Clear</button>
                            </div>
                        </form>
                        <table className="data-table">
                            <thead><tr><th>ID</th><th>Username</th><th>Role</th><th>Actions</th></tr></thead>
                            <tbody>
                            {users.map(u => (
                                <tr key={u.id}>
                                    <td>{u.id}</td><td>{u.username}</td><td>{u.role}</td>
                                    <td>
                                        <button className="btn-small" onClick={() => handleUserEdit(u)}>Edit</button>
                                        <button className="btn-small btn-danger" onClick={() => handleUserDelete(u.id)}>Del</button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </section>

                    {/* DEVICES Section */}
                    <section className="panel">
                        <h3>Manage Devices</h3>
                        <form onSubmit={handleDeviceSubmit} className="panel-form">
                            <div className="form-row">
                                <label>Name <input value={deviceForm.name} onChange={e => handleDeviceChange("name", e.target.value)} required /></label>
                                <label>Max W <input type="number" value={deviceForm.maxConsumptionW} onChange={e => handleDeviceChange("maxConsumptionW", e.target.value)} /></label>
                            </div>
                            <div className="form-row"><label>Desc <input value={deviceForm.description} onChange={e => handleDeviceChange("description", e.target.value)} /></label></div>
                            <div className="form-actions">
                                <button type="submit" className="btn-primary">{deviceForm.id ? "Update" : "Create"}</button>
                                <button type="button" className="btn-secondary" onClick={handleDeviceReset}>Clear</button>
                            </div>
                        </form>
                        <table className="data-table">
                            <thead><tr><th>ID</th><th>Name</th><th>Max W</th><th>Actions</th></tr></thead>
                            <tbody>
                            {devices.map(d => (
                                <tr key={d.id}>
                                    <td>{d.id}</td><td>{d.name}</td><td>{d.maxConsumptionW}</td>
                                    <td>
                                        <button className="btn-small" onClick={() => handleDeviceEdit(d)}>Edit</button>
                                        <button className="btn-small btn-danger" onClick={() => handleDeviceDelete(d.id)}>Del</button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </section>
                </div>

                <section className="panel" style={{marginTop: "20px"}}>
                    <h3>Assign Device</h3>
                    <form onSubmit={handleAssign} className="panel-form">
                        <div className="form-row">
                            <label>User
                                <select value={selectedUserIdForAssign} onChange={e => setSelectedUserIdForAssign(e.target.value)}>
                                    <option value="">Select User</option>
                                    {users.map(u => <option key={u.id} value={u.id}>{u.username}</option>)}
                                </select>
                            </label>
                            <label>Device
                                <select value={selectedDeviceIdForAssign} onChange={e => setSelectedDeviceIdForAssign(e.target.value)}>
                                    <option value="">Select Device</option>
                                    {devices.map(d => <option key={d.id} value={d.id}>{d.name}</option>)}
                                </select>
                            </label>
                        </div>
                        <button className="btn-primary" type="submit">Assign</button>
                    </form>
                </section>
            </main>
        </div>
    );
}