package com.cheonwangforest.laddergame.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Model {
    private List<String> participants;
    private int[][] ladder;
    private List<String> results;
    private String adminLoser;

    public Model() {
        // 기본 생성자
    }

    public List<String> getParticipants() {
        return participants;
    }

    public int[][] getLadder() {
        return ladder;
    }

    public void setAdminLoser(String adminLoser) {
        this.adminLoser = adminLoser;
    }

    public void setLadderData(List<String> participants, int loseCount, int passCount) {
        this.participants = participants;
        this.results = new ArrayList<>(Collections.nCopies(participants.size(), ""));

        generateLadder(participants.size());

        assignResults(loseCount, passCount);

        if (adminLoser != null && participants.contains(adminLoser)) {
            // 관리자 모드: 꽝 설정
            int loserIndex = participants.indexOf(adminLoser);
            int pathResultIndex = calculatePathResult(loserIndex);

            // 기존 꽝 결과와 교환
            int currentLoserIndex = results.indexOf("꽝");
            if (currentLoserIndex != -1) {
                String temp = results.get(pathResultIndex);
                results.set(pathResultIndex, "꽝");
                results.set(currentLoserIndex, temp);
            }
        }
    }

    private void generateLadder(int numParticipants) {
        int numRungs = 15; // 사다리 가로줄 개수 고정
        ladder = new int[numParticipants - 1][numRungs];
        Random random = new Random();

        for (int i = 0; i < numRungs; i++) {
            for (int j = 0; j < numParticipants - 1; j++) {
                if (random.nextBoolean()) {
                    ladder[j][i] = 1;
                    if (j + 1 < numParticipants - 1) {
                        ladder[j + 1][i] = 0; // 옆에 다리가 생기지 않도록
                    }
                }
            }
        }
    }

    private void assignResults(int loseCount, int passCount) {
        List<String> tempResults = new ArrayList<>();
        for (int i = 0; i < loseCount; i++) {
            tempResults.add("꽝");
        }
        for (int i = 0; i < passCount; i++) {
            tempResults.add("통과");
        }
        Collections.shuffle(tempResults);
        this.results = tempResults;
    }

    public int calculatePathResult(int startIndex) {
        int currentX = startIndex;
        int numRungs = ladder[0].length;

        for (int i = 0; i < numRungs; i++) {
            if (currentX > 0 && ladder[currentX - 1][i] == 1) {
                currentX--; // 왼쪽으로 이동
            } else if (currentX < participants.size() - 1 && ladder[currentX][i] == 1) {
                currentX++; // 오른쪽으로 이동
            }
        }
        return currentX;
    }

    public List<List<Point>> getLadderPaths() {
        List<List<Point>> paths = new ArrayList<>();
        int numParticipants = participants.size();
        int numRungs = ladder[0].length;
        int LADDER_WIDTH = 500;
        int LADDER_HEIGHT = 400;
        int colSpacing = LADDER_WIDTH / (numParticipants - 1);
        int rowSpacing = LADDER_HEIGHT / numRungs;

        for (int i = 0; i < numParticipants; i++) {
            List<Point> path = new ArrayList<>();
            int currentX = i;
            int currentY = 0;
            path.add(new Point(i * colSpacing, currentY));

            for (int j = 0; j < numRungs; j++) {
                // 현재 위치에서 다음 칸으로 수직 이동
                currentY += rowSpacing;
                path.add(new Point(currentX * colSpacing, currentY));

                // 수평 이동 확인
                if (currentX > 0 && ladder[currentX - 1][j] == 1) {
                    currentX--;
                    path.add(new Point(currentX * colSpacing, currentY));
                } else if (currentX < numParticipants - 1 && ladder[currentX][j] == 1) {
                    currentX++;
                    path.add(new Point(currentX * colSpacing, currentY));
                }
            }
            paths.add(path);
        }
        return paths;
    }

    public String getResultForParticipant(String participantName) {
        int index = participants.indexOf(participantName);
        if (index != -1) {
            int resultIndex = calculatePathResult(index);
            return results.get(resultIndex);
        }
        return "결과를 찾을 수 없습니다.";
    }
}
