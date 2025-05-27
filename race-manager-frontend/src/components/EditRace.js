import React, { useEffect, useState } from 'react';
import { getRaceById, updateRace } from '../services/RaceService';
import { useParams, useNavigate } from 'react-router-dom';
import './Form.css'; // Reuse the same styles from AddRace

function EditRace() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [race, setRace] = useState({
        distance: '',
        style: '',
        nrOfParticipants: ''
    });

    useEffect(() => {
        getRaceById(id).then((res) => setRace(res.data));
    }, [id]);

    const handleChange = (e) => {
        setRace({ ...race, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const formattedRace = {
            ...race,
            distance: parseInt(race.distance),
            nrOfParticipants: parseInt(race.nrOfParticipants)
        };
        await updateRace(id, formattedRace);
        navigate('/');
    };

    return (
        <div className="form-container">
            <h2>Edit Race</h2>
            <form onSubmit={handleSubmit}>
                <select name="distance" value={race.distance} onChange={handleChange} required>
                    <option value="">Select Distance</option>
                    <option value="50">50m</option>
                    <option value="200">200m</option>
                    <option value="800">800m</option>
                    <option value="1500">1500m</option>
                </select>

                <select name="style" value={race.style} onChange={handleChange} required>
                    <option value="">Select Style</option>
                    <option value="freestyle">Freestyle</option>
                    <option value="backstroke">Backstroke</option>
                    <option value="butterfly">Butterfly</option>
                    <option value="mixed">Mixed</option>
                </select>

                <input
                    type="number"
                    name="nrOfParticipants"
                    value={race.nrOfParticipants}
                    placeholder="Number of Participants"
                    onChange={handleChange}
                    required
                />
                <button type="submit">Update Race</button>
            </form>
        </div>
    );
}

export default EditRace;
