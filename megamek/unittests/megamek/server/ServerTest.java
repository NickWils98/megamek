package megamek.server;

import junit.framework.TestCase;
import megamek.common.*;
import megamek.common.net.Packet;
import megamek.server.commands.VictoryCommand;
import megamek.server.victory.Victory;
import megamek.server.victory.VictoryResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

@RunWith(JUnit4.class)
public class ServerTest {

    @Test
    public void testBasicVictory() throws IOException {
        IGame mockedGame = Mockito.mock(IGame.class);
        VictoryResult mockedVictoryResult = Mockito.mock(VictoryResult.class);
        Victory mockedVictory = Mockito.mock(Victory.class);

        Mockito.when(mockedGame.getVictory()).thenReturn(mockedVictory);
        Mockito.when(mockedVictory.checkForVictory(mockedGame, mockedGame.getVictoryContext())).thenReturn(mockedVictoryResult);
        Mockito.when(mockedGame.getEntities()).thenReturn(Mockito.mock(Iterator.class));
        Mockito.when(mockedGame.getPlayers()).thenReturn(Mockito.mock(Enumeration.class));
        Mockito.when(mockedGame.getAttacks()).thenReturn(Mockito.mock(Enumeration.class));

        Server server = new Server("TestServer", 1111);
        server.setGame(mockedGame);
        boolean victory_result = server.victory();
        TestCase.assertFalse(victory_result);
        server.die();
    }

    @Test
    public void testBasicVictorySuccess() throws IOException {
        IGame mockedGame = Mockito.mock(IGame.class);
        VictoryResult mockedVictoryResult = Mockito.mock(VictoryResult.class);
        Victory mockedVictory = Mockito.mock(Victory.class);

        Mockito.when(mockedVictoryResult.victory()).thenReturn(true);
        Mockito.when(mockedVictoryResult.getWinningPlayer()).thenReturn(IPlayer.PLAYER_NONE);
        Mockito.when(mockedGame.getVictory()).thenReturn(mockedVictory);
        Mockito.when(mockedVictory.checkForVictory(mockedGame, mockedGame.getVictoryContext())).thenReturn(mockedVictoryResult);
        Mockito.when(mockedGame.getEntities()).thenReturn(Mockito.mock(Iterator.class));
        Mockito.when(mockedGame.getPlayers()).thenReturn(Mockito.mock(Enumeration.class));
        Mockito.when(mockedGame.getAttacks()).thenReturn(Mockito.mock(Enumeration.class));
        Server server = new Server("TestServer", 1112);
        server.setGame(mockedGame);
        boolean victory_result = server.victory();
        TestCase.assertTrue(victory_result);
        server.die();
    }

    @Test
    public void testVictoryDraw() throws IOException {
        IGame mockedGame = Mockito.mock(IGame.class);
        VictoryResult mockedVictoryResult = Mockito.mock(VictoryResult.class);
        Victory mockedVictory = Mockito.mock(Victory.class);

        Mockito.when(mockedVictoryResult.victory()).thenReturn(true);
        Mockito.when(mockedVictoryResult.isDraw()).thenReturn(true);
        Mockito.when(mockedVictoryResult.getWinningPlayer()).thenReturn(IPlayer.PLAYER_NONE);
        Mockito.when(mockedGame.getVictory()).thenReturn(mockedVictory);
        Mockito.when(mockedVictory.checkForVictory(mockedGame, mockedGame.getVictoryContext())).thenReturn(mockedVictoryResult);
        Mockito.when(mockedGame.getEntities()).thenReturn(Mockito.mock(Iterator.class));
        Mockito.when(mockedGame.getPlayers()).thenReturn(Mockito.mock(Enumeration.class));
        Mockito.when(mockedGame.getAttacks()).thenReturn(Mockito.mock(Enumeration.class));

        Server server = new Server("TestServer", 1113);
        server.setGame(mockedGame);
        boolean victory_result = server.victory();
        TestCase.assertTrue(victory_result);
        Mockito.verify(mockedGame, Mockito.times(1)).setVictoryPlayerId(IPlayer.PLAYER_NONE);
        Mockito.verify(mockedGame, Mockito.times(1)).setVictoryTeam(IPlayer.TEAM_NONE);

    }

