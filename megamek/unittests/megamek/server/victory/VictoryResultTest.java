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

import java.util.*;
import java.io.IOException;
import java.util.HashMap;

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

    @Test
    public void testGetWinningPlayer() throws IOException {
        VictoryResult mockedVictoryResult = new VictoryResult(false);
        mockedVictoryResult.addPlayerScore(2, 3);
        mockedVictoryResult.addPlayerScore(1, 1);
        mockedVictoryResult.addPlayerScore(3, 2);

        int expectedWinningPlayer = 2;
        int actualWinningPlayer = mockedVictoryResult.getWinningPlayer();

        TestCase.assertEquals(expectedWinningPlayer, actualWinningPlayer);
    }

    @Test
    public void testGetWinningTeam() throws IOException {
        VictoryResult mockedVictoryResult = new VictoryResult(false);
        mockedVictoryResult.addTeamScore(1, 1);
        mockedVictoryResult.addTeamScore(2, 3);
        mockedVictoryResult.addTeamScore(3, 2);

        int expectedWinningTeam = 2;
        int actualWinningTeam = mockedVictoryResult.getWinningTeam();

        TestCase.assertEquals(expectedWinningTeam, actualWinningTeam);
    }

    @Test
    public void testGetWinningPlayerDraw() throws IOException {
        VictoryResult mockedVictoryResult = new VictoryResult(false);
        mockedVictoryResult.addPlayerScore(1, 3);
        mockedVictoryResult.addPlayerScore(2, 3);
        mockedVictoryResult.addPlayerScore(3, 2);

        int expectedWinningPlayer = IPlayer.PLAYER_NONE;
        int actualWinningPlayer = mockedVictoryResult.getWinningPlayer();

        TestCase.assertEquals(expectedWinningPlayer, actualWinningPlayer);
    }

    @Test
    public void testGetWinningTeamDraw() throws IOException {
        VictoryResult mockedVictoryResult = new VictoryResult(false);
        mockedVictoryResult.addTeamScore(1, 3);
        mockedVictoryResult.addTeamScore(2, 3);
        mockedVictoryResult.addTeamScore(3, 2);

        int expectedWinningTeam = IPlayer.TEAM_NONE;
        int actualWinningTeam = mockedVictoryResult.getWinningTeam();

        TestCase.assertEquals(expectedWinningTeam, actualWinningTeam);
    }
}
