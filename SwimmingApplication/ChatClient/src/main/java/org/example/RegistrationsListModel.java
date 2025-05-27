package org.example;

import org.example.model.ParticipantRace;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationsListModel extends AbstractListModel {
    private List<ParticipantRace> registrationsList;
    public RegistrationsListModel() {
        this.registrationsList = new ArrayList<ParticipantRace>();
    }
    @Override
    public int getSize() {
        return registrationsList.size();
    }

    @Override
    public Object getElementAt(int index) {
        return registrationsList.get(index);
    }
    public void newRegistratiosn(ParticipantRace registration){
        registrationsList.add(registration);
        fireContentsChanged(this,registrationsList.size()-1,registrationsList.size());
    }
}
