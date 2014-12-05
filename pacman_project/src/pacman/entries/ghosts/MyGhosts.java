//author: Conor Harney
//id: C00155687
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
import java.nio.channels.SeekableByteChannel;
import java.security.AllPermission;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

/*
 * The idea behind this AI is to have 1 ghost chase the player directly, while the rest of them try to get in front of the pacman.
 * They will do this by getting the index of the pacman and following its direction to a point where the player has a choice to turn. 
 * 1 ghost will go to this index while the next 2 ghosts will go to the next two turning points
 * If the player eats a power pill, all but the first ghost will flee instead.
 */

public class MyGhosts extends Controller<EnumMap<GHOST, MOVE>> {
	int ghostsAssigned;

	enum States {
		seeking_front_node, seeking_pacman, fleeing
	}

	enum Actions {
		hit_target, ate_power_pill, died, power_pill_expired
	}

	//The states for for Blinky, Pinky and Sue
	States blinkyState = States.seeking_front_node;
	States pinkyState = States.seeking_front_node;
	States sueState = States.seeking_front_node;

	private EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);

	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		myMoves.clear();
		ghostsAssigned = 0;
		int targetNode = game.getPacmanCurrentNodeIndex();

		// Inky directly chases player
		assignGhost(game, GHOST.INKY, targetNode);

		MOVE lastMoveStore = game.getPacmanLastMoveMade();
		int lastIndex = game.getPacmanCurrentNodeIndex();
		while (ghostsAssigned < 4) {

			int[] possiblePaths = game.getNeighbouringNodes(targetNode,
					lastMoveStore);

			if (possiblePaths.length > 1) {
				switch (ghostsAssigned) {
				case (1):
					if (game.getGhostEdibleTime(GHOST.BLINKY) > 0
							&& blinkyState != States.fleeing) {
						// should blinky flee
						blinkyState = getNextState(blinkyState,
								Actions.ate_power_pill);
					}
					if (blinkyState == States.fleeing) {
						// is blinky fleeing
						if (game.getGhostEdibleTime(GHOST.BLINKY) <= 0) {
							// should blinky stop fleeing
							blinkyState = getNextState(blinkyState, Actions.power_pill_expired);
						} else {
							// blinky should keep fleeing
							assignGhostFlee(game, GHOST.BLINKY);
						}
					}
					if (game.getGhostCurrentNodeIndex(GHOST.BLINKY) == targetNode) {
						// blinky reached the target node
						blinkyState = getNextState(blinkyState,	Actions.hit_target);
						assignGhost(game, GHOST.BLINKY, game.getPacmanCurrentNodeIndex());
					} 
					else if (blinkyState == States.seeking_pacman) {
						//blinky is moving directly towards pacman
						assignGhost(game, GHOST.BLINKY, game.getPacmanCurrentNodeIndex());
					} 
					else {
						//blinky is searching for a node in front of pacman
						assignGhost(game, GHOST.BLINKY, targetNode);
					}
					break;

				case (2):
					if (game.getGhostEdibleTime(GHOST.PINKY) > 0 && pinkyState != States.fleeing) {
						pinkyState = getNextState(pinkyState, Actions.ate_power_pill);
					}
					if (pinkyState == States.fleeing) {
						if (game.getGhostEdibleTime(GHOST.PINKY) <= 0) {
							pinkyState = getNextState(pinkyState, Actions.power_pill_expired);
						} 
						else {
							assignGhostFlee(game, GHOST.PINKY);
						}
					} 
					else if (game.getGhostCurrentNodeIndex(GHOST.PINKY) == targetNode) {
						pinkyState = getNextState(pinkyState, Actions.hit_target);
						assignGhost(game, GHOST.PINKY, game.getPacmanCurrentNodeIndex());
					} 
					else if (pinkyState == States.seeking_pacman) {
						assignGhost(game, GHOST.PINKY, game.getPacmanCurrentNodeIndex());
					} 
					else {
						assignGhost(game, GHOST.PINKY, targetNode);
					}
					break;

				case (3):
					if (game.getGhostEdibleTime(GHOST.SUE) > 0 && sueState != States.fleeing) {
						sueState = getNextState(sueState, Actions.ate_power_pill);
					}
					if (sueState == States.fleeing) {
						if (game.getGhostEdibleTime(GHOST.SUE) <= 0) {
							sueState = getNextState(sueState, Actions.power_pill_expired);
						} 
						else {
							assignGhostFlee(game, GHOST.SUE);
						}
					} 
					else if (game.getGhostCurrentNodeIndex(GHOST.SUE) == targetNode) {
						sueState = getNextState(sueState, Actions.hit_target);
						assignGhost(game, GHOST.SUE,
								game.getPacmanCurrentNodeIndex());
					} 
					else if (sueState == States.seeking_pacman) {
						assignGhost(game, GHOST.SUE,
								game.getPacmanCurrentNodeIndex());
					} 
					else {
						assignGhost(game, GHOST.SUE, targetNode);
					}
					break;
				default:
					break;
				}
			}

			lastIndex = targetNode;
			targetNode = possiblePaths[0];
			// set the target node for the next check
			lastMoveStore = game.getMoveToMakeToReachDirectNeighbour(lastIndex,	targetNode);
		}
		return myMoves;
	}

	private void assignGhost(Game game, GHOST ghostToAssign, int targetNode) {
		//set the given ghost's move to the quickest direction to the target node
		myMoves.put(ghostToAssign, game.getApproximateNextMoveTowardsTarget(
				game.getGhostCurrentNodeIndex(ghostToAssign), targetNode,
				game.getGhostLastMoveMade(ghostToAssign), DM.PATH));
		ghostsAssigned++;
	}

	private void assignGhostFlee(Game game, GHOST ghostToAssign) {
		//give the given ghost orders to flee from pacman
		MOVE nextMove = game.getNextMoveAwayFromTarget(
				game.getGhostCurrentNodeIndex(ghostToAssign),
				game.getPacmanCurrentNodeIndex(), DM.PATH);
		myMoves.put(ghostToAssign, nextMove);
		ghostsAssigned++;
	}

	private States getNextState(States currentState, Actions actionToNextState) {
		// get the next state from the xml file
		States retState = States.seeking_front_node;

		try {
			File fXmlFile = new File("src/pacman/entries/ghosts/states.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("currentstate");

			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					if (eElement.getAttribute("id").toString().equalsIgnoreCase(currentState.toString())) {//find the ghosts current staet options
						if (eElement.getElementsByTagName("actionToNextState").item(0).getTextContent()
								.equalsIgnoreCase(actionToNextState.toString())) {//chose the selection with the last action
							
							//find the next state
							String stateString = eElement.getElementsByTagName("nextstate").item(0).getTextContent();
							
							//convert it to a state
							if (stateString	.equalsIgnoreCase("seeking_front_node")) {
								retState = States.seeking_front_node;
							} 
							else if (stateString.equalsIgnoreCase("seeking_pacman")) {
								retState = States.seeking_pacman;
							} 
							else if (stateString.equalsIgnoreCase("fleeing")) {
								retState = States.fleeing;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retState;
	}
}