package ogm.arbitrage;

import java.util.ArrayList;

/**Created by ogallego on 11/21/2015.*/
public class Games {
    public static ArrayList<Game> games = new ArrayList<>();

    public static class Game {
        public int gameNumber;
        public String centralName;
        public String ar1Name;
        public String ar2Name;
        public String gameDate;
        public String gameTime;
        public String gameCategory;
        public String gameField;
        public String gamePayed;
        public String checkNumber;
    }

    public static void clear() {
        games.clear();
    }

    public static void addNewGame(Game game) { games.add(game); }

    public static void delGame(int i) { games.remove(i); }
}
