package com.example.administrator.networks;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by LeeSugyun on 2017-12-03.
 */

public class ServerWork extends Thread {
    private boolean flag = true;

    private String myName;
    private String myIP;
    private String opponentName;
    private String opponentIP;
    private int gamePort;

    private ServerSocket serverSocket;



    private HashMap<String, ServerThread> serverMap;
    private Vector<Player> player_Vector = new Vector<>();
    class Player {
        int x;
        int y;
        double angle;
        boolean move_Check;
    }



    public ServerWork(String myName, String myIP, String opponentName, String opponentIP, int gamePort) {
        this.myName = myName;
        this.myIP = myIP;
        this.opponentName = opponentName;
        this.opponentIP = opponentIP;
        this.gamePort = gamePort;

        serverMap = new HashMap<>();

        player_Vector.add(new Player());
        player_Vector.add(new Player());

        try {
            serverSocket = new ServerSocket(gamePort);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.start();
    }

    @Override
    public void run() {
        try {
            while (!serverMap.containsKey(myName) || !serverMap.containsKey(opponentName)) {
                Socket socket = serverSocket.accept();
                if (socket.getInetAddress().getHostAddress().equals(myIP)) {
                    serverMap.put(myName, new ServerThread(socket, 0));
                }
                if (socket.getInetAddress().getHostAddress().equals(opponentIP)) {
                    serverMap.put(opponentName, new ServerThread(socket, 1));
                }
            }

            serverSocket.close();

            // TODO 서버작업
            while (flag) {
                String sendData = "PlayerData " + 0 + " " + player_Vector.get(0).move_Check + " " + player_Vector.get(0).angle +" " + "PlayerData " + 1 + " " + player_Vector.get(1).move_Check + " " + player_Vector.get(1).angle;

                all_Write(sendData);

                this.sleep(15);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //연결된 클라이언트 전체에 메세지를 발송
    public void all_Write(String sendData) {
        serverMap.get(myName).write(sendData + "\n");
        serverMap.get(opponentName).write(sendData + "\n");
    }


    //각각의 클라이언트와 통신을 하는 쓰레드
    final class ServerThread extends Thread {
        private boolean flag = true;

        private Socket socket;

        private String readData = "PREPARE";

        private BufferedWriter writer;
        private BufferedReader reader;

        private int player_Num = 0;

        public int getPlayer_Num() {
            return player_Num;
        }

        public ServerThread(Socket socket, int player_Num) {
            this.socket = socket;
            this.player_Num = player_Num;

            try {
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.start();
        }


        //쓰레드가 지속적으로 돌면서 데이터를 받아 readData에 저장함
        @Override
        public void run() {
            try {
                while (flag) {
                    readData = reader.readLine();
                    check_Message(readData);
                }
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //연결되어 있는 클라이언트로 메세지를 보냄
        //writeData = 보낼 메세지
        public void write(String writeData) {
            try {
                writer.write(writeData);
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // TODO 서버로 오는 메세지를 가져와 분석하고 그에 맞는 일을 처리함
        public void check_Message(String string) {
            StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
            String tag = stringTokenizer.nextToken();
            switch (tag) {
                //클라이언트로 부터 지속적으로 받아오는 캐릭터의 무브체크와 무브각도
                case "PlayerData" : {
                    /*player_Vector.get(player_Num).x = Integer.parseInt(stringTokenizer.nextToken());
                    player_Vector.get(player_Num).y = Integer.parseInt(stringTokenizer.nextToken());*/
                    player_Vector.get(player_Num).move_Check = Boolean.parseBoolean(stringTokenizer.nextToken());
                    player_Vector.get(player_Num).angle = Double.parseDouble(stringTokenizer.nextToken());
                    break;
                }

                //클라이언트에서 주기적으로 받아오는 캐릭터의 좌표 이것을 통해 캐릭터의 좌표를 일정주기마다 동기화 시킴
                case "PlayerDataXY" : {
                    player_Vector.get(player_Num).x = Integer.parseInt(stringTokenizer.nextToken());
                    player_Vector.get(player_Num).y = Integer.parseInt(stringTokenizer.nextToken());

                    String sendData = "PlayerDataXY " + player_Num + " " + player_Vector.get(0).x + " " + player_Vector.get(0).y;

                    all_Write(sendData);
                    break;
                }

                //노트를 맞출때 호출되며 공격명령을 전달
                case "Attack" : {
                    all_Write("Attack " + player_Num);
                    break;
                }
            }
        }
    }
}