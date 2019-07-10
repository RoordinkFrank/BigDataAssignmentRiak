package Riak.RiakArt;

import java.util.concurrent.ExecutionException;

import riakModel.RiakProduct;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;

public class Riak {
	
	//Op het moment is delete overal dit stukje code. Er is een paar keer gekopierd maar vanaf nu verwijst de code hierheen.
	//Delete wordt nog steeds vanaf de CRUD aangeroepen. En het hoort daar en er kunnen in de toekomst afwijkingen in de delete functies komen.
	public static void delete(RiakClient client, String namespaceName, String key)
			throws ExecutionException, InterruptedException {
		Namespace bucket = new Namespace(namespaceName);
		Location location = new Location(bucket, key);
		DeleteValue deleteOp = new DeleteValue.Builder(location).build();
		client.execute(deleteOp);
	}
}
