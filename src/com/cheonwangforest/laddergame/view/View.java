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

        // 상단: 참가자 이름들
        JPanel namePanel = new JPanel(
            new GridLayout(1, controller.getModel().getParticipants().size()));
        namePanel.setOpaque(false);
        List<String> participants = controller.getModel().getParticipants();

        for (String name : participants) {
            JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
            nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
            nameLabel.setForeground(new Color(101, 67, 33)); // 갈색 텍스트
            nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
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

        // 하단: 결과 보기 버튼
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        JButton resultButton = new JButton("결과 확인하기");
        resultButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        resultButton.setPreferredSize(new Dimension(150, 40));
        resultButton.setBackground(new Color(255, 223, 0));
        resultButton.setForeground(new Color(101, 67, 33));
        resultButton.setBorder(BorderFactory.createRaisedBevelBorder());
        resultButton.addActionListener(e -> controller.checkResults());
        buttonPanel.add(resultButton);

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

        // 하단에 결과 구역 표시
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.setColor(new Color(34, 139, 34)); // 녹색
        int resultY = startY + ladderHeight + 30;

        for (int i = 0; i < numParticipants; i++) {
            int x = startX + i * colSpacing;
            g2d.drawRect(x - 25, resultY, 50, 30);

            // 결과 텍스트 (애니메이션이 끝나면 표시)
            if (!animationRunning.get()) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("맑은 고딕", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                String result = "?"; // 기본값
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

        Thread animationThread = new Thread(() -> {
            int numParticipants = controller.getModel().getParticipants().size();
            List<Point> currentPositions = new ArrayList<>();
            int[] pathIndices = new int[numParticipants];

            // 애니메이션 설정
            int stepSize = 3; // 이동 속도
            int animationDelay = 50; // 프레임 간격 (ms)

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
            int colSpacing = ladderWidth / (numParticipants - 1);

            // 각 참가자의 경로 계산
            List<List<Point>> paths = controller.getModel().getLadderPaths();

            // 경로를 실제 화면 좌표로 변환
            List<List<Point>> screenPaths = new ArrayList<>();
            for (int i = 0; i < numParticipants; i++) {
                List<Point> screenPath = new ArrayList<>();
                List<Point> originalPath = paths.get(i);

                for (Point p : originalPath) {
                    // 모델의 좌표를 화면 좌표로 변환
                    int screenX = startX + (int) ((double) p.x / 500 * ladderWidth);
                    int screenY = startY + (int) ((double) p.y / 400 * ladderHeight);
                    screenPath.add(new Point(screenX, screenY));
                }
                screenPaths.add(screenPath);
            }

            // 각 참가자의 시작 위치 설정
            for (int i = 0; i < numParticipants; i++) {
                Point startPos = screenPaths.get(i).get(0);
                currentPositions.add(new Point(startPos.x, startPos.y));
                pathIndices[i] = 1; // 0번째는 시작점이므로 1번째부터 시작
            }

            // 애니메이션 루프
            while (animationRunning.get()) {
                boolean allArrived = true;

                SwingUtilities.invokeLater(() -> {
                    Graphics2D g2d = (Graphics2D) ladderPanel.getGraphics();
                    if (g2d != null) {
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                        // 패널 전체를 다시 그리기
                        ladderPanel.repaint();

                        // 각 참가자의 공 그리기
                        for (int i = 0; i < numParticipants; i++) {
                            Point currentPos = currentPositions.get(i);

                            // 그라데이션 효과가 있는 공 그리기
                            RadialGradientPaint gradient = new RadialGradientPaint(
                                currentPos.x - 2, currentPos.y - 2, 8,
                                new float[]{0f, 1f},
                                new Color[]{Color.WHITE, getParticipantColor(i)}
                            );
                            g2d.setPaint(gradient);
                            g2d.fillOval(currentPos.x - 8, currentPos.y - 8, 16, 16);

                            // 공 테두리
                            g2d.setColor(Color.BLACK);
                            g2d.setStroke(new BasicStroke(1.5f));
                            g2d.drawOval(currentPos.x - 8, currentPos.y - 8, 16, 16);

                            // 참가자 번호 표시
                            g2d.setColor(Color.WHITE);
                            g2d.setFont(new Font("맑은 고딕", Font.BOLD, 12));
                            FontMetrics fm = g2d.getFontMetrics();
                            String num = String.valueOf(i + 1);
                            int textX = currentPos.x - fm.stringWidth(num) / 2;
                            int textY = currentPos.y + fm.getAscent() / 2 - 1;
                            g2d.drawString(num, textX, textY);
                        }
                        g2d.dispose();
                    }
                });

                // 각 참가자의 위치 업데이트
                for (int i = 0; i < numParticipants; i++) {
                    List<Point> path = screenPaths.get(i);
                    if (pathIndices[i] < path.size()) {
                        Point currentPos = currentPositions.get(i);
                        Point targetPos = path.get(pathIndices[i]);

                        // 목표 지점까지의 거리 계산
                        int dx = targetPos.x - currentPos.x;
                        int dy = targetPos.y - currentPos.y;
                        double distance = Math.sqrt(dx * dx + dy * dy);

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

                if (allArrived) {
                    animationRunning.set(false);
                    SwingUtilities.invokeLater(() -> ladderPanel.repaint());
                }

                try {
                    Thread.sleep(animationDelay);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    animationRunning.set(false);
                }
            }
        });

        animationThread.setDaemon(true);
        animationThread.start();
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
