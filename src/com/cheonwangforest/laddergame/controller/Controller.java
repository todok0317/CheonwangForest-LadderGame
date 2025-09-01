package com.cheonwangforest.laddergame.controller;

import com.cheonwangforest.laddergame.model.Model;
import com.cheonwangforest.laddergame.view.View;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Controller {
    private Model model;
    private View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    public Model getModel() {
        return model;
    }

    public void setAdminLoser(String adminLoser) {
        model.setAdminLoser(adminLoser);
    }

    public void showCountSettingsPopup() {
        view.showCountSettingsPopup();
    }

    public void showNameInputPopup(int participantCount, int loseCount, int passCount) {
        view.showNameInputPopup(participantCount, loseCount, passCount);
    }

    public void startLadderGame(List<String> participants, int loseCount, int passCount) {
        model.setLadderData(participants, loseCount, passCount);
        view.showLadderGameScreen();
    }

    public void checkResults() {
        Map<String, String> resultMap = new HashMap<>();
        for (String participant : model.getParticipants()) {
            String result = model.getResultForParticipant(participant);
            resultMap.put(participant, result);
        }

        StringBuilder resultMessage = new StringBuilder("게임 결과:\n");
        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            resultMessage.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
        }
        JOptionPane.showMessageDialog(view.getFrame(), resultMessage.toString(), "결과", JOptionPane.INFORMATION_MESSAGE);
    }
}
