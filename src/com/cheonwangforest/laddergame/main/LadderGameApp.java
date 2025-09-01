package com.cheonwangforest.laddergame.main;

import com.cheonwangforest.laddergame.model.Model;
import javax.swing.SwingUtilities;
import com.cheonwangforest.laddergame.controller.Controller;
import com.cheonwangforest.laddergame.view.View;

public class LadderGameApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Model model = new Model();
            View view = new View(null); // 초기에는 controller가 null
            Controller controller = new Controller(model, view);
            view.setController(controller);

            view.showWelcomeScreen();
        });
    }
}
