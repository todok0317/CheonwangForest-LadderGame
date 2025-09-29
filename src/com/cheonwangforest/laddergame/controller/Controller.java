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
    
    public void setAdminLosers(List<String> adminLosers) {
        model.setAdminLosers(adminLosers);
    }
    
    public void clearAdminSettings() {
        model.clearAdminLosers();
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

        // 결과 목록을 2줄로 표시
        List<Map.Entry<String, String>> entries = new ArrayList<>(resultMap.entrySet());
        int halfSize = (entries.size() + 1) / 2; // 반으로 나누기 (홀수면 첫 줄에 더 많이)
        
        // 결과를 담을 메인 패널 (2개의 열)
        JPanel resultsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        resultsPanel.setOpaque(false);
        
        // 왼쪽 열
        JPanel leftColumn = new JPanel();
        leftColumn.setOpaque(false);
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        
        for (int i = 0; i < halfSize && i < entries.size(); i++) {
            Map.Entry<String, String> entry = entries.get(i);
            JLabel resultLabel = new JLabel(String.format("%s → %s", 
                entry.getKey(), entry.getValue()), JLabel.CENTER);
            resultLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
            resultLabel.setForeground(new Color(101, 67, 33));
            resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            leftColumn.add(resultLabel);
            leftColumn.add(Box.createVerticalStrut(8));
        }
        
        // 오른쪽 열
        JPanel rightColumn = new JPanel();
        rightColumn.setOpaque(false);
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        
        for (int i = halfSize; i < entries.size(); i++) {
            Map.Entry<String, String> entry = entries.get(i);
            JLabel resultLabel = new JLabel(String.format("%s → %s", 
                entry.getKey(), entry.getValue()), JLabel.CENTER);
            resultLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
            resultLabel.setForeground(new Color(101, 67, 33));
            resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            rightColumn.add(resultLabel);
            rightColumn.add(Box.createVerticalStrut(8));
        }
        
        resultsPanel.add(leftColumn);
        resultsPanel.add(rightColumn);
        contentPanel.add(resultsPanel);

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
