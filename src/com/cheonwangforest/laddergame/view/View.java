package com.cheonwangforest.laddergame.view;

import com.cheonwangforest.laddergame.controller.Controller;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;

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
                        new File("src/com/cheonwangforest/images/팝업 창 1133 * 637.png"));
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } catch (IOException e) {
                    System.out.println("팝업 창 배경 이미지를 찾을 수 없습니다.");
                }
            }
        };

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        nameFields = new JTextField[totalParticipants];
        for (int i = 0; i < totalParticipants; i++) {
            JLabel nameLabel = new JLabel("참가자 " + (i + 1) + ":");
            nameLabel.setOpaque(false);
            nameFields[i] = new JTextField();
            nameFields[i].setOpaque(false);
            formPanel.add(nameLabel);
            formPanel.add(nameFields[i]);
        }

        JButton confirmButton = new JButton("확인");
        confirmButton.addActionListener(e -> {
            List<String> participants = new ArrayList<>();
            for (JTextField field : nameFields) {
                if (!field.getText().trim().isEmpty()) {
                    participants.add(field.getText().trim());
                }
            }

            if (participants.size() != totalParticipants) {
                JOptionPane.showMessageDialog(nameDialog, "모든 참가자의 이름을 입력하세요.", "경고",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            controller.startLadderGame(participants, loseCount, passCount);
            nameDialog.dispose();
        });

        backgroundPanel.add(scrollPane, BorderLayout.CENTER);
        backgroundPanel.add(confirmButton, BorderLayout.SOUTH);
        nameDialog.setContentPane(backgroundPanel);
        nameDialog.setVisible(true);
    }

    public void showLadderGameScreen() {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        ladderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bgImage = ImageIO.read(getClass().getResourceAsStream("/com/cheonwangforest/images/게임 창.png"));
                    if (bgImage != null) {
                        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                    } else {
                        System.err.println("배경 이미지를 찾을 수 없습니다: 게임 창.png");
                    }
                } catch (IOException e) {
                    System.err.println("배경 이미지를 로드하는 중 오류가 발생했습니다.");
                    e.printStackTrace();
                }
                drawLadder(g);
            }
        };
        ladderPanel.setPreferredSize(new Dimension(LADDER_WIDTH, LADDER_HEIGHT));
        ladderPanel.setLayout(null);

        JPanel namePanel = new JPanel(new GridLayout(1, controller.getModel().getParticipants().size()));
        for (String name : controller.getModel().getParticipants()) {
            namePanel.add(new JLabel(name, SwingConstants.CENTER));
        }

        JButton resultButton = new JButton("결과 확인하기");
        resultButton.addActionListener(e -> controller.checkResults());

        frame.add(namePanel, BorderLayout.NORTH);
        frame.add(ladderPanel, BorderLayout.CENTER);
        frame.add(resultButton, BorderLayout.SOUTH);

        frame.revalidate();
        frame.repaint();

        startAnimation();
    }

    private void drawLadder(Graphics g) {
        int numParticipants = controller.getModel().getParticipants().size();
        int[][] ladder = controller.getModel().getLadder();

        int startX = (ladderPanel.getWidth() - LADDER_WIDTH) / 2;
        int startY = (ladderPanel.getHeight() - LADDER_HEIGHT) / 2;
        int colSpacing = LADDER_WIDTH / (numParticipants - 1);
        int rowSpacing = LADDER_HEIGHT / (ladder[0].length);

        g.setColor(Color.BLACK);
        // 수직선 그리기
        for (int i = 0; i < numParticipants; i++) {
            g.drawLine(startX + i * colSpacing, startY, startX + i * colSpacing, startY + LADDER_HEIGHT);
        }

        // 수평선 그리기 - 무작위성 제거
        for (int i = 0; i < numParticipants - 1; i++) {
            for (int j = 0; j < ladder[i].length; j++) {
                if (ladder[i][j] == 1) {
                    int x1 = startX + i * colSpacing;
                    int y = startY + j * rowSpacing;
                    int x2 = startX + (i + 1) * colSpacing;
                    g.drawLine(x1, y, x2, y);
                }
            }
        }
    }

    private void startAnimation() {
        if (animationRunning.get()) return;
        animationRunning.set(true);

        Thread animationThread = new Thread(() -> {
            int numParticipants = controller.getModel().getParticipants().size();
            List<Point> currentPositions = new ArrayList<>();
            int[] pathIndices = new int[numParticipants];

            // 공 이동 속도 조절
            int stepSize = 5;

            int startX = (ladderPanel.getWidth() - LADDER_WIDTH) / 2;
            int startY = (ladderPanel.getHeight() - LADDER_HEIGHT) / 2;

            // 각 참가자의 현재 위치를 시작점으로 설정
            List<List<Point>> paths = controller.getModel().getLadderPaths();
            for (int i = 0; i < numParticipants; i++) {
                currentPositions.add(new Point(startX + paths.get(i).get(0).x, startY + paths.get(i).get(0).y));
            }

            while (animationRunning.get()) {
                boolean allArrived = true;
                Graphics g = ladderPanel.getGraphics();
                g.clearRect(0, 0, ladderPanel.getWidth(), ladderPanel.getHeight());
                drawLadder(g);

                for (int i = 0; i < numParticipants; i++) {
                    Point currentPos = currentPositions.get(i);
                    List<Point> path = paths.get(i);

                    if (pathIndices[i] < path.size()) {
                        Point targetPos = path.get(pathIndices[i]);

                        // 현재 위치에서 목표 위치까지의 거리 계산
                        int dx = targetPos.x - (currentPos.x - startX);
                        int dy = targetPos.y - (currentPos.y - startY);
                        double distance = Math.sqrt(dx * dx + dy * dy);

                        if (distance > stepSize) {
                            // 목표를 향해 조금씩 이동
                            currentPos.x += (int) (stepSize * dx / distance);
                            currentPos.y += (int) (stepSize * dy / distance);
                            allArrived = false;
                        } else {
                            // 목표에 도착했으면 다음 경로 지점으로 이동
                            currentPos.x = startX + targetPos.x;
                            currentPos.y = startY + targetPos.y;
                            pathIndices[i]++;
                            if (pathIndices[i] < path.size()) {
                                allArrived = false;
                            }
                        }
                    }

                    g.setColor(Color.BLUE);
                    g.fillOval(currentPos.x - 5, currentPos.y - 5, 10, 10);
                }

                if (allArrived) {
                    animationRunning.set(false);
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    animationRunning.set(false);
                }
            }
        });
        animationThread.start();
    }

    public void stopAnimation() {
        animationRunning.set(false);
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
     * @param imagePath 클래스패스 기준 이미지 경로. 예: "/com/cheonwangforest/images/image.png"
     */
    private JButton createButton(String imagePath) {
        JButton button = new JButton();
        try {
            // getClass().getResourceAsStream()을 사용하여 이미지를 스트림으로 읽음.
            // 이렇게 하면 JAR 파일 내의 리소스도 안정적으로 로드할 수 있습니다.
            java.io.InputStream imgStream = getClass().getResourceAsStream(imagePath.replace("src/", "/"));
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

    /**
     * 클래스패스에서 이미지를 로드하여 고정된 크기의 버튼을 생성하는 헬퍼 메서드.
     * @param imagePath 클래스패스 기준 이미지 경로. 예: "/com/cheonwangforest/images/image.png"
     * @param width 버튼의 너비
     * @param height 버튼의 높이
     */
    private JButton createButtonWithFixedSize(String imagePath, int width, int height) {
        JButton button = new JButton();
        try {
            // getClass().getResourceAsStream()을 사용하여 이미지를 스트림으로 읽음.
            java.io.InputStream imgStream = getClass().getResourceAsStream(imagePath.replace("src/", "/"));
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
