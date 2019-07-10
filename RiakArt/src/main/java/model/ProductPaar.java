package model;

public class ProductPaar {

	public Product firstProductKey;
	public Product otherProductKey;
	public int aantal;
	
	public ProductPaar(){
		
	}
	
	public ProductPaar(Product firstProductKey, Product otherProductKey,
			int aantal) {
		super();
		this.firstProductKey = firstProductKey;
		this.otherProductKey = otherProductKey;
		this.aantal = aantal;
	}

	@Override
	public String toString() {
		return "ProductPaar [firstProductKey=" + firstProductKey
				+ ", otherProductKey=" + otherProductKey + ", aantal=" + aantal
				+ "]";
	}
}
