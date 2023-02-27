package dataaccess;

import model.AuthToken;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthTokenDAO {
    private final Connection conn;

    public AuthTokenDAO(Connection conn) {
        this.conn = conn;
    }

    public void insertAuthToken(AuthToken authToken) throws DataAccessException {

        String sql = "INSERT INTO AuthToken (authtoken, username) VALUES(?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken.getAuthToken());
            stmt.setString(2, authToken.getUsername());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting an authToken into the database");
        }
    }

    public void updateAuthToken(AuthToken authToken) throws DataAccessException {
        String sql = "UPDATE AuthToken SET authtoken = ? WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken.getAuthToken());
            stmt.setString(2, authToken.getUsername());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while updating an authtoken in the database");
        }
    }

    /**
     * Find an authToken from the database with the given authtoken.
     * @param authtoken: the authtoken of the authToken to retrieveAuthToken.
     * @return: An AuthToken object representing the authToken found.
     * @throws DataAccessException
     */
    public AuthToken retrieveAuthToken(String authtoken) throws DataAccessException {
        AuthToken authToken;
        ResultSet rs;
        String sql = "SELECT * FROM AuthToken WHERE authtoken = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authtoken);
            rs = stmt.executeQuery();
            if (rs.next()) {
                authToken = new AuthToken(rs.getString("authtoken"), rs.getString("username"));
                return authToken;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding an authToken in the database");
        }
    }

    public String findAuthToken(String username) throws DataAccessException {
        String authtoken;
        ResultSet rs;
        String sql = "SELECT * FROM AuthToken WHERE username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                authtoken = rs.getString("authtoken");
                return authtoken;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding an authToken in the database");
        }
    }

    public void clearAuthTokens() throws DataAccessException {
        String sql = "DELETE FROM AuthToken";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the authToken table");
        }
    }

    public List<AuthToken> getAllAuthTokens() throws DataAccessException {
        List<AuthToken> allAuthTokens = new ArrayList<>();
        AuthToken authToken; // points to each newly created AuthToken object
        ResultSet rs;
        String sql = "SELECT * FROM AuthToken;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            rs = stmt.executeQuery();
            while (rs.next()) {
                authToken = new AuthToken(rs.getString("authtoken"), rs.getString("username"));
                allAuthTokens.add(authToken);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while fetching the AuthToken table");
        }

        if (allAuthTokens.size() > 0) {
            return allAuthTokens;
        }
        return null;
    }
}
