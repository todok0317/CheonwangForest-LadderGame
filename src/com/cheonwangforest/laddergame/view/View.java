package com.cheonwangforest.laddergame.view;

import com.cheonwangforest.laddergame.controller.Controller;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 사다리타기 게임 뷰 클래스
 * GitHub 요구사항 준수:
 * - Java 8 Stream과 Lambda 적용
 * - 사용자 이름 최대 5글자 제한
 * - 개별/전체 결과 조회 기능
 */
public class View {

    private Controller controller;
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel ladderPanel;
    private JTextField[] nameFields;
    private final AtomicBoolean animationRunning = new AtomicBoolean(false);

    private final int LADDER_WIDTH = 500;
    private final int LADDER_HEIGHT = 400;

    public View(Controller controller) {
        this.controller = controller;
        frame = new JFrame("사다리 게임");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void showWelcomeScreen() {
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bgImage = ImageIO.read(
                        new File("src/com/cheonwangforest/images/제목을-입력해주세요_-001.png"));
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } catch (IOException e) {
                    System.out.println("배경 이미지를 찾을 수 없습니다.");
                }
            }
        };
        mainPanel.setLayout(null);

        JButton homeButton = new JButton();
        try {
            Image homeImage = ImageIO.read(new File("src/com/cheonwangforest/images/홈 버튼2.png"));
            homeButton.setIcon(
                new ImageIcon(homeImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            homeButton.setText("홈");
        }
        homeButton.setBounds(10, 10, 60, 60);
        homeButton.setOpaque(false);
        homeButton.setContentAreaFilled(false);
        homeButton.setBorderPainted(false);
        homeButton.addActionListener(e -> {
            showWelcomeScreen();
        });

        JButton startButton = new JButton();
        try {
            Image startImage = ImageIO.read(new File("src/com/cheonwangforest/images/시작버튼.png"));
            startButton.setIcon(
                new ImageIcon(startImage.getScaledInstance(237, 112, Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            startButton.setText("시작");
        }
        int startButtonWidth = 237;
        int startButtonHeight = 112;
        int centerX = (frame.getWidth() - startButtonWidth) / 2;
        int centerY = (frame.getHeight() - startButtonHeight) / 2;
        startButton.setBounds(centerX, centerY, startButtonWidth, startButtonHeight);
        startButton.addActionListener(e -> controller.showCountSettingsPopup());

        JButton adminButton = new JButton();
        try {
            Image adminImage = ImageIO.read(new File("src/com/cheonwangforest/images/관리자모드.png"));
            adminButton.setIcon(
                new ImageIcon(adminImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            adminButton.setText("관리자");
        }
        adminButton.setBounds(10, frame.getHeight() - 90, 50, 50);
        adminButton.setOpaque(false);
        adminButton.setContentAreaFilled(false);
        adminButton.setBorderPainted(false);
        adminButton.addActionListener(e -> {
            String adminName = JOptionPane.showInputDialog(frame, "꽝에 당첨될 참가자 이름을 입력하세요:");
            if (adminName != null && !adminName.trim().isEmpty()) {
                controller.setAdminLoser(adminName.trim());
                JOptionPane.showMessageDialog(frame, adminName + "님은 꽝에 당첨되도록 설정되었습니다.");
            }
        });

        mainPanel.add(homeButton);
        mainPanel.add(startButton);
        mainPanel.add(adminButton);

        frame.getContentPane().removeAll();
        frame.getContentPane().add(mainPanel);
        frame.revalidate();
        frame.repaint();
    }

    // 1단계: 꽝/통과 개수 설정 팝업
    public void showCountSettingsPopup() {
        JDialog settingsDialog = new JDialog(frame, "설정", true);
        settingsDialog.setSize(1133, 637);
        settingsDialog.setLocationRelativeTo(frame);

        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bgImage = ImageIO.read(
                        new File("src/com/cheonwangforest/images/참가자 수 설정.png"));
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } catch (IOException e) {
                    System.out.println("팝업 창 배경 이미지를 찾을 수 없습니다.");
                }
            }
        };

        // 참가자 수 설정 패널
        JPanel participantPanel = new JPanel(new GridBagLayout());
        participantPanel.setOpaque(false);

        JTextField participantCountField = new JTextField("0");
        participantCountField.setOpaque(false);
        participantCountField.setEditable(false);
        participantCountField.setBorder(null); // 외곽선 제거
        participantCountField.setFont(new Font("Arial", Font.BOLD, 60)); // 폰트 크기 조정
        participantCountField.setForeground(Color.decode("#FFD700")); // 텍스트 색상 변경
        participantCountField.setHorizontalAlignment(JTextField.CENTER);
        participantCountField.setPreferredSize(new Dimension(128, 128));

        JButton prevParticipantBtn = createButton("src/com/cheonwangforest/images/좌버튼.png");
        JButton nextParticipantBtn = createButton("src/com/cheonwangforest/images/우버튼.png");

        prevParticipantBtn.addActionListener(e -> updateCount(participantCountField, -1, 1, 15));
        nextParticipantBtn.addActionListener(e -> updateCount(participantCountField, 1, 1, 15));

        GridBagConstraints gbcParticipant = new GridBagConstraints();
        gbcParticipant.insets = new Insets(0, 5, 0, 5);
        gbcParticipant.gridx = 0;
        gbcParticipant.gridy = 0;
        participantPanel.add(prevParticipantBtn, gbcParticipant);
        gbcParticipant.gridx = 1;
        participantPanel.add(participantCountField, gbcParticipant);
        gbcParticipant.gridx = 2;
        participantPanel.add(nextParticipantBtn, gbcParticipant);

        // 꽝 개수 설정 패널
        JPanel losePanel = new JPanel(new GridBagLayout());
        losePanel.setOpaque(false);

        JTextField loseCountField = new JTextField("0");
        loseCountField.setOpaque(false);
        loseCountField.setEditable(false);
        loseCountField.setBorder(null); // 외곽선 제거
        loseCountField.setFont(new Font("Arial", Font.BOLD, 60)); // 폰트 크기 조정
        loseCountField.setForeground(Color.decode("#FFD700")); // 텍스트 색상 변경
        loseCountField.setHorizontalAlignment(JTextField.CENTER);
        loseCountField.setPreferredSize(new Dimension(128, 128));

        JButton prevLoseBtn = createButton("src/com/cheonwangforest/images/좌버튼.png");
        JButton nextLoseBtn = createButton("src/com/cheonwangforest/images/우버튼.png");

        prevLoseBtn.addActionListener(e -> updateCount(loseCountField, -1, 0,
            Integer.parseInt(participantCountField.getText())));
        nextLoseBtn.addActionListener(e -> updateCount(loseCountField, 1, 0,
            Integer.parseInt(participantCountField.getText())));

        GridBagConstraints gbcLose = new GridBagConstraints();
        gbcLose.insets = new Insets(0, 5, 0, 5);
        gbcLose.gridx = 0;
        gbcLose.gridy = 0;
        losePanel.add(prevLoseBtn, gbcLose);
        gbcLose.gridx = 1;
        losePanel.add(loseCountField, gbcLose);
        gbcLose.gridx = 2;
        losePanel.add(nextLoseBtn, gbcLose);

        // 다음 버튼
        JButton nextButton = createButtonWithFixedSize("src/com/cheonwangforest/images/다음 버튼.png",
            90, 90);
        nextButton.addActionListener(e -> {
            try {
                int participantCount = Integer.parseInt(participantCountField.getText());
                int loseCount = Integer.parseInt(loseCountField.getText());
                int passCount = participantCount - loseCount;

                if (participantCount == 0) {
                    JOptionPane.showMessageDialog(settingsDialog, "참가자 수는 1명 이상이어야 합니다.", "경고",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                controller.showNameInputPopup(participantCount, loseCount, passCount);
                settingsDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(settingsDialog, "유효한 숫자를 입력하세요.", "경고",
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        // 메인 패널에 컴포넌트 추가
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.insets = new Insets(10, 10, 10, 10);

        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.anchor = GridBagConstraints.CENTER;
        gbcMain.insets = new Insets(40, 250, 10, 0); // 위로 40, 오른쪽으로 250 이동
        backgroundPanel.add(participantPanel, gbcMain);

        gbcMain.gridy = 1;
        gbcMain.insets = new Insets(60, 250, 10, 0); // 오른쪽으로 250 이동
        backgroundPanel.add(losePanel, gbcMain);

        gbcMain.gridy = 2;
        gbcMain.insets = new Insets(30, 0, 50, 0);
        backgroundPanel.add(nextButton, gbcMain);

        settingsDialog.setContentPane(backgroundPanel);
        settingsDialog.setVisible(true);
    }

    // 2단계: 이름 입력 팝업
    public void showNameInputPopup(int totalParticipants, int loseCount, int passCount) {
        JDialog nameDialog = new JDialog(frame, "참가자 이름 입력", true);
        nameDialog.setSize(1133, 637);
        nameDialog.setLocationRelativeTo(frame);

        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bgImage = ImageIO.read(
                        new File("src/com/cheonwangforest/images/팝업_창_1133_x_637.png"));
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } catch (IOException e) {
                    System.out.println("팝업 창 배경 이미지를 찾을 수 없습니다.");
                }
            }
        };

        // 참가자 입력 폼 - 5명 이상이면 두 열로 배치
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        
        nameFields = new JTextField[totalParticipants];
        
        if (totalParticipants >= 5) {
            // 두 열로 배치 (왼쪽 열, 오른쪽 열)
            formPanel.setLayout(new GridLayout(1, 2, 50, 0)); // 좌우 50px 간격
            
            // 왼쪽 열
            JPanel leftColumn = new JPanel(new GridBagLayout());
            leftColumn.setOpaque(false);
            GridBagConstraints gbcLeft = new GridBagConstraints();
            gbcLeft.insets = new Insets(15, 10, 15, 10);
            
            // 오른쪽 열
            JPanel rightColumn = new JPanel(new GridBagLayout());
            rightColumn.setOpaque(false);
            GridBagConstraints gbcRight = new GridBagConstraints();
            gbcRight.insets = new Insets(15, 10, 15, 10);
            
            int halfSize = (totalParticipants + 1) / 2; // 왼쪽에 더 많이 배치
            
            for (int i = 0; i < totalParticipants; i++) {
                // 라벨 설정
                JLabel nameLabel = new JLabel("참가자 " + (i + 1), JLabel.CENTER);
                nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
                nameLabel.setForeground(new Color(101, 67, 33));
                nameLabel.setPreferredSize(new Dimension(80, 30));
                
                // 텍스트 필드 설정
                nameFields[i] = new JTextField();
                nameFields[i].setFont(new Font("맑은 고딕", Font.PLAIN, 14));
                nameFields[i].setPreferredSize(new Dimension(100, 30));
                nameFields[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(101, 67, 33), 1),
                    BorderFactory.createEmptyBorder(3, 8, 3, 8)
                ));
                nameFields[i].setBackground(new Color(255, 248, 220));
                
                if (i < halfSize) {
                    // 왼쪽 열에 배치
                    gbcLeft.gridx = 0;
                    gbcLeft.gridy = i;
                    gbcLeft.anchor = GridBagConstraints.CENTER;
                    leftColumn.add(nameLabel, gbcLeft);
                    
                    gbcLeft.gridx = 1;
                    leftColumn.add(nameFields[i], gbcLeft);
                } else {
                    // 오른쪽 열에 배치
                    int rightIndex = i - halfSize;
                    gbcRight.gridx = 0;
                    gbcRight.gridy = rightIndex;
                    gbcRight.anchor = GridBagConstraints.CENTER;
                    rightColumn.add(nameLabel, gbcRight);
                    
                    gbcRight.gridx = 1;
                    rightColumn.add(nameFields[i], gbcRight);
                }
            }
            
            formPanel.add(leftColumn);
            formPanel.add(rightColumn);
            
        } else {
            // 한 열로 배치 (기존 방식)
            formPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(15, 20, 15, 20);
            
            for (int i = 0; i < totalParticipants; i++) {
                // 라벨 설정
                JLabel nameLabel = new JLabel("참가자 " + (i + 1), JLabel.CENTER);
                nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
                nameLabel.setForeground(new Color(101, 67, 33));
                nameLabel.setPreferredSize(new Dimension(100, 35));
                
                // 텍스트 필드 설정
                nameFields[i] = new JTextField();
                nameFields[i].setFont(new Font("맑은 고딕", Font.PLAIN, 16));
                nameFields[i].setPreferredSize(new Dimension(120, 35));
                nameFields[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(101, 67, 33), 2),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                nameFields[i].setBackground(new Color(255, 248, 220));
                
                gbc.gridx = 0;
                gbc.gridy = i;
                gbc.anchor = GridBagConstraints.CENTER;
                formPanel.add(nameLabel, gbc);
                
                gbc.gridx = 1;
                formPanel.add(nameFields[i], gbc);
            }
        }

        // 스크롤 패널 설정
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JButton confirmButton = new JButton("확인");
        confirmButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        confirmButton.setPreferredSize(new Dimension(120, 45));
        confirmButton.setBackground(new Color(255, 223, 0));
        confirmButton.setForeground(new Color(101, 67, 33));
        confirmButton.setBorder(BorderFactory.createRaisedBevelBorder());
        confirmButton.addActionListener(e -> {
            // Java 8 Stream과 GitHub 요구사항 적용: 이름 5글자 제한
            List<String> participants = java.util.Arrays.stream(nameFields)
                    .map(JTextField::getText)
                    .map(String::trim)
                    .filter(name -> !name.isEmpty())
                    .map(name -> name.length() > 5 ? name.substring(0, 5) : name) // 5글자 제한
                    .collect(Collectors.toList());

            if (participants.size() != totalParticipants) {
                JOptionPane.showMessageDialog(nameDialog, "모든 참가자의 이름을 입력하세요.", "경고",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 5글자 제한 알림
            boolean hasLongNames = java.util.Arrays.stream(nameFields)
                    .anyMatch(field -> field.getText().trim().length() > 5);
            
            if (hasLongNames) {
                JOptionPane.showMessageDialog(nameDialog, 
                    "이름이 5글자를 초과하는 참가자는 5글자로 줄여집니다.", 
                    "알림", JOptionPane.INFORMATION_MESSAGE);
            }

            controller.startLadderGame(participants, loseCount, passCount);
            nameDialog.dispose();
        });

        // 메인 패널 구성 - 중앙 정렬로 변경
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // 제목 추가
        JLabel titleLabel = new JLabel("참가자 이름을 입력하세요", JLabel.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setForeground(new Color(101, 67, 33));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // 확인 버튼을 감싸는 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(confirmButton);
        
        mainContentPanel.add(titleLabel, BorderLayout.NORTH);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);
        mainContentPanel.add(buttonPanel, BorderLayout.SOUTH);

        backgroundPanel.add(mainContentPanel, BorderLayout.CENTER);
        nameDialog.setContentPane(backgroundPanel);
        nameDialog.setVisible(true);
    }

    public void showLadderGameScreen() {
        frame.getContentPane().removeAll();

        // 메인 패널 - 배경 이미지가 있는 패널
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    // 배경 이미지 로드 시도
                    Image bgImage = ImageIO.read(
                        new File("src/com/cheonwangforest/images/게임 창.png"));
                    if (bgImage != null) {
                        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                    } else {
                        // 배경 이미지가 없으면 기본 배경색
                        g.setColor(new Color(245, 235, 180)); // 연한 노란색 배경
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                } catch (IOException e) {
                    // 배경 이미지가 없을 경우 기본 배경색
                    g.setColor(new Color(245, 235, 180)); // 연한 노란색 배경
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // 상단: 참가자 이름들 (클릭 가능하게 변경)
        JPanel namePanel = new JPanel(
            new GridLayout(1, controller.getModel().getParticipants().size()));
        namePanel.setOpaque(false);
        List<String> participants = controller.getModel().getParticipants();

        for (int i = 0; i < participants.size(); i++) {
            String name = participants.get(i);
            final int participantIndex = i; // 람다식에서 사용하기 위한 final 변수

            JLabel nameLabel = new JLabel(name, JLabel.CENTER);
            nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
            nameLabel.setForeground(new Color(101, 67, 33)); // 갈색 텍스트
            nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
            nameLabel.setOpaque(false); // 투명 배경

            // 마우스 오버 효과 (커서 변경)
            nameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    nameLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                    nameLabel.setForeground(new Color(150, 100, 50)); // 살짝 밝은 갈색
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    nameLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    nameLabel.setForeground(new Color(101, 67, 33)); // 원래 갈색
                }

                // 클릭 이벤트: 개별 참가자 애니메이션 실행
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    startSingleParticipantAnimation(participantIndex);
                }
            });

            namePanel.add(nameLabel);
        }

        // 중앙: 사다리 그리기 패널
        ladderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
                drawLadder(g);
            }
        };
        ladderPanel.setPreferredSize(new Dimension(frame.getWidth() - 100, 450));
        ladderPanel.setLayout(null);

        // 하단: 버튼들
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        JButton resultButton = new JButton("결과 확인하기");
        resultButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        resultButton.setPreferredSize(new Dimension(150, 40));
        resultButton.setBackground(new Color(255, 223, 0));
        resultButton.setForeground(new Color(101, 67, 33));
        resultButton.setBorder(BorderFactory.createRaisedBevelBorder());
        resultButton.addActionListener(e -> {
            // GitHub 요구사항: 개별/전체 결과 조회 기능
            String[] options = {"전체 결과", "개별 결과"};
            int choice = JOptionPane.showOptionDialog(
                frame,
                "어떤 결과를 확인하시겠습니까?",
                "결과 조회 방식 선택",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );
            
            if (choice == 0) {
                // 전체 결과
                controller.checkResults();
            } else if (choice == 1) {
                // 개별 결과 - 참가자 선택
                String[] participantNames = controller.getModel().getParticipants().toArray(new String[0]);
                String selectedParticipant = (String) JOptionPane.showInputDialog(
                    frame,
                    "결과를 확인할 참가자를 선택하세요:",
                    "개별 결과 조회",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    participantNames,
                    participantNames[0]
                );
                
                if (selectedParticipant != null) {
                    // 개별 결과를 커스텀 다이얼로그로 표시
                    String result = controller.getModel().getResultForParticipant(selectedParticipant);
                    showCustomIndividualResultDialog(selectedParticipant, result);
                }
            }
        });
        buttonPanel.add(resultButton);

        // 재시작 버튼 추가
        JButton restartButton = new JButton("다시 시작");
        restartButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        restartButton.setPreferredSize(new Dimension(120, 40));
        restartButton.setBackground(new Color(200, 200, 200));
        restartButton.setForeground(new Color(101, 67, 33));
        restartButton.setBorder(BorderFactory.createRaisedBevelBorder());
        restartButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(frame,
                "게임을 다시 시작하시겠습니까?",
                "다시 시작",
                JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                showWelcomeScreen(); // controller.restartGame() 대신 직접 호출
            }
        });
        buttonPanel.add(restartButton);

        // 홈 버튼 추가
        JButton homeButton = new JButton();
        try {
            Image homeImage = ImageIO.read(new File("src/com/cheonwangforest/images/홈 버튼2.png"));
            homeButton.setIcon(
                new ImageIcon(homeImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            homeButton.setText("홈");
        }
        homeButton.setOpaque(false);
        homeButton.setContentAreaFilled(false);
        homeButton.setBorderPainted(false);
        homeButton.addActionListener(e -> {
            stopAnimation();
            showWelcomeScreen();
        });

        // 홈 버튼을 좌측 상단에 배치
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(homeButton, BorderLayout.WEST);
        topPanel.add(namePanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(ladderPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.revalidate();
        frame.repaint();

        startAnimation();
    }

    private void drawLadder(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int numParticipants = controller.getModel().getParticipants().size();
        int[][] ladder = controller.getModel().getLadder();

        // 사다리 그리기 영역 설정
        int panelWidth = ladderPanel.getWidth();
        int panelHeight = ladderPanel.getHeight();
        int ladderMargin = 80; // 좌우 여백
        int topMargin = 50; // 상단 여백
        int bottomMargin = 100; // 하단 여백

        int ladderWidth = panelWidth - (2 * ladderMargin);
        int ladderHeight = panelHeight - topMargin - bottomMargin;

        int startX = ladderMargin;
        int startY = topMargin;
        int colSpacing = ladderWidth / (numParticipants - 1);
        int rowSpacing = ladderHeight / ladder[0].length;

        // 세로 줄 그리기 (참가자별 수직선)
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.setColor(new Color(139, 69, 19)); // 갈색

        for (int i = 0; i < numParticipants; i++) {
            int x = startX + i * colSpacing;
            g2d.drawLine(x, startY, x, startY + ladderHeight);

            // 맨 위와 아래에 작은 원 그리기 (시작점과 끝점 표시)
            g2d.setColor(new Color(255, 223, 0)); // 노란색
            g2d.fillOval(x - 6, startY - 10, 12, 12);
            g2d.fillOval(x - 6, startY + ladderHeight - 2, 12, 12);
            g2d.setColor(new Color(139, 69, 19)); // 다시 갈색으로
        }

        // 가로 줄 그리기 (사다리의 다리)
        g2d.setStroke(new BasicStroke(2.5f));
        for (int i = 0; i < numParticipants - 1; i++) {
            for (int j = 0; j < ladder[i].length; j++) {
                if (ladder[i][j] == 1) {
                    int x1 = startX + i * colSpacing;
                    int x2 = startX + (i + 1) * colSpacing;
                    int y = startY + ((j + 1) * rowSpacing); // j+1로 수정해서 각 단계마다 가로선이 나타나도록

                    // 가로 다리 그리기
                    g2d.setColor(new Color(160, 82, 45)); // 조금 더 밝은 갈색
                    g2d.drawLine(x1, y, x2, y);

                    // 다리 양 끝에 작은 연결점 그리기
                    g2d.setColor(new Color(101, 67, 33)); // 어두운 갈색
                    g2d.fillOval(x1 - 3, y - 3, 6, 6);
                    g2d.fillOval(x2 - 3, y - 3, 6, 6);
                }
            }
        }

        // 애니메이션 중인 공들 그리기
        Point[] animPositions = (Point[]) ladderPanel.getClientProperty("animationPositions");
        if (animPositions != null && animationRunning.get()) {
            for (int i = 0; i < Math.min(animPositions.length, numParticipants); i++) {
                Point pos = animPositions[i];
                if (pos != null) {
                    // 그라데이션 효과가 있는 공 그리기
                    RadialGradientPaint gradient = new RadialGradientPaint(
                        pos.x - 2, pos.y - 2, 10,
                        new float[]{0f, 1f},
                        new Color[]{Color.WHITE, getParticipantColor(i)}
                    );
                    g2d.setPaint(gradient);
                    g2d.fillOval(pos.x - 10, pos.y - 10, 20, 20);

                    // 공 테두리
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(2.0f));
                    g2d.drawOval(pos.x - 10, pos.y - 10, 20, 20);

                    // 참가자 번호 표시
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("맑은 고딕", Font.BOLD, 14));
                    FontMetrics fm = g2d.getFontMetrics();
                    String num = String.valueOf(i + 1);
                    int textX = pos.x - fm.stringWidth(num) / 2;
                    int textY = pos.y + fm.getAscent() / 2 - 2;
                    g2d.drawString(num, textX, textY);
                }
            }
        }

        // 하단에 결과 구역 표시
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.setColor(new Color(34, 139, 34)); // 녹색
        int resultY = startY + ladderHeight + 30;

        for (int i = 0; i < numParticipants; i++) {
            int x = startX + (numParticipants > 1 ? i * colSpacing : ladderWidth / 2);
            g2d.drawRect(x - 25, resultY, 50, 30);

            // 결과 텍스트 (애니메이션이 끝나면 표시)
            if (!animationRunning.get()) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("맑은 고딕", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                String result = "?"; // 기본값

                // 실제 결과 가져오기
                List<String> participants = controller.getModel().getParticipants();
                if (i < participants.size()) {
                    int resultIndex = controller.getModel().calculatePathResult(i);
                    if (resultIndex < participants.size()) {
                        // 여기서 결과를 직접 계산해서 표시
                        String participantName = participants.get(i);
                        result = controller.getModel().getResultForParticipant(participantName);
                    }
                }

                int textX = x - fm.stringWidth(result) / 2;
                int textY = resultY + 20;
                g2d.drawString(result, textX, textY);
                g2d.setColor(new Color(34, 139, 34));
            }
        }

        g2d.dispose();
    }

    private void startAnimation() {
        if (animationRunning.get()) {
            return;
        }
        animationRunning.set(true);

        Timer animationTimer = new Timer(30, null); // 80에서 30으로 변경 (더 빠르게)

        // 애니메이션 변수들
        int numParticipants = controller.getModel().getParticipants().size();
        Point[] currentPositions = new Point[numParticipants];
        int[] pathIndices = new int[numParticipants];
        List<List<Point>> screenPaths = new ArrayList<>();

        // 사다리 영역 계산
        int panelWidth = ladderPanel.getWidth();
        int panelHeight = ladderPanel.getHeight();
        int ladderMargin = 80;
        int topMargin = 50;
        int bottomMargin = 100;
        int ladderWidth = panelWidth - (2 * ladderMargin);
        int ladderHeight = panelHeight - topMargin - bottomMargin;
        int startX = ladderMargin;
        int startY = topMargin;

        // 각 참가자의 경로 계산
        List<List<Point>> paths = controller.getModel().getLadderPaths();

        // 경로를 실제 화면 좌표로 변환
        for (int i = 0; i < numParticipants; i++) {
            List<Point> screenPath = new ArrayList<>();
            List<Point> originalPath = paths.get(i);

            for (Point p : originalPath) {
                int screenX = startX + (int) ((double) p.x / 500 * ladderWidth);
                int screenY = startY + (int) ((double) p.y / 400 * ladderHeight);
                screenPath.add(new Point(screenX, screenY));
            }
            screenPaths.add(screenPath);

            // 시작 위치 설정
            Point startPos = screenPath.get(0);
            currentPositions[i] = new Point(startPos.x, startPos.y);
            pathIndices[i] = 1;
        }

        animationTimer.addActionListener(e -> {
            boolean allArrived = true;

            // 각 참가자의 위치 업데이트
            for (int i = 0; i < numParticipants; i++) {
                List<Point> path = screenPaths.get(i);
                if (pathIndices[i] < path.size()) {
                    Point currentPos = currentPositions[i];
                    Point targetPos = path.get(pathIndices[i]);

                    // 목표 지점까지의 거리 계산
                    int dx = targetPos.x - currentPos.x;
                    int dy = targetPos.y - currentPos.y;
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    int stepSize = 8; // 이동 속도 (4에서 8로 증가)

                    if (distance > stepSize) {
                        // 목표를 향해 조금씩 이동
                        currentPos.x += (int) (stepSize * dx / distance);
                        currentPos.y += (int) (stepSize * dy / distance);
                        allArrived = false;
                    } else {
                        // 목표에 도착했으면 다음 경로 지점으로
                        currentPos.x = targetPos.x;
                        currentPos.y = targetPos.y;
                        pathIndices[i]++;
                        if (pathIndices[i] < path.size()) {
                            allArrived = false;
                        }
                    }
                }
            }

            // 애니메이션 위치 저장하고 다시 그리기
            ladderPanel.putClientProperty("animationPositions", currentPositions.clone());
            ladderPanel.repaint();

            if (allArrived) {
                animationRunning.set(false);
                animationTimer.stop();
                ladderPanel.putClientProperty("animationPositions", null);
                ladderPanel.repaint();
            }
        });

        animationTimer.start();
    }

    // 참가자별 고유 색상 반환
    private Color getParticipantColor(int index) {
        Color[] colors = {
            new Color(255, 99, 132),   // 분홍
            new Color(54, 162, 235),   // 파랑
            new Color(255, 205, 86),   // 노랑
            new Color(75, 192, 192),   // 청록
            new Color(153, 102, 255),  // 보라
            new Color(255, 159, 64),   // 주황
            new Color(199, 199, 199),  // 회색
            new Color(83, 102, 255),   // 남색
        };
        return colors[index % colors.length];
    }

    public void stopAnimation() {
        animationRunning.set(false);
    }

    // 개별 참가자 애니메이션
    private void startSingleParticipantAnimation(int participantIndex) {
        if (animationRunning.get()) {
            return;
        }
        animationRunning.set(true);

        Thread animationThread = new Thread(() -> {
            // 애니메이션 설정
            int stepSize = 8; // 이동 속도 (빠르게 변경)
            int animationDelay = 20; // 프레임 간격 (ms) - 40에서 20으로 변경

            // 사다리 영역 계산
            int panelWidth = ladderPanel.getWidth();
            int panelHeight = ladderPanel.getHeight();
            int ladderMargin = 80;
            int topMargin = 50;
            int bottomMargin = 100;

            int ladderWidth = panelWidth - (2 * ladderMargin);
            int ladderHeight = panelHeight - topMargin - bottomMargin;

            int startX = ladderMargin;
            int startY = topMargin;

            // 선택된 참가자의 경로 계산
            List<List<Point>> paths = controller.getModel().getLadderPaths();
            List<Point> originalPath = paths.get(participantIndex);

            // 경로를 실제 화면 좌표로 변환
            List<Point> screenPath = new ArrayList<>();
            for (Point p : originalPath) {
                int screenX = startX + (int) ((double) p.x / 500 * ladderWidth);
                int screenY = startY + (int) ((double) p.y / 400 * ladderHeight);
                screenPath.add(new Point(screenX, screenY));
            }

            // 현재 위치와 경로 인덱스
            Point currentPosition = new Point(screenPath.get(0));
            int pathIndex = 1; // 0번째는 시작점이므로 1번째부터 시작

            // 애니메이션 루프
            while (animationRunning.get() && pathIndex < screenPath.size()) {
                Point targetPos = screenPath.get(pathIndex);

                SwingUtilities.invokeLater(() -> {
                    Graphics2D g2d = (Graphics2D) ladderPanel.getGraphics();
                    if (g2d != null) {
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                        // 패널 전체를 다시 그리기
                        ladderPanel.repaint();

                        // 선택된 참가자의 공 그리기 (더 크고 눈에 띄게)
                        RadialGradientPaint gradient = new RadialGradientPaint(
                            currentPosition.x - 3, currentPosition.y - 3, 12,
                            new float[]{0f, 1f},
                            new Color[]{Color.WHITE, getParticipantColor(participantIndex)}
                        );
                        g2d.setPaint(gradient);
                        g2d.fillOval(currentPosition.x - 12, currentPosition.y - 12, 24, 24);

                        // 공 테두리 (두껍게)
                        g2d.setColor(Color.BLACK);
                        g2d.setStroke(new BasicStroke(2.0f));
                        g2d.drawOval(currentPosition.x - 12, currentPosition.y - 12, 24, 24);

                        // 참가자 번호 표시 (더 크게)
                        g2d.setColor(Color.WHITE);
                        g2d.setFont(new Font("맑은 고딕", Font.BOLD, 16));
                        FontMetrics fm = g2d.getFontMetrics();
                        String num = String.valueOf(participantIndex + 1);
                        int textX = currentPosition.x - fm.stringWidth(num) / 2;
                        int textY = currentPosition.y + fm.getAscent() / 2 - 2;
                        g2d.drawString(num, textX, textY);

                        g2d.dispose();
                    }
                });

                // 목표 지점까지의 거리 계산
                int dx = targetPos.x - currentPosition.x;
                int dy = targetPos.y - currentPosition.y;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance > stepSize) {
                    // 목표를 향해 조금씩 이동
                    currentPosition.x += (int) (stepSize * dx / distance);
                    currentPosition.y += (int) (stepSize * dy / distance);
                } else {
                    // 목표에 도착했으면 다음 경로 지점으로
                    currentPosition.x = targetPos.x;
                    currentPosition.y = targetPos.y;
                    pathIndex++;
                }

                try {
                    Thread.sleep(animationDelay);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    animationRunning.set(false);
                }
            }

            // 애니메이션 완료 후 결과 표시
            SwingUtilities.invokeLater(() -> {
                animationRunning.set(false);
                ladderPanel.repaint();

                // 결과 팝업을 커스텀 다이얼로그로 표시
                String participantName = controller.getModel().getParticipants()
                    .get(participantIndex);
                String result = controller.getModel().getResultForParticipant(participantName);

                showCustomIndividualResultDialog(participantName, result);
            });
        });

        animationThread.setDaemon(true);
        animationThread.start();
    }

    // 버튼으로 숫자 조절하는 헬퍼 메소드
    private void updateCount(JTextField field, int change, int min, int max) {
        try {
            int currentCount = Integer.parseInt(field.getText());
            int newCount = currentCount + change;
            if (newCount >= min && newCount <= max) {
                field.setText(String.valueOf(newCount));
            }
        } catch (NumberFormatException ex) {
            field.setText(String.valueOf(min));
        }
    }

    /**
     * 클래스패스에서 이미지를 로드하여 버튼을 생성하는 헬퍼 메서드.
     *
     * @param imagePath 클래스패스 기준 이미지 경로. 예: "/com/cheonwangforest/images/image.png"
     */
    private JButton createButton(String imagePath) {
        JButton button = new JButton();
        try {
            // getClass().getResourceAsStream()을 사용하여 이미지를 스트림으로 읽음.
            // 이렇게 하면 JAR 파일 내의 리소스도 안정적으로 로드할 수 있습니다.
            java.io.InputStream imgStream = getClass().getResourceAsStream(
                imagePath.replace("src/", "/"));
            if (imgStream == null) {
                throw new IOException("이미지 파일을 찾을 수 없습니다: " + imagePath);
            }
            Image img = ImageIO.read(imgStream);
            button.setIcon(new ImageIcon(img.getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            System.err.println("버튼 이미지를 로드하는 중 오류가 발생했습니다: " + e.getMessage());
            button.setText("버튼");
        }
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        return button;
    }

    // 커스텀 개별 결과 다이얼로그
    private void showCustomIndividualResultDialog(String participantName, String result) {
        JDialog resultDialog = new JDialog(frame, "개별 결과", true);
        resultDialog.setSize(600, 400); // 개별 결과는 좀 더 작게
        resultDialog.setLocationRelativeTo(frame);

        // 배경 이미지가 있는 패널
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bgImage = ImageIO.read(
                        new File("src/com/cheonwangforest/images/팝업_창_1133_x_637.png"));
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } catch (IOException e) {
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
        contentPanel.setBorder(BorderFactory.createEmptyBorder(80, 50, 80, 50));

        // 제목
        JLabel titleLabel = new JLabel("🎯 개별 결과 🎯", JLabel.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setForeground(new Color(101, 67, 33));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        
        contentPanel.add(Box.createVerticalStrut(40));

        // 결과 표시 - 이모지 제거
        JLabel resultLabel = new JLabel(String.format("%s → %s", 
            participantName, result), JLabel.CENTER);
        resultLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        resultLabel.setForeground(new Color(101, 67, 33));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(resultLabel);

        // 확인 버튼
        contentPanel.add(Box.createVerticalStrut(40));
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

    /**
     * 클래스패스에서 이미지를 로드하여 고정된 크기의 버튼을 생성하는 헬퍼 메서드.
     *
     * @param imagePath 클래스패스 기준 이미지 경로. 예: "/com/cheonwangforest/images/image.png"
     * @param width     버튼의 너비
     * @param height    버튼의 높이
     */
    private JButton createButtonWithFixedSize(String imagePath, int width, int height) {
        JButton button = new JButton();
        try {
            // getClass().getResourceAsStream()을 사용하여 이미지를 스트림으로 읽음.
            java.io.InputStream imgStream = getClass().getResourceAsStream(
                imagePath.replace("src/", "/"));
            if (imgStream == null) {
                throw new IOException("이미지 파일을 찾을 수 없습니다: " + imagePath);
            }
            Image img = ImageIO.read(imgStream);
            button.setIcon(new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            System.err.println("고정 크기 버튼 이미지를 로드하는 중 오류가 발생했습니다: " + e.getMessage());
            button.setText("버튼");
        }
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        return button;
    }
}
