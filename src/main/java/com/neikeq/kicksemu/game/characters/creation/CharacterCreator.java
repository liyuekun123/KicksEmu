package com.neikeq.kicksemu.game.characters.creation;

import com.neikeq.kicksemu.game.characters.types.Animation;
import com.neikeq.kicksemu.game.characters.types.PlayerStats;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CharacterCreator {

    public static void createCharacter(Session session, ClientMessage msg) {
        short result;

        int accountId = session.getUserId();

        if (UserInfo.hasEmptySlot(session.getUserId())) {
            CharacterBase character = characterFromMessage(msg, accountId);

            result = CharacterValidator.validate(character);

            if (result == CreationResult.SUCCESS) {
                int resultId = create(character);

                if (resultId > 0) {
                    setCharacterOwner(resultId, session.getUserId());
                } else {
                    result = CreationResult.SYSTEM_PROBLEM;
                }
            }
        } else {
            result = CreationResult.CHARACTERS_LIMIT;
        }

        session.send(MessageBuilder.createCharacter(result));
    }

    private static int create(CharacterBase character) {
        int characterId = -1;

        try (Connection con = MySqlManager.getConnection()) {
            String creation_query = "INSERT INTO characters (owner, name, position, animation," +
                    " face, default_head, default_shirts, default_pants, default_shoes," +
                    " stats_points, stats_running, stats_endurance, stats_agility," +
                    " stats_ball_control, stats_dribbling, stats_stealing, stats_tackling," +
                    " stats_heading, stats_short_shots, stats_long_shots, stats_crossing," +
                    " stats_short_passes, stats_long_passes, stats_marking, stats_goalkeeping," +
                    " stats_punching, stats_defense, friends_list, ignored_list)" +
                    " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            try (PreparedStatement stmt = con.prepareStatement(creation_query)) {
                stmt.setInt(1, character.getOwner());
                stmt.setString(2, character.getName());
                stmt.setShort(3, character.getPosition());
                stmt.setShort(4, character.getAnimation().toShort());
                stmt.setShort(5, character.getFace());
                stmt.setInt(6, character.getDefaultHead());
                stmt.setInt(7, character.getDefaultShirts());
                stmt.setInt(8, character.getDefaultPants());
                stmt.setInt(9, character.getDefaultShoes());
                stmt.setShort(10, character.getStatsPoints());
                stmt.setShort(11, character.getStats().getRunning());
                stmt.setShort(12, character.getStats().getEndurance());
                stmt.setShort(13, character.getStats().getAgility());
                stmt.setShort(14, character.getStats().getBallControl());
                stmt.setShort(15, character.getStats().getDribbling());
                stmt.setShort(16, character.getStats().getStealing());
                stmt.setShort(17, character.getStats().getTackling());
                stmt.setShort(18, character.getStats().getHeading());
                stmt.setShort(19, character.getStats().getShortShots());
                stmt.setShort(20, character.getStats().getLongShots());
                stmt.setShort(21, character.getStats().getCrossing());
                stmt.setShort(22, character.getStats().getShortPasses());
                stmt.setShort(23, character.getStats().getLongPasses());
                stmt.setShort(24, character.getStats().getMarking());
                stmt.setShort(25, character.getStats().getGoalkeeping());
                stmt.setShort(26, character.getStats().getPunching());
                stmt.setShort(27, character.getStats().getDefense());
                stmt.setString(28, "");
                stmt.setString(29, "");

                stmt.executeUpdate();
            }

            String owner_query = "SELECT id FROM characters WHERE id = LAST_INSERT_ID();";

            try (PreparedStatement stmt2 = con.prepareStatement(owner_query)) {
                try (ResultSet rs  = stmt2.executeQuery()) {
                    if (rs.next()) {
                        characterId = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            characterId = -1;
        }

        return characterId;
    }

    private static void setCharacterOwner(int characterId, int ownerId) {
        switch (UserInfo.getFirstEmptySlot(ownerId)) {
            case 0:
                UserInfo.setSlotOne(characterId, ownerId);
                break;
            case 1:
                UserInfo.setSlotTwo(characterId, ownerId);
                break;
            case 2:
                UserInfo.setSlotThree(characterId, ownerId);
                break;
            default:
        }
    }

    private static CharacterBase characterFromMessage(ClientMessage msg, int accountId) {
        CharacterBase character = new CharacterBase();

        msg.readInt();
        character.setOwner(accountId);
        character.setName(msg.readString(15));
        character.setStatsPoints(msg.readShort());
        msg.ignoreBytes(2);
        character.setAnimation(Animation.fromShort(msg.readShort()));
        character.setFace(msg.readShort());
        character.setDefaultHead(msg.readInt());
        character.setDefaultShirts(msg.readInt());
        character.setDefaultPants(msg.readInt());
        character.setDefaultShoes(msg.readInt());
        character.setPosition(msg.readShort());
        msg.ignoreBytes(6);

        short[] stats = new short[17];

        for (int i = 0; i < stats.length; i++) {
            stats[i] = msg.readShort();
        }

        character.setStats(PlayerStats.fromArray(stats));

        return character;
    }
}
