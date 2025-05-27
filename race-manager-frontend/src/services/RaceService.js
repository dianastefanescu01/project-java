import axios from 'axios';

const API_URL = 'http://localhost:8080/swimming/api/races';

export const getAllRaces = () => axios.get(API_URL);
export const getRaceById = (id) => axios.get(`${API_URL}/${id}`);
export const addRace = (race) => axios.post(API_URL, race);
export const updateRace = (id, race) => axios.put(`${API_URL}/${id}`, race);
export const deleteRace = (id) => axios.delete(`${API_URL}/${id}`);