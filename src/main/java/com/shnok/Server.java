package com.shnok;

import com.shnok.serverpackets.ServerPacket;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static final int PORT = 11000;
    private static GameServerListener _gameServerListener;
    private static final List<GameClient> _clients = new ArrayList<>();

    public Server() {
        _gameServerListener = new GameServerListener(PORT);
        _gameServerListener.run();
    }

    public static void main(String[] av) {
        Server server = new Server();
    }

    public static GameServerListener getGameServerListener() {
        return _gameServerListener;
    }

    public static void addClient(Socket s) {
        GameClient client = new GameClient(s);
        client.start();
        _clients.add(client);
    }

    public static void removeClient(GameClient s) {
        synchronized (_clients) {
            _clients.remove(s);
        }
    }

    public static void broadcast(ServerPacket packet) {
        synchronized (_clients) {
            for(GameClient c : _clients) {
                if(c.authenticated) {
                    c.sendPacket(packet);
                }
            }
        }
    }

    public static boolean userExists(String user) {
        synchronized (_clients) {
            for(GameClient c : _clients) {
                if(c.authenticated) {
                    if(c.getUsername().equals(user)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            return false;
        }
    }
}