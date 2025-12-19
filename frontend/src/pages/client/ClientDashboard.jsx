import { useEffect, useState } from "react";
import { useAuth } from "../../auth/AuthContext";
import { apiRequest } from "../../services/api";
import NavBar from "../../components/Navbar";

export default function ClientDashboard() {
    const { token } = useAuth();

    const [profile, setProfile] = useState(null);
    const [devices, setDevices] = useState([]);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchClientData = async () => {
            try {
                // Ensure correct relative paths for your API calls
                const [p, ds] = await Promise.all([
                    apiRequest("/client/me", { token }),
                    apiRequest("/client/devices", { token }),
                ]);
                setProfile(p);
                setDevices(ds || []);
            } catch (err) {
                setError(err.message || "Failed to load client data");
            }
        };

        if (token) fetchClientData();
    }, [token]);

    return (
        <div className="page">
            <NavBar />

            <main className="page-content">
                <header style={{ marginBottom: '1.5rem' }}>
                    <h2>Client Dashboard</h2>
                </header>

                {error && <p className="error-text">{error}</p>}

                {/* Using grid-2 to match the Admin Dashboard layout */}
                <div className="grid-2">

                    {/* PROFILE CARD */}
                    <section className="panel">
                        <h3>My Profile</h3>
                        {profile ? (
                            <div className="profile-box">
                                <p><strong>Username:</strong> {profile.username}</p>
                                <p><strong>Full Name:</strong> {profile.fullName}</p>
                                <p><strong>Email:</strong> {profile.email}</p>
                                <p><strong>Role:</strong> <span className="btn-small" style={{cursor: 'default'}}>{profile.role}</span></p>
                            </div>
                        ) : (
                            <p>Loading profile...</p>
                        )}
                    </section>

                    {/* DEVICES CARD */}
                    <section className="panel">
                        <h3>My Devices</h3>
                        <table className="data-table">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Description</th>
                                <th>Max W</th>
                            </tr>
                            </thead>
                            <tbody>
                            {devices.map((d) => (
                                <tr key={d.id}>
                                    <td>{d.id}</td>
                                    <td>{d.name}</td>
                                    <td>{d.description}</td>
                                    <td>{d.maxConsumptionW}</td>
                                </tr>
                            ))}
                            {devices.length === 0 && (
                                <tr>
                                    <td colSpan="4" style={{ textAlign: "center", padding: '1rem' }}>
                                        No devices assigned.
                                    </td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </section>
                </div>
            </main>
        </div>
    );
}