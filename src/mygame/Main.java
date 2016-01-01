package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.font.BitmapFont;
import com.jme3.network.Client;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import mygame.character.NetworkedCharacterControl;
import mygame.character.ThirdPersonCharacterControl;
import mygame.game.InitAppState;
import mygame.game.PlayAppState;
import mygame.game.WaitingAppState;
import mygame.network.ClientMessageListener;

/**
 * The main class for my game. It has a number of purposes:
 * This is the starting point for the game.
 * Handles the init and update methods provided by the API.
 * Handles the controllers (i.e. player controller) used in this game.
 *
 * This is now implemented in AppStates, which can be found in the package: mygame.game
 *
 * Note that this is for the client application, not the server.
 * The server main class can be found at: mygame.network.ServerMain.java
 * @author Sameer Suri
 */
public class Main extends SimpleApplication
{
    public Spatial otherPlayer;

    /**
     * The player controller for the player controlled by THIS client
     */
    public ThirdPersonCharacterControl playerController;

    /**
     * The player controller for the player controlled by the OTHER client
     */
    public NetworkedCharacterControl networkedController;

    /**
     * Starting point for the application.
     * @param args The command line arguments passed
     */
    public static void main(String... args)
    {
        // Creates a new instance of our game and starts it.
        Main gp = new Main();
        gp.start();
    }

    /**
     * The networking client.
     */
    public Client client;

    public ClientMessageListener clientMessageListener;

    public boolean isPlayer1;

    private AppState currentAppState;

    /**
     * Called by the engine in order to start the game.
     */
    @Override
    public void simpleInitApp()
    {
        flyCam.setEnabled(false);

        // 2 game states:
        // Init -> Play
        currentAppState = new InitAppState();
        stateManager.attach(currentAppState);

        // Disables the default diagnostic
        setDisplayFps(false);
        setDisplayStatView(false);
    }

    /**
     * Cleans up resources, closes the window, and kills the app.
     */
    @Override
    public void destroy()
    {
        // Stops the networking to end the connection cleanly
        if(client != null)
            client.close();

        // Has the superclass finish cleanup
        super.destroy();
    }

    public void nextAppState()
    {
        // Init -> (Wait) -> Play
        // Wait may not occur, only for player 1
        // IN THE FUTURE:
        // Play -> Menu
        // Menu -> Play

        stateManager.detach(currentAppState);

        boolean newStateAttached = true;
        if(currentAppState instanceof InitAppState)
        {
            if(this.isPlayer1)
            {
                currentAppState = new WaitingAppState();
            }
            else
            {
                currentAppState = new PlayAppState();
            }
        }
        else if(currentAppState instanceof WaitingAppState)
        {
            currentAppState = new PlayAppState();
        }
        else
        {
            newStateAttached = false;
        }

        if(newStateAttached)
        {
            stateManager.attach(currentAppState);
        }
    }

    public BitmapFont getGuiFont()
    {
        return guiFont;
    }

    public AppSettings getSettings()
    {
        return settings;
    }
}
