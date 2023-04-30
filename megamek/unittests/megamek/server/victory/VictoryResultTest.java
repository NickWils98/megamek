package megamek.server.victory;

import junit.framework.TestCase;
import megamek.common.Entity;
import megamek.common.Game;
import megamek.common.IGame;
import megamek.common.IPlayer;
import megamek.server.commands.VictoryCommand;
import megamek.server.victory.Victory;
import megamek.server.victory.VictoryResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

@RunWith(JUnit4.class)
public class VictoryResultTest {
    @Test
    public void testAddPlayerScore() {
        VictoryResult victoryResult = new VictoryResult(false);
        victoryResult.addPlayerScore(0, 12);
        victoryResult.addPlayerScore(1, 3);

        TestCase.assertEquals(victoryResult.playerScore.get(1), Double.valueOf(3));
        TestCase.assertEquals(victoryResult.hiScore, Double.valueOf(12));
    }
}
