package pacman.entries.ghosts;

import java.lang.reflect.Array;
import java.security.AllPermission;
import java.util.EnumMap;
import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.*;
import java.lang.reflect.Array;
import java.security.AllPermission;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getActions() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.ghosts.mypackage).
 */
public class MyGhosts extends Controller<EnumMap<GHOST, MOVE>> {
	Boolean blinkyHitTarget = false, pinkyHitTarget = false, sueHitTarget = false;
	int ghostsAssigned;
	private EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);

	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		myMoves.clear();
		ghostsAssigned = 0;
		int targetNode = game.getPacmanCurrentNodeIndex();

		// inky directly chases player
		assignGhost(game, GHOST.INKY, targetNode);

		// int targetNode = game.getPacmanCurrentNodeIndex() +
		// game.getPacmanLastMoveMade();
		//MOVE temp = game.getPacmanLastMoveMade();

		MOVE lastMoveStore = game.getPacmanLastMoveMade();
		int lastIndex = game.getPacmanCurrentNodeIndex();
		while (ghostsAssigned < 4) {

			int[] possiblePaths = game.getNeighbouringNodes(targetNode,
					lastMoveStore);
			//int length = Array.getLength(possiblePaths);
			if (possiblePaths.length > 1) {
				switch (ghostsAssigned) {
				case (1):
					if(game.getGhostCurrentNodeIndex(GHOST.BLINKY) == targetNode){
						blinkyHitTarget = true;
						assignGhost(game, GHOST.BLINKY, game.getPacmanCurrentNodeIndex());
					}
					else if(blinkyHitTarget){
						assignGhost(game, GHOST.BLINKY, game.getPacmanCurrentNodeIndex());
					}
					else{
						assignGhost(game, GHOST.BLINKY, targetNode);
					}
					break;
					
				case (2):
					if(game.getGhostCurrentNodeIndex(GHOST.PINKY) == targetNode){
						blinkyHitTarget = true;
						assignGhost(game, GHOST.PINKY, game.getPacmanCurrentNodeIndex());
					}
					else if(blinkyHitTarget){
						assignGhost(game, GHOST.PINKY, game.getPacmanCurrentNodeIndex());
					}
					else{
						assignGhost(game, GHOST.PINKY, targetNode);
					}
					break;
					
				case (3):
					if(game.getGhostCurrentNodeIndex(GHOST.SUE) == targetNode){
						blinkyHitTarget = true;
						assignGhost(game, GHOST.SUE, game.getPacmanCurrentNodeIndex());
					}
					else if(blinkyHitTarget){
						assignGhost(game, GHOST.SUE, game.getPacmanCurrentNodeIndex());
					}
					else{
						assignGhost(game, GHOST.SUE, targetNode);
					}
					break;
					
				default:
					break;
				}
				targetNode = possiblePaths[0];
				
				
			} else {
				targetNode = possiblePaths[0];
			}
			lastMoveStore = game.getMoveToMakeToReachDirectNeighbour(lastIndex, targetNode);
			lastIndex = targetNode;
		}
		/*

		Node[] currGraph = game.getCurrentMaze().graph;
		EnumMap<MOVE, int[]> neighbours = currGraph[targetNode].allNeighbouringNodes;
		EnumMap<MOVE, MOVE[]> tempAllPosMoves = currGraph[targetNode].allPossibleMoves;

		int[] tempMove = neighbours.get(1);

		if (temp == MOVE.DOWN) {
			// game->currentmaze->graph->neighbourto(pacmanIndex)

			// currMaze.graph[targetNode];
			// game.getCurrentMaze().graph[targetNode];
			// game.getCurrentMaze().graph[targetNode].allNeighbouringNodes[targetNode].
			// [3];
		} else if (temp == MOVE.UP) {

		} else if (temp == MOVE.LEFT) {
			targetNode -= 1;
		} else if (temp == MOVE.RIGHT) {
			targetNode += 1;
		} else if (temp == MOVE.NEUTRAL) {

		}

		// Place your game logic here to play the game as the ghosts

		// blinky moves to players closest crossroads

		myMoves.put(GHOST.BLINKY, game.getApproximateNextMoveTowardsTarget(
				game.getGhostCurrentNodeIndex(GHOST.BLINKY), targetNode,
				game.getGhostLastMoveMade(GHOST.BLINKY), DM.PATH));

		myMoves.put(
				GHOST.PINKY,
				game.getApproximateNextMoveTowardsTarget(
						game.getGhostCurrentNodeIndex(GHOST.PINKY),
						game.getGhostCurrentNodeIndex(GHOST.BLINKY),
						game.getGhostLastMoveMade(GHOST.PINKY), DM.PATH));

		MOVE lastMPac = game.getPacmanLastMoveMade();

		myMoves.put(
				GHOST.SUE,
				game.getApproximateNextMoveTowardsTarget(
						game.getGhostCurrentNodeIndex(GHOST.SUE),
						game.getGhostCurrentNodeIndex(GHOST.BLINKY),
						game.getGhostLastMoveMade(GHOST.SUE), DM.PATH));
						
						*/

		return myMoves;
	}

	public void assignGhost(Game game, GHOST ghostToAssign, int targetNode) {
		myMoves.put(ghostToAssign, game.getApproximateNextMoveTowardsTarget(
				game.getGhostCurrentNodeIndex(ghostToAssign), targetNode,
				game.getGhostLastMoveMade(ghostToAssign), DM.PATH));
		ghostsAssigned++;
	}
}