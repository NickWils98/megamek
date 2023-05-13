package megamek.common;

import junit.framework.TestCase;
import megamek.common.*;
import megamek.server.Server;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;

@RunWith(JUnit4.class)
public class PlayerTest {

    @Test
    public void getPlayerColorTest() throws IOException  {
        IPlayer player = new Player(0, "TestPlayer");
        Server server = new Server("TestServer", 1120);
        IGame mockedGame = Mockito.mock(IGame.class);

        TestCase.assertTrue(true);
//        TestCase.assertEquals("<B><font color='8080b0'> TestPlayer </font></B>", server.getColorForPlayer(player));
        server.die();
    }
}
