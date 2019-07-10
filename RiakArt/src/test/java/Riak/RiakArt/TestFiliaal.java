package Riak.RiakArt;

import static org.junit.Assert.assertEquals;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.junit.BeforeClass;
import org.junit.Test;

import riakModel.RiakAankoop;
import riakModel.RiakFiliaal;
import riakModel.RiakKlant;
import testUtil.MockData;
import util.RiakUtil;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.RiakCluster;

public class TestFiliaal {
	
	private static RiakCluster cluster = null;
	private static RiakClient client = null;
	private static MockData mockData;

	@BeforeClass
	public static void setup() {
		try {
			cluster = RiakUtil.setUpCluster();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		client = new RiakClient(cluster);
		mockData = new MockData();
	}
	
	
	@Test
	public void testGetDistance0KlantKeys() throws ExecutionException, InterruptedException{
		System.out.println("testing testGetDistance0KlantKeys...");
		List<RiakKlant> riakKlanten = RiakUtil.ConvertKlantenToRiakKlanten(mockData.mockKlanten);
		List<RiakAankoop> riakAankopen = RiakUtil.ConvertAankopenToRiakAankopen(mockData.mockAankopen);
		List<RiakFiliaal> riakFilialen = RiakUtil.ConvertFilialenToRiakFilialen(mockData.mockFilialen);
		RiakKlantCRUD.createRiakKlanten(client, riakKlanten);
		RiakAankoopCRUD.createRiakAankopen(client, riakAankopen);
		RiakFiliaalCRUD.createRiakFilialen(client, riakFilialen);
		
		Set<String> klantKeys = RiakFiliaalCRUD.getDistance0KlantKeys(client, mockData.mockKlanten.get(2).key, RiakUtil.ConvertRiakAankopenToRiakAankoopKeys(riakAankopen));
		
		for (RiakKlant riakObject : riakKlanten){
			RiakKlantCRUD.delete(client, riakObject.key);
		}
		for (RiakAankoop riakObject : riakAankopen){
			RiakAankoopCRUD.delete(client, riakObject.key);
		}
		for (RiakFiliaal riakObject : riakFilialen){
			RiakFiliaalCRUD.delete(client, riakObject.key);
		}
		System.out.println("testGetDistance0KlantKeys: klantKeys found "+klantKeys);
		
		assertEquals(klantKeys.size(), 4);
	}
	
	
	//!!! Ik had assertEquals eerst voor de delete staan. Het zou kunnen dat er nog data over is.
	//Vandaar dat delete er twee maal in zit. Just to be sure.
	@Test
	public void testFindFiliaalDistanceNul() throws ExecutionException, InterruptedException{
		System.out.println("testing testFindFiliaalDistanceNul...");
		List<RiakKlant> riakKlanten = RiakUtil.ConvertKlantenToRiakKlanten(mockData.mockKlanten);
		List<RiakAankoop> riakAankopen = RiakUtil.ConvertAankopenToRiakAankopen(mockData.mockAankopen);
		List<RiakFiliaal> riakFilialen = RiakUtil.ConvertFilialenToRiakFilialen(mockData.mockFilialen);
		RiakKlantCRUD.createRiakKlanten(client, riakKlanten);
		RiakAankoopCRUD.createRiakAankopen(client, riakAankopen);
		RiakFiliaalCRUD.createRiakFilialen(client, riakFilialen);
		
		int filiaalDistance = RiakFiliaalCRUD.findFiliaalDistance(client , RiakUtil.ConvertKlantToRiakKlant(mockData.mockKlanten.get(2)), RiakUtil.ConvertKlantToRiakKlant(mockData.mockKlanten.get(3)), RiakUtil.ConvertRiakAankopenToRiakAankoopKeys(riakAankopen));		
		
		for (RiakKlant riakObject : riakKlanten){
			RiakKlantCRUD.delete(client, riakObject.key);
		}
		for (RiakAankoop riakObject : riakAankopen){
			RiakAankoopCRUD.delete(client, riakObject.key);
		}
		for (RiakFiliaal riakObject : riakFilialen){
			RiakFiliaalCRUD.delete(client, riakObject.key);
		}
		for (RiakKlant riakObject : riakKlanten){
			RiakKlantCRUD.delete(client, riakObject.key);
		}
		for (RiakAankoop riakObject : riakAankopen){
			RiakAankoopCRUD.delete(client, riakObject.key);
		}
		for (RiakFiliaal riakObject : riakFilialen){
			RiakFiliaalCRUD.delete(client, riakObject.key);
		}
		
		assertEquals(filiaalDistance, 0);
	}
	
	@Test
	public void testFindFiliaalDistanceOne() throws ExecutionException, InterruptedException{
		System.out.println("testing testFindFiliaalDistanceOne...");
		List<RiakKlant> riakKlanten = RiakUtil.ConvertKlantenToRiakKlanten(mockData.mockKlanten);
		List<RiakAankoop> riakAankopen = RiakUtil.ConvertAankopenToRiakAankopen(mockData.mockAankopen);
		List<RiakFiliaal> riakFilialen = RiakUtil.ConvertFilialenToRiakFilialen(mockData.mockFilialen);
		RiakKlantCRUD.createRiakKlanten(client, riakKlanten);
		RiakAankoopCRUD.createRiakAankopen(client, riakAankopen);
		RiakFiliaalCRUD.createRiakFilialen(client, riakFilialen);
		
		int filiaalDistance = RiakFiliaalCRUD.findFiliaalDistance(client , RiakUtil.ConvertKlantToRiakKlant(mockData.mockKlanten.get(3)), RiakUtil.ConvertKlantToRiakKlant(mockData.mockKlanten.get(5)), RiakUtil.ConvertRiakAankopenToRiakAankoopKeys(riakAankopen));		
		
		for (RiakKlant riakObject : riakKlanten){
			RiakKlantCRUD.delete(client, riakObject.key);
		}
		for (RiakAankoop riakObject : riakAankopen){
			RiakAankoopCRUD.delete(client, riakObject.key);
		}
		for (RiakFiliaal riakObject : riakFilialen){
			RiakFiliaalCRUD.delete(client, riakObject.key);
		}
		
		assertEquals(filiaalDistance, 1);
	}
}
