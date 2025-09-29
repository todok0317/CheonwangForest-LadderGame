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
    private List<String> adminLosers; // 여러 명의 꽝 설정 가능

    public Model() {
        // 기본 생성자
        this.gameTitle = "사다리타기"; // 기본 제목
        this.adminLosers = new ArrayList<>(); // 빈 리스트로 초기화
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
        if (adminLoser != null && !adminLoser.trim().isEmpty()) {
            this.adminLosers.add(adminLoser.trim());
        }
    }
    
    public void setAdminLosers(List<String> adminLosers) {
        this.adminLosers = new ArrayList<>(adminLosers);
    }
    
    public List<String> getAdminLosers() {
        return adminLosers;
    }
    
    public void clearAdminLosers() {
        this.adminLosers = new ArrayList<>();
    }

    public void setLadderData(List<String> participants, int loseCount, int passCount) {
        this.participants = participants;

        generateLadder(participants.size());

        assignResults(loseCount, passCount);

        // 관리자 모드: 여러 명의 꽝 설정
        if (adminLosers != null && !adminLosers.isEmpty()) {
            System.out.println("=== 관리자 모드 적용 ===");
            System.out.println("관리자 지정 꽝: " + adminLosers);
            
            // 이미 사용된 교환 인덱스 추적
            List<Integer> usedSwapIndices = new ArrayList<>();
            
            // 각 관리자 지정 참가자가 도착하는 결과 인덱스 계산
            for (String loserName : adminLosers) {
                if (!participants.contains(loserName)) {
                    System.out.println("경고: " + loserName + "은(는) 참가자 명단에 없습니다.");
                    continue;
                }
                
                int participantIndex = participants.indexOf(loserName);
                int pathResultIndex = calculatePathResult(participantIndex);
                
                System.out.println(loserName + " (인덱스 " + participantIndex + ") -> 결과 인덱스 " + pathResultIndex);
                
                // 해당 위치가 이미 꽝이면 스킵
                if (results.get(pathResultIndex).equals("꽝")) {
                    System.out.println(loserName + "의 결과는 이미 꽝입니다.");
                    usedSwapIndices.add(pathResultIndex); // 이미 꽝인 위치도 사용된 것으로 표시
                    continue;
                }
                
                // 꽝이 아니면, 다른 꽝 위치와 교환 (이미 사용된 위치는 제외)
                int swapIndex = -1;
                for (int i = 0; i < results.size(); i++) {
                    if (results.get(i).equals("꽝") && 
                        i != pathResultIndex && 
                        !usedSwapIndices.contains(i)) {
                        swapIndex = i;
                        break;
                    }
                }
                
                if (swapIndex != -1) {
                    String temp = results.get(pathResultIndex);
                    results.set(pathResultIndex, "꽝");
                    results.set(swapIndex, temp);
                    usedSwapIndices.add(swapIndex); // 사용된 꽝 위치 기록
                    usedSwapIndices.add(pathResultIndex); // 새로 꽝이 된 위치도 기록
                    System.out.println(loserName + "의 결과를 '꽝'으로 설정 (교환: " + swapIndex + " <-> " + pathResultIndex + ")");
                } else {
                    System.out.println("경고: 교환할 꽝 위치를 찾을 수 없습니다.");
                }
            }
            System.out.println("최종 결과: " + results);
            System.out.println("=== 관리자 모드 적용 완료 ===");
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
