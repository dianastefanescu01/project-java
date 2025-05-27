package org.example.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Race;
import org.example.repository.rest.RaceRestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("swimming/api/races")
public class RaceRestController {
    private static final Logger logger = LogManager.getLogger(RaceRestController.class);
    private final RaceRestRepository repoRace;

    @Autowired
    public RaceRestController(RaceRestRepository repoRace) {
        logger.info("Initializing RaceRestController");
        this.repoRace = repoRace;
    }

    @GetMapping
    public List<Race> getAll() {
        logger.info("REST request to get all races");
        List<Race> races = (List<Race>) repoRace.findAll();
        logger.debug("Returning {} races", races.size());
        return races;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Integer id) {
        logger.info("REST request to get race with id={}", id);
        Optional<Race> raceOpt = repoRace.find(id);
        if (raceOpt.isEmpty()) {
            logger.warn("Race with id={} not found", id);
            return new ResponseEntity<>("Race not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(raceOpt.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody Race race) {
        logger.info("REST request to create race: {}", race);
        race.setId(null); // Ignore ID if provided

        Optional<Race> saved = repoRace.save(race);
        if (saved.isPresent()) {
            logger.info("Created race with id={}", saved.get().getId());
            return ResponseEntity.status(HttpStatus.CREATED).build(); // ✅ 201 with no body
        } else {
            logger.error("Failed to create race");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id, @RequestBody Race race) {
        logger.info("REST request to update race with id={}", id);
        Optional<Race> existing = repoRace.find(id);
        if (existing.isEmpty()) {
            logger.warn("Race with id={} not found for update", id);
            return new ResponseEntity<>("Race not found", HttpStatus.NOT_FOUND);
        }

        race.setId(id);
        Optional<Race> updated = repoRace.update(race);
        if (updated.isPresent()) {
            logger.info("Race with id={} updated", id);
            return new ResponseEntity<>(updated.get(), HttpStatus.OK);
        } else {
            logger.error("Failed to update race with id={}", id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        logger.info("REST request to delete race with id={}", id);
        Optional<Race> deleted = repoRace.delete(id);
        if (deleted.isEmpty()) {
            logger.warn("Race with id={} not found for deletion", id);
            return new ResponseEntity<>("Race not found", HttpStatus.NOT_FOUND);
        }

        logger.info("Race with id={} deleted", id);
        return ResponseEntity.noContent().build();
    }
}
