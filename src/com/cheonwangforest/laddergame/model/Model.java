package com.cheonwangforest.laddergame.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 사다리타기 게임 모델 클래스
 * GitHub 요구사항 준수:
 * - Java 8 Stream과 Lambda 적용
 * - 모든 엔티티를 작게 유지 (인스턴스 변수 5개)
 * - 함수형 프로그래밍 패러다임 적용
 */
public class Model {

    private String gameTitle; // 게임 제목 추가
    private List<String> participants;
    private int[][] ladder;
    private List<String> results;
    private String adminLoser;

    public Model() {
        // 기본 생성자
        this.gameTitle = "사다리타기"; // 기본 제목
    }

    // 게임 제목 설정/조회 메서드 추가
    public void setGameTitle(String title) {
        this.gameTitle = title;
    }

    public String getGameTitle() {
        return gameTitle;
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

        generateLadder(participants.size());

        assignResults(loseCount, passCount);

        if (adminLoser != null && participants.contains(adminLoser)) {
            // 관리자 모드: 꽝 설정
            int loserIndex = participants.indexOf(adminLoser);
            int pathResultIndex = calculatePathResult(loserIndex);

            // 기존 꽝 결과와 교환
            int currentLoserIndex = results.indexOf("꽝");
            if (currentLoserIndex != -1 && currentLoserIndex != pathResultIndex) {
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
                // 라인이 겹치지 않도록 수정: 이전 라인이 있으면 현재 라인은 생성하지 않음
                if (j == 0 || ladder[j - 1][i] == 0) { // 왼쪽에 라인이 없을 때만
                    if (random.nextBoolean()) {
                        ladder[j][i] = 1;
                        // 다음 위치는 강제로 0으로 설정하여 겹치지 않도록 함
                        if (j + 1 < numParticipants - 1) {
                            j++; // 다음 인덱스를 건너뛰어서 겹치지 않도록
                        }
                    }
                }
            }
        }
    }

    private void assignResults(int loseCount, int passCount) {
        // 디버깅용 출력 추가
        System.out.println("=== assignResults 호출됨 ===");
        System.out.println("participants 수: " + participants.size());
        System.out.println("loseCount: " + loseCount);
        System.out.println("passCount: " + passCount);
        
        // Java 8 Stream을 사용한 개선된 결과 배정
        List<String> tempResults = Stream.concat(
            // 꽝 결과 생성
            IntStream.range(0, loseCount)
                    .mapToObj(i -> "꽝"),
            // 통과 결과 생성
            IntStream.range(0, participants.size() - loseCount)
                    .mapToObj(i -> "통과")
        ).collect(Collectors.toList());
        
        // 결과 검증
        if (tempResults.size() != participants.size()) {
            System.err.println("결과 수와 참가자 수가 일치하지 않습니다!");
            tempResults = IntStream.range(0, participants.size())
                    .mapToObj(i -> i < loseCount ? "꽝" : "통과")
                    .collect(Collectors.toList());
        }
        
        Collections.shuffle(tempResults);
        this.results = tempResults;
        
        // 디버깅용 출력
        System.out.println("생성된 결과: " + tempResults);
        System.out.println("=== assignResults 완료 ===");
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

        int colSpacing = numParticipants > 1 ? LADDER_WIDTH / (numParticipants - 1) : LADDER_WIDTH / 2;
        int rowSpacing = LADDER_HEIGHT / numRungs;

        for (int i = 0; i < numParticipants; i++) {
            List<Point> path = new ArrayList<>();
            int currentX = i;

            // 시작점
            int startX = numParticipants > 1 ? i * colSpacing : LADDER_WIDTH / 2;
            path.add(new Point(startX, 0));

            for (int j = 0; j < numRungs; j++) {
                int nextY = (j + 1) * rowSpacing;
                boolean hasMoved = false;

                // 왼쪽 이동 체크 (calculatePathResult와 동일한 로직)
                if (currentX > 0 && ladder[currentX - 1][j] == 1) {
                    // 수직 이동 후 수평 이동
                    int currentPosX = numParticipants > 1 ? currentX * colSpacing : LADDER_WIDTH / 2;
                    path.add(new Point(currentPosX, nextY));
                    currentX--;
                    int newPosX = numParticipants > 1 ? currentX * colSpacing : LADDER_WIDTH / 2;
                    path.add(new Point(newPosX, nextY));
                    hasMoved = true;
                }
                // 오른쪽 이동 체크
                else if (currentX < numParticipants - 1 && ladder[currentX][j] == 1) {
                    // 수직 이동 후 수평 이동
                    int currentPosX = numParticipants > 1 ? currentX * colSpacing : LADDER_WIDTH / 2;
                    path.add(new Point(currentPosX, nextY));
                    currentX++;
                    int newPosX = numParticipants > 1 ? currentX * colSpacing : LADDER_WIDTH / 2;
                    path.add(new Point(newPosX, nextY));
                    hasMoved = true;
                }

                // 수평 이동이 없었다면 수직 이동만
                if (!hasMoved) {
                    int currentPosX = numParticipants > 1 ? currentX * colSpacing : LADDER_WIDTH / 2;
                    path.add(new Point(currentPosX, nextY));
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
            String result = results.get(resultIndex);
            return result;
        }
        return "결과를 찾을 수 없습니다.";
    }
}
