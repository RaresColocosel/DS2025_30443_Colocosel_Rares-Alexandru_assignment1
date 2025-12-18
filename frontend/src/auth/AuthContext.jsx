import { createContext, useContext, useEffect, useState } from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [token, setToken] = useState(null);
    const [role, setRole] = useState(null);
    const [username, setUsername] = useState(null);

    useEffect(() => {
        const stored = localStorage.getItem("auth");
        if (stored) {
            const parsed = JSON.parse(stored);
            setToken(parsed.token);
            setRole(parsed.role);
            setUsername(parsed.username);
        }
    }, []);

    const login = (jwtToken, userRole, userName) => {
        setToken(jwtToken);
        setRole(userRole);
        setUsername(userName);

        localStorage.setItem(
            "auth",
            JSON.stringify({ token: jwtToken, role: userRole, username: userName })
        );
    };

    const logout = () => {
        setToken(null);
        setRole(null);
        setUsername(null);
        localStorage.removeItem("auth");
    };

    return (
        <AuthContext.Provider value={{ token, role, username, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export const useAuth = () => useContext(AuthContext);
