package megamek.server;

import junit.framework.TestCase;
import megamek.common.Entity;
import megamek.common.Game;
import megamek.common.IGame;
import megamek.server.commands.VictoryCommand;
import megamek.server.victory.Victory;
import megamek.server.victory.VictoryResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

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
    }
}
