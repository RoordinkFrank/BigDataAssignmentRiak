package Riak.RiakArt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import riakModel.RiakAankoop;
import riakModel.RiakProduct;
import riakModel.RiakProductPaar;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.cap.UnresolvedConflictException;
import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.MultiFetch;
import com.basho.riak.client.api.commands.kv.MultiFetch.Builder;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.api.commands.kv.UpdateValue;
import com.basho.riak.client.core.RiakFuture;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;

public class RiakProductCRUD {

	private static String namespaceName = "products";
	
	//geen batchcreate, wordt gewoon achter elkaar gedaan, het enige wat gewonnen is de namespace.
	public static void createProducts(RiakClient client, List<RiakProduct> products)
			throws ExecutionException, InterruptedException {
		Namespace bucket = new Namespace(namespaceName);
		for (RiakProduct product : products) {
			Location location = new Location(bucket, product.key);
			StoreValue store0p = new StoreValue.Builder(product).withLocation(
					location).build();
			client.execute(store0p);
		}
	}

	public static void create(RiakClient client, RiakProduct product)
			throws ExecutionException, InterruptedException {
		Namespace bucket = new Namespace(namespaceName);
		Location location = new Location(bucket, product.key);
		StoreValue storeOp = new StoreValue.Builder(product).withLocation(
				location).build();
		client.execute(storeOp);
	}

	public static void update(RiakClient client, RiakProduct product)
			throws ExecutionException, InterruptedException {
		Namespace bucket = new Namespace(namespaceName);
		Location location = new Location(bucket, product.key);
		RiakProduct.ProductUpdate updatedProduct = new RiakProduct.ProductUpdate(
				product);
		UpdateValue updateValue = new UpdateValue.Builder(location).withUpdate(
				updatedProduct).build();
		UpdateValue.Response response = client.execute(updateValue);
	}

	public static RiakProduct fetch(RiakClient client, String productKey)
			throws UnresolvedConflictException, ExecutionException,
			InterruptedException {
		Namespace bucket = new Namespace(namespaceName);
		Location location = new Location(bucket, productKey);
		FetchValue fetchOp = new FetchValue.Builder(location).build();
		RiakProduct fetchedProduct = client.execute(fetchOp)
				.getValue(RiakProduct.class);
		return fetchedProduct;
	}
	
	//http://basho.github.io/riak-java-client/2.0.1/com/basho/riak/client/api/commands/kv/MultiFetch.html
	//http://www.massapi.com/class/com/basho/riak/client/api/commands/kv/MultiFetch.html
	public static List<RiakProduct> multiFetch(RiakClient client, List<String> productKeys) throws ExecutionException, InterruptedException{
		 Namespace bucket = new Namespace(namespaceName);
		 List<Location> locations = new ArrayList<Location>();
		 for (String productKey : productKeys){
			 Location location = new Location(bucket, productKey);
			 locations.add(location);
		 }
		
		 MultiFetch multifetch = new MultiFetch.Builder().addLocations(locations).build();
		 MultiFetch.Response response = client.execute(multifetch);
		 List<RiakProduct> myResults = new ArrayList<RiakProduct>();
		 for (RiakFuture<FetchValue.Response, Location> f : response)
		 {
		     try
		     {
		          FetchValue.Response response2 = f.get();
		          myResults.add(response2.getValue(RiakProduct.class));
		     }
		     catch (ExecutionException e)
		     {
		         // log error, etc.
		     }
		 }
		 return myResults;
	}

	public static void delete(RiakClient client, String productKey)
			throws ExecutionException, InterruptedException {
		Namespace bucket = new Namespace(namespaceName);
		Location location = new Location(bucket, productKey);
		DeleteValue deleteOp = new DeleteValue.Builder(location).build();
		client.execute(deleteOp);
	}
	
	//Vind welke productparen het vaakst tegelijk gekocht worden (optioneel per filiaal).
	public static RiakProductPaar fetchMostBoughtProductPairs(RiakClient client, List<String> riakAankoopKeys) throws ExecutionException, InterruptedException{
		List<RiakAankoop> riakAankopen = RiakAankoopCRUD.multiFetch(client,riakAankoopKeys);	
		HashMap<String, RiakProductPaar> productParen = new HashMap<String, RiakProductPaar>();	
		for (RiakAankoop aankoop : riakAankopen){
			for (RiakAankoop otherAankoop : riakAankopen){
				if (!aankoop.equals(otherAankoop)){
					if (aankoop.datum.compareTo(otherAankoop.datum) == 0){
						RiakProductPaar paar = productParen.get(aankoop.productKey+otherAankoop.productKey);
						if (paar == null){
							paar = new RiakProductPaar(aankoop.productKey, otherAankoop.productKey);
						}
						paar.samenGekocht += 1;
						productParen.put(aankoop.productKey+otherAankoop.productKey, paar); //is misschien niet nodig tenzij paar null is?, snap nog te weinig van hashmap.
					}
				}
			}
		}
		
		RiakProductPaar meestGekocht = null;
		for (Map.Entry<String, RiakProductPaar> productPaar : productParen.entrySet()){
			if(meestGekocht == null){
				meestGekocht = productPaar.getValue();
			}
			else{
				if (productPaar.getValue().samenGekocht > meestGekocht.samenGekocht){
					meestGekocht = productPaar.getValue();
				}
			}
		}
		return meestGekocht;	
	}
}
