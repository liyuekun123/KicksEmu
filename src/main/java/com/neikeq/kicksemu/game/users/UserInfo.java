package com.neikeq.kicksemu.game.users;

import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.storage.ConnectionRef;
import com.neikeq.kicksemu.storage.SqlUtils;
import com.neikeq.kicksemu.utils.DateUtils;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserInfo {

    private static final String TABLE = "users";

    // Sql getters

    public static String getPassword(int id, ConnectionRef... con) {
        return SqlUtils.getString("password", TABLE, id, con);
    }

    public static int getCash(int id, ConnectionRef ... con) {
        return SqlUtils.getInt("kash", TABLE, id, con);
    }

    public static UserSettings getSettings(int id) {
        final String query = "SELECT settings_camera, settings_names, vol_effects, vol_music," +
                " settings_invites, settings_shadows, settings_whispers, settings_country" +
                " FROM " + TABLE + " WHERE id = ? LIMIT 1;";

        try (ConnectionRef connection = ConnectionRef.ref();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserSettings settings = new UserSettings();

                    settings.setCamera(rs.getByte("settings_camera"));
                    settings.setNames(rs.getByte("settings_names"));
                    settings.setVolEffects(rs.getByte("vol_effects"));
                    settings.setVolMusic(rs.getByte("vol_music"));
                    settings.setInvites(rs.getBoolean("settings_invites"));
                    settings.setShadows(rs.getBoolean("settings_shadows"));
                    settings.setWhispers(rs.getBoolean("settings_whispers"));
                    settings.setCountry(rs.getInt("settings_country"));

                    return settings;
                } else {
                    return new UserSettings();
                }
            }
        } catch (SQLException e) {
            return new UserSettings();
        }
    }

    public static Timestamp getLastCharDeletion(int id, ConnectionRef ... con) {
        return SqlUtils.getTimestamp("last_char_deletion", TABLE, id, con);
    }

    public static int getSlotOne(int id, ConnectionRef ... con) {
        return SqlUtils.getInt("slot_one", TABLE, id, con);
    }

    public static int getSlotTwo(int id, ConnectionRef ... con) {
        return SqlUtils.getInt("slot_two", TABLE, id, con);
    }

    public static int getOnline(int id, ConnectionRef ... con) {
        return SqlUtils.getInt("online", TABLE, id, con);
    }

    public static short getServer(int id, ConnectionRef ... con) {
        return SqlUtils.getShort("server", TABLE, id, con);
    }

    public static int getSlotThree(int id, ConnectionRef ... con) {
        return SqlUtils.getInt("slot_three", TABLE, id, con);
    }

    // Sql setters

    public static void sumCash(int value, int id, ConnectionRef ... con) {
        SqlUtils.sumInt("kash", value, TABLE, id, con);
    }

    public static void setLastCharDeletion(Date value, int id, ConnectionRef ... con) {
        SqlUtils.setTimestamp("last_char_deletion", DateUtils.toTimestamp(value), TABLE, id, con);
    }

    public static void setSlotOne(int value, int id, ConnectionRef ... con) {
        SqlUtils.setInt("slot_one", value, TABLE, id, con);
    }

    public static void setSlotTwo(int value, int id, ConnectionRef ... con) {
        SqlUtils.setInt("slot_two", value, TABLE, id, con);
    }

    public static void setSlotThree(int value, int id, ConnectionRef ... con) {
        SqlUtils.setInt("slot_three", value, TABLE, id, con);
    }

    public static void setOnline(int value, int id, ConnectionRef ... con) {
        SqlUtils.setInt("online", value, TABLE, id, con);
    }

    public static void setServer(short value, int id, ConnectionRef ... con) {
        SqlUtils.setShort("server", value, TABLE, id, con);
    }

    public static void setSettings(UserSettings settings, int id) {
        final String query = "UPDATE users SET settings_camera = ?, settings_names = ?," +
                " vol_effects = ?, vol_music = ?, settings_invites = ?," +
                " settings_shadows = ?, settings_whispers = ?, settings_country = ?" +
                " WHERE id = ? LIMIT 1;";

        try (ConnectionRef connection = ConnectionRef.ref();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setByte(1, settings.getCamera());
            stmt.setByte(2, settings.getNames());
            stmt.setByte(3, settings.getVolEffects());
            stmt.setByte(4, settings.getVolMusic());
            stmt.setBoolean(5, settings.getInvites());
            stmt.setBoolean(6, settings.getShadows());
            stmt.setBoolean(7, settings.getWhispers());
            stmt.setInt(8, settings.getCountry());
            stmt.setInt(9, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            Output.println("Exception when updating user settings: " +
                    e.getMessage(), Level.DEBUG);
        }
    }

    // Method utils

    public static boolean hasEmptySlot(int id) {
        return (getSlotOne(id) <= 0) || (getSlotTwo(id) <= 0) || (getSlotThree(id) <= 0);
    }

    public static int getFirstEmptySlot(int id) {
        if (getSlotOne(id) <= 0) {
            return 0;
        } else if (getSlotTwo(id) <= 0) {
            return 1;
        } else if (getSlotThree(id) <= 0) {
            return 2;
        } else {
            return -1;
        }
    }

    public static int characterSlot(int characterId, int id) {
        if (getSlotOne(id) == characterId) {
            return 0;
        } else if (getSlotTwo(id) == characterId) {
            return 1;
        } else if (getSlotThree(id) == characterId) {
            return 2;
        } else {
            return -1;
        }
    }

    public static boolean hasCharacter(int characterId, int id) {
        return characterSlot(characterId, id) >= 0;
    }

    public static void clearSlotByIndex(int index, int id) {
        switch (index) {
            case 0:
                setSlotOne(0, id);
                break;
            case 1:
                setSlotTwo(0, id);
                break;
            case 2:
                setSlotThree(0, id);
                break;
            default:
        }
    }
}
