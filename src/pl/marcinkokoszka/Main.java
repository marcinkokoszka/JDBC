package pl.marcinkokoszka;

import pl.marcinkokoszka.model.Artist;
import pl.marcinkokoszka.model.OracleDatabase;
import pl.marcinkokoszka.model.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
//        System.out.println("-------- Oracle JDBC Connection Testing ------");
//
        OracleDatabase od = new OracleDatabase();
//
        if (od.open()) System.out.println("-------- Oracle JDBC Connection Opened ------");
        else System.out.println("-------- Oracle JDBC Connection Failed ------");
//
        System.out.println("-------- Deleting Artists Table If Exists ------");
        System.out.println(od.deleteArtistsTable());
//
        System.out.println("-------- Creating Artists Table ------");
        System.out.println(od.createArtistsTable());
//
//        System.out.println("-------- Adding Artist Record ------");
//        System.out.println(od.addArtist("Kokoszka"));
//
//        System.out.println("-------- Getting Artists List ------");
//        System.out.println(od.queryArtists(1));
//
//        if (od.close()) System.out.println("-------- Oracle JDBC Connection Closed ------");
//        else System.out.println("-------- Oracle JDBC Connection Not Closed ------");

        copyArtistsFromSQLiteToOracle();

        System.out.println("-------- Getting Artists List ------");
        System.out.println(od.queryArtists(1));
    }

    public static void copyArtistsFromSQLiteToOracle(){
        SQLiteDatabase ld = new SQLiteDatabase();
        OracleDatabase od = new OracleDatabase();

        ld.open();
        List<Artist> artists = ld.queryArtists(1);
        ld.close();

        System.out.println(artists);

        od.open();
        od.addArtistFromListWithPreparedStatement(artists);
        od.close();
    }
}
