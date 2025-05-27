import React, { useState } from 'react';
import { addRace } from '../services/RaceService';
import { useNavigate } from 'react-router-dom';
import './Form.css'; // import the CSS

function AddRace() {
    const [race, setRace] = useState({
        distance: '',
        style: '',
        nrOfParticipants: ''
    });

    const navigate = useNavigate();

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
        await addRace(formattedRace);
        navigate('/');
    };

    return (
        <div className="form-container">
            <h2>Add Race</h2>
            <form onSubmit={handleSubmit}>
                <select
                    name="distance"
                    value={race.distance}   // ✅ bind value to state
                    onChange={handleChange}
                    required
                >
                    <option value="">Select Distance</option>
                    <option value="50">50m</option>
                    <option value="200">200m</option>
                    <option value="800">800m</option>
                    <option value="1500">1500m</option>
                </select>

                <select
                    name="style"
                    value={race.style}    // ✅ bind value to state
                    onChange={handleChange}
                    required
                >
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
                    onChange={handleChange}
                    placeholder="Number of Participants"
                    required
                />
                <button type="submit">Add Race</button>
            </form>
        </div>
    );
}

export default AddRace;
