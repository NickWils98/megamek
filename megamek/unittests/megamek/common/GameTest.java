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
import java.util.Enumeration;
import java.util.Iterator;

public class GameTest {

    @Test
    public void testCancelVictory() throws IOException {
//        Mock the game
        IGame game = new Game();

        game.cancelVictory();
        TestCase.assertFalse(game.isForceVictory());
        TestCase.assertEquals(game.getVictoryPlayerId(), IPlayer.PLAYER_NONE);
        TestCase.assertEquals(game.getVictoryTeam(), IPlayer.TEAM_NONE);
    }
}
