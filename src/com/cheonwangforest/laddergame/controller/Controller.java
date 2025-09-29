package com.cheonwangforest.laddergame.controller;

import com.cheonwangforest.laddergame.model.Model;
import com.cheonwangforest.laddergame.view.View;
import com.cheonwangforest.laddergame.util.ImageLoader;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
        view.stopAnimation();
        
        // LinkedHashMap으로 순서 유지하며 결과 수집
        Map<String, String> resultMap = model.getParticipants().stream()
                .collect(Collectors.toMap(
                    participant -> participant,
                    participant -> model.getResultForParticipant(participant),
                    (e1, e2) -> e1, LinkedHashMap::new
                ));

        // 커스텀 결과 다이얼로그 생성
        showCustomResultDialog(resultMap);
    }

    // 커스텀 결과 다이얼로그 표시
    private void showCustomResultDialog(Map<String, String> resultMap) {
        JDialog resultDialog = new JDialog(view.getFrame(), "게임 결과", true);
        resultDialog.setSize(800, 500); // 적당한 크기로 조정
        resultDialog.setLocationRelativeTo(view.getFrame());

        // 배경 이미지가 있는 패널
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bgImage = ImageLoader.loadImage("/com/cheonwangforest/images/팝업_창_1133_x_637.png");
                    if (bgImage != null) {
                        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                    } else {
                        // 배경 이미지가 없으면 기본 배경
                        g.setColor(new Color(245, 235, 180));
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                } catch (Exception e) {
                    // 배경 이미지가 없으면 기본 배경
                    g.setColor(new Color(245, 235, 180));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // 결과 내용 패널
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // 제목
        JLabel titleLabel = new JLabel("사다리타기 결과", JLabel.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setForeground(new Color(101, 67, 33));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        
        contentPanel.add(Box.createVerticalStrut(30));

        // 결과 목록
        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            JPanel resultRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
            resultRow.setOpaque(false);
            
            // 이모지 제거하고 텍스트만 표시
            JLabel resultLabel = new JLabel(String.format("%s → %s", 
                entry.getKey(), entry.getValue()));
            resultLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
            resultLabel.setForeground(new Color(101, 67, 33));
            
            resultRow.add(resultLabel);
            contentPanel.add(resultRow);
            contentPanel.add(Box.createVerticalStrut(10));
        }

        // 확인 버튼
        contentPanel.add(Box.createVerticalStrut(20));
        JButton okButton = new JButton("확인");
        okButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        okButton.setPreferredSize(new Dimension(100, 40));
        okButton.setBackground(new Color(255, 223, 0));
        okButton.setForeground(new Color(101, 67, 33));
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.addActionListener(e -> resultDialog.dispose());
        contentPanel.add(okButton);

        backgroundPanel.add(contentPanel, BorderLayout.CENTER);
        resultDialog.setContentPane(backgroundPanel);
        resultDialog.setVisible(true);
    }
}
