import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { apiRequest } from "../services/api";

export default function LoginPage() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        try {
            const data = await apiRequest("/auth/login", {
                method: "POST",
                body: { username, password },
            });

            // data: { token, role }
            login(data.token, data.role, username);

            if (data.role === "ADMIN") {
                navigate("/admin", { replace: true });
            } else if (data.role === "CLIENT") {
                navigate("/client", { replace: true });
            } else {
                navigate("/", { replace: true });
            }
        } catch (err) {
            setError(err.message || "Login failed");
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-card">
                <h1>Login</h1>
                <form onSubmit={handleSubmit} className="auth-form">
                    <label>
                        Username
                        <input
                            type="text"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </label>

                    <label>
                        Password
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </label>

                    {error && <p className="error-text">{error}</p>}

                    <button type="submit" className="btn-primary">
                        Login
                    </button>
                </form>

                <p className="auth-footer-text">
                    Don't have an account?{" "}
                    <Link to="/register">Register as client</Link>
                </p>
            </div>
        </div>
    );
}
