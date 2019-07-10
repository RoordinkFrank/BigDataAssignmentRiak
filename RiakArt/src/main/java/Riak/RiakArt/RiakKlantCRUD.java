package Riak.RiakArt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import riakModel.RiakAankoop;
import riakModel.RiakKlant;
import riakModel.RiakKlantPaar;
import riakModel.RiakProduct;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.cap.UnresolvedConflictException;
import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.api.commands.kv.UpdateValue;
import com.basho.riak.client.api.commands.search.StoreIndex;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.search.YokozunaIndex;

public class RiakKlantCRUD {
	private static String namespaceName = "klanten";

	public static void create(RiakClient client, RiakKlant klant)
			throws ExecutionException, InterruptedException {
		Namespace bucket = new Namespace(namespaceName);
		Location location = new Location(bucket, klant.key);
		StoreValue store0p = new StoreValue.Builder(klant).withLocation(
				location).build();
		client.execute(store0p);
	}
	
	public static void createRiakKlanten(RiakClient client, List<RiakKlant> klanten) throws ExecutionException, InterruptedException{
		for(RiakKlant klant : klanten){
			create(client, klant);
		}
	}

	public static void update(RiakClient client, RiakKlant klant)
			throws ExecutionException, InterruptedException {
		Namespace bucket = new Namespace(namespaceName);
		Location location = new Location(bucket, klant.key);
		RiakKlant.KlantUpdate updatedKlant = new RiakKlant.KlantUpdate(klant);
		UpdateValue updateValue = new UpdateValue.Builder(location).withUpdate(
				updatedKlant).build();
		UpdateValue.Response response = client.execute(updateValue);
	}

	public static RiakKlant fetch(RiakClient client, String klantKey)
			throws UnresolvedConflictException, ExecutionException,
			InterruptedException {
		Namespace bucket = new Namespace(namespaceName);
		Location location = new Location(bucket, klantKey);
		FetchValue fetchOp = new FetchValue.Builder(location).build();
		RiakKlant fetchedKlant = client.execute(fetchOp).getValue(
				RiakKlant.class);
		return fetchedKlant;
	}

	public static void delete(RiakClient client, String klantKey)
			throws ExecutionException, InterruptedException {
		Namespace bucket = new Namespace(namespaceName);
		Location location = new Location(bucket, klantKey);
		DeleteValue deleteOp = new DeleteValue.Builder(location).build();
		client.execute(deleteOp);
	}

	// vind alle klantparen die 4 of meer van dezelfde producten hebben gekocht.
	// heeft nodig, klantKey, ProductKey n
	public static List<RiakKlantPaar> fetchKlantparenMetZelfdeProducten(RiakClient client,
			List<String> riakAankoopKeys, int aantalDezelfdeProducten)
			throws ExecutionException, InterruptedException {
		List<RiakAankoop> riakAankopen = RiakAankoopCRUD.multiFetch(client,riakAankoopKeys); //asynchroon
		
		
		
		HashMap<String, List<RiakAankoop>> klantMap = new HashMap<String, List<RiakAankoop>>();		
		
		for (RiakAankoop aankoop : riakAankopen){
			List<RiakAankoop> productAankopen = klantMap.get(aankoop.klantKey);
			if (productAankopen == null){
				productAankopen = new ArrayList<RiakAankoop>();
			}
			productAankopen.add(aankoop);
			klantMap.put(aankoop.klantKey, productAankopen); //replaces old value
			//System.out.println(aankoop.klantKey+productAankopen);
		}
		
		HashMap<String, List<RiakAankoop>> otherKlantMap = new HashMap<String, List<RiakAankoop>>();
		otherKlantMap.putAll(klantMap);
		//als niet dezelfde klant_key
		List<RiakKlantPaar> klantParen = new ArrayList<RiakKlantPaar>();

		//https://stackoverflow.com/questions/1066589/iterate-through-a-hashmap
		Iterator<Entry<String, List<RiakAankoop>>> itKlantMap = klantMap.entrySet().iterator();
	    while (itKlantMap.hasNext()) {
	    	Entry<String, List<RiakAankoop>> klant = itKlantMap.next();
	    		for (Map.Entry<String, List<RiakAankoop>> otherKlant : otherKlantMap.entrySet()) {
	    			if (!klant.getKey().equals(otherKlant.getKey())){
		    			int overeenkomstigeProducten = checkOvereenkomstigeProducten(klant, otherKlant);
		    			if (overeenkomstigeProducten >= aantalDezelfdeProducten){
		    				RiakKlantPaar riakKlantPaar = new RiakKlantPaar(new RiakKlant(klant.getKey()), new RiakKlant(otherKlant.getKey()), overeenkomstigeProducten);
			    			riakKlantPaar.setKlantenAlfabetical(); //voorkomt Bob, Jeffrey en Jeffrey, bob als klantparen. Wordt wel 2x aangemaakt en overschrijft dan.	    			
			    			if (!klantParen.contains(riakKlantPaar)){		
				    			System.out.println(riakKlantPaar);
				    			klantParen.add(riakKlantPaar);
			    			}
		    			}	    			
		    		}
	    		}

	    		
		      //  itOtherKlantMap.remove(); // avoids a ConcurrentModificationException //Dit zorgt ervoor dat ik maar 1x kan itereren in de while loop.
	        itKlantMap.remove(); // avoids a ConcurrentModificationException
	    }
		System.out.println("einde");	    
		return klantParen;
	}
	
	
	private static int checkOvereenkomstigeProducten(Entry<String, List<RiakAankoop>> klant, Entry<String, List<RiakAankoop>> otherKlant){
		int overeenkomstigeProducten = 0;
		for (RiakAankoop aankoop : klant.getValue()){
			for (RiakAankoop otherAankoop : otherKlant.getValue()){
				if (aankoop.productKey.equals(otherAankoop.productKey)){			
					overeenkomstigeProducten +=  Math.max(aankoop.aantal, otherAankoop.aantal);
					//4 same product van product A en 5 products van product B met alle 9 zeflde naam.
					//hebben 5 overeenkomstige producten. Zo interperteerde ik de opdracht in elk geval. Anders is het gewoon deze rekensom hierzo veranderen.
				}
			}
		}
		return overeenkomstigeProducten;		
	}
}
