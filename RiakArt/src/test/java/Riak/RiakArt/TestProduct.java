package Riak.RiakArt;

import static org.junit.Assert.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.joda.time.LocalDateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import riakModel.RiakAankoop;
import riakModel.RiakProduct;
import riakModel.RiakProductPaar;
import testUtil.MockData;
import util.RiakUtil;
import Riak.RiakArt.RiakProductCRUD;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;

public class TestProduct {

	private static RiakCluster cluster = null;
	private static RiakClient client = null;
	private static MockData mockData;

	@BeforeClass
	public static void setup() {
		try {
			cluster = RiakUtil.setUpCluster();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client = new RiakClient(cluster);
		mockData = new MockData();
	}
	
	@Test
	public void testDeleteProduct() throws ExecutionException, InterruptedException{
		RiakProductCRUD.delete(client, "product8888");
		assertTrue(RiakProductCRUD.fetch(client, "product8888") == null); //this will also give true if there was no data to begin with.
	}
	
	@Test
	public void testMultiFetchProducts() throws ExecutionException, InterruptedException{
		List<RiakProduct> products = RiakUtil.ConvertProductenToRiakProducten(mockData.mockProducten);
		Collections.sort(products, RiakProduct.COMPARE_BY_KEY); //Method is asynchroon so sort before and after.
		RiakProductCRUD.createProducts(client, products);
		
		List<String> productKeys = new ArrayList<String>();
		for (RiakProduct product : products){
			productKeys.add(product.key);
		}
		List<RiakProduct> fetchedProducts = RiakProductCRUD.multiFetch(client, productKeys);
		Collections.sort(fetchedProducts, RiakProduct.COMPARE_BY_KEY);
	    for (int i = 0;i<products.size();i++){
	    	assertTrue(products.get(i).equals(fetchedProducts.get(i)));
	    	//System.out.println("true");
	    }
	    
	    for (RiakProduct product : products) {
			RiakProductCRUD.delete(client, product.key);
		}
	}

	//create
	@Test
	public void testCreateProduct() throws ExecutionException, InterruptedException {
		RiakProduct product = RiakUtil.ConvertProductToRiakProduct(mockData.mockProduct);

		System.out.println("testCreateProduct "+LocalDateTime.now() + " CreateStart");
		RiakProductCRUD.create(client, product);
		System.out.println("testCreateProduct "+LocalDateTime.now() + " CreateEnd");
		assertTrue(product.equals(RiakProductCRUD.fetch(client, "product0")));

		RiakProductCRUD.delete(client, product.key);
	}

	//create
	@Test
	public void testCreateProducts() throws ExecutionException,
			InterruptedException {
		List<RiakProduct> products = RiakUtil.ConvertProductenToRiakProducten(mockData.mockProducten);
		for (RiakProduct product : products) {
			RiakProductCRUD.create(client, product);
		}
		
		for (RiakProduct product : products){
			assertTrue(product.equals(RiakProductCRUD.fetch(client, product.key)));
		}
				
		//als assert verkeert gaat ruimt deze niet op denk ik. hier moet nog iets mee als ik het netjes wil doen.
		for (RiakProduct product : products) {
			RiakProductCRUD.delete(client, product.key);
		}
	}

	//createProducts
	@Test
	public void testcreateProducts() throws ExecutionException,
			InterruptedException {
		List<RiakProduct> products = RiakUtil.ConvertProductenToRiakProducten(mockData.mockProducten);
		System.out.println("testcreateProducts: "+LocalDateTime.now() + " batchCreateStart");
		RiakProductCRUD.createProducts(client, products);
		System.out.println("testcreateProducts: "+LocalDateTime.now() + " batchCreateEnd");
		
		
		for (RiakProduct product : products){
			assertTrue(product.equals(RiakProductCRUD.fetch(client, product.key)));
		}
		for (RiakProduct product : products) {
			RiakProductCRUD.delete(client, product.key);
		}
	}
	
	@Test
	public void testMostBoughtProductPairs() throws ExecutionException, InterruptedException{
		//5 is de most bought product pair, door asynchroon kan de most bought pair wel verschillen aangezien er meerdere paren op 5 zitten.
		//List<RiakProduct> products = RiakUtil.ConvertProductenToRiakProducten(mockData.mockProducten);
		List<RiakAankoop> aankopen = RiakUtil.ConvertAankopenToRiakAankopen(mockData.mockAankopen);
		//RiakProductCRUD.createProducts(client, products);
		RiakAankoopCRUD.createRiakAankopen(client, aankopen);
		
		//fetching
		List<String> riakAankoopKeys = new ArrayList<String>();
		for (RiakAankoop aankoop : aankopen){
			riakAankoopKeys.add(aankoop.key);
		}
		RiakProductPaar riakProductPaar = RiakProductCRUD.fetchMostBoughtProductPairs(client, riakAankoopKeys);
		
		assertEquals(riakProductPaar.samenGekocht, 5);
		System.out.println("testMostBoughtProductPairs: mostbought product pair has: "+riakProductPaar.samenGekocht+"products");
		
		//zou misschien in een clean up methode kunnen, maar dan moet alle testen in deze classes die clean up methode kunnen gebruiken.
		for (RiakAankoop object : aankopen) {
			RiakAankoopCRUD.delete(client, object.key);
		}
	}
}
