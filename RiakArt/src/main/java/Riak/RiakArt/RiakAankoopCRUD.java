package Riak.RiakArt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import riakModel.RiakAankoop;
import riakModel.RiakProduct;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.cap.UnresolvedConflictException;
import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.MultiFetch;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.api.commands.search.StoreIndex;
import com.basho.riak.client.core.RiakFuture;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.search.YokozunaIndex;

public class RiakAankoopCRUD {
	private static String namespaceName = "riakAankopen";

	public static void create(RiakClient client, RiakAankoop aankoop)
			throws ExecutionException, InterruptedException {
		Namespace bucket = new Namespace(namespaceName);
		Location location = new Location(bucket, aankoop.key);
		StoreValue store0p = new StoreValue.Builder(aankoop).withLocation(
				location).build();
		client.execute(store0p);
	}
	
	public static void createRiakAankopen(RiakClient client, List<RiakAankoop> aankopen) throws ExecutionException, InterruptedException{
		for(RiakAankoop aankoop : aankopen){
			create(client, aankoop);
		}
	}

	public static RiakProduct fetch(RiakClient client, String key)
			throws UnresolvedConflictException, ExecutionException,
			InterruptedException {
		Namespace bucket = new Namespace(namespaceName);
		Location location = new Location(bucket, key);
		FetchValue fetchOp = new FetchValue.Builder(location).build();
		RiakProduct fetchedView = client.execute(fetchOp).getValue(
				RiakProduct.class);
		return fetchedView;
	}

	// http://basho.github.io/riak-java-client/2.0.1/com/basho/riak/client/api/commands/kv/MultiFetch.html
	// http://www.massapi.com/class/com/basho/riak/client/api/commands/kv/MultiFetch.html
	public static List<RiakAankoop> multiFetch(RiakClient client,
			List<String> riakAankoopKeys) throws ExecutionException,
			InterruptedException {
		Namespace bucket = new Namespace(namespaceName);
		List<Location> locations = new ArrayList<Location>();
		for (String riakAankoopKey : riakAankoopKeys) {
			Location location = new Location(bucket, riakAankoopKey);
			locations.add(location);
		}

		MultiFetch multifetch = new MultiFetch.Builder()
				.addLocations(locations).build();
		MultiFetch.Response response = client.execute(multifetch);
		List<RiakAankoop> myResults = new ArrayList<RiakAankoop>();
		for (RiakFuture<FetchValue.Response, Location> f : response) {
			try {
				FetchValue.Response response2 = f.get();
				myResults.add(response2.getValue(RiakAankoop.class));
			} catch (ExecutionException e) {
				// log error, etc.
			}
		}
		return myResults;
	}
	
	public static void delete(RiakClient client, String RiakAankoopKey)
			throws ExecutionException, InterruptedException {
		Namespace bucket = new Namespace(namespaceName);
		Location location = new Location(bucket, RiakAankoopKey);
		DeleteValue deleteOp = new DeleteValue.Builder(location).build();
		client.execute(deleteOp);
	}	
	
	public static void deleteAankopen(RiakClient client, List<String> RiakAankoopkeys) throws ExecutionException, InterruptedException{
		for (String aankoopKey : RiakAankoopkeys){
			delete(client, aankoopKey);
		}
	}
}
