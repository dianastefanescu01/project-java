package org.example;

import org.example.model.Participant;
import org.example.model.User;
import org.example.repository.IParticipantRepository;
import org.example.repository.ParticipantRepository;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {

        Properties props = new Properties();

        try {
            props.load(new FileReader("D:\\sem4\\project PA\\SwimmingApplication\\src\\main\\resources\\db.config"));
        } catch (IOException e) {
            System.out.println("Cannot find db.config: " + e);
            return;
        }

        ParticipantRepository participantRepository = new ParticipantRepository(props);
        System.out.println("Test ParticipantRepository: ");
        Participant participant1 = new Participant("Adela",26, 3);
        participantRepository.save(participant1);

        for(Participant p : participantRepository.findAll()){
            System.out.println(p);
        }
    }
}