import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import RaceList from './components/RaceList';
import AddRace from './components/AddRace';
import EditRace from './components/EditRace';

function App() {
  return (
      <Router>
        <Routes>
          <Route path="/" element={<RaceList />} />
          <Route path="/add" element={<AddRace />} />
          <Route path="/edit/:id" element={<EditRace />} />
        </Routes>
      </Router>
  );
}

export default App;

