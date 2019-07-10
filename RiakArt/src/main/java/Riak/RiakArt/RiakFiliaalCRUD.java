package Riak.RiakArt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import riakModel.RiakAankoop;
import riakModel.RiakFiliaal;
import riakModel.RiakKlant;
import model.Klant;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.MultiFetch;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.RiakFuture;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;

public class RiakFiliaalCRUD {
	private static String namespaceName = "riakFilialen";

	public static void create(RiakClient client, RiakFiliaal filiaal)
			throws ExecutionException, InterruptedException {
		Namespace bucket = new Namespace(namespaceName);
		Location location = new Location(bucket, filiaal.key);
		StoreValue store0p = new StoreValue.Builder(filiaal).withLocation(
				location).build();
		client.execute(store0p);
	}

	public static void createRiakFilialen(RiakClient client,
			List<RiakFiliaal> filialen) throws ExecutionException,
			InterruptedException {
		for (RiakFiliaal filiaal : filialen) {
			create(client, filiaal);
		}
	}

	// http://basho.github.io/riak-java-client/2.0.1/com/basho/riak/client/api/commands/kv/MultiFetch.html
	// http://www.massapi.com/class/com/basho/riak/client/api/commands/kv/MultiFetch.html
	public static List<RiakFiliaal> multiFetch(RiakClient client,
			List<String> riakFiliaalKeys) throws ExecutionException,
			InterruptedException {
		Namespace bucket = new Namespace(namespaceName);
		List<Location> locations = new ArrayList<Location>();
		for (String riakFiliaalKey : riakFiliaalKeys) {
			Location location = new Location(bucket, riakFiliaalKey);
			locations.add(location);
		}

		MultiFetch multifetch = new MultiFetch.Builder()
				.addLocations(locations).build();
		MultiFetch.Response response = client.execute(multifetch);
		List<RiakFiliaal> myResults = new ArrayList<RiakFiliaal>();
		for (RiakFuture<FetchValue.Response, Location> f : response) {
			try {
				FetchValue.Response response2 = f.get();
				myResults.add(response2.getValue(RiakFiliaal.class));
			} catch (ExecutionException e) {
				// log error, etc.
			}
		}
		return myResults;
	}

	public static void delete(RiakClient client, String key)
			throws ExecutionException, InterruptedException {
		Riak.delete(client, namespaceName, key);
	}

	//Klanten die al geweest zijn mogen niet in de futureLijst komen, anders is er een oneindige loop.
	//De HashSets voorkomen automatisch Doubles aangezien key input unique moet zijn.
	public static int findFiliaalDistance(RiakClient client, RiakKlant a,
			RiakKlant b, List<String> allRiakAankoopKeys) throws ExecutionException, InterruptedException {	
		int filiaalDistance = 0;
		Set<String> pastInputKlantKeys = new HashSet<String>(); //sets om unique keys af te dwingen, als het goed is kunnen er nu geen dubbele
		Set<String> currentInputKlantKeys = new HashSet<String>(); //inputs in verschijnen, ongtest met huidige testData.
		Set<String> futureInputKlantKeys = new HashSet<String>();
		currentInputKlantKeys.add(a.key);
		Boolean foundDistance = false;
		while (!foundDistance && currentInputKlantKeys.size() != 0){
			for (String inputKlantKey : currentInputKlantKeys){
				Set<String> klantKeys = getDistance0KlantKeys(client, inputKlantKey, allRiakAankoopKeys);
				System.out.println("findFiliaalDistance: klantKeys found: "+klantKeys);
				futureInputKlantKeys.addAll(klantKeys);
			}
			//remove doubles from futureklantKeys todo, is niet erg wel langzamer.
			for (String inputKlantKey : futureInputKlantKeys){
				if (inputKlantKey.equals(b.key)){
					System.out.println("findFiliaalDistance: b.key has been found");
					foundDistance = true;
					return filiaalDistance;
				}
			}
			filiaalDistance++;
			pastInputKlantKeys.addAll(currentInputKlantKeys); //doubles zouden niet mogen.
			currentInputKlantKeys.clear();
			currentInputKlantKeys.addAll(futureInputKlantKeys);
			currentInputKlantKeys.removeAll(pastInputKlantKeys);
			//Dit is feitelijk een star/tree functie, dit zorgt er nu voor dat het niet terugkan gaan naar al begaande nodes en een loop
			//veroorzaken. Het is wel ongetest, mijn testData gaat maar tot distance1. Dit kan pas bij distance 2 gebeuren.
			futureInputKlantKeys.clear();
			System.out.println("findFiliaalDistance: filiaalDistance increased to : "+filiaalDistance);
			System.out.println("findFiliaalDistance: pastInputKeysUpdatedTo : "+pastInputKlantKeys);
			System.out.println("findFiliaalDistance: newInputKeysUpdatedTo : "+currentInputKlantKeys);
			System.out.println("findFiliaalDistance: futureInputKeys should be emptied : "+futureInputKlantKeys);
		}
		return 999; //wordt aangeroepen als er niets wordt gevonden
	}

	// ik wil eigenlijk de gehele tabel, de riakAankoopkeys mee moeten geven is
	// geen schone oplossing hier. Zou ideaal niet meegegeven te horen worden.
	public static Set<String> getDistance0KlantKeys(RiakClient client,
			String inputKlantKey, List<String> allRiakAankoopKeys)
			throws ExecutionException, InterruptedException {
		Set<String> riakKlantKeys = new HashSet<String>();
		List<RiakAankoop> allAankopen = RiakAankoopCRUD.multiFetch(client, allRiakAankoopKeys);
		Set<String> filiaalKeys = new HashSet<String>();
		for (RiakAankoop riakAankoop : allAankopen) {
			if (riakAankoop.klantKey.equals(inputKlantKey)) {
				//riakKlantKeys.add(riakAankoop.klantKey);
				filiaalKeys.add(riakAankoop.filiaalKey);
			}
			//for loop zou hier afgekapt kunnen worden. break denk ik.
		}
		System.out.println("getDistance0KlantKeys: de input klant "+inputKlantKey+" heeft de volgende filialen"+filiaalKeys);
		for (RiakAankoop riakAankoop : allAankopen){
			for (String filiaalKey : filiaalKeys){
				if(filiaalKey.equals(riakAankoop.filiaalKey)){
					if (!riakAankoop.klantKey.equals(inputKlantKey)){
						riakKlantKeys.add(riakAankoop.klantKey);
					}
				}
			}	
		}
		
		return riakKlantKeys;
	}
}
