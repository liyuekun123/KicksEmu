package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.Training;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;

import java.net.InetSocketAddress;
import java.util.Map;

public class CharacterManager {

    public static void playerInfo(Session session, ClientMessage msg) {
        sendItemList(session);
        sendTrainingList(session);
        sendSkillList(session);
        sendCelebrationList(session);
        sendPlayerInfo(session);
    }

    public static void sendPlayerInfo(Session session) {
        PlayerInfo character = session.getPlayerInfo();

        ServerMessage msg = MessageBuilder.playerInfo(character, (byte)0);
        session.send(msg);
    }

    public static void sendItemList(Session session) {
        Map<Integer, Item> items = session.getPlayerInfo().getInventoryItems();

        ServerMessage msg = MessageBuilder.itemList(items, (byte)0);
        session.send(msg);
    }

    public static void sendTrainingList(Session session) {
        Map<Integer, Training> trainings = session.getPlayerInfo().getInventoryTraining();

        ServerMessage msg = MessageBuilder.trainingList(trainings, (byte) 0);
        session.send(msg);
    }

    public static void sendSkillList(Session session) {
        Map<Integer, Skill> items = session.getPlayerInfo().getInventorySkills();

        ServerMessage msg = MessageBuilder.skillList(items, (byte) 0);
        session.send(msg);
    }

    public static void sendCelebrationList(Session session) {
        Map<Integer, Celebration> items = session.getPlayerInfo().getInventoryCelebration();

        ServerMessage msg = MessageBuilder.celebrationList(items, (byte) 0);
        session.send(msg);
    }

    public static void gameExit(Session session) {
        InetSocketAddress clientIp = session.getRemoteAddress();
        int characterId = session.getPlayerInfo().getId();

        ServerMessage response = MessageBuilder.gameExit(clientIp, characterId);
        session.send(response);

        session.close();
    }
}