package mygame.game;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.font.BitmapFont;
import com.jme3.network.Client;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import mygame.character.ThirdPersonCharacterControl;
import mygame.network.ClientMessageListener;

/**
 * The main class for the client application. It has a number of purposes:
 * This is the starting point for the game.
 * Handles the app states that take care of the game
 * Handles the player controller and other player spatial for the characters.
 *
 * The AppStates can be found in the package: mygame.game (where this file is)
 *
 * @author Sameer Suri
 */
public class Main extends SimpleApplication
{
    // Game stuff:

    /**
     * The player controller for the player controlled by THIS client
     */
    ThirdPersonCharacterControl playerController;

    /**
     * The spatial for the player controlled by the OTHER client. Note that
     * it is a Spatial and not a control as all of the movements are happenning
     * externally, and we are just applying them, so a control is not needed.
     */
    Spatial otherPlayer;

    /**
     * The networking client.
     */
    Client client;

    ClientMessageListener clientMessageListener;

    boolean isPlayer1;

    private AppState currentAppState;


    // Starting point of the client app
    public static void main(String... args)
    {
        // Creates a new instance of our game and starts it.
        Main gp = new Main();
        gp.start();
    }

    // Called by the engine in order to start the game.
    @Override
    public void simpleInitApp()
    {
        this.setPauseOnLostFocus(false);
        // Disables the default "fly" cam, we will use a different one. Keeping it enabled causes GUI bugs.
        flyCam.setEnabled(false);

        /*
            This game is implemented through App States.
            The first app state is Init.
            Then, if the server has two players, it will go to the Play app state.
            If not, it will go to the Wait app phase until a second player joins.
            Then it will enter Play app state.
            Once it enters the Play app state, it will play the game.
            A 'menu' app state will also be added at some point.
            For info about each app state, read the comments in its source file.
        */

        // Starts off with the Init app state.
        currentAppState = new InitAppState();
        stateManager.attach(currentAppState);

        // Disables the default diagnostic text that is useless for a release game
        setDisplayFps(false);
        setDisplayStatView(false);
    }

    // Cleans up resources, closes the window, and kills the app.
    @Override
    public void destroy()
    {
        // Stops the networking to end the connection cleanly
        if(client != null)
            client.close();

        // Has the superclass finish cleanup
        super.destroy();
    }

    // Called by an app state when it is finished.
    void nextAppState()
    {
        // This is a quick diagram of app states:
        // Init -> (Wait) -> Play <-> Menu
        // Menu does not currently exist

        // Removes the already attached state so a new one can be added
        stateManager.detach(currentAppState);

        // A boolean to track if we are actually adding a new app state, to avoid
        // a NullPointerException
        boolean newStateAttached = true;

        // Goes to either Wait or Play after Init
        if(currentAppState instanceof InitAppState)
        {
            // Goes to Wait if we are player 1 (and thus, we need a player 2)
            if(this.isPlayer1)
            {
                currentAppState = new WaitAppState();
            }
            // Goes to Play if we are player 2 (and thus, a player 1 has already connected)
            else
            {
                currentAppState = new PlayAppState();
            }
        }
        // Goes to Play after Wait
        else if(currentAppState instanceof WaitAppState)
        {
            currentAppState = new PlayAppState();
        }
        // If no app state was changed, reflect that in the boolean flag
        else
        {
            newStateAttached = false;
        }

        if(newStateAttached)
        {
            // Actually attaches the next app state
            stateManager.attach(currentAppState);
        }
    }

    /**
     * Gets the default font for use with Nifty GUI
     * @return The default GUI font
     */
    public BitmapFont getGuiFont()
    {
        return guiFont;
    }

    /**
     * Gets the settings established by the player in the settings menu.
     * @return The settings.
     */
    public AppSettings getSettings()
    {
        return settings;
    }
}