    @Test
    public void testCancelVictory() throws IOException {
//        Mock the game
        IGame mockedGame = Mockito.mock(IGame.class);
//        Set up the test data
        Mockito.when(mockedGame.getEntities()).thenReturn(Mockito.mock(Iterator.class));
        Mockito.when(mockedGame.getPlayers()).thenReturn(Mockito.mock(Enumeration.class));
        Mockito.when(mockedGame.getAttacks()).thenReturn(Mockito.mock(Enumeration.class));

//        start test server
        Server server = new Server("TestServer", 1114);
        server.setGame(mockedGame);

//        Call function under test
        server.cancelVictory();

//        Verify results
        Mockito.verify(mockedGame,Mockito.times(1)).setForceVictory(false);
        Mockito.verify(mockedGame, Mockito.times(1)).setVictoryPlayerId(IPlayer.PLAYER_NONE);
        Mockito.verify(mockedGame, Mockito.times(1)).setVictoryTeam(IPlayer.TEAM_NONE);
        server.die();
    }

    @Test
    public void testForceVictoryTeam() throws IOException{
//        Mock the game
        IGame mockedGame = Mockito.mock(IGame.class);
//        Mock the player
        IPlayer mockedPlayer = Mockito.mock(IPlayer.class);

//        Set up the test data
        Mockito.when(mockedGame.getEntities()).thenReturn(Mockito.mock(Iterator.class));
        Mockito.when(mockedGame.getPlayers()).thenReturn(Mockito.mock(Enumeration.class));
        Mockito.when(mockedGame.getAttacks()).thenReturn(Mockito.mock(Enumeration.class));

        Mockito.when(mockedPlayer.getTeam()).thenReturn(2);
        Mockito.when(mockedPlayer.getId()).thenReturn(1);

//        start test server
        Mockito.when(mockedGame.getPlayersVector()).thenReturn(new Vector<IPlayer>(){{add(mockedPlayer);}});

        Server server = new Server("TestServer", 1115);
        server.setGame(mockedGame);

//        Call function under test
        server.forceVictory(mockedPlayer);

//        Verify results
        Mockito.verify(mockedGame).setForceVictory(true);
        Mockito.verify(mockedGame).setVictoryPlayerId(IPlayer.PLAYER_NONE);
        Mockito.verify(mockedGame).setVictoryTeam(2);

        Mockito.verify(mockedPlayer).setAdmitsDefeat(false);
        server.die();
    }


    @Test
    public void testForceVictoryPlayer() throws IOException{
//        Mock the game
        IGame mockedGame = Mockito.mock(IGame.class);
//        Mock the player
        IPlayer mockedPlayer = Mockito.mock(IPlayer.class);

//        Set up the test data
        Mockito.when(mockedGame.getEntities()).thenReturn(Mockito.mock(Iterator.class));
        Mockito.when(mockedGame.getPlayers()).thenReturn(Mockito.mock(Enumeration.class));
        Mockito.when(mockedGame.getAttacks()).thenReturn(Mockito.mock(Enumeration.class));
        Mockito.when(mockedPlayer.getId()).thenReturn(1);

        Mockito.when(mockedGame.getPlayersVector()).thenReturn(new Vector<IPlayer>(){{add(mockedPlayer);}});

//        start test server
        Server server = new Server("TestServer", 1116);
        server.setGame(mockedGame);

//        Call function under test
        server.forceVictory(mockedPlayer);

//        Verify results
        Mockito.verify(mockedGame).setForceVictory(true);
        Mockito.verify(mockedGame).setVictoryPlayerId(1);
        Mockito.verify(mockedGame).setVictoryTeam(IPlayer.TEAM_NONE);
        Mockito.verify(mockedPlayer).setAdmitsDefeat(false);
        server.die();
    }
}
