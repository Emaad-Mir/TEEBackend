package com.nighthawk.spring_portfolio.mvc.player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class player {

    public static void main(String[] args) {
        // SQLite database URL
        String url = "jdbc:sqlite:players.db";

        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection != null) {
                System.out.println("Connected to the database");

                // Create Players table with JSONB column
                String createPlayersTableSQL = "CREATE TABLE IF NOT EXISTS Players ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "name TEXT NOT NULL UNIQUE,"
                        + "last_team_nba TEXT,"
                        + "position TEXT,"
                        + "player_data JSONB);";

                try (Statement statement = connection.createStatement()) {
                    statement.execute(createPlayersTableSQL);
                    System.out.println("Players table created");

                    // Insert players with JSONB data
                    insertPlayer(connection, "Jeremy Lin", "Toronto Raptors", "Point Guard", "{\"height\": \"6'3\", \"weight\": 200}");
                    insertPlayer(connection, "Ja Morant", "Memphis Grizzlies", "Point Guard", "{\"height\": \"6'3\", \"weight\": 174}");
                    insertPlayer(connection, "Kawhi Leonard", "Los Angeles Clippers", "Small Forward", "{\"height\": \"6'7\", \"weight\": 225}");
                    insertPlayer(connection, "Ray Allen", "Miami Heat", "Shooting Guard", "{\"height\": \"6'5\", \"weight\": 213}");

                    // Create Teams table (for NBA teams)
                    String createNBATeamsTableSQL = "CREATE TABLE IF NOT EXISTS NBATeams ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "name TEXT NOT NULL UNIQUE);";

                    statement.execute(createNBATeamsTableSQL);
                    System.out.println("NBATeams table created");

                    // Create Many-to-Many relationship table (PlayerNBATeams)
                    String createPlayerNBATeamsTableSQL = "CREATE TABLE IF NOT EXISTS PlayerNBATeams ("
                            + "player_id INTEGER,"
                            + "team_id INTEGER,"
                            + "PRIMARY KEY (player_id, team_id),"
                            + "FOREIGN KEY (player_id) REFERENCES Players(id),"
                            + "FOREIGN KEY (team_id) REFERENCES NBATeams(id));";

                    statement.execute(createPlayerNBATeamsTableSQL);
                    System.out.println("PlayerNBATeams table created");

                    // Insert NBA teams
                    insertNBATeam(connection, "Toronto Raptors");
                    insertNBATeam(connection, "Memphis Grizzlies");
                    insertNBATeam(connection, "Los Angeles Clippers");
                    insertNBATeam(connection, "Miami Heat");

                    // Assign players to NBA teams (many-to-many relationship)
                    assignPlayerToNBATeam(connection, "Jeremy Lin", "Toronto Raptors");
                    assignPlayerToNBATeam(connection, "Ja Morant", "Memphis Grizzlies");
                    assignPlayerToNBATeam(connection, "Kawhi Leonard", "Los Angeles Clippers");
                    assignPlayerToNBATeam(connection, "Ray Allen", "Miami Heat");

                    System.out.println("Players assigned to NBA teams");

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertPlayer(Connection connection, String playerName, String lastTeamNBA, String position, String playerData) throws SQLException {
        String insertPlayerSQL = "INSERT INTO Players (name, last_team_nba, position, player_data) VALUES (?, ?, ?, ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertPlayerSQL)) {
            preparedStatement.setString(1, playerName);
            preparedStatement.setString(2, lastTeamNBA);
            preparedStatement.setString(3, position);
            preparedStatement.setString(4, playerData);
            preparedStatement.executeUpdate();
            System.out.println("Player '" + playerName + "' inserted");
        }
    }

    private static void insertNBATeam(Connection connection, String nbaTeamName) throws SQLException {
        String insertNBATeamSQL = "INSERT INTO NBATeams (name) VALUES (?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertNBATeamSQL)) {
            preparedStatement.setString(1, nbaTeamName);
            preparedStatement.executeUpdate();
            System.out.println("NBA Team '" + nbaTeamName + "' inserted");
        }
    }

    private static void assignPlayerToNBATeam(Connection connection, String playerName, String nbaTeamName) throws SQLException {
        String assignPlayerToNBATeamSQL = "INSERT INTO PlayerNBATeams (player_id, team_id) VALUES ("
                + "(SELECT id FROM Players WHERE name = ?),"
                + "(SELECT id FROM NBATeams WHERE name = ?));";

        try (PreparedStatement preparedStatement = connection.prepareStatement(assignPlayerToNBATeamSQL)) {
            preparedStatement.setString(1, playerName);
            preparedStatement.setString(2, nbaTeamName);
            preparedStatement.executeUpdate();
            System.out.println("Player '" + playerName + "' assigned to NBA Team '" + nbaTeamName + "'");
        }
    }
}
