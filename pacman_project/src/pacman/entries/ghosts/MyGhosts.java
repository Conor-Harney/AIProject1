package pacman.entries.ghosts;

import java.util.EnumMap;
import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.PacMan;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getActions() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.ghosts.mypackage).
 */
public class MyGhosts extends Controller<EnumMap<GHOST,MOVE>>
{
	private EnumMap<GHOST, MOVE> myMoves=new EnumMap<GHOST, MOVE>(GHOST.class);
	
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue)
	{
		myMoves.clear();
		int targetNode=game.getPacmanCurrentNodeIndex();
		//int targetNode = game.getPacmanCurrentNodeIndex() + game.getPacmanLastMoveMade();
		MOVE temp = game.getPacmanLastMoveMade();
		//if()
		
		//Place your game logic here to play the game as the ghosts
		
		
		myMoves.put(GHOST.BLINKY,
				game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.BLINKY),targetNode,game.getGhostLastMoveMade(GHOST.BLINKY),DM.PATH));	
		
		myMoves.put(GHOST.INKY,
				game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.INKY),game.getGhostCurrentNodeIndex(GHOST.BLINKY),game.getGhostLastMoveMade(GHOST.INKY),DM.PATH));
		myMoves.put(GHOST.PINKY,
				game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.PINKY),game.getGhostCurrentNodeIndex(GHOST.BLINKY),game.getGhostLastMoveMade(GHOST.PINKY),DM.PATH));
		
		MOVE lastMPac =  game.getPacmanLastMoveMade();
		
		myMoves.put(GHOST.SUE,
				game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.SUE),game.getGhostCurrentNodeIndex(GHOST.BLINKY),game.getGhostLastMoveMade(GHOST.SUE),DM.PATH));
		
		return myMoves;
	}
}