package Riak.RiakArt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.UnknownHostException;
//import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

import model.KlantPaar;

import org.junit.BeforeClass;
import org.junit.Test;

import riakModel.RiakAankoop;
import riakModel.RiakKlant;
import riakModel.RiakKlantPaar;
import riakModel.RiakProduct;
import testUtil.MockData;
import util.RiakUtil;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;

public class TestKlant {
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
	public void testDeleteKlant() throws ExecutionException, InterruptedException{
		RiakKlantCRUD.delete(client, "klant8888");
		assertTrue(RiakKlantCRUD.fetch(client, "klant8888") == null); //this will also give true if there was no data to begin with.
	}

	@Test
	public void testCreateKlant() throws ExecutionException, InterruptedException {
		RiakKlant klant = RiakUtil.ConvertKlantToRiakKlant(mockData.mockKlant);
		RiakKlantCRUD.create(client, klant);
		assertTrue(klant.equals(RiakKlantCRUD.fetch(client, "Gijs")));

		RiakKlantCRUD.delete(client, klant.key);
	}

	@Test
	public void testCreateKlanten() throws ExecutionException,
			InterruptedException {
		List<RiakKlant> klanten = RiakUtil.ConvertKlantenToRiakKlanten(mockData.mockKlanten);
		for (RiakKlant klant : klanten) {
			RiakKlantCRUD.create(client, klant);
		}
		for (RiakKlant klant : klanten){
			assertTrue(klant.equals(RiakKlantCRUD.fetch(client, klant.key)));
		}
		
		for (RiakKlant klant : klanten) {
			RiakKlantCRUD.delete(client, klant.key);
		}
	}
	
	@Test
	public void testSelectKlantparenMetVierZelfdeProducten(){
		try {
			List<RiakAankoop> riakAankopen = RiakUtil.ConvertAankopenToRiakAankopen(mockData.mockAankopen);
			RiakAankoopCRUD.createRiakAankopen(client, riakAankopen);
			List<RiakKlantPaar> klantParen = RiakKlantCRUD.fetchKlantparenMetZelfdeProducten(client, RiakUtil.ConvertRiakAankopenToRiakAankoopKeys(riakAankopen), 4);
			assertEquals(klantParen.size(), 9);
			RiakAankoopCRUD.deleteAankopen(client, RiakUtil.ConvertRiakAankopenToRiakAankoopKeys(riakAankopen));
		} catch (ExecutionException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
