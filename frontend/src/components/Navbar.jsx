import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export default function NavBar() {
    const { username, role, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate("/login", { replace: true });
    };

    return (
        <header className="navbar">
            <div className="navbar-left">
                <span className="navbar-title">EMS Dashboard</span>
            </div>
            <div className="navbar-right">
                {username && (
                    <span className="navbar-user">
            Logged in as <strong>{username}</strong> ({role})
          </span>
                )}
                <button className="btn-secondary" onClick={handleLogout}>
                    Logout
                </button>
            </div>
        </header>
    );
}
