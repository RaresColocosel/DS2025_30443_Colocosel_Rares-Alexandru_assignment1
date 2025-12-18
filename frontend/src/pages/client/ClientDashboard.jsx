import { useEffect, useState } from "react";
import { useAuth } from "../auth/AuthContext";
import { apiRequest } from "../services/api";
import NavBar from "../components/NavBar";

export default function ClientDashboard() {
    const { token } = useAuth();

    const [profile, setProfile] = useState(null);
    const [devices, setDevices] = useState([]);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchClientData = async () => {
            try {
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

        fetchClientData();
    }, [token]);

    return (
        <div className="page">
            <NavBar />
            <main className="page-content">
                <h2>Client Dashboard</h2>

                {error && <p className="error-text">{error}</p>}

                <section className="panel">
                    <h3>My Profile</h3>
                    {profile ? (
                        <div className="profile-box">
                            <p>
                                <strong>Username:</strong> {profile.username}
                            </p>
                            <p>
                                <strong>Full Name:</strong> {profile.fullName}
                            </p>
                            <p>
                                <strong>Email:</strong> {profile.email}
                            </p>
                            <p>
                                <strong>Role:</strong> {profile.role}
                            </p>
                        </div>
                    ) : (
                        <p>Loading profile...</p>
                    )}
                </section>

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
                                <td colSpan="4" style={{ textAlign: "center" }}>
                                    No devices assigned.
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </section>
            </main>
        </div>
    );
}