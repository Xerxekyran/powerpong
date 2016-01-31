package pfk;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.domain.player.ETeam;
import de.noobisoft.powerpong.domain.player.Player;
import de.noobisoft.powerpong.domain.player.weapon.EWeapons;
import de.noobisoft.powerpong.network.INetworkMessageReceiver;
import de.noobisoft.powerpong.network.MinaClient;
import de.noobisoft.powerpong.network.messages.INetworkMessage;
import de.noobisoft.powerpong.network.messages.PlayerJoined;
import de.noobisoft.powerpong.network.messages.PlayerMoved;
import de.noobisoft.powerpong.network.messages.PlayerShot;

/**
 * This class represents a minimal client program, that only tests the network
 * connection with the server. multiples instances are created and send
 * simultaniously messages to the server
 * 
 * @author Lars George
 * 
 */
public class TestClient extends Thread implements INetworkMessageReceiver
{
	static Logger		logger							= Logger.getLogger(TestClient.class);

	public static int	TIME_BETWEEN_UPDATE_MESSAGES	= 100;
	public static int	TIME_BEFORE_CLIENTS_SHUTDOWN	= 10000;
	public static int	NUM_SIMULATED_CLIENTS			= 10;

	public static int	numIncomingMessages				= 0;
	private boolean		outputterClient					= false;
	private long		startTime						= 0;
	private MinaClient	networkClient					= null;
	private boolean		abort							= false;

	/**
	 * 
	 */
	public TestClient()
	{
		try
		{
			networkClient = new MinaClient("localhost", 9090, this);
			Thread t = new Thread(networkClient);
			// t.setDaemon(true);
			t.start();
		}
		catch (Throwable e)
		{
			logger.error(e);
		}
	}

	@Override
	public void addIncomingMessage(INetworkMessage msg)
	{
		if (outputterClient)
			TestClient.numIncomingMessages++;
	}

	@Override
	public void run()
	{
		try
		{

			// send the join message
			PlayerJoined helloMSG = new PlayerJoined();
			helloMSG.setPlayer(new Player(this.getName(), ETeam.RED, false));

			while (!networkClient.send(helloMSG.getNetworkData()))
			{
				Thread.sleep(100);
			}

			PlayerMoved pMoveMSG = new PlayerMoved();
			pMoveMSG.setNewPosition(new Vector3(1, 2, 3));
			pMoveMSG.setNewRotationY(10);
			pMoveMSG.setPlayerID(this.getName());

			PlayerShot pShot = new PlayerShot();
			pShot.setDirection(new Vector3(1, 2, 3));
			pShot.setPlayerID(this.getName());
			pShot.setPosition(new Vector3(4, 5, 6));
			pShot.setWeaponType(EWeapons.GlueGun);

			String networkData = pMoveMSG.getNetworkData();
			startTime = System.currentTimeMillis();

			// repeat sending update messages to the server
			while (!abort)
			{
				Thread.sleep(TIME_BETWEEN_UPDATE_MESSAGES);
				networkClient.send(networkData);
			}

			networkClient.setAbort(true);
		}
		catch (Exception e)
		{
			logger.error(e);
		}

	}

	/**
	 * @return the abort
	 */
	public boolean isAbort()
	{
		return abort;
	}

	/**
	 * @param abort
	 *            the abort to set
	 */
	public void setAbort(boolean abort)
	{
		this.abort = abort;

		if (this.outputterClient)
		{
			System.out.println("**********************************************************");
			System.out.println("RESULTS:");
			System.out.println("Client running time: "
					+ ((double) System.currentTimeMillis() - startTime) + " ms");
			System.out.println("Received Messages: "
					+ TestClient.numIncomingMessages);
			System.out.println("AVG per Second: "
					+ (double) TestClient.numIncomingMessages
					/ ((double) System.currentTimeMillis() - startTime) * 1000);
			System.out.println("**********************************************************");
		}
	}

	/**
	 * Startpoint
	 * 
	 * @param args
	 *            console parameters
	 */
	public static void main(String[] args)
	{
		try
		{
			// Logger.getRootLogger().setLevel(Level.ERROR);

			LinkedList<TestClient> testClients = new LinkedList<TestClient>();

			// Create the Clients
			for (int i = 0; i < NUM_SIMULATED_CLIENTS; i++)
			{
				TestClient t = new TestClient();
				t.setName("TestClient_" + i);
				testClients.add(t);

				if (i == 0)
					t.setOutputterClient(true);
				Thread.sleep(100);
				t.start();
			}

			// whait a bit
			Thread.sleep(TIME_BEFORE_CLIENTS_SHUTDOWN);

			logger.info("Stopping all TestClients");

			// stop all TestClients
			for (TestClient tc : testClients)
			{
				tc.setAbort(true);
			}

		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

	/**
	 * @param outputterClient
	 *            the outputterClient to set
	 */
	public void setOutputterClient(boolean outputterClient)
	{
		this.outputterClient = outputterClient;
	}
}
