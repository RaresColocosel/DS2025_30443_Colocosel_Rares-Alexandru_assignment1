import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { apiRequest } from "../services/api";
import { useAuth } from "../auth/AuthContext";

export default function RegisterPage() {
    const [username, setUsername] = useState("");
    const [fullName, setFullName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        try {
            // backend ignores role from client and forces CLIENT
            const data = await apiRequest("/auth/register-client", {
                method: "POST",
                body: { username, password, fullName, email },
            });
            // TokenResponse: { token, userId, username, role }

            login(data.token, data.role, data.username || username);
            navigate("/client", { replace: true });
        } catch (err) {
            setError(err.message || "Registration failed");
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-card">
                <h1>Register (Client)</h1>
                <form onSubmit={handleSubmit} className="auth-form">
                    <label>
                        Username
                        <input
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </label>

                    <label>
                        Full Name
                        <input
                            value={fullName}
                            onChange={(e) => setFullName(e.target.value)}
                        />
                    </label>

                    <label>
                        Email
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
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
                        Create Account
                    </button>
                </form>

                <p className="auth-footer-text">
                    Already have an account? <Link to="/login">Login</Link>
                </p>
            </div>
        </div>
    );
}
