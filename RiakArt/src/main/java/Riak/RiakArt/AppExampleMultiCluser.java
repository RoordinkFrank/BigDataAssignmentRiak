package Riak.RiakArt;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakFuture;
import com.basho.riak.client.core.RiakNode;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;

/**
 * Hello world!
 */
public class AppExampleMultiCluser 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
       
        //RiakClient client = RiakClient.newClient("192.168.1.1","192.168.1.2","192.168.1.3");  
        
        //set nodes and client
        RiakNode.Builder builder = new RiakNode.Builder();
        builder.withMinConnections(10);
        builder.withMaxConnections(50);
        
        List<String> addresses = new LinkedList<String>();
        addresses.add("192.168.1.1");
        addresses.add("192.168.1.2");
        addresses.add("192.168.1.3");
        
        List<RiakNode> nodes = RiakNode.Builder.buildNodes(builder, addresses);
        RiakCluster cluster = new RiakCluster.Builder(nodes).build();
        cluster.start();
        RiakClient client = new RiakClient(cluster);
        //set nodes and client
        
        Namespace ns = new Namespace("default","my_bucket");
        Location loc = new Location(ns, "my_key");
        FetchValue fv = new FetchValue.Builder(loc).build();
        FetchValue.Response response;
		try {
			response = client.execute(fv);
			RiakObject obj = response.getValue(RiakObject.class);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
}
