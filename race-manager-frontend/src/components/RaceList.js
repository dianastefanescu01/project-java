import React, { useEffect, useState } from 'react';
import { getAllRaces, deleteRace } from '../services/RaceService';
import { Link } from 'react-router-dom';
import './RaceList.css';

function RaceList() {
    const [races, setRaces] = useState([]);

    useEffect(() => {
        fetchRaces();
    }, []);

    const fetchRaces = async () => {
        const res = await getAllRaces();
        setRaces(res.data);
    };

    const handleDelete = async (id) => {
        await deleteRace(id);
        fetchRaces();
    };

    return (
        <div className="race-list-container">
            <h2>All Races</h2>
            <Link className="add-button" to="/add">Add Race</Link>
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Distance (m)</th>
                    <th>Style</th>
                    <th>Participants</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {races.map((race) => (
                    <tr key={race.id}>
                        <td>{race.id}</td>
                        <td>{race.distance}</td>
                        <td>{race.style}</td>
                        <td>{race.nrOfParticipants}</td>
                        <td>
                            <Link to={`/edit/${race.id}`} className="edit-button">Edit</Link>
                            <button onClick={() => handleDelete(race.id)} className="delete-button">Delete</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default RaceList;
