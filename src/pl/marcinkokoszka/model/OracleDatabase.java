package pl.marcinkokoszka.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kokoseq on 14.03.2017.
 */
public class OracleDatabase {

    private static final String DATABASE_URL = "jdbc:oracle:thin:@localhost:1521:MyDatabase";
    private static final String USERNAME = "SYSTEM";
    private static final String PASSWORD = "Oracle_1";

    private static final int ORDER_BY_NONE = 1;
    private static final int ORDER_BY_ASC = 2;
    private static final int ORDER_BY_DESC = 3;

    private static final String TABLE_ARTISTS = "artists";
    private static final String COLUMN_ARTIST_ID = "id";
    private static final String COLUMN_ARTIST_NAME = "name";
    private static final int INDEX_ARTIST_ID = 1;
    private static final int INDEX_ARTIST_NAME = 2;

    private Connection connection;

    public boolean open(){
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            return true;
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database: " + e.getMessage());
            return	false;
        }
    }

    public boolean close() {
        try {
            if(connection != null) {
                connection.close();
                return true;
            }
            return false;
        } catch(SQLException e) {
            System.out.println("Couldn't close connection: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteArtistsTable(){
        StringBuilder sb = new StringBuilder("DROP TABLE ");
        sb.append(TABLE_ARTISTS);

        try(Statement statement = connection.createStatement()){
            statement.execute(sb.toString());
            return true;
        } catch(SQLException e) {
            System.out.println("Table " + TABLE_ARTISTS + " not deleted: " + e.getMessage());
            return false;
        }
    }

    public boolean createArtistsTable(){

        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        sb.append(TABLE_ARTISTS);
        sb.append(" ( ");
        sb.append(COLUMN_ARTIST_ID);
        sb.append(" NUMBER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1), ");
        sb.append(COLUMN_ARTIST_NAME);
        sb.append(" VARCHAR2(600) NOT NULL, ");
        sb.append("CONSTRAINT PK_");
        sb.append(TABLE_ARTISTS);
        sb.append(" PRIMARY KEY (");
        sb.append(COLUMN_ARTIST_ID);
        sb.append("))");

        try(Statement statement = connection.createStatement()){
            statement.execute(sb.toString());
            return true;
        } catch(SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return false;
        }
    }

    public boolean addArtist(String name){
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(TABLE_ARTISTS);
        sb.append("(");
        sb.append(COLUMN_ARTIST_NAME);
        sb.append(") VALUES ('");
        sb.append(name.replaceAll("'", "''"));
        sb.append("')");

        try(Statement statement = connection.createStatement()) {
            statement.execute(sb.toString());
            return true;
        } catch (SQLException e){
            System.out.println("Failed to insert artist: " + e.getMessage());
            return false;
        }
    }

    public List<Artist> queryArtists(int sortOrder){

        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(TABLE_ARTISTS);
        if(sortOrder != ORDER_BY_NONE) {
            sb.append(" ORDER BY ");
            sb.append(COLUMN_ARTIST_NAME);
            sb.append(" COLLATE NOCASE ");
            if (sortOrder == ORDER_BY_DESC) {
                sb.append("DESC");
            } else {
                sb.append("ASC");
            }
        }

        try(Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(sb.toString())) {
            
            List<Artist> artists = new ArrayList<>();
            while(results.next()) {
                Artist artist = new Artist();
                artist.setId(results.getInt(INDEX_ARTIST_ID));
                artist.setName(results.getString(INDEX_ARTIST_NAME));
                artists.add(artist);
            }
            
            return artists;
        } catch(SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
    }

    public boolean addArtistFromList(List<Artist> artists) {

        try(Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);

            for (Artist a: artists){
                StringBuilder sb = new StringBuilder("INSERT INTO ");
                sb.append(TABLE_ARTISTS);
                sb.append("(");
                sb.append(COLUMN_ARTIST_NAME);
                sb.append(") VALUES ('");
                sb.append(a.getName().replaceAll("'", "''"));
                sb.append("')");
                statement.addBatch(sb.toString());
            }
            statement.executeBatch();

            connection.commit();
            connection.setAutoCommit(true);

            return true;
        } catch (SQLException e){
            System.out.println("Failed to insert artists: " + e.getMessage());
            return false;
        }
    }

    public boolean addArtistFromListWithPreparedStatement(List<Artist> artists) {

        String insertTableSQL = "INSERT INTO " + TABLE_ARTISTS
                + "(" + COLUMN_ARTIST_NAME + ") VALUES"
                + "(?)";

        try(PreparedStatement preparedStatement = connection.prepareStatement(insertTableSQL)) {

            connection.setAutoCommit(false);

            for (Artist a: artists){
                preparedStatement.setString(1, a.getName());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();

            connection.commit();
            connection.setAutoCommit(true);

            return true;
        } catch (SQLException e){
            System.out.println("Failed to insert artists: " + e.getMessage());
            return false;
        }
    }

}
