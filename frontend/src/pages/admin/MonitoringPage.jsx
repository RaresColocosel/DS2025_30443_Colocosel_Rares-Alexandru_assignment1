import { useEffect, useState } from "react";
import { useAuth } from "../auth/AuthContext";
import { getMonitoringData } from "../services/api";

export default function MonitoringPage() {
    const { token } = useAuth();
    const [data, setData] = useState([]);
    const [filterId, setFilterId] = useState("");
    const [filterDate, setFilterDate] = useState("");
    const [loading, setLoading] = useState(false);

    const loadData = async () => {
        setLoading(true);
        try {
            const res = await getMonitoringData(token, filterId, filterDate);
            setData(res || []);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { loadData(); }, [token]);

    return (
        <div className="page-content">
            <h2>Monitoring Database</h2>
            <div className="panel">
                <form onSubmit={(e) => { e.preventDefault(); loadData(); }} style={{ display: 'flex', gap: '10px' }}>
                    <input type="number" placeholder="Device ID" value={filterId} onChange={e => setFilterId(e.target.value)} />
                    <input type="date" value={filterDate} onChange={e => setFilterDate(e.target.value)} />
                    <button type="submit" className="btn-primary" disabled={loading}>Filter</button>
                    <button type="button" className="btn-secondary" onClick={() => { setFilterId(""); setFilterDate(""); }}>Clear</button>
                </form>
            </div>
            <div className="panel">
                <table className="data-table">
                    <thead><tr><th>DB ID</th><th>Device</th><th>Timestamp</th><th>Value (W)</th></tr></thead>
                    <tbody>
                    {data.map(r => (
                        <tr key={r.id}>
                            <td>{r.id}</td>
                            <td>{r.deviceId}</td>
                            <td>{new Date(r.hourTimestamp).toLocaleString()}</td>
                            <td>{r.totalValue}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}