package util;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.Aankoop;
import model.Filiaal;
import model.Klant;
import model.Product;
import riakModel.RiakAankoop;
import riakModel.RiakFiliaal;
import riakModel.RiakKlant;
import riakModel.RiakProduct;

import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;

public class RiakUtil {
	 
	// This will create a client object that we can use to interact with Riak
	public static RiakCluster setUpCluster() throws UnknownHostException {
		// This example will use only one node listening on localhost:10017
		RiakNode node = new RiakNode.Builder().withRemoteAddress("127.0.0.1")
				.withRemotePort(8087)
				.build();
		// This cluster object takes our one node as an argument
		RiakCluster cluster = new RiakCluster.Builder(node).build();
		// The cluster must be started to work, otherwise you will see errors
		cluster.start();
		return cluster;
	}
	
	public static RiakKlant ConvertKlantToRiakKlant(Klant klant){
		return new RiakKlant(klant.key);
	}
	
	public static List<RiakKlant> ConvertKlantenToRiakKlanten(List<Klant> klanten){
		List<RiakKlant> riakKlanten = new ArrayList<RiakKlant>();
		for (Klant klant : klanten){
			riakKlanten.add(ConvertKlantToRiakKlant(klant));
		}
		return riakKlanten;
	}
	
	public static RiakFiliaal ConvertFiliaalToRiakFiliaal(Filiaal filiaal){
		return new RiakFiliaal(filiaal.key);
	}
	
	public static List<RiakFiliaal> ConvertFilialenToRiakFilialen(List<Filiaal> filialen){
		List<RiakFiliaal> riakFilialen = new ArrayList<RiakFiliaal>();
		for (Filiaal filiaal : filialen){
			riakFilialen.add(ConvertFiliaalToRiakFiliaal(filiaal));
		}
		return riakFilialen;
	}
	 
	public static RiakProduct ConvertProductToRiakProduct(Product product){
		return new RiakProduct(product.key, product.omschrijving, product.aantal);
	}
	
	public static List<RiakProduct> ConvertProductenToRiakProducten(List<Product> producten){
		List<RiakProduct> riakProducten = new ArrayList<RiakProduct>();
		for (Product product : producten){
			riakProducten.add(ConvertProductToRiakProduct(product));
		}
		return riakProducten;
	}
	
	public static RiakAankoop ConvertAankoopToRiakAankoop(Aankoop aankoop){
		return new RiakAankoop(aankoop.key, aankoop.aantal, aankoop.datum, aankoop.product.key, aankoop.filiaal.key, aankoop.klant.key);
	}
	
	public static List<RiakAankoop> ConvertAankopenToRiakAankopen(List<Aankoop> aankopen){
		List<RiakAankoop> riakAankopen = new ArrayList<RiakAankoop>();
		for (Aankoop aankoop : aankopen){
			riakAankopen.add(ConvertAankoopToRiakAankoop(aankoop));
		}
		return riakAankopen;
	}
	
	public static List<String> ConvertRiakAankopenToRiakAankoopKeys(List<RiakAankoop> riakAankopen){
		return riakAankopen.stream().map(RiakAankoop::getKey).collect(Collectors.toList());
	}
}