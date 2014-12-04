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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getActions() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.ghosts.mypackage).
 */
public class MyGhosts extends Controller<EnumMap<GHOST, MOVE>> {
	int ghostsAssigned;
	Boolean blinkyHitTarget = false, pinkyHitTarget = false,
			sueHitTarget = false;

	private EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);

	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		myMoves.clear();
		ghostsAssigned = 0;
		int targetNode = game.getPacmanCurrentNodeIndex();
		// only used if there are 2 or 3 valid neighbors from blinky's target
		boolean splitPaths = false;
		int splitPath2targetIndex = 0;
		int splitPath2lastIndex = 0;
		MOVE splitPath2Move = MOVE.NEUTRAL;

		readXMLDoc();

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
					if (game.getGhostCurrentNodeIndex(GHOST.BLINKY) == targetNode) {
						blinkyHitTarget = true;
						assignGhost(game, GHOST.BLINKY,
								game.getPacmanCurrentNodeIndex());
					} else if (blinkyHitTarget) {
						assignGhost(game, GHOST.BLINKY,
								game.getPacmanCurrentNodeIndex());
					} else {
						assignGhost(game, GHOST.BLINKY, targetNode);
					}
					if (possiblePaths.length > 2) {
						splitPaths = true;
						splitPath2lastIndex = targetNode;
						splitPath2targetIndex = possiblePaths[1];
						splitPath2Move = game
								.getMoveToMakeToReachDirectNeighbour(
										splitPath2lastIndex,
										splitPath2targetIndex);
					}
					break;

				case (2):
					if (game.getGhostCurrentNodeIndex(GHOST.PINKY) == targetNode) {
						pinkyHitTarget = true;
						assignGhost(game, GHOST.PINKY,
								game.getPacmanCurrentNodeIndex());
					} else if (pinkyHitTarget) {
						assignGhost(game, GHOST.PINKY,
								game.getPacmanCurrentNodeIndex());
					} else {
						assignGhost(game, GHOST.PINKY, targetNode);
					}
					if (splitPaths) {
						lastIndex = splitPath2lastIndex;
						targetNode = splitPath2targetIndex;
						lastMoveStore = splitPath2Move;
					}
					break;

				case (3):
					if (game.getGhostCurrentNodeIndex(GHOST.SUE) == targetNode) {
						sueHitTarget = true;
						assignGhost(game, GHOST.SUE,
								game.getPacmanCurrentNodeIndex());
					} else if (sueHitTarget) {
						assignGhost(game, GHOST.SUE,
								game.getPacmanCurrentNodeIndex());
					} else {
						assignGhost(game, GHOST.SUE, targetNode);
					}
					break;
				default:
					break;
				}
				// targetNode = possiblePaths[0];

			} else {// target node only has 1 way forward
				// targetNode = possiblePaths[0];
			}
			if (ghostsAssigned != 3 || splitPaths == false) {
				lastIndex = targetNode;
				targetNode = possiblePaths[0];// set the target node for the
												// next check
				lastMoveStore = game.getMoveToMakeToReachDirectNeighbour(
						lastIndex, targetNode);
			}

		}
		splitPaths = false;
		return myMoves;
	}

	private void assignGhost(Game game, GHOST ghostToAssign, int targetNode) {
		myMoves.put(ghostToAssign, game.getApproximateNextMoveTowardsTarget(
				game.getGhostCurrentNodeIndex(ghostToAssign), targetNode,
				game.getGhostLastMoveMade(ghostToAssign), DM.PATH));
		ghostsAssigned++;
	}

	private void testGhosts() {
		myMoves.put(GHOST.INKY, MOVE.LEFT);
		myMoves.put(GHOST.BLINKY, MOVE.RIGHT);
		myMoves.put(GHOST.PINKY, MOVE.RIGHT);
		myMoves.put(GHOST.SUE, MOVE.RIGHT);
	}

	private void readXMLDoc() {
		try {
			File fXmlFile = new File(
					"C:/Users/Conor Harney/workspace/pacman_project/src/pacman/entries/ghosts/staff.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			//System.out.println("Root element :"
					//+ doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("currentstate");

			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				System.out.println("\nNode name :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					System.out.println("Atribute : " + eElement.getAttribute("id"));
					
					System.out.println("Tag Name : " + eElement.getElementsByTagName("nextstate").item(0).getTextContent());
					
					/*System.out.println("Last Name : "
							+ eElement.getElementsByTagName("lastname").item(0)
									.getTextContent());
					System.out.println("Nick Name : "
							+ eElement.getElementsByTagName("nickname").item(0)
									.getTextContent());
					System.out.println("Salary : "
							+ eElement.getElementsByTagName("salary").item(0)
									.getTextContent());*/

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}