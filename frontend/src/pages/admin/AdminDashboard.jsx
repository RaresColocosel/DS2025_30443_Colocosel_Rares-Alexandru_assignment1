import { useEffect, useState } from "react";
import { useAuth } from "../../auth/AuthContext";
import { apiRequest } from "../../services/api";
import NavBar from "../../components/Navbar";

export default function AdminDashboard() {
    const { token } = useAuth();

    const [users, setUsers] = useState([]);
    const [devices, setDevices] = useState([]);

    const [userForm, setUserForm] = useState({
        id: null,
        username: "",
        fullName: "",
        email: "",
        role: "CLIENT",
    });

    const [deviceForm, setDeviceForm] = useState({
        id: null,
        name: "",
        description: "",
        maxConsumptionW: "",
    });

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

    // -------- USERS CRUD --------
    const handleUserChange = (field, value) => {
        setUserForm((prev) => ({ ...prev, [field]: value }));
    };

    const handleUserEdit = (u) => {
        setUserForm({
            id: u.id,
            username: u.username,
            fullName: u.fullName || "",
            email: u.email || "",
            role: u.role || "CLIENT",
        });
    };

    const handleUserReset = () => {
        setUserForm({
            id: null,
            username: "",
            fullName: "",
            email: "",
            role: "CLIENT",
        });
    };

    const handleUserSubmit = async (e) => {
        e.preventDefault();
        setError("");

        try {
            if (userForm.id == null) {
                // create
                const created = await apiRequest("/users", {
                    method: "POST",
                    body: {
                        username: userForm.username,
                        fullName: userForm.fullName,
                        email: userForm.email,
                        role: userForm.role,
                    },
                    token,
                });
                setUsers((prev) => [...prev, created]);
            } else {
                // update
                const updated = await apiRequest(`/users/${userForm.id}`, {
                    method: "PUT",
                    body: {
                        username: userForm.username,
                        fullName: userForm.fullName,
                        email: userForm.email,
                        role: userForm.role,
                    },
                    token,
                });
                setUsers((prev) =>
                    prev.map((u) => (u.id === updated.id ? updated : u))
                );
            }
            handleUserReset();
        } catch (err) {
            setError(err.message || "Failed to save user");
        }
    };

    const handleUserDelete = async (id) => {
        if (!window.confirm("Delete this user?")) return;
        setError("");

        try {
            await apiRequest(`/users/${id}`, { method: "DELETE", token });
            setUsers((prev) => prev.filter((u) => u.id !== id));
        } catch (err) {
            setError(err.message || "Failed to delete user");
        }
    };

    // -------- DEVICES CRUD --------
    const handleDeviceChange = (field, value) => {
        setDeviceForm((prev) => ({ ...prev, [field]: value }));
    };

    const handleDeviceEdit = (d) => {
        setDeviceForm({
            id: d.id,
            name: d.name,
            description: d.description || "",
            maxConsumptionW: d.maxConsumptionW ?? "",
        });
    };

    const handleDeviceReset = () => {
        setDeviceForm({
            id: null,
            name: "",
            description: "",
            maxConsumptionW: "",
        });
    };

    const handleDeviceSubmit = async (e) => {
        e.preventDefault();
        setError("");

        try {
            const payload = {
                name: deviceForm.name,
                description: deviceForm.description,
                maxConsumptionW:
                    deviceForm.maxConsumptionW === ""
                        ? null
                        : Number(deviceForm.maxConsumptionW),
            };

            if (deviceForm.id == null) {
                const created = await apiRequest("/devices", {
                    method: "POST",
                    body: payload,
                    token,
                });
                setDevices((prev) => [...prev, created]);
            } else {
                const updated = await apiRequest(`/devices/${deviceForm.id}`, {
                    method: "PUT",
                    body: payload,
                    token,
                });
                setDevices((prev) =>
                    prev.map((d) => (d.id === updated.id ? updated : d))
                );
            }
            handleDeviceReset();
        } catch (err) {
            setError(err.message || "Failed to save device");
        }
    };

    const handleDeviceDelete = async (id) => {
        if (!window.confirm("Delete this device?")) return;
        setError("");

        try {
            await apiRequest(`/devices/${id}`, { method: "DELETE", token });
            setDevices((prev) => prev.filter((d) => d.id !== id));
        } catch (err) {
            setError(err.message || "Failed to delete device");
        }
    };

    // -------- ASSIGN DEVICE → USER --------
    const handleAssign = async (e) => {
        e.preventDefault();
        setError("");

        if (!selectedUserIdForAssign || !selectedDeviceIdForAssign) {
            setError("Please select both a user and a device.");
            return;
        }

        try {
            await apiRequest(
                `/devices/${Number(selectedDeviceIdForAssign)}/assign`,
                {
                    method: "POST",
                    body: { userId: Number(selectedUserIdForAssign) },
                    token,
                }
            );
            alert("Device assigned successfully.");
        } catch (err) {
            setError(err.message || "Failed to assign device");
        }
    };

    return (
        <div className="page">
            <NavBar />

            <main className="page-content">
                <h2>Admin Dashboard</h2>

                {error && <p className="error-text">{error}</p>}

                <div className="grid-2">
                    {/* USERS */}
                    <section className="panel">
                        <h3>Manage Users</h3>

                        <form onSubmit={handleUserSubmit} className="panel-form">
                            <div className="form-row">
                                <label>
                                    Username
                                    <input
                                        value={userForm.username}
                                        onChange={(e) =>
                                            handleUserChange("username", e.target.value)
                                        }
                                        required
                                    />
                                </label>
                                <label>
                                    Full Name
                                    <input
                                        value={userForm.fullName}
                                        onChange={(e) =>
                                            handleUserChange("fullName", e.target.value)
                                        }
                                    />
                                </label>
                            </div>

                            <div className="form-row">
                                <label>
                                    Email
                                    <input
                                        type="email"
                                        value={userForm.email}
                                        onChange={(e) =>
                                            handleUserChange("email", e.target.value)
                                        }
                                    />
                                </label>
                                <label>
                                    Role
                                    <select
                                        value={userForm.role}
                                        onChange={(e) =>
                                            handleUserChange("role", e.target.value)
                                        }
                                    >
                                        <option value="CLIENT">CLIENT</option>
                                        <option value="ADMIN">ADMIN</option>
                                    </select>
                                </label>
                            </div>

                            <div className="form-actions">
                                <button type="submit" className="btn-primary">
                                    {userForm.id == null ? "Create User" : "Update User"}
                                </button>
                                <button
                                    type="button"
                                    className="btn-secondary"
                                    onClick={handleUserReset}
                                >
                                    Clear
                                </button>
                            </div>
                        </form>

                        <table className="data-table">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Username</th>
                                <th>Full Name</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th />
                            </tr>
                            </thead>
                            <tbody>
                            {users.map((u) => (
                                <tr key={u.id}>
                                    <td>{u.id}</td>
                                    <td>{u.username}</td>
                                    <td>{u.fullName}</td>
                                    <td>{u.email}</td>
                                    <td>{u.role}</td>
                                    <td>
                                        <button
                                            className="btn-small"
                                            onClick={() => handleUserEdit(u)}
                                        >
                                            Edit
                                        </button>
                                        <button
                                            className="btn-small btn-danger"
                                            onClick={() => handleUserDelete(u.id)}
                                        >
                                            Delete
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            {users.length === 0 && (
                                <tr>
                                    <td colSpan="6" style={{ textAlign: "center" }}>
                                        No users
                                    </td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </section>

                    {/* DEVICES */}
                    <section className="panel">
                        <h3>Manage Devices</h3>

                        <form onSubmit={handleDeviceSubmit} className="panel-form">
                            <div className="form-row">
                                <label>
                                    Name
                                    <input
                                        value={deviceForm.name}
                                        onChange={(e) =>
                                            handleDeviceChange("name", e.target.value)
                                        }
                                        required
                                    />
                                </label>
                                <label>
                                    Max consumption (W)
                                    <input
                                        type="number"
                                        value={deviceForm.maxConsumptionW}
                                        onChange={(e) =>
                                            handleDeviceChange("maxConsumptionW", e.target.value)
                                        }
                                    />
                                </label>
                            </div>

                            <div className="form-row">
                                <label style={{ flex: 1 }}>
                                    Description
                                    <input
                                        value={deviceForm.description}
                                        onChange={(e) =>
                                            handleDeviceChange("description", e.target.value)
                                        }
                                    />
                                </label>
                            </div>

                            <div className="form-actions">
                                <button type="submit" className="btn-primary">
                                    {deviceForm.id == null ? "Create Device" : "Update Device"}
                                </button>
                                <button
                                    type="button"
                                    className="btn-secondary"
                                    onClick={handleDeviceReset}
                                >
                                    Clear
                                </button>
                            </div>
                        </form>

                        <table className="data-table">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Description</th>
                                <th>Max W</th>
                                <th />
                            </tr>
                            </thead>
                            <tbody>
                            {devices.map((d) => (
                                <tr key={d.id}>
                                    <td>{d.id}</td>
                                    <td>{d.name}</td>
                                    <td>{d.description}</td>
                                    <td>{d.maxConsumptionW}</td>
                                    <td>
                                        <button
                                            className="btn-small"
                                            onClick={() => handleDeviceEdit(d)}
                                        >
                                            Edit
                                        </button>
                                        <button
                                            className="btn-small btn-danger"
                                            onClick={() => handleDeviceDelete(d.id)}
                                        >
                                            Delete
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            {devices.length === 0 && (
                                <tr>
                                    <td colSpan="5" style={{ textAlign: "center" }}>
                                        No devices
                                    </td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </section>
                </div>

                {/* ASSIGNMENT SECTION */}
                <section className="panel">
                    <h3>Assign Device to User</h3>
                    <form onSubmit={handleAssign} className="panel-form">
                        <div className="form-row">
                            <label>
                                User
                                <select
                                    value={selectedUserIdForAssign}
                                    onChange={(e) =>
                                        setSelectedUserIdForAssign(e.target.value)
                                    }
                                >
                                    <option value="">-- select user --</option>
                                    {users.map((u) => (
                                        <option key={u.id} value={u.id}>
                                            {u.username} (id={u.id})
                                        </option>
                                    ))}
                                </select>
                            </label>

                            <label>
                                Device
                                <select
                                    value={selectedDeviceIdForAssign}
                                    onChange={(e) =>
                                        setSelectedDeviceIdForAssign(e.target.value)
                                    }
                                >
                                    <option value="">-- select device --</option>
                                    {devices.map((d) => (
                                        <option key={d.id} value={d.id}>
                                            {d.name} (id={d.id})
                                        </option>
                                    ))}
                                </select>
                            </label>
                        </div>

                        <div className="form-actions">
                            <button className="btn-primary" type="submit">
                                Assign
                            </button>
                        </div>
                    </form>
                </section>
            </main>
        </div>
    );
}
