package ru.spbhse.smirnov.database;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhoneDatabase {
    @NotNull private final String databaseUrl;

    public PhoneDatabase(@NotNull String databaseShortName) throws SQLException {
        databaseUrl = "jdbc:sqlite:" + databaseShortName + ".db";
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS Owners ("
                        + "id   INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "name TEXT NOT NULL,"
                        + "UNIQUE (name)"
                        + ")");
                statement.execute("CREATE TABLE IF NOT EXISTS Phones ("
                        + "id          INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "phoneNumber TEXT NOT NULL,"
                        + "UNIQUE (phoneNumber)"
                        + ")");
                statement.execute("CREATE TABLE IF NOT EXISTS OwnersPhones ("
                        + "ownerId INTEGER,"
                        + "phoneId INTEGER,"
                        + "FOREIGN KEY(ownerId) REFERENCES Owners(id),"
                        + "FOREIGN KEY(phoneId) REFERENCES Phones(id),"
                        + "UNIQUE(ownerId, phoneId) ON CONFLICT REPLACE"
                        + ")");
            }
        }
    }

    @NotNull
    public List<NamePhonePair> getAllNamePhonePairs() throws SQLException {
        List<NamePhonePair> allPairs = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(
                        "SELECT Owners.name, Phones.phoneNumber FROM Phones, Owners, OwnersPhones "
                                + "WHERE Phones.id = OwnersPhones.phoneId "
                                + "AND Owners.id = OwnersPhones.ownerId ")) {
                    while (resultSet.next()) {
                        allPairs.add(new NamePhonePair(resultSet.getString("name"),
                                resultSet.getString("phoneNumber")));
                    }
                }
            }
        }
        return allPairs;
    }

    @NotNull
    public List<String> getAllNamesByPhone(@NotNull String phoneNumber) throws SQLException {
        List<String> allNames = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String query = "SELECT Owners.name FROM Phones, Owners, OwnersPhones "
                    + "WHERE Phones.id = OwnersPhones.phoneId "
                    + "AND Owners.id = OwnersPhones.ownerId "
                    + "AND Phones.phoneNumber = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, phoneNumber);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        allNames.add(resultSet.getString("name"));
                    }
                }
            }
        }
        return allNames;
    }

    @NotNull
    public List<String> getAllPhonesByName(@NotNull String name) throws SQLException {
        List<String> allPhones = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String query = "SELECT Phones.phoneNumber FROM Phones, Owners, OwnersPhones "
                    + "WHERE Phones.id = OwnersPhones.phoneId "
                    + "AND Owners.id = OwnersPhones.ownerId "
                    + "AND Owners.name = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, name);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        allPhones.add(resultSet.getString("phoneNumber"));
                    }
                }
            }
        }
        return allPhones;
    }

    public void addRecord(@NotNull String ownerName, @NotNull String phoneNumber) throws SQLException {
        addNameIfNotExists(ownerName);
        addPhoneIfNotExists(phoneNumber);
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String query = "INSERT OR IGNORE INTO OwnersPhones(ownerId, phoneId)"
                    + "VALUES ((SELECT id FROM Owners WHERE name = ?),"
                    + " (SELECT id FROM Phones WHERE phoneNumber = ?))";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, ownerName);
                statement.setString(2, phoneNumber);
                statement.executeUpdate();
            }
        }
    }

    public void deleteRecord(@NotNull String ownerName, @NotNull String phoneNumber) throws SQLException {
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String query = "DELETE FROM OwnersPhones "
                    + "WHERE ownerId = (SELECT id FROM Owners WHERE name = ?) "
                    + "AND phoneId = (SELECT id FROM Phones WHERE phoneNumber = ?)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, ownerName);
                statement.setString(2, phoneNumber);
                statement.executeUpdate();
            }
        }
    }

    @SuppressWarnings("Duplicates")
    public void replaceNameByPair(@NotNull String oldName, @NotNull String phoneNumber, @NotNull String newName) throws SQLException {
        addNameIfNotExists(newName);
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String query = "UPDATE OwnersPhones "
                    + "SET ownerId = (SELECT id FROM Owners WHERE name = ?) "
                    + "WHERE ownerId = "
                    + "(SELECT id FROM Owners WHERE name = ?) "
                    + "AND phoneId = "
                    + "(SELECT id FROM Phones WHERE phoneNumber = ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, newName);
                statement.setString(2, oldName);
                statement.setString(3, phoneNumber);
                statement.executeUpdate();
            }
        }
    }

    @SuppressWarnings("Duplicates")
    public void replacePhoneByPair(@NotNull String name, @NotNull String oldPhoneNumber, @NotNull String newPhoneNumber) throws SQLException {
        addPhoneIfNotExists(newPhoneNumber);
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String query = "UPDATE OwnersPhones "
                    + "SET phoneId = (SELECT id FROM Phones WHERE phoneNumber = ?) "
                    + "WHERE ownerId = "
                    + "(SELECT id FROM Owners WHERE name = ?) "
                    + "AND phoneId = "
                    + "(SELECT id FROM Phones WHERE phoneNumber = ?) ";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, newPhoneNumber);
                statement.setString(2, name);
                statement.setString(3, oldPhoneNumber);
                statement.executeUpdate();
            }
        }
    }

    private void addNameIfNotExists(@NotNull String name) throws SQLException {
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String query = "INSERT OR IGNORE INTO Owners(name) VALUES(?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, name);
                statement.executeUpdate();
            }
        }
    }

    private void addPhoneIfNotExists(@NotNull String phoneNumber) throws SQLException {
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String query = "INSERT OR IGNORE INTO Phones(phoneNumber) VALUES(?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, phoneNumber);
                statement.executeUpdate();
            }
        }
    }
}